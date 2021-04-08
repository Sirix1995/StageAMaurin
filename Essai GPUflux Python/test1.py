import pyopencl as cl

fichier = open("kernel/lightmodel_kernel.cl", "r")

kernelSource = fichier.read()

fichier.close()

options = " -D MEASURE_FULL_SPECTRUM"
options += " -D MEASURE_MIN_LAMBDA=380"
options += " -D MEASURE_MAX_LAMBDA=780"
options += " -D MEASURE_SPECTRUM_BINS=81"
options += " -D SPECTRAL_WAVELENGTH_MIN=360"
options += " -D SPECTRAL_WAVELENGTH_MAX=830";
options += " -D SPECTRAL_WAVELENGTH_BINS=1";

options += " -I kernel/"

context = cl.create_some_context()
queue = cl.CommandQueue(context)
program = cl.Program(context, kernelSource).build(options)

