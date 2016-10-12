package edu.mit.compilers.highir.nodes;

import edu.mit.compilers.grammar.DecafParser.LocationContext;
import edu.mit.compilers.highir.DecafSemanticChecker;

abstract public class Location extends Expression {

    public static Location create(DecafSemanticChecker checker, LocationContext ctx) {
        return (Location) checker.visit(ctx);
    }
}