package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.util.ArrayList;

import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser.ExprContext;
import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.highir.descriptor.MethodDescriptor;
import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import exceptions.TypeMismatchError;


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
    	operator.cfgPrint(pw, " ");
    	rhs.cfgPrint(pw, " ");
    }

    @Override
    public CFG generateCFG(CFGContext context) {
        CFG lhsCFG = lhs.generateCFG(context);
        CFG operatorCFG = operator.generateCFG(context);

        lhsCFG.setNextBlock(operatorCFG.getEntryBlock());
        operatorCFG.setPreviousBlock(lhsCFG.getExitBlock());

        CFG rhsCFG = rhs.generateCFG(context);
        operatorCFG.setNextBlock(rhsCFG.getEntryBlock());
        rhsCFG.setPreviousBlock(operatorCFG.getExitBlock());

        return new CFG (lhsCFG.getEntryBlock(), rhsCFG.getExitBlock());
    }
}
