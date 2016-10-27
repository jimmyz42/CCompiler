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
 * Generates edge names by invoking {@link #toString()} on them. This assumes that the edge's
 * {@link #toString()} method returns a unique String representation for each edge.
 *
 * @param <E> the graph edge type
 *
 * @author Trevor Harmon
 */
public class StringEdgeNameProvider<E>
    implements EdgeNameProvider<E>
{

    /**
     * Returns the String representation an edge.
     *
     * @param edge the edge to be named
     * @return a unique String edge representation
     */
    @Override
    public String getEdgeName(E edge)
    {
        return edge.toString();
    }
}

// End StringEdgeNameProvider.java
