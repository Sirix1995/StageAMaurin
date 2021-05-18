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
            points = [(trSet.pointAt(face[0])[0], trSet.pointAt(face[0])[1], trSet.pointAt(face[0])[2]), (trSet.pointAt(face[1])[0], trSet.pointAt(face[1])[1], trSet.pointAt(face[1])[2]), (trSet.pointAt(face[2])[0], trSet.pointAt(face[2])[1], trSet.pointAt(face[2])[2])]
            rIndices = [(0, 1, 2)]
            triangle = TriangleSet(points, rIndices)
            triangle.normalPerVertex = True
            triangle.normalList = (trSet.normalList[face[0]], trSet.normalList[face[1]], trSet.normalList[face[2]])
            resultList.append(triangle)
        return resultList

    def serializePrimitive(self, dataTable, prim, primType, groupIndex, shaderOffset, indexOfReflexion):
        #First Infos
        dataTable["type"].fill(primType)
        dataTable["groupIndex"].fill(groupIndex)
        dataTable["shaderOffset"].fill(shaderOffset)
        dataTable["indexOfReflexion"].fill(indexOfReflexion)

        #BoundingBox
        bbox = BoundingBox(prim)
        dataTable["xMin"].fill(bbox.getXMin())
        dataTable["xMax"].fill(bbox.getXMax())
        dataTable["yMin"].fill(bbox.getYMin())
        dataTable["yMax"].fill(bbox.getYMax())
        dataTable["zMin"].fill(bbox.getZMin())
        dataTable["zMax"].fill(bbox.getZMax())

        #World to Object Matrix
        dataTable.fill["mat00"].fill(1.0)
        dataTable.fill["mat01"].fill(0.0)
        dataTable.fill["mat02"].fill(0.0)
        dataTable.fill["mat10"].fill(0.0)
        dataTable.fill["mat11"].fill(1.0)
        dataTable.fill["mat12"].fill(0.0)
        dataTable.fill["mat20"].fill(0.0)
        dataTable.fill["mat21"].fill(0.0)
        dataTable.fill["mat22"].fill(1.0)
        dataTable.fill["mat30"].fill(0.0)
        dataTable.fill["mat31"].fill(0.0)
        dataTable.fill["mat32"].fill(0.0)

    def serializeTriangle(self, triangle, groupIndex, shaderOffset, indexOfReflexion):
        polygon = np.array(1, dtype= self.polygon)
        self.serializePrimitive(polygon, triangle, 5, groupIndex, shaderOffset, indexOfReflexion)
        normalVertex = triangle.normalList

        #Vertex coords
        polygon["x1"].fill(triangle.pointAt(0)[0])
        polygon["y1"].fill(triangle.pointAt(0)[1])
        polygon["z1"].fill(triangle.pointAt(0)[2])

        polygon["x2"].fill(triangle.pointAt(1)[0])
        polygon["y2"].fill(triangle.pointAt(1)[1])
        polygon["z2"].fill(triangle.pointAt(1)[2])

        polygon["x3"].fill(triangle.pointAt(2)[0])
        polygon["y3"].fill(triangle.pointAt(2)[1])
        polygon["z3"].fill(triangle.pointAt(2)[2])

        #Face normal
        triangle.normelPerVertex = False
        triangle.computeNormalList()

        polygon["faceNormalX"].fill(triangle.normalList[0][0])
        polygon["faceNormalX"].fill(triangle.normalList[0][1])
        polygon["faceNormalX"].fill(triangle.normalList[0][2])

        #UV coords
        polygon["uvX1"].fill(0.0)
        polygon["uvY1"].fill(0.0)

        polygon["uvX2"].fill(0.0)
        polygon["uvY2"].fill(0.0)

        polygon["uvX3"].fill(0.0)
        polygon["uvY3"].fill(0.0)

        #Vertex Normals
        polygon["normalX1"].fill(normalVertex[0][0])
        polygon["normalY1"].fill(normalVertex[0][1])
        polygon["normalZ1"].fill(normalVertex[0][2])

        polygon["normalX2"].fill(normalVertex[0][0])
        polygon["normalY2"].fill(normalVertex[0][1])
        polygon["normalZ2"].fill(normalVertex[0][2])

        polygon["normalX3"].fill(normalVertex[0][0])
        polygon["normalY3"].fill(normalVertex[0][1])
        polygon["normalZ3"].fill(normalVertex[0][2])

        return polygon

    def serializeTriangleSet(self, trSet, groupIndex, shaderOffset, indexOfReflexion):
        triangles = self.getTriangles(trSet)
        triangleDataList = []
        bytechain = "\x00"

        for triangle in triangles:
            triangleDataList.append(self.serializeTriangle(triangle, groupIndex, shaderOffset, indexOfReflexion))

        for triangleData in triangleDataList:
            bytechain+= triangleData.tobytes()

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

resultat = serial.serializeTriangleSet(tetra, 0, 0, 0.0)
print(resultat)