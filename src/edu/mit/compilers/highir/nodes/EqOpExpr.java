package edu.mit.compilers.highir.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.mit.compilers.cfg.Condition;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.ImmediateValue;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.Storage;
import edu.mit.compilers.lowir.instructions.Cmove;
import edu.mit.compilers.lowir.instructions.Cmovne;
import edu.mit.compilers.lowir.instructions.Cmp;
import edu.mit.compilers.lowir.instructions.Instruction;
import edu.mit.compilers.lowir.instructions.Mov;
import edu.mit.compilers.optimizer.Optimizable;
import exceptions.TypeMismatchError;

public class EqOpExpr extends BinOpExpr implements Condition {
    public EqOpExpr(EqOp operator, Expression lhs, Expression rhs) {
        super(operator, lhs, rhs);
    }

    public static EqOpExpr create(DecafSemanticChecker checker, DecafParser.EqOpExprContext ctx) {
        EqOp operator = EqOp.create(checker, ctx.EQ_OP());
        Expression lhs = Expression.create(checker, ctx.expr(0));
        Expression rhs = Expression.create(checker, ctx.expr(1));

        if (lhs.getType() != rhs.getType() || !(lhs.getType() instanceof ScalarType)) {
            throw new TypeMismatchError("Expected scalar arguments of the same type", ctx);
        }

        return new EqOpExpr(operator, lhs, rhs);
    }

    @Override
    public Type getType() {
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

        List<Instruction> expression = new ArrayList<>();

        Register src = rhs.allocateRegister(ctx);
        Register dest = lhs.allocateRegister(ctx);
        Register result = ctx.allocateRegister(getStorageTuple());

    	Storage btrue = ImmediateValue.create(true);
    	Storage bfalse = ImmediateValue.create(false);

    	//move 0 into result
    	expression.add(Mov.create(bfalse, result));

    	//move 1 into temp
    	Storage temp = Register.create("%rax");
    	expression.add(Mov.create(btrue, temp));

    	//compare lhs to rhs
        Instruction opInstruction = new Cmp(src, dest);
        expression.add(opInstruction);

    	//if expression is true, put temp (1) into result
        if(operator.getTerminal().equals("==")) {
            expression.add(Cmove.create(temp, result));
        } else {
            expression.add(Cmovne.create(temp, result));
        }

        ctx.addInstructions(expression);

        //result = 0 or 1
        //	0: expression evaluated to false
        //	1: expression evaluated to true

		ctx.storeStack(getStorageTuple(), result);
        ctx.deallocateRegister(src);
        ctx.deallocateRegister(dest);
        ctx.deallocateRegister(result);
    }

	@Override
	public long getNumStackAllocations() {
		return lhs.getNumStackAllocations() + rhs.getNumStackAllocations() + 1;
	}

	@Override
	public Optimizable doAlgebraicSimplification() {
		lhs = (Expression) lhs.doAlgebraicSimplification();
		rhs = (Expression) rhs.doAlgebraicSimplification();
		if(lhs instanceof IntLiteral && rhs instanceof IntLiteral) {
			if(operator.getTerminal().equals("==")) {
				return new BoolLiteral(((IntLiteral)lhs).getValue() == ((IntLiteral)rhs).getValue());
			} else
				return new BoolLiteral(((IntLiteral)lhs).getValue() != ((IntLiteral)rhs).getValue());
		}
		if(lhs instanceof BoolLiteral && rhs instanceof BoolLiteral) {
			if(operator.getTerminal().equals("==")) {
				return new BoolLiteral(((BoolLiteral)lhs).getValue() == ((BoolLiteral)rhs).getValue());
			} else
				return new BoolLiteral(((BoolLiteral)lhs).getValue() != ((BoolLiteral)rhs).getValue());
		}
		if(lhs instanceof CharLiteral && rhs instanceof CharLiteral) {
			if(operator.getTerminal().equals("==")) {
				return new BoolLiteral(((CharLiteral)lhs).getValue() == ((CharLiteral)rhs).getValue());
			} else
				return new BoolLiteral(((CharLiteral)lhs).getValue() != ((CharLiteral)rhs).getValue());
		}
		// a==true: a, a==false: !a, a!=true: !a, a!=false: a, a==a: true, a!=a: false
		if(operator.getTerminal().equals("==")) {
			if(lhs instanceof BoolLiteral && ((BoolLiteral)lhs).getValue()) {
				return rhs;
			}
			if(rhs instanceof BoolLiteral && ((BoolLiteral)rhs).getValue()) {
				return lhs;
			}
			if(lhs instanceof BoolLiteral && !((BoolLiteral)lhs).getValue()) {
				return new NotExpr(rhs);
			}
			if(rhs instanceof BoolLiteral && !((BoolLiteral)rhs).getValue()) {
				return new NotExpr(lhs);
			}
			if(lhs.equals(rhs)) {
				return new BoolLiteral(true);
			}
		} else { // !=
			if(lhs instanceof BoolLiteral && !((BoolLiteral)lhs).getValue()) {
				return rhs;
			}
			if(rhs instanceof BoolLiteral && !((BoolLiteral)rhs).getValue()) {
				return lhs;
			}
			if(lhs instanceof BoolLiteral && ((BoolLiteral)lhs).getValue()) {
				return new NotExpr(rhs);
			}
			if(rhs instanceof BoolLiteral && ((BoolLiteral)rhs).getValue()) {
				return new NotExpr(lhs);
			}
			if(lhs.equals(rhs)) {
				return new BoolLiteral(false);
			}
		}
		return this; //cannot simplify
	}
	
	@Override
	public Expression clone() {
		return new EqOpExpr(new EqOp(operator.getTerminal()), lhs.clone(), rhs.clone());
	}
}
