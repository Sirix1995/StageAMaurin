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

kernelSource = primitiveCL + """
                #include "geo/prim.h"
                
                __kernel void vadd() {

                 }"""


program = cl.Program(context, kernelSource).build(options)