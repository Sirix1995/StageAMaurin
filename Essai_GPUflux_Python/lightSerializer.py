import sys
import pyopencl as cl
import pyopencl.tools
import pyopencl.array
import numpy as np
from openalea.plantgl.all import *  

class LightSerializer():
    def __init__(self, bins) -> None:

        self.firstInfos = [("type", np.int32), ("Samples", np.int32)]

        self.matrix = [("mat00", np.float32), ("mat01", np.float32), ("mat02", np.float32),
                       ("mat10", np.float32), ("mat11", np.float32), ("mat12", np.float32),
                       ("mat20", np.float32), ("mat21", np.float32), ("mat22", np.float32),
                       ("mat30", np.float32), ("mat31", np.float32), ("mat32", np.float32)]
        