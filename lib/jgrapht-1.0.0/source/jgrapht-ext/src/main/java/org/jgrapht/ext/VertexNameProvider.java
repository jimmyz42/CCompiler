/*
 * (C) Copyright 2005-2016, by Avner Linder and Contributors.
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
 * Assigns a display name for each of the graph vertices.
 * 
 * @param <V> the graph vertex type
 * 
 */
public interface VertexNameProvider<V>
{
    /**
     * Returns a unique name for a vertex. This is useful when exporting a a graph, as it ensures
     * that all vertices are assigned simple, consistent names.
     *
     * @param vertex the vertex to be named
     *
     * @return the name of the vertex
     */
    String getVertexName(V vertex);
}

// End VertexNameProvider.java
