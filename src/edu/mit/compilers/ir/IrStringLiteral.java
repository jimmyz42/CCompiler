package edu.mit.compilers.ir;

class IrStringLiteral extends IrExternArg {
    private String terminal;

    public IrStringLiteral(String terminal) {
        this.terminal = terminal;
    }
}