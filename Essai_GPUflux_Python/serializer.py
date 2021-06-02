import sys
import pyopencl as cl
import pyopencl.tools
import pyopencl.array
import numpy as np
from openalea.plantgl.all import *
import structfill

class Serializer():
    def __init__(self) -> None:
        self.firstInfos = [("type", np.int32), ("groupIndex", np.int32), ("shaderOffset", np.int32), ("indexOfReflexion", np.float32)]
        self.boundingBox = [("xMin", np.float32), ("xMax", np.float32), ("yMin", np.float32), ("yMax", np.float32), ("zMin", np.float32), ("zMax", np.float32)]
        self.matrix = [("WtOMatrix", np.float32, 12)]

        self.primitive = self.firstInfos + self.boundingBox + self.matrix

        self.polygon = self.primitive + [("point1", np.float32, 3),
                                         ("point2", np.float32, 3),
                                         ("point3", np.float32, 3),
                                         ("faceNormal", np.float32, 3),
                                         ("uvPoint1", np.float32, 2),
                                         ("uvPoint2", np.float32, 2),
                                         ("uvPoint3", np.float32, 2),
                                         ("normalPoint1", np.float32, 3),
                                         ("normalPoint2", np.float32, 3),
                                         ("normalPoint3", np.float32, 3)]

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
    def setPrimInfos(self, buffer, prim, primType, groupIndex, shaderOffset, indexOfReflexion, matrix):

        #Type checking
        primType = np.int32(primType)
        groupIndex = np.int32(groupIndex)
        indexOfReflexion = np.float32(indexOfReflexion)

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
        structfill.fillMatrix34(buffer, "WtOMatrix", matrix)

    #Serialize a triangle (matrix definition will be changed)
    def serializeTriangle(self, triangle, groupIndex, shaderOffset, indexOfReflexion):
        buffer = np.array(1, dtype= self.polygon)
        self.setPrimInfos(buffer, triangle, 5, groupIndex, shaderOffset, indexOfReflexion, Matrix4((1, 0, 0, 0 , 0, 1, 0, 0 , 0, 0, 1, 0 , 0, 0, 0, 1)))
        normalVertex = triangle.normalList

        #Vertex coords
        structfill.fillVec3(buffer, "point1", triangle.pointAt(0))
        structfill.fillVec3(buffer, "point2", triangle.pointAt(1))
        structfill.fillVec3(buffer, "point3", triangle.pointAt(2))

        #Face normal
        triangle.normelPerVertex = False
        triangle.computeNormalList()
        structfill.fillVec3(buffer, "faceNormal", triangle.normalList[0])

        #UV coords
        structfill.fillVec2(buffer, "uvPoint1", Vector2(0.0, 0.0))
        structfill.fillVec2(buffer, "uvPoint2", Vector2(0.0, 0.0))
        structfill.fillVec2(buffer, "uvPoint3", Vector2(0.0, 0.0))

        #Vertex Normals
        structfill.fillVec3(buffer, "normalPoint1", normalVertex[0])
        structfill.fillVec3(buffer, "normalPoint2", normalVertex[1])
        structfill.fillVec3(buffer, "normalPoint3", normalVertex[2])

        return buffer

    #Serialize a TriangleSet
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

#Class test (will be removed)

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
boule = Sphere(2)
tessel = Tesselator()
boule.apply(tessel)
triBoule = tessel.triangulation
print(triBoule)

primitives, offsets = serial.serializeTriangleSet(triBoule, 0, 0, 0.0)
# print(primitives)
# print(offsets)

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
#options += " -D CL_KHR_LOCAL_INT32_BASE_ATOMICS"
#options += " -D CL_KHR_LOCAL_INT32_EXTENDED_ATOMICS"
#options += " -D CL_KHR_FP64"
#options += " -D CL_KHR_BYTE_ADDRESSABLE_STORE"
#options += " -D CL_KHR_ICD"
#options += " -D CL_KHR_GL_SHARING"
#options += " -D CL_NV_COMPILER_OPTIONS"
#options += " -D CL_NV_DEVICE_ATTRIBUTE_QUERY"
#options += " -D CL_NV_PRAGMA_UNROLL"
#options += " -D CL_NV_COPY_OPTS"
#options += " -D CL_KHR_GL_EVENT"
#options += " -D CL_NV_CREATE_BUFFER"
options += " -D CL_KHR_INT64_BASE_ATOMICS"
#options += " -D CL_KHR_INT64_EXTENDED_ATOMICS"

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
                 }"""

taille = len(offsets)

# Input buffers
bufPrim = cl.Buffer(context, cl.mem_flags.READ_ONLY | cl.mem_flags.COPY_HOST_PTR, hostbuf=primitives)
bufOffsets = cl.Buffer(context, cl.mem_flags.READ_ONLY | cl.mem_flags.COPY_HOST_PTR, hostbuf=offsets)

#Output buffers
bufTypes = cl.Buffer(context, cl.mem_flags.WRITE_ONLY, offsets.nbytes)

program = cl.Program(context, kernelSource).build(options)

program.structTest(queue, (taille,), None, bufPrim, bufOffsets, bufTypes)

print(bufTypes)

resultat = np.empty(taille, np.int32)
cl.enqueue_copy(queue, resultat, bufTypes)

print(resultat)