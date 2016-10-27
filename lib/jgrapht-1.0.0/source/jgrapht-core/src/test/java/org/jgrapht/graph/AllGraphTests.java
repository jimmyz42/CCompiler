/*
 * (C) Copyright 2003-2016, by Barak Naveh and Contributors.
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
package org.jgrapht.graph;

import org.junit.runner.*;
import org.junit.runners.*;

/**
 * A TestSuite for all tests in this package.
 *
 * @author Barak Naveh
 * @since Aug 3, 2003
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ AsUndirectedGraphTest.class, AsUnweightedGraphTest.class,
    AsWeightedGraphTest.class, CloneTest.class, DefaultDirectedGraphTest.class,
    EqualsAndHashCodeTest.class, GenericGraphsTest.class, ListenableGraphTest.class,
    MaskEdgeSetTest.class, MaskVertexSetTest.class, SerializationTest.class,
    SimpleDirectedGraphTest.class, GraphWalkTest.class, SubgraphTest.class,
    SimpleIdentityDirectedGraphTest.class, UnionGraphTest.class })
public final class AllGraphTests
{
}
// End AllGraphTests.java
