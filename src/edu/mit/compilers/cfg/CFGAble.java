package edu.mit.compilers.cfg;

import java.io.PrintWriter;
import java.util.List;

import edu.mit.compilers.cfg.components.CFG;
import edu.mit.compilers.lowir.instructions.Instruction;

public interface CFGAble {
    public abstract CFG generateCFG(CFGContext context);
    public abstract void cfgPrint(PrintWriter pw, String prefix);
    public List<Instruction> generateAssembly();
}
