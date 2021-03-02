package de.lmu.ifi.dbs.elki.utilities.pairs;

/*
 This file is part of ELKI:
 Environment for Developing KDD-Applications Supported by Index-Structures

 Copyright (C) 2013
 Ludwig-Maximilians-Universität München
 Lehr- und Forschungseinheit für Datenbanksysteme
 ELKI Development Team

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.Comparator;

/**
 * Pair storing an integer and a double.
 * 
 * Since double and int are native types, this can't be done via the
 * {@link CPair} generic.
 * 
 * @author Erich Schubert
 * 
 * @apiviz.has Comparator
 */
public class DoubleIntPair implements Comparable<DoubleIntPair>, PairInterface<Double, Integer> {
  /**
   * first value
   */
  public double first;

  /**
   * second value
   */
  public int second;

  /**
   * Constructor
   * 
   * @param first First value
   * @param second Second value
   */
  public DoubleIntPair(double first, int second) {
    super();
    this.first = first;
    this.second = second;
  }

  /**
   * Trivial equals implementation
   * 
   * @param obj Object to compare to
   */
  @Override
  public boolean equals(Object obj) {
    if(this == obj) {
      return true;
    }
    if(obj == null) {
      return false;
    }
    if(getClass() != obj.getClass()) {
      return false;
    }

    DoubleIntPair other = (DoubleIntPair) obj;
    return (this.first == other.first) && (this.second == other.second);
  }

  /**
   * Trivial hashCode implementation mixing the two integers.
   */
  @Override
  public final int hashCode() {
    long firsthash = Double.doubleToLongBits(first);
    firsthash = firsthash ^ (firsthash >> 32);
    // primitive hash function mixing the two integers.
    // this number does supposedly not have any factors in common with 2^32
    return (int) (firsthash * 2654435761L + second);
  }

  /**
   * Implementation of comparable interface, sorting by first then second.
   * 
   * @param other Object to compare to
   * @return comparison result
   */
  @Override
  public int compareTo(DoubleIntPair other) {
    int fdiff = Double.compare(this.first, other.first);
    if(fdiff != 0) {
      return fdiff;
    }
    return this.second - other.second;
  }

  /**
   * Implementation of comparableSwapped interface, sorting by second then
   * first.
   * 
   * @param other Object to compare to
   * @return comparison result
   */
  public int compareSwappedTo(DoubleIntPair other) {
    int fdiff = this.second - other.second;
    if(fdiff != 0) {
      return fdiff;
    }
    return Double.compare(this.second, other.second);
  }

  /**
   * @deprecated use pair.first to avoid boxing!
   */
  @Override
  @Deprecated
  public final Double getFirst() {
    return Double.valueOf(first);
  }

  /**
   * Set first value
   * 
   * @param first new value
   */
  public final void setFirst(double first) {
    this.first = first;
  }

  /**
   * @deprecated use pair.first to avoid boxing!
   */
  @Override
  @Deprecated
  public final Integer getSecond() {
    return Integer.valueOf(second);
  }

  /**
   * Set second value
   * 
   * @param second new value
   */
  public final void setSecond(int second) {
    this.second = second;
  }

  /**
   * Comparator to compare by first component only
   */
  public static final Comparator<DoubleIntPair> BYFIRST_COMPARATOR = new Comparator<DoubleIntPair>() {
    @Override
    public int compare(DoubleIntPair o1, DoubleIntPair o2) {
      return Double.compare(o1.first, o2.first);
    }
  };

  /**
   * Comparator to compare by second component only
   */
  public static final Comparator<DoubleIntPair> BYSECOND_COMPARATOR = new Comparator<DoubleIntPair>() {
    @Override
    public int compare(DoubleIntPair o1, DoubleIntPair o2) {
      return o1.second - o2.second;
    }
  };

  /**
   * Comparator to compare by swapped components
   */
  public static final Comparator<DoubleIntPair> SWAPPED_COMPARATOR = new Comparator<DoubleIntPair>() {
    @Override
    public int compare(DoubleIntPair o1, DoubleIntPair o2) {
      return o1.compareSwappedTo(o2);
    }
  };
}