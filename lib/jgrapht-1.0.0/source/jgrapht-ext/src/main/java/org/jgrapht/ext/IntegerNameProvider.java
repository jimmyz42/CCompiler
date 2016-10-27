/*
 * (C) Copyright 2005-2016, by Charles Fry and Contributors.
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

import java.util.*;

import org.jgrapht.event.*;

/**
 * Assigns a unique integer to represent each vertex. Each instance of IntegerNameProvider maintains
 * an internal map between every vertex it has ever seen and the unique integer representing that
 * vertex. As a result it is probably desirable to have a separate instance for each distinct graph.
 *
 * @param <V> the graph vertex type
 *
 * @author Charles Fry
 */
public class IntegerNameProvider<V>
    implements VertexNameProvider<V>
{
    private int nextID = 1;
    private final Map<V, Integer> idMap = new HashMap<>();

    /**
     * Clears all cached identifiers, and resets the unique identifier counter.
     */
    public void clear()
    {
        nextID = 1;
        idMap.clear();
    }

    /**
     * Returns the String representation of the unique integer representing a vertex.
     *
     * @param vertex the vertex to be named
     *
     * @return the name of
     *
     * @see GraphListener#edgeAdded(GraphEdgeChangeEvent)
     */
    @Override
    public String getVertexName(V vertex)
    {
        Integer id = idMap.get(vertex);
        if (id == null) {
            id = nextID++;
            idMap.put(vertex, id);
        }

        return id.toString();
    }
}

// End IntegerNameProvider.java
