package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;

public class StringLiteral extends ExternArg {
    private String terminal;

    public StringLiteral(String terminal) {
        this.terminal = terminal;
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
        pw.print(prefix + terminal);
    }
}