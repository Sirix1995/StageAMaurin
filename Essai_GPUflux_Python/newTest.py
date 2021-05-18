import sys
import pyopencl as cl
import pyopencl.tools
import pyopencl.array
import numpy as np
from openalea.plantgl.all import *  

import serializer

serial = Serializer()

points = [(0.0, 0.0, 0.0),
          (0.0, 1.0, 0.0),
          (1.0, 0.0, 0.0),
         (1.0, 1.0, 1.0)]

indices = [(0, 1, 2),
           (0, 1, 3),
           (0, 2, 3),
           (1, 2, 3)]

tetra = TriangleSet(points, indices)

resultat = serial.serializeTriangleSet(tetra, 0, 0, 0.0)
print(resultat)