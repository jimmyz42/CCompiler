package edu.mit.compilers.ir;

import org.antlr.v4.runtime.tree.TerminalNode;

class IrMulOp extends IrBinOp {
    private String terminal;

    public IrMulOp(String terminal) {
        super(terminal);
    }

    public static IrMulOp create(DecafSemanticChecker checker, TerminalNode ctx) {
        String terminal = ctx.getText();
        return new IrMulOp(terminal);
    }
}