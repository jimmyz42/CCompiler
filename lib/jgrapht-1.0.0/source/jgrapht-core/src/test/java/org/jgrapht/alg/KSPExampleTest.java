/*
 * (C) Copyright 2007-2016, by France Telecom and Contributors.
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

import org.jgrapht.graph.*;

import junit.framework.*;

public class KSPExampleTest
    extends TestCase
{
    // ~ Methods ----------------------------------------------------------------

    public void testFourReturnedPathsJGraphT()
    {
        SimpleWeightedGraph<String, DefaultWeightedEdge> graph = new KSPExampleGraph();

        String sourceVertex = "S";
        KShortestPaths<String, DefaultWeightedEdge> ksp =
            new KShortestPaths<>(graph, sourceVertex, 4);

        String targetVertex = "T";
        assertEquals(3, ksp.getPaths(targetVertex).size());
    }

    public void testThreeReturnedPathsJGraphT()
    {
        SimpleWeightedGraph<String, DefaultWeightedEdge> graph = new KSPExampleGraph();

        String sourceVertex = "S";
        int nbPaths = 3;
        KShortestPaths<String, DefaultWeightedEdge> ksp =
            new KShortestPaths<>(graph, sourceVertex, nbPaths);

        String targetVertex = "T";
        assertEquals(nbPaths, ksp.getPaths(targetVertex).size());
    }

    public void testTwoReturnedPathsJGraphT()
    {
        SimpleWeightedGraph<String, DefaultWeightedEdge> graph = new KSPExampleGraph();

        String sourceVertex = "S";
        int nbPaths = 2;
        KShortestPaths<String, DefaultWeightedEdge> ksp =
            new KShortestPaths<>(graph, sourceVertex, nbPaths);

        String targetVertex = "T";
        assertEquals(nbPaths, ksp.getPaths(targetVertex).size());
    }
}

// End $file.name$
