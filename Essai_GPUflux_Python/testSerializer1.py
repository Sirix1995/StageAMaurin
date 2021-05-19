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
print((len(octets),))

kernelSource = structureCL + """
                __kernel void vadd( __global char* donnees, __global int* resultat1, __global float* resultat2) {
                        __global structure *params = (const __global structure*)donnees;
                        resultat1[get_global_id(0)] = params[get_global_id(0)].entier;
                        resultat2[get_global_id(0)] = params[get_global_id(0)].flottant;
                 }"""

retour1 = cl.Buffer(context, cl.mem_flags.WRITE_ONLY, 32)
retour2 = cl.Buffer(context, cl.mem_flags.WRITE_ONLY, 32)

program = cl.Program(context, kernelSource).build()

program.vadd(queue, (1,), None, buffer, retour1, retour2)

resultat1 = np.empty(1, np.int32)
resultat2 = np.empty(1, np.float32)

cl.enqueue_copy(queue, resultat1, retour1)
cl.enqueue_copy(queue, resultat2, retour2)

print("Résultats : ", resultat1, " - ", resultat2)