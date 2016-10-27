/*
 * (C) Copyright 2010-2016, by Tom Conerly and Contributors.
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
import org.jgrapht.alg.interfaces.*;
import org.jgrapht.graph.*;

import junit.framework.*;

public class MinimumSpanningTreeTest
    extends TestCase
{
    // ~ Static fields/initializers ---------------------------------------------

    private static final String A = "A";
    private static final String B = "B";
    private static final String C = "C";
    private static final String D = "D";
    private static final String E = "E";
    private static final String F = "F";
    private static final String G = "G";
    private static final String H = "H";

    // ~ Instance fields --------------------------------------------------------

    private DefaultWeightedEdge AB;
    private DefaultWeightedEdge AC;
    private DefaultWeightedEdge BD;
    private DefaultWeightedEdge DE;
    private DefaultWeightedEdge EG;
    private DefaultWeightedEdge GH;
    private DefaultWeightedEdge FH;

    // ~ Methods ----------------------------------------------------------------

    public void testKruskal()
    {

        testMinimumSpanningTreeBuilding(
            new KruskalMinimumSpanningTree<>(createSimpleConnectedWeightedGraph()),
            Arrays.asList(AB, AC, BD, DE), 15.0);

        testMinimumSpanningTreeBuilding(
            new KruskalMinimumSpanningTree<>(createSimpleDisconnectedWeightedGraph()),
            Arrays.asList(AB, AC, BD, EG, GH, FH), 60.0);

        testKruskalMinimumSpanningTreeBuildingBackwardCompatibility(
            new KruskalMinimumSpanningTree<>(createSimpleConnectedWeightedGraph()),
            Arrays.asList(AB, AC, BD, DE), 15.0);

    }

    public void testPrim()
    {

        testMinimumSpanningTreeBuilding(
            new PrimMinimumSpanningTree<>(createSimpleConnectedWeightedGraph()),
            Arrays.asList(AB, AC, BD, DE), 15.0);

        testMinimumSpanningTreeBuilding(
            new PrimMinimumSpanningTree<>(createSimpleDisconnectedWeightedGraph()),
            Arrays.asList(AB, AC, BD, EG, GH, FH), 60.0);

    }

    protected <V, E> void testMinimumSpanningTreeBuilding(
        final MinimumSpanningTree<V, DefaultWeightedEdge> mst, final Collection<E> edgeSet,
        final double weight)
    {

        assertEquals(weight, mst.getMinimumSpanningTreeTotalWeight());
        assertTrue(mst.getMinimumSpanningTreeEdgeSet().containsAll(edgeSet));

    }

    protected <V, E> void testKruskalMinimumSpanningTreeBuildingBackwardCompatibility(
        final KruskalMinimumSpanningTree<V, DefaultWeightedEdge> mst, final Collection<E> edgeSet,
        final double weight)
    {

        assertEquals(weight, mst.getMinimumSpanningTreeTotalWeight());
        assertTrue(mst.getMinimumSpanningTreeEdgeSet().containsAll(edgeSet));

    }

    protected Graph<String, DefaultWeightedEdge> createSimpleDisconnectedWeightedGraph()
    {

        Graph<String, DefaultWeightedEdge> g = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        /**
         *
         * A -- B E -- F | | | | C -- D G -- H
         *
         */

        g.addVertex(A);
        g.addVertex(B);
        g.addVertex(C);
        g.addVertex(D);

        AB = Graphs.addEdge(g, A, B, 5);
        AC = Graphs.addEdge(g, A, C, 10);
        BD = Graphs.addEdge(g, B, D, 15);
        Graphs.addEdge(g, C, D, 20);

        g.addVertex(E);
        g.addVertex(F);
        g.addVertex(G);
        g.addVertex(H);

        Graphs.addEdge(g, E, F, 20);
        EG = Graphs.addEdge(g, E, G, 15);
        GH = Graphs.addEdge(g, G, H, 10);
        FH = Graphs.addEdge(g, F, H, 5);

        return g;
    }

    protected Graph<String, DefaultWeightedEdge> createSimpleConnectedWeightedGraph()
    {

        Graph<String, DefaultWeightedEdge> g = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        double bias = 1;

        g.addVertex(A);
        g.addVertex(B);
        g.addVertex(C);
        g.addVertex(D);
        g.addVertex(E);

        AB = Graphs.addEdge(g, A, B, bias * 2);
        AC = Graphs.addEdge(g, A, C, bias * 3);
        BD = Graphs.addEdge(g, B, D, bias * 5);
        Graphs.addEdge(g, C, D, bias * 20);
        DE = Graphs.addEdge(g, D, E, bias * 5);
        Graphs.addEdge(g, A, E, bias * 100);

        return g;
    }

}

// End MinimumSpanningTreeTest.java
