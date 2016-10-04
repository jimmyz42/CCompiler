package edu.mit.compilers.ir;

import org.antlr.v4.runtime.tree.TerminalNode;

import edu.mit.compilers.grammar.DecafParser;

class IrId extends Ir {
    private String terminal;

    public IrId(String terminal) {
        this.terminal = terminal;
    }

    public static IrId create(DecafSemanticChecker checker, DecafParser.IdLocationContext ctx) {
        String terminal = ctx.getText();
        return new IrId(terminal);
    }

    public static IrId create(DecafSemanticChecker checker, TerminalNode ctx) {
        String terminal = ctx.getText();
        return new IrId(terminal);
    }
}