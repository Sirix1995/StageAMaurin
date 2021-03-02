
/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package de.grogra.xl.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BidirectionalHashMap<K,V> {

	private final Map<K, V> keyToValue = new HashMap<K, V>();
	private final Map<V, K> valueToKey = new HashMap<V, K>();

	public void put(K key, V value) {
		keyToValue.put(key, value);
		valueToKey.put(value, key);
	}

	public V get(K key) {
		return keyToValue.get(key);
	}

	public K getKey(V value) {
		return valueToKey.get(value);
	}
	
	public boolean containsKey(K key) {
		return keyToValue.containsKey(key);
	}
	
	public boolean containsValue(V value) {
		return valueToKey.containsKey(value);
	}
	
	public Collection<V> getValueMap() {
		return keyToValue.values();
	}
	
	public Collection<K> getKeyMap() {
		return valueToKey.values();
	}
	
	public int size() {
		int size = keyToValue.size();
		assert size == valueToKey.size();
		return size;
	}
	
	public String toString() {
		return keyToValue.toString();
	}

}
