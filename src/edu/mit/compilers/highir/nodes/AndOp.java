package edu.mit.compilers.highir.nodes;

import org.antlr.v4.runtime.tree.TerminalNode;
import edu.mit.compilers.highir.DecafSemanticChecker;

public class AndOp extends BinOp {
    private String terminal;

    public AndOp(String terminal) {
        super(terminal);
    }

    public static AndOp create(DecafSemanticChecker checker, TerminalNode ctx) {
        String terminal = ctx.getText();
        return new AndOp(terminal);
    }
}