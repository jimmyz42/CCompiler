/*
 * (C) Copyright 2003-2016, by Trevor Harmon and Contributors.
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

import java.io.*;
import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;

import junit.framework.*;

/**
 * .
 *
 * @author Trevor Harmon
 */
public class DOTExporterTest
    extends TestCase
{
    // ~ Static fields/initializers ---------------------------------------------

    private static final String V1 = "v1";
    private static final String V2 = "v2";
    private static final String V3 = "v3";

    private static final String NL = System.getProperty("line.separator");

    // TODO jvs 23-Dec-2006: externalized diff-based testing framework

    private static final String UNDIRECTED = "graph G {" + NL + "  1 [ label=\"a\" ];" + NL
        + "  2 [ x=\"y\" ];" + NL + "  3;" + NL + "  1 -- 2;" + NL + "  3 -- 1;" + NL + "}" + NL;

    // ~ Methods ----------------------------------------------------------------

    public void testUndirected()
        throws UnsupportedEncodingException, ExportException
    {
        UndirectedGraph<String, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        g.addVertex(V1);
        g.addVertex(V2);
        g.addEdge(V1, V2);
        g.addVertex(V3);
        g.addEdge(V3, V1);

        ComponentAttributeProvider<String> vertexAttributeProvider =
            new ComponentAttributeProvider<String>()
            {
                @Override
                public Map<String, String> getComponentAttributes(String v)
                {
                    Map<String, String> map = new LinkedHashMap<>();
                    switch (v) {
                    case V1:
                        map.put("label", "a");
                        break;
                    case V2:
                        map.put("x", "y");
                        break;
                    default:
                        map = null;
                        break;
                    }
                    return map;
                }
            };

        DOTExporter<String, DefaultEdge> exporter = new DOTExporter<>(
            new IntegerNameProvider<>(), null, null, vertexAttributeProvider, null);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        exporter.exportGraph(g, os);
        String res = new String(os.toByteArray(), "UTF-8");
        assertEquals(UNDIRECTED, res);
    }

    public void testValidNodeIDs()
        throws ExportException
    {
        DOTExporter<String, DefaultEdge> exporter =
            new DOTExporter<>(new StringNameProvider<>(), new StringNameProvider<>(), null);

        List<String> validVertices =
            Arrays.asList("-9.78", "-.5", "12", "a", "12", "abc_78", "\"--34asdf\"");
        for (String vertex : validVertices) {
            Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
            graph.addVertex(vertex);
            exporter.exportGraph(graph, new ByteArrayOutputStream());
        }

        List<String> invalidVertices = Arrays.asList("2test", "--4", "foo-bar", "", "t:32");
        for (String vertex : invalidVertices) {
            Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
            graph.addVertex(vertex);

            try {
                exporter.exportGraph(graph, new ByteArrayOutputStream());
                Assert.fail(vertex);
            } catch (RuntimeException re) {
                // this is a negative test so exception is expected
            }
        }
    }
}

// End DOTExporterTest.java
