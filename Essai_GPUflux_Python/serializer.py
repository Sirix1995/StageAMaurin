import sys
import pyopencl as cl
import pyopencl.tools
import pyopencl.array
import numpy as np
from openalea.plantgl.all import *  

class Serializer():
    def __init__(self) -> None:
        self.firstInfos = [("type", np.int32), ("groupIndex", np.int32), ("shaderOffset", np.int32), ("indexOfReflexion", np.float32)]
        self.boundingBox = [("xMin", np.float32), ("xMax", np.float32), ("yMin", np.float32), ("yMax", np.float32), ("zMin", np.float32), ("zMax", np.float32)]
        self.matrix = [("mat00", np.float32), ("mat01", np.float32), ("mat02", np.float32),
                       ("mat10", np.float32), ("mat11", np.float32), ("mat12", np.float32),
                       ("mat20", np.float32), ("mat21", np.float32), ("mat22", np.float32),
                       ("mat30", np.float32), ("mat31", np.float32), ("mat32", np.float32)]

        self.primitive = self.firstInfos + self.boundingBox + self.matrix

        self.polygon = self.primitive + [("x1", np.float32), ("y1", np.float32), ("z1", np.float32),
                                         ("x2", np.float32), ("y2", np.float32), ("z2", np.float32),
                                         ("x3", np.float32), ("y3", np.float32), ("z3", np.float32),
                                         ("faceNormalX", np.float32), ("faceNormalY", np.float32), ("faceNormalZ", np.float32),
                                         ("uvX1", np.float32), ("uvY1", np.float32),
                                         ("uvX2", np.float32), ("uvY2", np.float32),
                                         ("uvX3", np.float32), ("uvY3", np.float32),
                                         ("normalX1", np.float32), ("normalY1", np.float32), ("normalZ1", np.float32),
                                         ("normalX2", np.float32), ("normalY2", np.float32), ("normalZ2", np.float32),
                                         ("normalX3", np.float32), ("normalY3", np.float32), ("normalZ3", np.float32)]

    # Return a triangleSet for each triangle of trSet in a list
    def getTriangles(self, trSet):
        resultList = []
        indices = trSet.indexList
        trSet.normalPerVertex = True
        trSet.computeNormalList()
        for face in indices:
            points = [(trSet.pointAt(face[0])[0], trSet.pointAt(face[0])[1], trSet.pointAt(face[0])[2]), 
                      (trSet.pointAt(face[1])[0], trSet.pointAt(face[1])[1], trSet.pointAt(face[1])[2]), 
                      (trSet.pointAt(face[2])[0], trSet.pointAt(face[2])[1], trSet.pointAt(face[2])[2])]
            rIndices = [(0, 1, 2)]
            triangle = TriangleSet(points, rIndices)
            triangle.normalPerVertex = True
            triangle.normalList = (trSet.normalList[face[0]], trSet.normalList[face[1]], trSet.normalList[face[2]])
            resultList.append(triangle)
        return resultList

    # Put the generic infos of the primitive in the buffer
    def setPrimInfos(self, buffer, prim, primType, groupIndex, shaderOffset, indexOfReflexion):
        #First Infos
        buffer["type"].fill(primType)
        buffer["groupIndex"].fill(groupIndex)
        buffer["shaderOffset"].fill(shaderOffset)
        buffer["indexOfReflexion"].fill(indexOfReflexion)

        #BoundingBox
        bbox = BoundingBox(prim)
        buffer["xMin"].fill(bbox.getXMin())
        buffer["xMax"].fill(bbox.getXMax())
        buffer["yMin"].fill(bbox.getYMin())
        buffer["yMax"].fill(bbox.getYMax())
        buffer["zMin"].fill(bbox.getZMin())
        buffer["zMax"].fill(bbox.getZMax())

        #World to Object Matrix
        buffer["mat00"].fill(1.0)
        buffer["mat01"].fill(0.0)
        buffer["mat02"].fill(0.0)
        buffer["mat10"].fill(0.0)
        buffer["mat11"].fill(1.0)
        buffer["mat12"].fill(0.0)
        buffer["mat20"].fill(0.0)
        buffer["mat21"].fill(0.0)
        buffer["mat22"].fill(1.0)
        buffer["mat30"].fill(0.0)
        buffer["mat31"].fill(0.0)
        buffer["mat32"].fill(0.0)

    def serializeTriangle(self, triangle, groupIndex, shaderOffset, indexOfReflexion):
        buffer = np.array(1, dtype= self.polygon)
        self.setPrimInfos(buffer, triangle, 5, groupIndex, shaderOffset, indexOfReflexion)
        normalVertex = triangle.normalList

        #Vertex coords
        buffer["x1"].fill(triangle.pointAt(0)[0])
        buffer["y1"].fill(triangle.pointAt(0)[1])
        buffer["z1"].fill(triangle.pointAt(0)[2])

        buffer["x2"].fill(triangle.pointAt(1)[0])
        buffer["y2"].fill(triangle.pointAt(1)[1])
        buffer["z2"].fill(triangle.pointAt(1)[2])

        buffer["x3"].fill(triangle.pointAt(2)[0])
        buffer["y3"].fill(triangle.pointAt(2)[1])
        buffer["z3"].fill(triangle.pointAt(2)[2])

        #Face normal
        triangle.normelPerVertex = False
        triangle.computeNormalList()

        buffer["faceNormalX"].fill(triangle.normalList[0][0])
        buffer["faceNormalX"].fill(triangle.normalList[0][1])
        buffer["faceNormalX"].fill(triangle.normalList[0][2])

        #UV coords
        buffer["uvX1"].fill(0.0)
        buffer["uvY1"].fill(0.0)

        buffer["uvX2"].fill(0.0)
        buffer["uvY2"].fill(0.0)

        buffer["uvX3"].fill(0.0)
        buffer["uvY3"].fill(0.0)

        #Vertex Normals
        buffer["normalX1"].fill(normalVertex[0][0])
        buffer["normalY1"].fill(normalVertex[0][1])
        buffer["normalZ1"].fill(normalVertex[0][2])

        buffer["normalX2"].fill(normalVertex[0][0])
        buffer["normalY2"].fill(normalVertex[0][1])
        buffer["normalZ2"].fill(normalVertex[0][2])

        buffer["normalX3"].fill(normalVertex[0][0])
        buffer["normalY3"].fill(normalVertex[0][1])
        buffer["normalZ3"].fill(normalVertex[0][2])

        return buffer

    def serializeTriangleSet(self, trSet, groupIndex, shaderOffset, indexOfReflexion):
        triangles = self.getTriangles(trSet)
        triangleDataList = []

        for triangle in triangles:
            triangleDataList.append(self.serializeTriangle(triangle, groupIndex, shaderOffset, indexOfReflexion))

        bytechain = triangleDataList[0].tobytes()

        offsets = np.empty(len(triangleDataList), np.int32)
        offsetIndex = 1
        offsets[0] = 0
        acc = len(bytechain)

        for triangleData in triangleDataList[1:]:
            triangleInBytes = triangleData.tobytes()
            bytechain+= triangleInBytes
            offsets[offsetIndex] = acc
            offsetIndex+=1
            acc+= len(triangleInBytes)

        return bytechain, offsets

serial = Serializer()

points = [(0.0, 0.0, 0.0),
          (0.0, 1.0, 0.0),
          (1.0, 0.0, 0.0),
         (1.0, 1.0, 1.0)]

indices = [(0, 1, 2),
           (0, 1, 3),
           (0, 2, 3),
           (1, 2, 3)]

tetra = TriangleSet(points, indices)

primitives, offsets = serial.serializeTriangleSet(tetra, 0, 0, 0.0)
print(primitives)
print(offsets)

# Options GPUFlux
options = " -D MEASURE_FULL_SPECTRUM"
options += " -D MEASURE_MIN_LAMBDA=380"
options += " -D MEASURE_MAX_LAMBDA=720"
options += " -D MEASURE_SPECTRUM_BINS=340"
options += " -D SPECTRAL_WAVELENGTH_MIN=360"
options += " -D SPECTRAL_WAVELENGTH_MAX=830"
options += " -D SPECTRAL_WAVELENGTH_BINS=1"
options += " -D BVH"
options += " -D ENABLE_SENSORS"

# Options machine
options += " -D CL_KHR_GLOBAL_INT32_BASE_ATOMICS"
options += " -D CL_KHR_GLOBAL_INT32_EXTENDED_ATOMICS"
options += " -D CL_KHR_LOCAL_INT32_BASE_ATOMICS"
options += " -D CL_KHR_LOCAL_INT32_EXTENDED_ATOMICS"
options += " -D CL_KHR_FP64"
options += " -D CL_KHR_BYTE_ADDRESSABLE_STORE"
options += " -D CL_KHR_ICD"
options += " -D CL_KHR_GL_SHARING"
options += " -D CL_NV_COMPILER_OPTIONS"
options += " -D CL_NV_DEVICE_ATTRIBUTE_QUERY"
options += " -D CL_NV_PRAGMA_UNROLL"
options += " -D CL_NV_COPY_OPTS"
options += " -D CL_KHR_GL_EVENT"
options += " -D CL_NV_CREATE_BUFFER"
options += " -D CL_KHR_INT64_BASE_ATOMICS"
options += " -D CL_KHR_INT64_EXTENDED_ATOMICS"

# Options répertoire
options += " -I kernel/"

context = cl.create_some_context()
queue = cl.CommandQueue(context)

kernelSource =  """
                #include "geo/prim.h"
                #include "geo/polygon.h"
                
                __kernel void structTest(__global char* prims, __global int* offsets, __global int* types) {
                    int i = get_global_id(0);
                    int offset = offsets[i];
                    const __global Polygon *prim = (const __global Polygon*)(prims + offset);

                    types[i] = prim->base.type;
                 }
                 
                __kernel void structSize(int polySize, int primSize) {
                    polySize = sizeof(Polygon);
                    primSize = sizeof(Prim);
                }"""

taille = len(offsets)

# Input buffers
bufPrim = cl.Buffer(context, cl.mem_flags.READ_ONLY | cl.mem_flags.COPY_HOST_PTR, hostbuf=primitives)
bufOffsets = cl.Buffer(context, cl.mem_flags.READ_ONLY | cl.mem_flags.COPY_HOST_PTR, hostbuf=offsets)

#Output buffers
bufTypes = cl.Buffer(context, cl.mem_flags.WRITE_ONLY, offsets.nbytes)

program = cl.Program(context, kernelSource).build(options)

program.structTest(queue, (taille,), None, bufPrim, bufOffsets, bufTypes)

'''polySize = 0
primSize = 0
program.structSize(queue, None, None, polySize, primSize)

print(polySize, " + ", primSize)'''

print(bufTypes)

resultat = np.empty(taille, np.int32)
cl.enqueue_copy(queue, resultat, bufTypes)

print(resultat)