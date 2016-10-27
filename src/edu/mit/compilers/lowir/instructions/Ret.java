package edu.mit.compilers.lowir.instructions;

public class Ret extends Instruction {
    public Ret() {
    }

    public static Ret create() {
        return new Ret();
    }
}