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
        self.tree = aabbtree.AABBTree()
        self.scene = sce
        self.serializedNodes = 0

    #Convert a PlantGL BoundingBox to an AABBTree BoundingBox
    def plantGLBBtoAABB(self, bb):
        #Type check
        assert type(bb) == openalea.plantgl.scenegraph._pglsg.BoundingBox, "Param is not a PlantGL BoundingBox"

        return aabbtree.AABB([(bb.getXMin(), bb.getXMax()), (bb.getYMin(), bb.getYMax()), (bb.getZMin(), bb.getZMax())])

    #Build the BVH using AABBTree
    def buildBVHfromScene(self):
        for i in range(len(self.scene)):
            bb = BoundingBox(scene[i].geometry)
            aabb = self.plantGLBBtoAABB(bb)
            aabb.value = self.scene[i]
            self.tree.add(aabb)

    #Serialize the BVH
    def serializeBVH(self):
        bvhInBytes = self.serializeBVHRec(self.tree)
        return bvhInBytes

    #Serialize a node
    #If node is a leaf, value1 refers to the prim index, and value2 refers to pcount.
    #If node isn't a leaf, value2 refers to left child index, and value2 to right child index.
    def serializeNode(self, isLeaf, value1, value2, n0xy, nz, n1xy):
        buffer = None
        
        if isLeaf:
            buffer = np.array(dtype= self.leaf)
            buffer["idx"].fill(value1)
            buffer["pcount"].fill(value2)
        else:
            buffer = np.array(dtype= self.branch)
            buffer["c0idx"].fill(value1)
            buffer["c1idx"].fill(value2)

        buffer["n0xy"].fill(n0xy)
        buffer["nz"].fill(nz)
        buffer["n1xy"].fill(n1xy)

        return buffer.tobytes()

    #Recursive call to serialize the tree (need to change pcount value).
    def serializeBVHRec(self, node):
        bufferInBytes = None

        if node.is_leaf:
            bufferInBytes = self.serializeNode(True, node.value, 0, [-1.0, -1.0, -1.0, -1.0], [-1.0, -1.0, -1.0, -1.0], [-1.0, -1.0, -1.0, -1.0]).tobytes()
        else:
            tamponGauche, c0idx = self.serializeBVHRec(node.left)
            tamponDroit, c1idx = self.serializeBVHRec(node.right)

            if node.left.is_leaf:
                c0idx = -c0idx - 1

            if node.right.is_leaf:
                c1idx = -c1idx - 1

            bufferInBytes = self.serializeNode(False, c0idx, c1idx, [node.left.limits[0][0], node.left.limits[0][1], node.left.limits[1][0], node.left.limits[1][1]], [node.left.limits[2][0], node.left.limits[2][1], node.right.limits[2][0], node.right.limits[2][1]], [node.right.limits[0][0], node.right.limits[0][1], node.right.limits[1][0], node.right.limits[1][1]]).tobytes()

        self.serializedNodes+= 1

        return bufferInBytes, self.serializedNodes


#Class test (will be removed)
points = [(0.0, 0.0, 0.0),
          (0.0, 1.0, 0.0),
          (1.0, 0.0, 0.0),
         (1.0, 1.0, 1.0)]

indices = [(0, 1, 2),
           (0, 1, 3),
           (0, 2, 3),
           (1, 2, 3)]

tetra = TriangleSet(points, indices)
boule = Sphere(2)
tessel = Tesselator()
boule.apply(tessel)
triBoule = tessel.triangulation
scene = Scene()
scene.add(tetra)
scene.add(triBoule)

builder = BVHBuilder(scene)

builder.buildBVHfromScene()
bytechain = builder.serializeBVH()

print("Résultat : ", bytechain)