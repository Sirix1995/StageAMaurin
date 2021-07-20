import sys
import pyopencl as cl
import pyopencl.tools
import pyopencl.array
import numpy as np
from openalea.plantgl.all import *
import structfill
import aabbtree
import bvhBuilder

class sensorSerializer():
    def __init__(self) -> None:
        self.sensor = [("goupIndex", np.int32), ("WtOMatrix", np.float32, 12), ("twoSided", np.bool8), ("color", np.float32, 3), ("exponent", np.float32)]

        self.tree = aabbtree.AABBTree()

    def addSensor(self):
        pass