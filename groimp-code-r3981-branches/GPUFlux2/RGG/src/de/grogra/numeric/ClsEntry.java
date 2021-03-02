package de.grogra.numeric;

import java.util.ArrayList;

import de.grogra.rgg.model.PropertyRuntime.GraphProperty;

/**
 * Helper class to facilitate offset calculation for properties.
 * The fields parent, child and next are used to build up a tree structure.
 * 
 * @author Reinhard Hemmerling
 *
 */
class ClsEntry {
	Class cls;
	ClsEntry parent;
	ClsEntry child;
	ClsEntry next;
	int size;
	final ArrayList<GraphProperty> props = new ArrayList<GraphProperty>();
}
