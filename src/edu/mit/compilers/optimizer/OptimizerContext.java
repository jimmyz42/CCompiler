package edu.mit.compilers.optimizer;

import edu.mit.compilers.highir.descriptor.VariableDescriptor;
import edu.mit.compilers.highir.nodes.Type;

public class OptimizerContext {
	private int tempVarNonce;
	
	public VariableDescriptor generateNewTemporary(Type type) {
		tempVarNonce++;
		return VariableDescriptor.create("t"+tempVarNonce, type, false);
	}

}
