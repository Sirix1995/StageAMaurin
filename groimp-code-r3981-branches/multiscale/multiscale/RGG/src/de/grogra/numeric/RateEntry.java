package de.grogra.numeric;

import de.grogra.rgg.model.PropertyRuntime.GraphProperty;

/**
 * Helper class to facilitate offset calculation for properties.
 * 
 * @author Reinhard Hemmerling
 *
 */
class RateEntry {
	Class cls;
	int m; // #props for this type and its supertypes
	GraphProperty[] props;
}
