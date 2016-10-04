package edu.mit.compilers.ir;

import org.antlr.v4.runtime.tree.TerminalNode;

class IrAndOp extends IrBinOp {
    private String terminal;

    public IrAndOp(String terminal) {
        super(terminal);
    }

    public static IrAndOp create(DecafSemanticChecker checker, TerminalNode ctx) {
        String terminal = ctx.getText();
        return new IrAndOp(terminal);
    }
}