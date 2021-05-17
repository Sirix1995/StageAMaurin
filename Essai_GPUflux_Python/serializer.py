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

        self.primitive = firstInfos + boundingBox + matrix

        self.polygon = primitive + [("x1", np.float32), ("y1", np.float32), ("z1", np.float32),
                                    ("x2", np.float32), ("y2", np.float32), ("z2", np.float32),
                                    ("x3", np.float32), ("y3", np.float32), ("z3", np.float32),
                                    ("faceNormalx", np.float32), ("faceNormalY", np.float32), ("faceNormalZ", np.float32),
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

    def serializePrimitive(self, prim, primType, groupIndex, shaderOffset, indexOfReflexion):
        #First Infos
        dataTable = np.array(1, dtype= self.primitive)
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
        dataTable.fill["mat00"].fill(1)
        dataTable.fill["mat01"].fill(0)
        dataTable.fill["mat02"].fill(0)
        dataTable.fill["mat10"].fill(0)
        dataTable.fill["mat11"].fill(1)
        dataTable.fill["mat12"].fill(0)
        dataTable.fill["mat20"].fill(0)
        dataTable.fill["mat21"].fill(0)
        dataTable.fill["mat22"].fill(1)
        dataTable.fill["mat30"].fill(0)
        dataTable.fill["mat31"].fill(0)
        dataTable.fill["mat32"].fill(0)
        
        return dataTable

    def serializeTriangle(self, triangle, groupIndex, shaderOffset, indexOfReflexion):
        firstInfos = serializePrimitive(triangle, 5, groupIndex, shaderOffset, indexOfReflexion)
        polygon = np.array(1, dtype= self.polygon)

    def serializeTriangleSet(self, trSet):
        triangles = getTriangles(trSet)
        