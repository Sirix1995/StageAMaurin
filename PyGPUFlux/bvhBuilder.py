import sys
import pyopencl as cl
import pyopencl.tools
import pyopencl.array
import numpy as np
from openalea.plantgl.all import *
import structfill
import aabbtree

class BVHBuilder():
    def __init__(self, sce):
        #Type check
        assert type(sce) == openalea.plantgl.scenegraph._pglsg.Scene, "Tree must be constructed with a PlantGL scene."

        #Structures
        self.firstInfos = [("n0xy", np.float32, 4), ("nz", np.float32, 4), ("n1xy", np.float32, 4)] #left BBox XY min and Max, both BBOx Z min and max, and right BBox XY min and Max
        self.branch = self.firstInfos + [("c0idx", np.int32), ("c1idx", np.int32)] #left child and right child index
        self.leaf = self.firstInfos + [("idx", np.int32), ("pcount", np.int32)] #primitive index, and pcount (?)

        #Attributes
        self.tree == aabbtree.AABBTree()
        self.scene == sce
        self.serializedNodes = 0

    #Convert a PlantGL BoundingBox to an AABBTree BoundingBox
    def plantGLBBtoAABB(self, bb):
        #Type check
        assert type(bb) == openalea.plantgl.scenegraph._pglsg.BoundingBox, "Param is not a PlantGL BoundingBox"

        return aabbtree.AABB([(bb.getXMin(), bb.getXMax()), (bb.getYMin(), bb.getYMax()), (bb.getZMin(), bb.getZMax())])

    #Build the BVH using AABBTree
    def buildBVHfromScene(self):
        for shape in self.scene[1:]:
            bb = BoundingBox(shape.geometry)
            aabb = self.plantGLBBtoAABB(bb)
            aabb.value
            self.tree.add()

    #Serialize the BVH
    def serializeBVH(self):
        bvhInBytes = self.serializeBVHRec(self.tree)
        return bvhInBytes

    #Serialize a node
    def serializeBVHRec(self, node):
        buffer = None
        bufferInBytes = None
        if node.is_leaf():
            buffer = np.array(dtype= self.leaf)
            buffer["idx"]
        else:
            buffer = np.array(dtype= self.branch)
            
            if node.left.is_leaf():
                pass

        return 
