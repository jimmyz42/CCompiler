package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import edu.mit.compilers.PrettyPrintable;
import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.cfg.components.EnterBlock;
import edu.mit.compilers.cfg.components.LeaveBlock;
import edu.mit.compilers.cfg.components.NonVoidBlock;
import edu.mit.compilers.grammar.DecafParser.Extern_declContext;
import edu.mit.compilers.grammar.DecafParser.Field_declContext;
import edu.mit.compilers.grammar.DecafParser.Method_declContext;
import edu.mit.compilers.grammar.DecafParser.ProgramContext;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.descriptor.Descriptor;
import edu.mit.compilers.highir.descriptor.ExternDescriptor;
import edu.mit.compilers.highir.descriptor.FunctionDescriptor;
import edu.mit.compilers.highir.descriptor.MethodDescriptor;
import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import edu.mit.compilers.highir.symboltable.GlobalSymbolTable;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.optimizer.OptimizerContext;
import exceptions.SemanticError;
import exceptions.UndeclaredIdentifierError;

public class Program extends Ir implements PrettyPrintable, CFGAble {
    private final GlobalSymbolTable symbolTable;

    public Program(GlobalSymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }


    public static Program create(DecafSemanticChecker checker, ProgramContext ctx) {
        GlobalSymbolTable symbolTable = new GlobalSymbolTable();
        checker.pushSymbolTable(symbolTable);

        for (Extern_declContext externDecl : ctx.extern_decl()) {
            try {
                symbolTable.addFunction(new ExternDescriptor(externDecl.ID().getText()), externDecl);
            } catch (SemanticError e) {
                checker.handleSemanticError(e);
            }
        }

        for (Field_declContext fieldDecl : ctx.field_decl()) {
            try {
                symbolTable.addVariablesFromFieldDecl(checker, fieldDecl);
            } catch (SemanticError e) {
                checker.handleSemanticError(e);
            }
        }

        for (Method_declContext methodDecl : ctx.method_decl()) {
            try {
                MethodDescriptor method = MethodDescriptor.create(checker, methodDecl);
                symbolTable.addFunction(method, ctx);
                method.loadBody(checker, methodDecl);
            } catch (SemanticError e) {
                checker.handleSemanticError(e);
            }
        }

        checker.popSybmolTable();

        FunctionDescriptor fnMain = null;
        try {
            fnMain = symbolTable.getFunction("main", ctx);
        } catch (SemanticError e) {
            checker.handleSemanticError(e);
        }
        if (fnMain == null) {
            checker.handleSemanticError(new UndeclaredIdentifierError("No main method found", ctx));
        } else if (!(fnMain.getType() instanceof VoidType)) {
            checker.handleSemanticError(new UndeclaredIdentifierError("Main method must have type void", ctx));
        }

        return new Program(symbolTable);
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        for (Descriptor desc : symbolTable.getDescriptors().values()) {
            desc.prettyPrint(pw, prefix);
        }
    }

    public CFG generateCFG(CFGContext context) {
        ArrayList<CFGAble> components = new ArrayList<>();
        for(Descriptor desc: symbolTable.getDescriptors().values()) {
            if(desc instanceof VariableDescriptor) {
                components.add(desc);
            }
        }
        BasicBlock symbolBlock = BasicBlock.create(components);
        CFG currentCFG = symbolBlock;

        for(Descriptor desc : symbolTable.getDescriptors().values()) {
            if(desc instanceof MethodDescriptor) {
            	//is the method not void? if yes, true, else false
            	boolean isNotVoid = desc.getType().toString().equals("void") ? false : true;
            	
            	EnterBlock start = EnterBlock.create(desc.getName());
            	LeaveBlock end = LeaveBlock.create(isNotVoid);
            	
            	CFG methodCFG = new CFG(start, end);
            	context.addMethodCFG((MethodDescriptor)desc, methodCFG);
                
            	CFG innerCFG = desc.generateCFG(context);
                
            	long numStackAllocations = innerCFG.getNumStackAllocations();
                start.setNumStackAllocations(numStackAllocations);
                end.setNumStackAllocations(numStackAllocations);

                start.setNextBlock(innerCFG.getEntryBlock());
                innerCFG.addPreviousBlock(start);
                
                if(isNotVoid){ //Non-void Method
                	NonVoidBlock error = NonVoidBlock.create();
                	innerCFG.setNextBlock(error);
                	error.addPreviousBlock(innerCFG.getExitBlock());
                	
                	error.setNextBlock(end);
                	end.addPreviousBlock(error);
                } else { //void method
                    innerCFG.setNextBlock(end);
                    end.addPreviousBlock(innerCFG.getExitBlock());
                }
                currentCFG.setNextBlock(methodCFG.getEntryBlock());
                methodCFG.setPreviousBlock(currentCFG.getExitBlock());
                currentCFG = methodCFG;
            }
        }
        return CFG.createWithOptimizations(symbolBlock, currentCFG.getExitBlock());
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
        pw.println("");
    }
    
    @Override
    public void generateAssembly(AssemblyContext ctx) {
    	
    }
    
    @Override
    public long getNumStackAllocations() {
    	return 0;
    }

	@Override
	public Set<Descriptor> getConsumedDescriptors() {
		return Collections.emptySet();
	}

	@Override
	public Set<Descriptor> getGeneratedDescriptors() {
		return Collections.emptySet();
	}

	@Override
	public List<CFGAble> generateTemporaries(OptimizerContext context) {
		return Collections.emptyList();
	}
}
