package edu.mit.compilers.highir.nodes;

public class CondOp extends BinOp {
    private String terminal;

    public CondOp(String terminal) {
        super(terminal);
    }
}