package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;

import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.ImmediateValue;
import edu.mit.compilers.lowir.Storage;

public class StringLiteral extends ExternArg {
    private String terminal;

    public StringLiteral(String terminal) {
        this.terminal = terminal;
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
        pw.print(prefix + terminal);
    }
    
    public Storage allocateLocation(AssemblyContext ctx) {
    	return ImmediateValue.create(terminal);
    }
}