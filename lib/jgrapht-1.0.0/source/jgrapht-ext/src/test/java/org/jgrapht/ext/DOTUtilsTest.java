/*
 * (C) Copyright 2003-2016, by Christoph Zauner and Contributors.
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

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.junit.*;
import org.junit.rules.*;
import org.junit.runner.*;

/**
 * @author Christoph Zauner
 */
public class DOTUtilsTest
{

    // @Rule
    public TestRule watcher = new TestWatcher()
    {
        @Override
        protected void starting(Description description)
        {
            System.out.println("\n+++ Test: " + description.getMethodName() + " +++\n");
        }
    };

    //@formatter:off
    /**
     * Graph to convert to a String:
     *
     *             +--> C
     *             |
     * A +--> B +--+
     *             |
     *             +--> D
     */
    //@formatter:on
    @Test
    public void testConvertGraphToDotString()
    {

        DirectedGraph<String, DefaultEdge> graph =
            new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

        String a = "A";
        String b = "B";
        String c = "C";
        String d = "D";

        graph.addVertex(a);
        graph.addVertex(b);
        graph.addVertex(c);
        graph.addVertex(d);

        graph.addEdge(a, b);
        graph.addEdge(b, c);
        graph.addEdge(b, d);

        //@formatter:off
        String expectedGraphAsDotString =
                "digraph G {"            +
                  "  1 [ label=\"A\" ];" +
                  "  2 [ label=\"B\" ];" +
                  "  3 [ label=\"C\" ];" +
                  "  4 [ label=\"D\" ];" +
                  "  1 -> 2;"            +
                  "  2 -> 3;"            +
                  "  2 -> 4;"            +
                "}";
        //@formatter:on

        String graphAsDotString =
            DOTUtils.convertGraphToDotString(graph).replaceAll("(\\r|\\n)", "");

        Assert.assertEquals(expectedGraphAsDotString, graphAsDotString);
    }
}

// End DOTUtilsTest.java
