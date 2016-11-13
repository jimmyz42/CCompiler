package edu.mit.compilers.highir.nodes;

import org.antlr.v4.runtime.tree.TerminalNode;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;

public class Id extends Ir {
    private String terminal;

    public Id(String terminal) {
        this.terminal = terminal;
    }

    public static Id create(DecafSemanticChecker checker, DecafParser.IdLocationContext ctx) {
        String terminal = ctx.getText();
        return new Id(terminal);
    }

    public static Id create(DecafSemanticChecker checker, TerminalNode ctx) {
        String terminal = ctx.getText();
        return new Id(terminal);
    }
}