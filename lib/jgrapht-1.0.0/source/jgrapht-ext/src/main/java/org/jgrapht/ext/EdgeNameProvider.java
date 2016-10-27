/*
 * (C) Copyright 2005-2016, by Trevor Harmon and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package org.jgrapht.ext;

/**
 * Assigns a display name for each of the graph edges.
 * 
 * @param <E> the graph edge type
 */
public interface EdgeNameProvider<E>
{
    /**
     * Returns a unique name for an edge. This is useful when exporting a graph, as it ensures that
     * all edges are assigned simple, consistent names.
     *
     * @param edge the edge to be named
     *
     * @return the name of the edge
     */
    String getEdgeName(E edge);
}

// End EdgeNameProvider.java
