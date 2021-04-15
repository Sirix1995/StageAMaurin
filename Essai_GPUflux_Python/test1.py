import pyopencl as cl

fichier = open("kernel/lightmodel_kernel.cl", "r")

kernelSource = fichier.read()

fichier.close()

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
program = cl.Program(context, kernelSource).build(options)

compute = program.compute

