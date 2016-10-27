/*
 * (C) Copyright 2008-2016, by Andrew Newell and Contributors.
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
 * .
 *
 * @author Andrew Newell
 */
public class EulerianCircuitTest
    extends TestCase
{
    // ~ Methods ----------------------------------------------------------------

    /**
     * .
     */
    public void testEulerianCircuit()
    {
        UndirectedGraph<Object, DefaultEdge> completeGraph1 = new SimpleGraph<>(DefaultEdge.class);
        CompleteGraphGenerator<Object, DefaultEdge> completeGenerator1 =
            new CompleteGraphGenerator<>(6);
        completeGenerator1
            .generateGraph(completeGraph1, new ClassBasedVertexFactory<>(Object.class), null);

        // A complete graph of order 6 will have all vertices with degree 5
        // which is odd, therefore this graph is not Eulerian
        assertFalse(EulerianCircuit.isEulerian(completeGraph1));
        assertTrue(EulerianCircuit.getEulerianCircuitVertices(completeGraph1) == null);

        UndirectedGraph<Object, DefaultEdge> completeGraph2 = new SimpleGraph<>(DefaultEdge.class);
        CompleteGraphGenerator<Object, DefaultEdge> completeGenerator2 =
            new CompleteGraphGenerator<>(5);
        completeGenerator2
            .generateGraph(completeGraph2, new ClassBasedVertexFactory<>(Object.class), null);
        assertTrue(EulerianCircuit.isEulerian(completeGraph2));

        // There are 10 edges total in this graph, so an Eulerian circuit
        // labeled by vertices should have 11 vertices
        assertEquals(11, EulerianCircuit.getEulerianCircuitVertices(completeGraph2).size());
    }
}

// End EulerianCircuitTest.java
