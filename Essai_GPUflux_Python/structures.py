import numpy as np

firstInfos = [("type", np.int32), ("groupIndex", np.int32), ("shaderOffset", np.int32), ("indexOfReflexion", np.float32)]
boundingBox = [("xMin", np.float32), ("xMax", np.float32), ("yMin", np.float32), ("yMax", np.float32), ("zMin", np.float32), ("zMax", np.float32)]
matrix = [("mat00", np.float32), ("mat01", np.float32), ("mat02", np.float32),
         ("mat10", np.float32), ("mat11", np.float32), ("mat12", np.float32),
         ("mat20", np.float32), ("mat21", np.float32), ("mat22", np.float32),
         ("mat30", np.float32), ("mat31", np.float32), ("mat32", np.float32)]

primitive = firstInfos + boundingBox + matrix

polygon = primitive + [("x1", np.float32), ("y1", np.float32), ("z1", np.float32),
                       ("x2", np.float32), ("y2", np.float32), ("z2", np.float32),
                       ("x3", np.float32), ("y3", np.float32), ("z3", np.float32),
                       ("xNormal", np.float32), ("yNormal", np.float32), ("zNormal", np.float32),
                       ("uvX1", np.float32), ("uvY1", np.float32),
                       ("uvX2", np.float32), ("uvY2", np.float32),
                       ("uvX3", np.float32), ("uvY3", np.float32),
                       ("xN1", np.float32), ("yN1", np.float32), ("zN1", np.float32),
                       ("xN2", np.float32), ("yN2", np.float32), ("zN2", np.float32),
                       ("xN3", np.float32), ("yN3", np.float32), ("zN3", np.float32)]