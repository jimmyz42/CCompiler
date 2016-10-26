package edu.mit.compilers.highir.descriptor;

import java.io.PrintWriter;
import java.util.List;

import edu.mit.compilers.PrettyPrintable;
import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.highir.nodes.Type;
import edu.mit.compilers.lowir.instructions.Instruction;
import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.cfg.components.BasicBlock;

public abstract class Descriptor implements PrettyPrintable, CFGAble {
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
    public List<Instruction> generateAssembly() {
    	//TODO
    	return null;
    }
}