package edu.mit.compilers.highir.nodes;

import org.antlr.v4.runtime.tree.TerminalNode;
import edu.mit.compilers.highir.DecafSemanticChecker;

public class EqOp extends BinOp {

    public EqOp(String terminal) {
        super(terminal);
    }

    public static EqOp create(DecafSemanticChecker checker, TerminalNode ctx) {
        String terminal = ctx.getText();
        return new EqOp(terminal);
    }
}