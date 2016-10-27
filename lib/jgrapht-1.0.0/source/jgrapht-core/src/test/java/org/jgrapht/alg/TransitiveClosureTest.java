/*
 * (C) Copyright 2007-2016, by Vinayak R Borkar and Contributors.
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

import org.jgrapht.*;
import org.jgrapht.generate.*;
import org.jgrapht.graph.*;

import junit.framework.*;

/**
 */
public class TransitiveClosureTest
    extends TestCase
{
    // ~ Methods ----------------------------------------------------------------

    public void testLinearGraph()
    {
        SimpleDirectedGraph<Integer, DefaultEdge> graph =
            new SimpleDirectedGraph<>(DefaultEdge.class);

        int N = 10;
        LinearGraphGenerator<Integer, DefaultEdge> gen = new LinearGraphGenerator<>(N);

        VertexFactory<Integer> vf = new VertexFactory<Integer>()
        {
            private int m_index = 0;

            @Override
            public Integer createVertex()
            {
                return m_index++;
            }
        };
        gen.generateGraph(graph, vf, null);
        TransitiveClosure.INSTANCE.closeSimpleDirectedGraph(graph);

        assertEquals(true, graph.edgeSet().size() == ((N * (N - 1)) / 2));
        for (int i = 0; i < N; ++i) {
            for (int j = i + 1; j < N; ++j) {
                assertEquals(true, graph.getEdge(i, j) != null);
            }
        }
    }

    public void testRingGraph()
    {
        SimpleDirectedGraph<Integer, DefaultEdge> graph =
            new SimpleDirectedGraph<>(DefaultEdge.class);

        int N = 10;
        RingGraphGenerator<Integer, DefaultEdge> gen = new RingGraphGenerator<>(N);

        VertexFactory<Integer> vf = new VertexFactory<Integer>()
        {
            private int m_index = 0;

            @Override
            public Integer createVertex()
            {
                return m_index++;
            }
        };
        gen.generateGraph(graph, vf, null);
        TransitiveClosure.INSTANCE.closeSimpleDirectedGraph(graph);

        assertEquals(true, graph.edgeSet().size() == (N * (N - 1)));
        for (int i = 0; i < N; ++i) {
            for (int j = 0; j < N; ++j) {
                assertEquals(true, (i == j) || (graph.getEdge(i, j) != null));
            }
        }
    }
}

// End TransitiveClosureTest.java
