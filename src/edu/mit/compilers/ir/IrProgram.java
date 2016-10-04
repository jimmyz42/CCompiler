package edu.mit.compilers.ir;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.grammar.DecafParser.Extern_declContext;
import edu.mit.compilers.grammar.DecafParser.Field_declContext;
import edu.mit.compilers.grammar.DecafParser.Method_declContext;
import edu.mit.compilers.grammar.DecafParser.ProgramContext;

class IrProgram extends Ir {
    private List<IrExternDecl> externs;
    private List<IrFieldDecl> fields;
    private List<IrMethodDecl> methods;
    
    public IrProgram(List<IrExternDecl> externs, List<IrFieldDecl> fields, List<IrMethodDecl> methods) {
        this.externs = externs;
        this.fields = fields;
        this.methods = methods;
    }


    public static Object create(DecafSemanticChecker checker, ProgramContext ctx) {
        List<IrExternDecl> externs = new ArrayList<>();
        List<IrFieldDecl> fields = new ArrayList<>();
        List<IrMethodDecl> methods = new ArrayList<>();
        
        for (Extern_declContext extern : ctx.extern_decl()) {
            // TODO process externs
        }
        
        for (Field_declContext field : ctx.field_decl()) {
            // TODO process fields
        }
        
        for (Method_declContext method : ctx.method_decl()) {
            methods.add(IrMethodDecl.create(checker, method));
        }
        
        return new IrProgram(externs, fields, methods);
    }
}