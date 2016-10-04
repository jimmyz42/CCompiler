package edu.mit.compilers.ir;

abstract class IrBinOp extends Ir {
    private String terminal;

    public IrBinOp(String terminal) {
        this.terminal = terminal;
    }
}