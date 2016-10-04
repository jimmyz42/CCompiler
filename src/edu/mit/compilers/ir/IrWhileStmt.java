package edu.mit.compilers.ir;

import edu.mit.compilers.grammar.DecafParser;
import exceptions.TypeMismatchException;

class IrWhileStmt extends IrStatement {
    private IrExpression condition;
    private IrBlock block;
    
    public IrWhileStmt(IrExpression condition, IrBlock block) {
        this.condition = condition;
        this.block = block;
    }

    public static IrWhileStmt create(DecafSemanticChecker checker, DecafParser.WhileStmtContext ctx, SymbolTable symbolTable) {
        IrExpression condition = IrExpression.create(checker, ctx.expr());
        if (condition.getExpressionType() != TypeScalar.BOOL) {
            throw new TypeMismatchException("While statement condition must be a bool");
        }
        
        IrBlock block = IrBlock.create(checker, ctx.block(), symbolTable);

        return new IrWhileStmt(condition, block);
    }
}