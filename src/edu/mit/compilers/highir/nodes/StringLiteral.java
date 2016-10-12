package edu.mit.compilers.highir.nodes;

public class StringLiteral extends ExternArg {
    private String terminal;

    public StringLiteral(String terminal) {
        this.terminal = terminal;
    }
}