package edu.mit.compilers.highir.nodes;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;

public class BoolLiteral extends Literal {
    private boolean terminal;

    public BoolLiteral(boolean terminal) {
        this.terminal = terminal;
    }

    public static BoolLiteral create(DecafSemanticChecker checker, DecafParser.BoolLiteralContext ctx) {
         String text = ctx.BOOL_LITERAL().getText();
         Boolean terminal = Boolean.valueOf(text);
         return new BoolLiteral(terminal);
    }
    
    @Override
    public Type getExpressionType() {
        return ScalarType.BOOL;
    }
    
    @Override
    public String toString() {
        return Boolean.toString(terminal);
    }
}