package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;

abstract public class BinOp extends Ir {
    private String terminal;

    public BinOp(String terminal) {
        this.terminal = terminal;
    }

    @Override
    public String toString() {
        return terminal;
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
    	pw.println(prefix + terminal);
    }
    
    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
    	pw.print(prefix + terminal);
    }
}