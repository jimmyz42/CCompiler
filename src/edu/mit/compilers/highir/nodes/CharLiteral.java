package edu.mit.compilers.highir.nodes;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;

public class CharLiteral extends Literal {
    private char terminal;

    public CharLiteral(char terminal) {
        this.terminal = terminal;
    }

    public static CharLiteral create(DecafSemanticChecker checker, DecafParser.CharLiteralContext ctx) {
        String text = ctx.CHAR_LITERAL().getText();
        Character terminal = Character.valueOf(text.charAt(0));
        return new CharLiteral(terminal);
    }
    
    @Override
    public Type getExpressionType() {
        // Chars are treated as ints in decaf
        return ScalarType.INT;
    }
    
    @Override
    public String toString() {
        return "'" + terminal + "' /*" + (int)terminal + "*/";
    }
}