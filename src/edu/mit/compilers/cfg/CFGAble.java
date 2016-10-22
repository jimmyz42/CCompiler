package edu.mit.compilers.cfg;

import java.io.PrintWriter;
import edu.mit.compilers.cfg.components.CFG;

public interface CFGAble {
    public abstract CFG generateCFG();
    public abstract void concisePrint(PrintWriter pw, String prefix);
}
