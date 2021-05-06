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
                __kernel void vadd( __global char* donnees, int retour1, float retour2) {
                        __global structure *params = (const __global structure*)donnees;
                        retour1 = params->entier+= 2;
                        retour2 = params->flottant+= 3.5;
                 }"""

program = cl.Program(context, kernelSource).build()

retour1 = 0
retour2 = 0.0

program.vadd(queue, (len(octets)), None, buffer, retour1, retour2)
print("Valeur de l'entier après exécution : ", retour1)
print("Valeur du flottant après exécution : ", retour2)