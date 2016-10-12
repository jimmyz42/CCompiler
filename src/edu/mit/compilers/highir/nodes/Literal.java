package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;

abstract public class Literal extends Expression {
    public static Literal create(DecafSemanticChecker checker, DecafParser.LiteralContext ctx) {
        return (Literal) checker.visit(ctx);
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        super.prettyPrint(pw, prefix);
    	pw.println(prefix + "-value: " + this);
    }
}