package edu.mit.compilers.ir;

import java.io.PrintWriter;

abstract class IrBinOp extends Ir {
    private String terminal;

    public IrBinOp(String terminal) {
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
}