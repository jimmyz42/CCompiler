package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;

abstract public class BinOp extends Ir {
    private String terminal;

    public BinOp(String terminal) {
        this.terminal = terminal;
    }

    public String getTerminal()
    {
        return terminal;
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
    public int hashCode() {
    	return terminal.hashCode();
    }

	@Override
	public boolean equals(Object obj) {
		return hashCode() == obj.hashCode();
	}
}