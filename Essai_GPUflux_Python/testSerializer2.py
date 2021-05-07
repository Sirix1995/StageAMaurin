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
                      ("mat30", np.float32), ("mat31", np.float32), ("mat32", np.float32)])

print("Structure Python : ", primitive)
primitive, primitiveCL = cl.tools.match_dtype_to_c_struct(context.devices[0], "prim", primitive)
print("Structure OpenCL : ", primitiveCL)

kernelSource = primitiveCL + """
                __kernel void vadd() {

                 }"""


program = cl.Program(context, kernelSource).build()