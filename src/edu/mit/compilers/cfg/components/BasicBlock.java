package edu.mit.compilers.cfg.components;

import edu.mit.compilers.highir.nodes.Statement;
import java.util.List;

public class BasicBlock {

    private List<Statement> statements;
    private ProgramPoint entryPoint;
    private ProgramPoint exitPoint;

    public BasicBlock(List<Statement> statements) {
    }
}