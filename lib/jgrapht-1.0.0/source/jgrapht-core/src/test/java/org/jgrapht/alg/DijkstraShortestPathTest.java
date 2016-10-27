/*
 * (C) Copyright 2003-2016, by John V Sichi and Contributors.
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
package org.jgrapht.alg;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;

/**
 * .
 *
 * @author John V. Sichi
 */
public class DijkstraShortestPathTest
    extends ShortestPathTestCase
{
    // ~ Methods ----------------------------------------------------------------

    /**
     * .
     */
    public void testConstructor()
    {
        DijkstraShortestPath<String, DefaultWeightedEdge> path;
        Graph<String, DefaultWeightedEdge> g = create();

        path = new DijkstraShortestPath<>(g, V3, V4, Double.POSITIVE_INFINITY);
        assertEquals(Arrays.asList(new DefaultEdge[] { e13, e12, e24 }), path.getPathEdgeList());
        assertEquals(10.0, path.getPathLength(), 0);

        path = new DijkstraShortestPath<>(g, V3, V4, 7);
        assertNull(path.getPathEdgeList());
        assertEquals(Double.POSITIVE_INFINITY, path.getPathLength(), 0);
    }

    @Override
    protected List<DefaultWeightedEdge> findPathBetween(
        Graph<String, DefaultWeightedEdge> g, String src, String dest)
    {
        return DijkstraShortestPath.findPathBetween(g, src, dest);
    }
}

// End DijkstraShortestPathTest.java
