package edu.mit.compilers.ir;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.grammar.DecafParser.BlockContext;
import edu.mit.compilers.grammar.DecafParser.Field_declContext;
import edu.mit.compilers.grammar.DecafParser.StatementContext;

class IrBlock extends Ir {
    private List<IrFieldDecl> fieldDeclarations;
    private List<IrStatement> statements;
    
    public IrBlock(List<IrFieldDecl> fieldDeclarations, List<IrStatement> statements) {
        this.fieldDeclarations = fieldDeclarations;
        this.statements = statements;
    }

    public static IrBlock create(DecafSemanticChecker checker, BlockContext ctx) {
        List<IrFieldDecl> fieldDeclarations = new ArrayList<>();
        List<IrStatement> statements = new ArrayList<>();
        
        for (Field_declContext field : ctx.field_decl()) {
            //TODO Process fields
//            fieldDeclarations.add(IrFieldDecl.create(checker, ctx));
        }
        
        for (StatementContext statement : ctx.statement()) {
            //TODO Process statements
//            statements.add(StatementContext.create(checker, ctx));
        }
        
        return new IrBlock(fieldDeclarations, statements);
    }

    public static IrBlock empty() {
        List<IrFieldDecl> fieldDeclarations = new ArrayList<>();
        List<IrStatement> statements = new ArrayList<>();
        return new IrBlock(fieldDeclarations, statements);
    }
}