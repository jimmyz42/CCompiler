package edu.mit.compilers.ir;

class IrArithOp extends IrBinOp {
    private String terminal;

    public IrArithOp(String terminal) {
        this.terminal = terminal;
    }
}