package edu.mit.compilers.highir.descriptor;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.mit.compilers.Util;
import edu.mit.compilers.grammar.DecafParser.Method_argument_declContext;
import edu.mit.compilers.grammar.DecafParser.Method_declContext;
import edu.mit.compilers.highir.DecafSemanticChecker;
import edu.mit.compilers.highir.nodes.Block;
import edu.mit.compilers.highir.nodes.ReturnStmt;
import edu.mit.compilers.highir.nodes.Type;
import edu.mit.compilers.highir.nodes.VoidType;
import edu.mit.compilers.highir.nodes.ScalarType;
import edu.mit.compilers.highir.symboltable.ArgumentSymbolTable;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.ImmediateValue;
import edu.mit.compilers.lowir.instructions.Enter;
import edu.mit.compilers.lowir.instructions.Instruction;
import edu.mit.compilers.lowir.instructions.Label;
import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.cfg.components.BasicBlock;
import exceptions.TypeMismatchError;

public class MethodDescriptor extends FunctionDescriptor {
    private final List<VariableDescriptor> arguments;
    private final Block body;

    public MethodDescriptor(String name, Type returnType, List<VariableDescriptor> arguments, Block body) {
        super(name, returnType);
        this.arguments = Collections.unmodifiableList(arguments);
        this.body = body;
    }

    public List<VariableDescriptor> getArguments() {
        return arguments;
    }

    public static MethodDescriptor create(DecafSemanticChecker checker, Method_declContext ctx) {
        Type returnType = ctx.type() == null ? VoidType.VOID : ScalarType.create(checker, ctx.type());
        String name = ctx.ID().getText();
        ArgumentSymbolTable argTable = new ArgumentSymbolTable(checker.currentSymbolTable());

        Block body = Block.createEmpty(argTable, false);

        List<VariableDescriptor> arguments = new ArrayList<>();
        for (Method_argument_declContext argumentDecl : ctx.method_argument_decl()) {
            Type type = ScalarType.create(checker, argumentDecl.type());
            String argName = argumentDecl.ID().getText();
            arguments.add(argTable.addVariable(type, argName, argumentDecl));
        }

        if (name.equals("main")) {
            if (returnType != VoidType.VOID) {
                throw new TypeMismatchError("Main must return void", ctx);
            }
            if (!arguments.isEmpty()) {
                throw new TypeMismatchError("Main must not take any arguments", ctx);
            }
        }

        return new MethodDescriptor(name, returnType, arguments, body);
    }

    public void loadBody(DecafSemanticChecker checker, Method_declContext ctx) {
        checker.pushMethodDescriptor(this);
        body.loadBlock(checker, ctx.block());
        checker.pushMethodDescriptor(this);
    }

    @Override
    public void prettyPrint(PrintWriter pw, String prefix) {
        pw.println(prefix + getClass().getSimpleName());
        pw.print(prefix + "-returnType: ");
        getType().prettyPrint(pw,"");
        pw.println("\n" + prefix + "-name: " + getName());
        pw.print(prefix + "-parameters:\n");
        for(VariableDescriptor var : arguments ){
            var.prettyPrint(pw, prefix + "    ");
        }
        pw.println();
        body.prettyPrint(pw, prefix + "    ");
        pw.println(prefix + "end " + getName() + " " + getClass().getSimpleName());
        pw.println();
    }

    @Override
    public CFG generateCFG(CFGContext context) {
        ArrayList<CFGAble> components = new ArrayList<>();
        for(VariableDescriptor argument: arguments) {
            components.add(argument);
        }
        BasicBlock symbolBlock = BasicBlock.create(components);
        symbolBlock.setDescription("arguments");
        BasicBlock methodBlock = BasicBlock.create(this);
        CFG bodyCFG = body.generateCFG(context);
        BasicBlock returnBlock = BasicBlock.create(ReturnStmt.create());
        
        methodBlock.setNextBlock(symbolBlock);
        symbolBlock.addPreviousBlock(methodBlock);
        symbolBlock.setNextBlock(bodyCFG.getEntryBlock());
        bodyCFG.addPreviousBlock(symbolBlock);
        bodyCFG.addNextBlock(returnBlock);
        returnBlock.setPreviousBlock(bodyCFG.getExitBlock());
        
        return new CFG(methodBlock, returnBlock);
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
        pw.println(getType() + " " + getName());
    }

    @Override
    public void generateAssembly(AssemblyContext ctx) {
        ctx.addInstruction(Label.create(getName()));
        ctx.addInstruction(Enter.create(arguments.size()));
        //TODO: push arguments onto the stack
    }
}
