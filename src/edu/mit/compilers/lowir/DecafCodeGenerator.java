// Generated from /home/firescar/Documents/MIT/6.035/nchinda26035/src/edu/mit/compilers/grammar/Decaf.g4 by ANTLR 4.5.3

package edu.mit.compilers.lowir;

import java.util.Stack;

import edu.mit.compilers.lowir.Register;

/**
* This class walks through a high level DecafSemanticChecker graph a low level control
* flow graph which can be used to output assembly code
*/
public class DecafCodeGenerator {
    private Stack<Object> stack = new Stack<>();
    public Object popStack() {
        return stack.pop();
    }
    public void pushStack(Object item) {
        stack.push(item);
    }

    //todo initiallize with the set of available registers
    private Stack<Register> registers = new Stack<>();
    //return a register for use
    public Register allocateRegister() {
        //TODO: make sure to handle the case where no registers are available
        //and an instruction to push a register onto the stack must be created
        return registers.pop();
    }

    //release a register back into the pool
    public void deallocateRegister(Register reg) {
        registers.push(reg);
    }
}