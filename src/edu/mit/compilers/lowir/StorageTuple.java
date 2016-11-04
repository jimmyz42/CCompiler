package edu.mit.compilers.lowir;

public class StorageTuple { 
	public final Storable base; 
	public final Register index; 
	public StorageTuple(Storable base, Register index) { 
		this.base = base; 
		this.index = index; 
	}
	
	public static StorageTuple create(Storable base, Register index) {
		return new StorageTuple(base, index);
	}
	
	public static StorageTuple create(Storable base) {
		return new StorageTuple(base, null);
	}
} 