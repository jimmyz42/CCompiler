package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;

import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.Storage;
import exceptions.TypeMismatchError;

abstract public class Expression extends ExternArg {
    public static Expression create(DecafSemanticChecker checker, DecafParser.ExprContext ctx) {
        return (Expression) checker.visit(ctx);
    }

    public abstract Type getType();

    // For bool expressions, returns entry block
    public BasicBlock shortCircuit(CFG trueBranch, CFG falseBranch) {
    	// Bool expressions will override to return short circuit CFG
    	return null;
    }
}