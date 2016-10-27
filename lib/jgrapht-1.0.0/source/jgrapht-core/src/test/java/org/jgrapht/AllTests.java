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
package org.jgrapht;

import org.jgrapht.alg.*;
import org.jgrapht.alg.util.*;
import org.jgrapht.generate.*;
import org.jgrapht.graph.*;
import org.jgrapht.traverse.*;
import org.jgrapht.util.*;
import org.junit.runner.*;
import org.junit.runners.*;

/**
 * Runs all unit tests of the JGraphT library.
 *
 * @author Barak Naveh
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ AllAlgTests.class, AllAlgUtilTests.class, AllGenerateTests.class,
    AllGraphTests.class, AllTraverseTests.class, AllUtilTests.class })
public final class AllTests
{
}
// End AllTests.java
