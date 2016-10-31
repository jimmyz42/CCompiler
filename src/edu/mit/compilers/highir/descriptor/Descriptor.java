package edu.mit.compilers.highir.descriptor;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.PrettyPrintable;
import edu.mit.compilers.highir.nodes.Type;
import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.ImmediateValue;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.Storable;
import edu.mit.compilers.lowir.Storage;
import edu.mit.compilers.lowir.instructions.Cmovg;
import edu.mit.compilers.lowir.instructions.Cmovge;
import edu.mit.compilers.lowir.instructions.Cmovl;
import edu.mit.compilers.lowir.instructions.Cmovle;
import edu.mit.compilers.lowir.instructions.Cmp;
import edu.mit.compilers.lowir.instructions.Instruction;
import edu.mit.compilers.lowir.instructions.Mov;

public abstract class Descriptor implements PrettyPrintable, CFGAble, Storable {
    private final String name;
    private final Type returnType;

    public Descriptor(String name, Type returnType) {
        this.name = name;
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return returnType;
    }

    @Override
    public CFG generateCFG(CFGContext context) {
        return BasicBlock.create(this);
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
        pw.println(prefix + getType() + " " + getName());
    }

    @Override
    public void generateAssembly(AssemblyContext ctx) {
    	ctx.pushStack(this, ImmediateValue.create(0));
    }
    
    @Override
    public Storage getLocation(AssemblyContext ctx) {
    	return ctx.getStackLocation(this);
    }
    
    @Override
    public Register allocateRegister(AssemblyContext ctx) {
    	return ctx.allocateRegister(this);
    }
    
    @Override
    public Register deallocateRegister(AssemblyContext ctx) {
    	return ctx.allocateRegister(this);
    }
}