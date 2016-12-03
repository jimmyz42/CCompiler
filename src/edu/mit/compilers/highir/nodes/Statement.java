package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.io.StringWriter;

import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.lowir.AssemblyContext;

abstract public class Statement extends Ir implements CFGAble {
    public static Statement create(DecafSemanticChecker checker, DecafParser.StatementContext ctx) {
        return (Statement) checker.visit(ctx);
    }

    @Override
    public String toString() {
        StringWriter sw = new StringWriter();
        cfgPrint(new PrintWriter(sw), "");
        return sw.toString();
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
        pw.println("");
    }
    
    @Override
    public void generateAssembly(AssemblyContext ctx) {
    	
    }
    
    @Override
    public long getNumStackAllocations() {
    	return 0;
    }
}