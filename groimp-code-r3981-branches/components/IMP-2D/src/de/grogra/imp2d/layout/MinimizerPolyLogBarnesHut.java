package de.grogra.imp2d.layout;

/*
 * Copyright (C) Brandenburg Technical University at Cottbus, 2002-2004
 *
 * This program is distributed in the hope that it will be useful,
 * but without any warranty; without even the implied warranty of
 * merchantability or fitness for a particular purpose.  
 */

/**
 * Minimizer for the r-PolyLog energy model,
 *   based on the Barnes-Hut algorithm.
 * For more information on the r-PolyLog energy model, see
 * "Energy Models for Drawing Clustered Small-World Graphs",
 * Technical Report I-07/2003, Technical University Cottbus, 2003.
 * Available at 
 * <a href="http://www-sst.informatik.tu-cottbus.de/CrocoCosmos/linlog.html">
 * <code>www-sst.informatik.tu-cottbus.de/CrocoCosmos/linlog.html</code></a>.
 *
 * @author Andreas Noack (an@informatik.tu-cottbus.de)
 * @version 07.03.04
 */
public class MinimizerPolyLogBarnesHut { 
    /** Number of nodes. */
    private int nodeNr;
    /** Position in 3-dimensional space for every node. */
    private float pos[][];

    /** Symmetric similarity matrix. */
    private float attr[][];
    /** Node indexes of the similarity lists. */ 
    private int attrIndexes[][];
    /** Similarity values of the similarity lists. */
    private float attrValues[][];
    /** Exponent of the Euclidean distance in the attraction energy
        = parameter r of the r-PolyLog model. */
    private float attrExponent = 3.0f;
    
    /** Position of the barycenter of the nodes. */
    private float[] baryCenter = new float[3];
    /** Factor for the gravitation energy = attraction to the barycenter.
        Set to 0.0f for no gravitation. */
    private float gravitationFactor = 0.001f;

    /** Factor for repulsion energy that normalizes average distance 
      * between pairs of nodes with maximum similarity to (roughly) 1. */
    private float repuFactor = 1.f;
    /** Factors for the repulsion force for pulsing. */
    private static final float[] repuStrategy 
        = { 0.95f, 0.9f, 0.85f, 0.8f, 0.75f, 0.8f, 0.85f, 0.9f, 0.95f, 1.0f,
            1.1f, 1.2f, 1.3f, 1.4f, 1.5f, 1.4f, 1.3f, 1.2f, 1.1f, 1.0f };
    /** Octtree for repulsion computation. */               
    private OctTree octTree = null;
   

    /**
     * Sets the number of nodes, the similarity matrices (edge weights), 
     *   and the position matrix.
     * @param nodeNr  number of nodes.
     * @param attr    similarity matrix (edge weights).
     *                Is not copied and not modified by this class.
     *                For unweighted graphs use 1.0f for edges 
     *                  and 0.0f for non-edges.
     *                Preconditions: dimension at least [nodeNr][nodeNr];
     *                  attr[i][i] == 0 for all i;
     *                  attr[i][j] == attr[j][i] for all i, j.
     * @param pos     position matrix.
     *                Is not copied and serves as input and output 
     *                  of <code>minimizeEnergy</code>.
     *                If the input is two-dimensional (i.e. pos[i][2] == 0
     *                for all i), the output is also two-dimensional.
     *                Random initial positions are appropriate.
     *                Preconditions: dimension at least [nodeNr][3];
     *                  no two different nodes have the same position
     */
    public MinimizerPolyLogBarnesHut(int nodeNr, float[][] attr, float[][] pos ) {
        this.nodeNr = nodeNr;
        this.attr = attr;
        this.pos = pos;
     }

    /**
     * Sets the exponent of the Euclidean distance in the attraction energy
     *   (the parameter r of the r-PolyLog model).  
     * @param attrExp  exponent of the distance in the attraction energy
     *                 = parameter r of the r-PolyLog model.
     *                 Is 1.0f in the LinLog model and 3.0f in the energy
     *                 version of the Fruchterman-Reingold model.
     */
    public void setAttractionExponent(float attrExp) {
        this.attrExponent = attrExp;
    }

    /**
     * Sets the factor for the gravitation = attraction to the barycenter.
     * @param gravitationFactor  factor for the gravitation energy.
     *                           Set to 0.0f for no gravitation.
     */
    public void setGravitationFactor(float gravitationFactor) {
         this.gravitationFactor = gravitationFactor;
    }


    /**
     * Iteratively minimizes energy using the Barnes-Hut algorithm.
     * Starts from the positions in <code>pos</code>, 
     * and stores the computed positions in <code>pos</code>.
     * @param nrIterations  number of iterations. Choose appropriate values
     *                      by observing the convergence of energy.
     */
    public void minimizeEnergy(int nrIterations) {
        if (nodeNr <= 1) {
            return;
        }

//        analyzeDistances();
        computeRepuFactor();
        float finalRepuFactor = repuFactor;
        attrMatrixToAttrList();

        // compute initial energy
        buildOctTree();
        float energySum = 0.0f;
        for (int i = 0; i < nodeNr; i++) {
            energySum += getEnergy(i);
        }
//        System.out.println();
//        System.out.println("iteration 0   energy " + energySum
//           + "   repulsion " + repuFactor);

        // minimize energy
        float[] oldPos = new float[3];
        float[] bestDir = new float[3];
        for (int step = 1; step <= nrIterations; step++) {
            computeBaryCenter();
            buildOctTree();

            // except in the last 20 iterations, vary the repulsion factor
            // according to repuStrategy
            if (step/repuStrategy.length < (nrIterations-20)/repuStrategy.length) {
                repuFactor = finalRepuFactor 
                    * (float)Math.pow(repuStrategy[step%repuStrategy.length], attrExponent);
            } else {
                repuFactor = finalRepuFactor;
            }

            // for all non-fixed nodes: minimize energy
            energySum = 0.0f;
            for (int i = 0; i < nodeNr; i++) {
                float oldEnergy = getEnergy(i);
                // compute direction of the move of the node
                getDirection(i, bestDir);

                // line search: compute length of the move
                oldPos[0] = pos[i][0]; oldPos[1] = pos[i][1]; oldPos[2] = pos[i][2]; 
                float bestEnergy = oldEnergy;
                int bestMultiple = 0;
                bestDir[0] /= 32; bestDir[1] /= 32; bestDir[2] /= 32;
                for (int multiple = 32;
                     multiple >= 1 && (bestMultiple==0 || bestMultiple/2==multiple);
                     multiple /= 2) {
                    pos[i][0] = oldPos[0] + bestDir[0] * multiple;
                    pos[i][1] = oldPos[1] + bestDir[1] * multiple; 
                    pos[i][2] = oldPos[2] + bestDir[2] * multiple; 
                    float curEnergy = getEnergy(i);
                    if (curEnergy < bestEnergy) {
                        bestEnergy = curEnergy;
                        bestMultiple = multiple;
                    }
                }
                    
                for (int multiple = 64; 
                     multiple <= 128 && bestMultiple == multiple/2; 
                     multiple *= 2) {
                    pos[i][0] = oldPos[0] + bestDir[0] * multiple;
                    pos[i][1] = oldPos[1] + bestDir[1] * multiple; 
                    pos[i][2] = oldPos[2] + bestDir[2] * multiple; 
                    float curEnergy = getEnergy(i);
                    if (curEnergy < bestEnergy) {
                        bestEnergy = curEnergy;
                        bestMultiple = multiple;
                    }
                }

                pos[i][0] = oldPos[0] + bestDir[0] * bestMultiple;
                pos[i][1] = oldPos[1] + bestDir[1] * bestMultiple; 
                pos[i][2] = oldPos[2] + bestDir[2] * bestMultiple;
                if (bestMultiple > 0) {
                    octTree.moveNode(oldPos, pos[i], 1.0f);
                }
                energySum += bestEnergy;
				

            }
//            System.out.println("iteration " + step 
//              + "   energy " + energySum
//              + "   repulsion " + repuFactor);
        }
     //   analyzeDistances();
        //new JTreeFrame(octTree);
    }


    /**
     * Returns the Euclidean distance between the specified positions.
     * @return the Euclidean distance between the specified positions
     */
    private float getDist(float[] pos1, float[] pos2) {
        float xDiff = pos1[0] - pos2[0];
        float yDiff = pos1[1] - pos2[1];
        float zDiff = pos1[2] - pos2[2];
        return (float)Math.sqrt(xDiff*xDiff + yDiff*yDiff + zDiff*zDiff);
    }


    /**
     * Returns the Euclidean distance between node i and the baryCenter.
     * @return Euclidean distance between node i and the baryCenter.
     */
    private float getDistToBaryCenter(int i) {
        float xDiff = pos[i][0] - baryCenter[0];
        float yDiff = pos[i][1] - baryCenter[1];
        float zDiff = pos[i][2] - baryCenter[2];
        return (float)Math.sqrt(xDiff*xDiff + yDiff*yDiff + zDiff*zDiff);
    }


    /** 
     * Returns the repulsion energy between the node with the specified index
     * and the nodes in the octtree.
     * 
     * @param index index of the repulsing node
     * @param tree  octtree containing repulsing nodes
     * @return repulsion energy between the node with the specified index
     *         and the nodes in the octtree.
     */
    private float getRepulsionEnergy(int index, OctTree tree) {
        if (tree == null || tree.index == index) {
            return 0.0f;
        }
        
        float dist = getDist(pos[index], tree.position);
        if (tree.index < 0 && dist < tree.width()) {
            float energy = 0.0f;
            for (int i = 0; i < tree.children.length; i++) {
                energy += getRepulsionEnergy(index, tree.children[i]);
            }
            return energy;
        } 
        
        return -repuFactor * tree.weight * (float)Math.log(dist);
    }

    /**
     * Returns the energy of the specified node.
     * @param   index   index of a node
     * @return  energy of the node
    */
    private float getEnergy(int index) {
        // repulsion energy
        float energy = getRepulsionEnergy(index, octTree);
        
        // attraction energy
        for (int i = 0; i < attrIndexes[index].length; i++) {
            if (attrIndexes[index][i] != index) {
                float dist = getDist(pos[attrIndexes[index][i]], pos[index]);
                energy += attrValues[index][i] * Math.pow(dist, attrExponent) / attrExponent;
            }
        }
        
        // gravitation energy
        float dist = getDistToBaryCenter(index);
        energy += gravitationFactor * repuFactor * (nodeNr-1) 
                  * 0.5f * dist * dist;

        return energy;
    }
    
    /**
     * Computes the direction of the repulsion force from the tree 
     *     on the specified node.
     * @param  index   index of the repulsed node
     * @param  tree    repulsing octtree
     * @param  dir     direction of the repulsion force acting on the node
     *                 is added to this variable (output parameter)
     * @return approximate second derivation of the repulsion energy
     */
    private float addRepulsionDir(int index, OctTree tree, float[] dir) {
        if (tree == null || tree.index == index) {
            return 0.0f;
        }
        
        float dist = getDist(pos[index], tree.position);
        if (tree.index < 0 && dist < tree.width()) {
            float dir2 = 0.0f;
            for (int i = 0; i < tree.children.length; i++) {
                dir2 += addRepulsionDir(index, tree.children[i], dir);
            }
            return dir2;
        } 

        if (dist != 0.0) {
            float tmp = -repuFactor * tree.weight / (dist * dist);
            for (int j = 0; j < 3; j++) {
                dir[j] += (tree.position[j] - pos[index][j]) * tmp;
            }
            return -tmp;
        }
        
        return 0.0f;
    }

    /**
     * Computes the direction of the total force acting on the specified node.
     * @param  index   index of a node
     * @param  dir     direction of the total force acting on the node
     *                 (output parameter)
     */
    private void getDirection(int index, float[] dir) {
        dir[0] = 0.0f; dir[1] = 0.0f; dir[2] = 0.0f;

        // compute repulsion force vector        
        float dir2 = addRepulsionDir(index, octTree, dir);

        // compute attraction force vector       
        for (int i = 0; i < attrIndexes[index].length; i++) {
            if (attrIndexes[index][i] != index) {
                float dist = getDist(pos[attrIndexes[index][i]], pos[index]);
                float tmp = attrValues[index][i] * (float)Math.pow(dist, attrExponent-2);
                dir2 += tmp * (attrExponent-1);
                for (int j = 0; j < 3; j++) {
                    dir[j] += (pos[attrIndexes[index][i]][j] - pos[index][j]) * tmp;
                }
            }
        }

        // compute gravitation force vector      
        dir2 += gravitationFactor * repuFactor * (nodeNr-1);
        for (int j = 0; j < 3; j++) {
            dir[j] += gravitationFactor * repuFactor * (nodeNr-1) 
                      * (baryCenter[j] - pos[index][j]);
        }

        // normalize force vector with second derivation of energy
        dir[0] /= dir2; dir[1] /= dir2; dir[2] /= dir2;
         
        // ensure that the length of dir is at most 1/8
        // of the maximum Euclidean distance between nodes
        float length = (float)Math.sqrt(dir[0]*dir[0] + dir[1]*dir[1] + dir[2]*dir[2]);
        if (length > octTree.width()/8) {
            length /= octTree.width()/8;
            dir[0] /= length; dir[1] /= length; dir[2] /= length;
        }
    }    
    
    /**
     * Builds the octtree.
     */
    private void buildOctTree() {
        // compute mimima and maxima of positions in each dimension
        float[] minPos = new float[] { Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE };
        float[] maxPos = new float[] { Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE };
        for (int i = 0; i < nodeNr; i++) {
            for (int j = 0; j < 3; j++) {
                if (pos[i][j] < minPos[j]) {
                    minPos[j] = pos[i][j];
                }
                if (pos[i][j] > maxPos[j]) {
                    maxPos[j] = pos[i][j];
                }
            }
        }
        
//        System.out.println(minPos[0] + " " + minPos[1] + " " + minPos[2] + " " + maxPos[0] + " " + maxPos[1] + " " + maxPos[2]);
        
        // add nodes to the octtree
        octTree = new OctTree(0, pos[0], 1.0f, minPos, maxPos);
        for (int i = 1; i < nodeNr; i++) {
            octTree.addNode(i, pos[i], 1.0f, 0);
        }
    }

    /**
     * Computes the factor for repulsion forces <code>repuFactor</code>
     * such that in the energy minimum the average Euclidean distance
     * between pairs of nodes with similarity 1.0 is approximately 1.
     */
    private void computeRepuFactor() {
        repuFactor = 0.0f;
        for (int i = 1; i < nodeNr; i++) {
            for (int j = 0; j < i; j++) {
                repuFactor += attr[i][j];
            }
        }
        repuFactor /= nodeNr * (nodeNr-1) / 2;
        if (repuFactor == 0.0f) {
            repuFactor = 1.0f;
        }
   
    }
    
    /** 
     * Computes the position of the barycenter <code>baryCenter</code>
     * of all nodes.
     */
    private void computeBaryCenter() {
        baryCenter[0] = 0.0f; baryCenter[1] = 0.0f; baryCenter[2] = 0.0f;
        for (int i = 0; i < nodeNr; i++) {
            baryCenter[0] += pos[i][0];
            baryCenter[1] += pos[i][1];
            baryCenter[2] += pos[i][2];
        }
        baryCenter[0] /= nodeNr;
        baryCenter[1] /= nodeNr;
        baryCenter[2] /= nodeNr;
    }

    /**
     * Computes and outputs some statistics. 
     */
    private void analyzeDistances() {
        float edgeLengthSum = 0.0f;
        float edgeLengthLogSum = 0.0f;
        float attrSum = 0.0f;
        float repuSum = 0.0f;
        float distanceLogSum = 0.0f;
        
        for (int i = 1; i < nodeNr; i++) {
            for (int j = 0; j < i; j++) {
                float dist = getDist(pos[i], pos[j]);
                float distLog = (float)Math.log(dist);
                edgeLengthSum += attr[i][j] * dist;
                edgeLengthLogSum += attr[i][j] * distLog;
                attrSum += attr[i][j];
                distanceLogSum += distLog;
            }
        }
        System.out.println("Number of Nodes: " + nodeNr);
        System.out.println("Overall Attraction: " + attrSum);
        System.out.println("Arithmetic mean of edge lengths: " + edgeLengthSum / attrSum);
        System.out.println("Geometric mean of edge lengths: " + 
            + (float)Math.exp(edgeLengthLogSum / attrSum));
        System.out.println("Geometric mean of distances: " 
            + (float)Math.exp(2.0f * distanceLogSum / (nodeNr * nodeNr)));
    }
    
    /**
     * Computes the similarity lists in <code>attrIndexes</code> 
     * and <code>attrValues</code> from the similarity matrix <code>attr</code>.
     */
    private void attrMatrixToAttrList() {
        attrIndexes = new int[nodeNr][];
        attrValues = new float[nodeNr][];
        for (int i = 0; i < nodeNr; i++) {
            int attrCounter = 0;
            for (int j = 0; j < nodeNr; j++) {
                if (attr[i][j] > 0.0f) {
                    attrCounter++;
                }
            }
            
            attrIndexes[i] = new int[attrCounter];
            attrValues[i] = new float[attrCounter];
            
            attrCounter = 0;
            for (int j = 0; j < nodeNr; j++) {
                if (attr[i][j] > 0.0f) {
                    attrIndexes[i][attrCounter] = j;
                    attrValues[i][attrCounter] = attr[i][j];
                    attrCounter++;
                }
            }
        }
    }
    
    /**
     * Octtree for graph nodes with positions in 3D space.
     * Contains all graph nodes that are located in a given cuboid in 3D space.
     * 
     * @author Andreas Noack
     */
    class OctTree {
        /** For leafs, the unique index of the graph node; for non-leafs -1. */
        private int index;
        /** Children of this tree node. */
        private OctTree[] children = new OctTree[8];
        /** Barycenter of the contained graph nodes. */
        private float[] position;
        /** Total weight of the contained graph nodes. */
        private float weight;
        /** Minimum coordinates of the cuboid in each of the 3 dimensions. */
        private float[] minPos;
        /** Maximum coordinates of the cuboid in each of the 3 dimensions. */
        private float[] maxPos;
        private static final int MAX_DEPTH = 20;
    
        /**
         * Creates an octtree containing one graph node.
         *  
         * @param index    unique index of the graph node
         * @param position position of the graph node
         * @param weight   weight of the graph node
         * @param minPos   minimum coordinates of the cuboid
         * @param maxPos   maximum coordinates of the cuboid
         */
        public OctTree(int index, float[] position, float weight, float[] minPos, float[] maxPos) {
            this.index = index;
            this.position = new float[] { position[0], position[1], position[2] };
            this.weight = weight;
            this.minPos = minPos;
            this.maxPos = maxPos;
        }
    
        /**
         * Adds a graph node to the octtree.
         * 
         * @param nodeIndex  unique index of the graph node
         * @param nodePos    position of the graph node
         * @param nodeWeight weight of the graph node
         */
        public void addNode(int nodeIndex, float[] nodePos, float nodeWeight, int depth) {
            if (nodeWeight == 0.0f) {
                return;
            }
            
            if (depth > MAX_DEPTH) {
//				System.out.println("OctTree: Graph node dropped because tree depth > " + MAX_DEPTH +".");
//				System.out.println("Graph node position: " + nodePos[0] + " " + nodePos[1] + " " + nodePos[2] + "  " + nodeIndex + ".");
//				System.out.println("Tree node position: " + position[0] + " " + position[1] + " " + position[2] + ".");
				return;
            }
        
            if (index >= 0) {
                addNode2(index, position, weight, depth);
                index = -1;
            }

            for (int i = 0; i < 3; i++) {
                position[i] = (position[i]*weight + nodePos[i]*nodeWeight) / (weight+nodeWeight);
            }
            weight += nodeWeight;
        
            addNode2(nodeIndex, nodePos, nodeWeight, depth);
        }
    
        /**
         * Adds a graph node to the octtree, 
         * without changing the position and weight of the root.
         * 
         * @param nodeIndex  unique index of the graph node
         * @param nodePos    position of the graph node
         * @param nodeWeight weight of the graph node
         */
        private void addNode2(int nodeIndex, float[] nodePos, float nodeWeight, int depth) {
            int childIndex = 0;
            for (int i = 0; i < 3; i++) {
                if (nodePos[i] > (minPos[i]+maxPos[i])/2) {
                    childIndex += 1 << i;
                }
            }
        
            if (children[childIndex] == null) {
                float[] newMinPos = new float[3];           
                float[] newMaxPos = new float[3];
                for (int i = 0; i < 3; i++) {
                    if ((childIndex & 1<<i) == 0) {
                        newMinPos[i] = minPos[i];
                        newMaxPos[i] = (minPos[i] + maxPos[i]) / 2;
                    } else {
                        newMinPos[i] = (minPos[i] + maxPos[i]) / 2;
                        newMaxPos[i] = maxPos[i];
                    }
                }
                children[childIndex] = new OctTree(nodeIndex, nodePos, nodeWeight, newMinPos, newMaxPos);
            } else {
                children[childIndex].addNode(nodeIndex, nodePos, nodeWeight, depth+1);
            }
        }
    
        /**
         * Updates the positions of the octtree nodes 
         * when the position of a graph node has changed.
         * 
         * @param oldPos     previous position of the graph node
         * @param newPos     new position of the graph node
         * @param nodeWeight weight of the graph node
         */
        public void moveNode(float[] oldPos, float[] newPos, float nodeWeight) {
            for (int i = 0; i < 3; i++) {
                position[i] += (newPos[i]-oldPos[i]) * (nodeWeight/weight);
            }
        
            int childIndex = 0;
            for (int i = 0; i < 3; i++) {
                if (oldPos[i] > (minPos[i]+maxPos[i])/2) {
                    childIndex += 1 << i;
                }
            }
            if (children[childIndex] != null) {
                children[childIndex].moveNode(oldPos, newPos, nodeWeight);
            }
        }

        /**
         * Returns the maximum extension of the octtree.
         * 
         * @return maximum over all dimensions of the extension of the octtree
         */
        public float width() {
            float width = 0.0f;
            for (int i = 0; i < 3; i++) {
                if (maxPos[i] - minPos[i] > width) {
                    width = maxPos[i] - minPos[i];
                }
            }
            return width;
        }
    }

}
