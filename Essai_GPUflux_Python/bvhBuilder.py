import sys
import pyopencl as cl
import pyopencl.tools
import pyopencl.array
import numpy as np
from openalea.plantgl.all import *
import structfill
import aabbtree

class BVHBuilder():
    def __init__(self):
        self.tree == aabbtree.AABBTree()

    def buildBVHfromScene(self, scene):
        for shape in scene[1:]:
            bb = BoundingBox(shape.geometry)
            self.tree.add(aabbtree.AABB([(bb.getXMin(), bb.getXMax()), (bb.getYMin(), bb.getYMax()), (bb.getZMin(), bb.getZMax())]))

    def serializeBVH():
        pass