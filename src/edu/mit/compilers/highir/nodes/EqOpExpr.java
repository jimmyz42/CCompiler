package edu.mit.compilers.highir.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.PrintWriter;

import edu.mit.compilers.cfg.Condition;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.Storage;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.instructions.Cmp;
import edu.mit.compilers.lowir.instructions.Cmove;
import edu.mit.compilers.lowir.instructions.Cmovne;
import edu.mit.compilers.lowir.instructions.Mov;
import edu.mit.compilers.lowir.instructions.Instruction;
import exceptions.TypeMismatchError;

public class EqOpExpr extends BinOpExpr implements Condition {
    public EqOpExpr(EqOp operator, Expression lhs, Expression rhs) {
        super(operator, lhs, rhs);
    }

    public static EqOpExpr create(DecafSemanticChecker checker, DecafParser.EqOpExprContext ctx) {
        EqOp operator = EqOp.create(checker, ctx.EQ_OP());
        Expression lhs = Expression.create(checker, ctx.expr(0));
        Expression rhs = Expression.create(checker, ctx.expr(1));

        if (lhs.getExpressionType() != rhs.getExpressionType() || !(lhs.getExpressionType() instanceof ScalarType)) {
            throw new TypeMismatchError("Expected scalar arguments of the same type", ctx);
        }

        return new EqOpExpr(operator, lhs, rhs);
    }

    @Override
    public Type getExpressionType() {
        return ScalarType.BOOL;
    }

    @Override
    public BasicBlock shortCircuit(CFG trueBranch, CFG falseBranch) {
        BasicBlock block = BasicBlock.createWithCondition(this);
        block.setNextBlocks(Arrays.asList(trueBranch.getEntryBlock(), falseBranch.getEntryBlock()));
        trueBranch.addPreviousBlock(block);
        falseBranch.addPreviousBlock(block);
        return block;
    }

    @Override
    public void generateAssembly(AssemblyContext ctx){
        lhs.generateAssembly(ctx);
        rhs.generateAssembly(ctx);

        Storage src = rhs.allocateLocation(ctx);
        Storage dest = lhs.allocateLocation(ctx);
        
        List<Instruction> expression = new ArrayList<>();
        Instruction opInstruction = new Cmp(src, dest);
        expression.add(opInstruction);

        if(operator.getTerminal().equals("==")) {
            expression.add(Mov.create(false, dest));
            expression.add(Cmove.create(true, dest));
        } else {
            expression.add(Mov.create(false, dest));
            expression.add(Cmovne.create(true, dest));
        }

        ctx.addInstructions(expression);

        ctx.pushStack(this, dest);
        rhs.deallocateLocation(ctx);
        lhs.deallocateLocation(ctx);
    }
}