package edu.mit.compilers.cfg;

import java.io.PrintWriter;
import edu.mit.compilers.cfg.components.BasicBlock;

public interface CFGAble {
    public abstract BasicBlock generateCFG();
    public abstract void concisePrint(PrintWriter pw, String prefix);
}
