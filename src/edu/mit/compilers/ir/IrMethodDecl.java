package edu.mit.compilers.ir;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.grammar.DecafParser.Method_argument_declContext;
import edu.mit.compilers.grammar.DecafParser.Method_declContext;
import edu.mit.compilers.grammar.DecafParser.ProgramContext;

class IrMethodDecl extends Ir {
    private IrType returnType;
    private IrId name;
    private List<IrArgumentMethodDecl> arguments;
    private IrBlock body;
    
    public IrMethodDecl(IrType returnType, IrId name, List<IrArgumentMethodDecl> arguments, IrBlock body) {
        this.returnType = returnType;
        this.name = name;
        this.arguments = arguments;
        this.body = body;
    }

    public static IrMethodDecl create(DecafSemanticChecker checker, Method_declContext ctx) {
        IrType returnType = new IrType(); // TODO process type
        IrId name = IrId.create(checker, ctx.ID());
        List<IrArgumentMethodDecl> arguments = new ArrayList<IrArgumentMethodDecl>();
        for (Method_argument_declContext argument : ctx.method_argument_decl()) {
            // TODO process arguments
//            arguments.add(IrArgumentMethodDecl.create(ctx, argument));
        }
        IrBlock body = IrBlock.create(checker, ctx.block());
        
        return new IrMethodDecl(returnType, name, arguments, body);
    }
}