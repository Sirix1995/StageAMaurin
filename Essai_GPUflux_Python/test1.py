import sys
import pyopencl as cl
import numpy as np
from openalea.plantgl.all import *  

def getTriangles(trSet):
    resultList = []
    indices = trSet.indexList
    trSet.normalPerVertex = True
    trSet.computeNormalList()
    for face in indices:
        points = [(trSet.pointAt(face[0])[0], trSet.pointAt(face[0])[1], trSet.pointAt(face[0])[2]), (trSet.pointAt(face[1])[0], trSet.pointAt(face[1])[1], trSet.pointAt(face[1])[2]), (trSet.pointAt(face[2])[0], trSet.pointAt(face[2])[1], trSet.pointAt(face[2])[2])]
        rIndices = [(0, 1, 2)]
        triangle = TriangleSet(points, rIndices)
        triangle.normalPerVertex = True
        triangle.normalList = (trSet.normalList[face[0]], trSet.normalList[face[1]], trSet.normalList[face[2]])
        resultList.append(triangle)
    return resultList

def serializePrimitives(trSet, matrix, groupIndex, shaderOffset, ior) :
    triangles = getTriangles(trSet)
    polygons = None
    firstinfos = np.array([5, int(groupIndex), int(shaderOffset)])

    for triangle in triangles:
        bbox = BoundingBox(triangle)
        normalVertex = triangle.normalList
        tBBox = np.array([bbox.getXMin(), bbox.getXMax(), bbox.getYMin(), bbox.getYMax(), bbox.getZMin(), bbox.getZMax()])
        tMatrix = np.array([(matrix[0][0], matrix[0][1], matrix[0][2]),
                            (matrix[1][0], matrix[1][1], matrix[1][2]),
                            (matrix[2][0], matrix[2][1], matrix[2][2]),
                            (matrix[3][0], matrix[3][1], matrix[3][2])])
        # Primitive info
        polygon = firstinfos.tobytes() # Type, Group Index, Shader offset
        polygon = polygon + np.array([ior]).tobytes() # Index of Reflexion

        polygon = polygon + tBBox.tobytes() #Bounding Box
        
        for x in range(len(matrix)): # World2Object Matrix
            for y in range(x):
                polygon = polygon + np.array([matrix[x][y]]).tobytes()
        
        # Polygon info
        polygon = polygon + np.array([triangle.pointAt(0)[0], triangle.pointAt(0)[1], triangle.pointAt(0)[2]]).tobytes() # Vertex
        polygon = polygon + np.array([triangle.pointAt(1)[0], triangle.pointAt(1)[1], triangle.pointAt(1)[2]]).tobytes()
        polygon = polygon + np.array([triangle.pointAt(2)[0], triangle.pointAt(2)[1], triangle.pointAt(2)[2]]).tobytes()

        triangle.normelPerVertex = False
        triangle.computeNormalList()
        
        polygon = polygon + np.array([triangle.normalList[0][0], triangle.normalList[0][1], triangle.normalList[0][2]]).tobytes() # Face Normal

        polygon = polygon + np.array([0.0, 0.0, 0.0]).tobytes() # UV coords
        polygon = polygon + np.array([0.0, 0.0, 0.0]).tobytes()
        polygon = polygon + np.array([0.0, 0.0, 0.0]).tobytes()

        polygon = polygon + np.array([normalVertex[0][0], normalVertex[0][1], normalVertex[0][2]]).tobytes()
        polygon = polygon + np.array([normalVertex[1][0], normalVertex[1][1], normalVertex[1][2]]).tobytes()
        polygon = polygon + np.array([normalVertex[2][0], normalVertex[2][1], normalVertex[2][2]]).tobytes()
        polygons.append(polygon)
    
    return polygons


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

# Compilation du programme
context = cl.create_some_context()
queue = cl.CommandQueue(context)
# program = cl.Program(context, kernelSource).build(options)

# compute = program.compute

# Scene
points = [(0.0, 0.0, 0.0),
          (0.0, 1.0, 0.0),
          (1.0, 0.0, 0.0),
         (1.0, 1.0, 1.0)]

indices = [(0, 1, 2),
           (0, 1, 3),
           (0, 2, 3),
           (1, 2, 3)]

tetra = TriangleSet(points, indices)
matrice = [(1.0, 1.0, 1.0, 1.0),
           (1.0, 1.0, 1.0, 1.0),
           (1.0, 1.0, 1.0, 1.0),
           (1.0, 1.0, 1.0, 1.0)]

polygons = getPolygons(tetra, matrice, 0, 0, 0.0)

print(polygons)

# Params
nbThread = 4
sampleOffset = 0 # ?
nSample = 0 # ?

absorbedPower = None
irradiance = None

detectors = None # ?
measurementBits = 0 # ?

np = 0 # ?
ninfp = 0 # ?
primitives = []
offsets = [] # ?
root = 0 # ?
bvh = [] # ?
shaders = []
channels = [] # ?
nl = 0 # ?
lights = []
lightOffsets = [] # ?
cumulatedLightPower = []
skyOffset = 0 # ?

ns = 0 # ?
sensors = []
sensorRoot = 0 # ?
sensorBVH = None # ?

deph = 0
minPower = 0.0
bounds = None # ?
sensivityCurve = None # ?
seed = 0
grid = None # ?

# Buffers de sortie
'''bufAbsorbedPower = cl.Buffer(context, cl.mem_flags.WRITE_ONLY, sys.getsizeof(absorbedPower))
bufIrradiance = cl.Buffer(context, cl.mem_flags.WRITE_ONLY, sys.getsizeof(irradiance))'''

# Buffers d'entrée
'''bufDetectors = cl.Buffer(context, cl.mem_flags.READ_ONLY | cl.mem_flags.COPY_HOST_PTR, hostbuf=detectors)
bufPrims = cl.Buffer(context, cl.mem_flags.READ_ONLY | cl.mem_flags.COPY_HOST_PTR, hostbuf=primitives)
bufOffsets = cl.Buffer(context, cl.mem_flags.READ_ONLY | cl.mem_flags.COPY_HOST_PTR, hostbuf=offsets)
bufBVH = cl.Buffer(context, cl.mem_flags.READ_ONLY | cl.mem_flags.COPY_HOST_PTR, hostbuf=bvh)
bufShaders = cl.Buffer(context, cl.mem_flags.READ_ONLY | cl.mem_flags.COPY_HOST_PTR, hostbuf=shaders)
bufChannels = cl.Buffer(context, cl.mem_flags.READ_ONLY | cl.mem_flags.COPY_HOST_PTR, hostbuf=channels)
bufLights = cl.Buffer(context, cl.mem_flags.READ_ONLY | cl.mem_flags.COPY_HOST_PTR, hostbuf=lights)
bufLightOffsets = cl.Buffer(context, cl.mem_flags.READ_ONLY | cl.mem_flags.COPY_HOST_PTR, hostbuf=lightOffsets)
bufSensors = cl.Buffer(context, cl.mem_flags.READ_ONLY | cl.mem_flags.COPY_HOST_PTR, hostbuf=sensors)
bufSensorsBVH = cl.Buffer(context, cl.mem_flags.READ_ONLY | cl.mem_flags.COPY_HOST_PTR, hostbuf=sensorBVH)
bufSensivityCurve = cl.Buffer(context, cl.mem_flags.READ_ONLY | cl.mem_flags.COPY_HOST_PTR, hostbuf=sensivityCurve)'''

