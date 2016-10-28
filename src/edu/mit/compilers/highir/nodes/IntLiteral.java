package edu.mit.compilers.highir.nodes;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.ImmediateValue;
import edu.mit.compilers.lowir.Storage;
import exceptions.IntegerSizeError;
import org.antlr.v4.runtime.ParserRuleContext;

public class IntLiteral extends Literal {
    private long terminal;

    public IntLiteral(long terminal) {
        this.terminal = terminal;
    }

    public static IntLiteral create(DecafSemanticChecker checker, DecafParser.IntLiteralContext ctx) {
         String text = ctx.INT_LITERAL().getText();
         Long terminal = parseLong(text, ctx);
         return new IntLiteral(terminal);
    }

    public static long parseLong(String text, ParserRuleContext ctx) {
        try {
            return Long.decode(text.replace("ll", ""));
        }
        catch (NumberFormatException e) {
            throw new IntegerSizeError("Integer Values must be at most 64 bits", ctx);
        }
    }

    @Override
    public Type getExpressionType() {
        return ScalarType.INT;
    }

    @Override
    public String toString() {
        return Long.toString(terminal);
    }
    
    public Storage allocateLocation(AssemblyContext ctx) {
    	return ImmediateValue.create(terminal);
    }
}