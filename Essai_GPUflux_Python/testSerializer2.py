import numpy as np
import pyopencl as cl
import pyopencl.tools
import pyopencl.array

context = cl.create_some_context(interactive=False)
queue = cl.CommandQueue(context)
primitive = np.dtype([("type", np.int32), ("groupIndex", np.int32), ("shaderOffset", np.int32), ("indexOfReflexion", np.float32),
                      ("xMin", np.float32), ("xMax", np.float32), ("yMin", np.float32), ("yMax", np.float32), ("zMin", np.float32), ("zMax", np.float32),
                      ("mat00", np.float32), ("mat01", np.float32), ("mat02", np.float32),
                      ("mat10", np.float32), ("mat11", np.float32), ("mat12", np.float32),
                      ("mat20", np.float32), ("mat21", np.float32), ("mat22", np.float32),
                      ("mat30", np.float32), ("mat31", np.float32), ("mat32", np.float32),
                      ("x1", np.float32), ("y1", np.float32), ("z1", np.float32),
                      ("x2", np.float32), ("y2", np.float32), ("z2", np.float32),
                      ("x3", np.float32), ("y3", np.float32), ("z3", np.float32),
                      ("xNormal", np.float32), ("yNormal", np.float32), ("zNormal", np.float32),
                      ("uvX1", np.float32), ("uvY1", np.float32),
                      ("uvX2", np.float32), ("uvY2", np.float32),
                      ("uvX3", np.float32), ("uvY3", np.float32),
                      ("xN1", np.float32), ("yN1", np.float32), ("zN1", np.float32),
                      ("xN2", np.float32), ("yN2", np.float32), ("zN2", np.float32),
                      ("xN3", np.float32), ("yN3", np.float32), ("zN3", np.float32)])

print("Structure Python : ", primitive)
primitive, primitiveCL = cl.tools.match_dtype_to_c_struct(context.devices[0], "prim", primitive)
print("Structure OpenCL : ", primitiveCL)

options = " -I kernel/"

kernelSource = primitiveCL + """
                #include "geo/prim.h"
                
                __kernel void vadd() {

                 }"""


program = cl.Program(context, kernelSource).build(options)