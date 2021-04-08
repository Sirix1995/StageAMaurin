import pyopencl as cl

fichier = open("lightmodel_kernel.cl", "r")

kernelSource = fichier.read()

fichier.close()

context = cl.create_some_context()
queue = cl.CommandQueue(context)
program = cl.Program(context, kernelSource).build()

