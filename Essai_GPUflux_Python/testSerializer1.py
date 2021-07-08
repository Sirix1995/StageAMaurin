import numpy as np
import pyopencl as cl
import pyopencl.tools
import pyopencl.array

context = cl.create_some_context(interactive=False)
queue = cl.CommandQueue(context)
structure = np.dtype([("aInt", np.int32), ("aFloat", np.float32)])
print("Python structure : ", structure)
structure, structureCL = cl.tools.match_dtype_to_c_struct(context.devices[0], "structure", structure)
print("\nOpenCL structure : ", structureCL)

table = np.empty(1, structure)
table["aInt"].fill(1)
table["aFloat"].fill(3.6)
print("Input array : ", table)

octets = table.tobytes()
print("Input array in bytes : ", octets)
print("Number of bytes in the chain :" , len(octets))

buffer = cl.Buffer(context, cl.mem_flags.READ_ONLY | cl.mem_flags.COPY_HOST_PTR, hostbuf=octets)

print("\nBuffer : ", buffer)
print("\nSending buffer to OpenCL, kernel will normally add 3 to the int and 3.5 to the float.\n")

kernelSource = structureCL + """
                __kernel void vadd( __global char* donnees, __global int* resultat1, __global float* resultat2) {
                        __global structure *params = (const __global structure*)donnees;
                        resultat1[get_global_id(0)] = params[get_global_id(0)].aInt + 3;
                        resultat2[get_global_id(0)] = params[get_global_id(0)].aFloat + 3.5;
                 }"""

retour1 = cl.Buffer(context, cl.mem_flags.WRITE_ONLY, 32)
retour2 = cl.Buffer(context, cl.mem_flags.WRITE_ONLY, 32)

program = cl.Program(context, kernelSource).build()

program.vadd(queue, (1,), None, buffer, retour1, retour2)

resultat1 = np.empty(1, np.int32)
resultat2 = np.empty(1, np.float32)

cl.enqueue_copy(queue, resultat1, retour1)
cl.enqueue_copy(queue, resultat2, retour2)

print("\nOutput result : ", resultat1, " - ", resultat2)
if resultat1 == table["aInt"] + 3 and resultat2 == table["aFloat"] + 3.5:
	print("Results are correct.")
else:
	print("Results are incorrect.")