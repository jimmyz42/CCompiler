package edu.mit.compilers.highir.nodes;

import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;

import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.ImmediateValue;
import edu.mit.compilers.optimizer.Optimizable;
import edu.mit.compilers.optimizer.OptimizerContext;
import exceptions.IntegerSizeError;

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
    public Type getType() {
        return ScalarType.INT;
    }

    @Override
    public String toString() {
        return Long.toString(terminal);
    }

    @Override
    public void generateAssembly(AssemblyContext ctx){
        ctx.storeStack(getStorageTuple(), ImmediateValue.create(terminal));
    }

	@Override
	public long getNumStackAllocations() {
		return 1;
	}

    @Override
    public ImmediateValue getLocation(AssemblyContext ctx) {
        return ImmediateValue.create(terminal);
    }

	@Override
	public List<Optimizable> generateTemporaries(OptimizerContext context) {
		return Collections.emptyList();
	}
	
	@Override
	public int hashCode() {
		return (int) terminal;
	}
}