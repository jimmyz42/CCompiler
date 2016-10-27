/*
 * (C) Copyright 2012-2016, by Joris Kinable and Contributors.
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
import java.util.stream.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;

import junit.framework.*;

/**
 * Unit test for the MaxBipartiteMatching class
 * 
 * @author Joris Kinable
 *
 */
public class HopcroftKarpBipartiteMatchingTest
    extends TestCase
{

    /**
     * Random test graph 1
     */
    public void testBipartiteMatching1()
    {
        UndirectedGraph<Integer, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        List<Integer> partition1 = Arrays.asList(0, 1, 2, 3);
        List<Integer> partition2 = Arrays.asList(4, 5, 6, 7);
        Graphs.addAllVertices(graph, partition1);
        Graphs.addAllVertices(graph, partition2);

        DefaultEdge e02 = graph.addEdge(partition1.get(0), partition2.get(2));
        DefaultEdge e11 = graph.addEdge(partition1.get(1), partition2.get(1));
        DefaultEdge e20 = graph.addEdge(partition1.get(2), partition2.get(0));

        HopcroftKarpBipartiteMatching<Integer, DefaultEdge> bm =
            new HopcroftKarpBipartiteMatching<>(
                graph, new HashSet<>(partition1), new HashSet<>(partition2));
        assertEquals(3, bm.getMatching().size(), 0);
        List<DefaultEdge> l1 = Arrays.asList(e11, e02, e20);
        Set<DefaultEdge> matching = new HashSet<>(l1);
        assertEquals(matching, bm.getMatching());
    }

    /**
     * Random test graph 2
     */
    public void testBipartiteMatching2()
    {
        UndirectedGraph<Integer, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        List<Integer> partition1 = Arrays.asList(0, 1, 2, 3, 4, 5);
        List<Integer> partition2 = Arrays.asList(6, 7, 8, 9, 10, 11);
        Graphs.addAllVertices(graph, partition1);
        Graphs.addAllVertices(graph, partition2);

        DefaultEdge e00 = graph.addEdge(partition1.get(0), partition2.get(0));
        DefaultEdge e13 = graph.addEdge(partition1.get(1), partition2.get(3));
        DefaultEdge e21 = graph.addEdge(partition1.get(2), partition2.get(1));
        DefaultEdge e34 = graph.addEdge(partition1.get(3), partition2.get(4));
        DefaultEdge e42 = graph.addEdge(partition1.get(4), partition2.get(2));
        DefaultEdge e55 = graph.addEdge(partition1.get(5), partition2.get(5));

        HopcroftKarpBipartiteMatching<Integer, DefaultEdge> bm =
            new HopcroftKarpBipartiteMatching<>(
                graph, new HashSet<>(partition1), new HashSet<>(partition2));
        assertEquals(6, bm.getMatching().size(), 0);
        List<DefaultEdge> l1 = Arrays.asList(e21, e13, e00, e42, e34, e55);
        Set<DefaultEdge> matching = new HashSet<>(l1);
        assertEquals(matching, bm.getMatching());
    }

    /**
     * Find a maximum matching on a graph without edges
     */
    public void testEmptyMatching()
    {
        UndirectedGraph<Integer, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        List<Integer> partition1 = Collections.singletonList(0);
        List<Integer> partition2 = Collections.singletonList(1);
        Graphs.addAllVertices(graph, partition1);
        Graphs.addAllVertices(graph, partition2);
        HopcroftKarpBipartiteMatching<Integer, DefaultEdge> bm =
            new HopcroftKarpBipartiteMatching<>(
                graph, new HashSet<>(partition1), new HashSet<>(partition2));
        assertEquals(Collections.EMPTY_SET, bm.getMatching());
    }

    /**
     * Issue 233 instance
     */
    public void testBipartiteMatchingIssue233()
    {
        UndirectedGraph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);

        Graphs.addAllVertices(g, IntStream.rangeClosed(0, 3).boxed().collect(Collectors.toList()));

        Set<Integer> left = new HashSet<>(Arrays.asList(0, 1));
        Set<Integer> right = new HashSet<>(Arrays.asList(2, 3));

        g.addEdge(0, 2);
        g.addEdge(0, 3);
        g.addEdge(1, 2);

        Set<DefaultEdge> m =
            new HopcroftKarpBipartiteMatching<Integer, DefaultEdge>(g, left, right).getMatching();
        assertTrue(m.contains(g.getEdge(1, 2)));
        assertTrue(m.contains(g.getEdge(0, 3)));
        assertEquals(2, m.size());
    }

}
