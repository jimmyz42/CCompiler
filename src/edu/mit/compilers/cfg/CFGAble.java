package edu.mit.compilers.cfg;

import java.io.PrintWriter;
import edu.mit.compilers.cfg.components.CFG;

public interface CFGAble {
    public abstract CFG generateCFG(CFGContext context);
    public abstract void cfgPrint(PrintWriter pw, String prefix);
}