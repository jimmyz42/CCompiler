package edu.mit.compilers.ir;

class IrCondOp extends IrBinOp {
    private String terminal;

    public IrCondOp(String terminal) {
        super(terminal);
    }
}