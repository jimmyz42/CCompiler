package edu.mit.compilers.cfg.components;

import java.io.PrintWriter;
import java.util.ArrayList;

import edu.mit.compilers.optimizer.Optimizable;

public class TerminalBlock extends BasicBlock {

    public TerminalBlock() {
        super(new ArrayList<Optimizable>());
    }

    public static TerminalBlock create() {
        return new TerminalBlock();
    }

    @Override
    public TerminalBlock getEntryBlock() {
        return this;
    }

    @Override
    public TerminalBlock getExitBlock() {
        return this;
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
    }
}
