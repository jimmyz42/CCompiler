package edu.mit.compilers.highir.nodes;

import org.antlr.v4.runtime.tree.TerminalNode;
import edu.mit.compilers.highir.DecafSemanticChecker;

public class MulOp extends BinOp {
    private String terminal;

    public MulOp(String terminal) {
        super(terminal);
    }

    public static MulOp create(DecafSemanticChecker checker, TerminalNode ctx) {
        String terminal = ctx.getText();
        return new MulOp(terminal);
    }
}