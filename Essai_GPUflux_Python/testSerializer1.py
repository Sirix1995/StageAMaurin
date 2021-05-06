import numpy as np
import pyopencl as cl
import pyopencl.tools
import pyopencl.array

context = cl.create_some_context(interactive=False)
queue = cl.CommandQueue(context)
structure = np.dtype([("entier", np.int32), ("flottant", np.float32)])
print("Structure Python : ", structure)
structure, structureCL = cl.tools.match_dtype_to_c_struct(context.devices[0], "structure", structure)
print("Structure OpenCL : ", structureCL)

table = np.empty(1, structure)
table["entier"].fill(1)
table["flottant"].fill(3.6)
print("Table d'envoi : ", table)

octets = table.tobytes()
print("Table en Octets : ", octets)

buffer = cl.Buffer(context, cl.mem_flags.READ_ONLY | cl.mem_flags.COPY_HOST_PTR, hostbuf=octets)
print(buffer)

kernelSource = structureCL + """
                __kernel void vadd( __global char* donnees) {
                        __global structure *params = (const __global structure*)donnees;
                 }"""

program = cl.Program(context, kernelSource).build()

program.vadd(queue, (len(octets)), None, buffer)