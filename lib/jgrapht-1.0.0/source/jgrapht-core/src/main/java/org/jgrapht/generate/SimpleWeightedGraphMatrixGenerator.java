/*
 * (C) Copyright 2016-2016, by Barak Naveh and Contributors.
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
package org.jgrapht.generate;

import java.util.*;

import org.jgrapht.*;

/**
 * A simple weighted graph matrix generator.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 */
public class SimpleWeightedGraphMatrixGenerator<V, E>
    extends WeightedGraphGeneratorAdapter<V, E, V>
{
    protected List<V> vertices;

    /**
     * Set the generator vertices.
     * 
     * @param vertices the graph vertices
     * @return the generator
     */
    public SimpleWeightedGraphMatrixGenerator<V, E> vertices(List<V> vertices)
    {
        this.vertices = vertices;
        return this;
    }

    @Override
    public void generateGraph(
        WeightedGraph<V, E> target, VertexFactory<V> vertexFactory, Map<String, V> resultMap)
    {
        if (weights == null) {
            throw new IllegalArgumentException(
                "Graph may not be constructed without weight-matrix specified");
        }

        if (vertices == null) {
            throw new IllegalArgumentException(
                "Graph may not be constructed without vertex-set specified");
        }

        assert vertices.size() == weights.length;

        for (V vertex : vertices) {
            target.addVertex(vertex);
        }

        for (int i = 0; i < vertices.size(); ++i) {
            assert vertices.size() == weights[i].length;

            for (int j = 0; j < vertices.size(); ++j) {
                if (i != j) {
                    target.setEdgeWeight(
                        target.addEdge(vertices.get(i), vertices.get(j)), weights[i][j]);
                }
            }
        }
    }
}

// End SimpleWeightedGraphMatrixGenerator.java
