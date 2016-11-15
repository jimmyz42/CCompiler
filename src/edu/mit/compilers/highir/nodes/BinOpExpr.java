package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import edu.mit.compilers.optimizer.OptimizerContext;


abstract public class BinOpExpr extends Expression {
    protected BinOp operator;
    protected Expression lhs;
    protected Expression rhs;

    public BinOpExpr(BinOp operator, Expression lhs, Expression rhs) {
        this.operator = operator;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        pw.println(prefix + getClass().getSimpleName());
        pw.println(prefix + "-lhs:");
        lhs.prettyPrint(pw, prefix + "    ");
        pw.println(prefix + "-operator: " + operator);
        pw.println(prefix + "-rhs:");
        rhs.prettyPrint(pw, prefix + "    ");
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
    	pw.print(prefix);
    	lhs.cfgPrint(pw, "");
    	pw.print(" " + operator.getTerminal());;
    	rhs.cfgPrint(pw, " ");
    }

	@Override
	public Set<Descriptor> getConsumedDescriptors() {
		Set<Descriptor> consumed = new HashSet<>();
		consumed.addAll(lhs.getConsumedDescriptors());
		consumed.addAll(rhs.getConsumedDescriptors());
		return consumed;
	}

	@Override
	public Set<Descriptor> getGeneratedDescriptors() {
		return Collections.emptySet();
	}

	@Override
	public List<CFGAble> generateTemporaries(OptimizerContext context) {
		List<CFGAble> temps = new ArrayList<>();
		
    	temps.addAll(lhs.generateTemporaries(context));
    	temps.addAll(rhs.generateTemporaries(context));

    	VariableDescriptor temp = context.addExpression(this);
    	temps.add(temp);
    	temps.add(AssignStmt.create(IdLocation.create(temp), "=", this));
    	
        return temps;
	}
	
	@Override
    public int hashCode() {
        return lhs.hashCode() + operator.hashCode() + rhs.hashCode();
    }
}
