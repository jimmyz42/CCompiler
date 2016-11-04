package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.PrettyPrintable;
import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.instructions.Instruction;
import edu.mit.compilers.cfg.components.BasicBlock;

abstract public class Ir implements PrettyPrintable, CFGAble {
    @Override
    public String toString() {
        StringWriter sw = new StringWriter();
        prettyPrint(new PrintWriter(sw), "");
        return sw.toString();
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        pw.println(prefix + getClass().getSimpleName());
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
        pw.println("");
    }

    @Override
    public CFG generateCFG(CFGContext context) {
        return BasicBlock.create(this);
    }
    
    public void generateAssembly(AssemblyContext ctx) {
    	
    }
    
    public long getNumStackAllocations() {
    	return 0;
    }
}
