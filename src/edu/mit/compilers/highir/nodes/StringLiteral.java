package edu.mit.compilers.highir.nodes;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.BoolValue;
import edu.mit.compilers.lowir.ImmediateValue;
import edu.mit.compilers.lowir.Storage;
import edu.mit.compilers.lowir.StringValue;
import edu.mit.compilers.lowir.instructions.Cmove;
import edu.mit.compilers.lowir.instructions.Cmovg;
import edu.mit.compilers.lowir.instructions.Cmovge;
import edu.mit.compilers.lowir.instructions.Cmovl;
import edu.mit.compilers.lowir.instructions.Cmovle;
import edu.mit.compilers.lowir.instructions.Cmovne;
import edu.mit.compilers.lowir.instructions.Cmp;
import edu.mit.compilers.lowir.instructions.Instruction;
import edu.mit.compilers.lowir.instructions.Mov;
import edu.mit.compilers.lowir.instructions.StringInstruction;

public class StringLiteral extends ExternArg {
    private String terminal;

    public StringLiteral(String terminal) {
        this.terminal = terminal;
    }

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
        pw.print(prefix + terminal);
    }
    
    public Storage allocateLocation(AssemblyContext ctx) {
    	return StringValue.create(terminal);
    }
    
    @Override
    public void generateAssembly(AssemblyContext ctx) {

        Instruction stringInstruction = new StringInstruction( //TODO make less gross
        		terminal + Integer.toString(ThreadLocalRandom.current().nextInt(0, 500)), 
        		terminal);
        
        ctx.addInstruction(stringInstruction);
        
        System.out.println("ADDING A STRING INSTRUCTION");
    }
}