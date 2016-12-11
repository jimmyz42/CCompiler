package edu.mit.compilers.cfg.components;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.LinkedList;

import org.jgrapht.ext.DOTExporter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedPseudograph;

import edu.mit.compilers.cfg.CFGContext;


public class CFG {
	protected BasicBlock entryBlock;
	protected BasicBlock exitBlock;
	private List<BasicBlock> orderedBlocks;

	public CFG(BasicBlock entryBlock, BasicBlock exitBlock) {
		this.entryBlock = entryBlock;
		this.exitBlock = exitBlock;
	}

	// For purposes of creating a NOP start and a NOP end
	// to link to, note that entry and exit block are not linked
	// when creating the actual interior CFG link to the NOPs appropriately
	public CFG() {
		this.orderedBlocks = Collections.emptyList();
	}

	public BasicBlock getEntryBlock() {
		return entryBlock;
	}

	public BasicBlock getExitBlock() {
		return exitBlock;
	}

	public BasicBlock getPreviousBlock() {
		return getPreviousBlocks().isEmpty()? null: getPreviousBlocks().get(0);
	}

	public BasicBlock getNextBlock() {
		return getNextBlock(true);
	}

	public BasicBlock getNextBlock(boolean condition) {
		if(getNextBlocks().size() == 0)
			return null;
		if(condition) {
			return exitBlock.getNextBlocks().get(0);
		} else {
			return exitBlock.getNextBlocks().get(1);
		}
	}

	public List<BasicBlock> getPreviousBlocks() {
		return entryBlock.getPreviousBlocks();
	}

	public List<BasicBlock> getNextBlocks() {
		return exitBlock.getNextBlocks();
	}
	
	public List<BasicBlock> getOrderedBlocks() {
		return orderedBlocks;
	}

	public void setPreviousBlocks(List<BasicBlock> prevBlocks) {
		entryBlock.setPreviousBlocks(prevBlocks);
	}

	public void setNextBlocks(List<BasicBlock> nextBlocks) {
		exitBlock.setNextBlocks(nextBlocks);
	}

	public void setPreviousBlock(BasicBlock prevBlock) {
		setPreviousBlocks(Arrays.asList(prevBlock));
	}

	public void setNextBlock(BasicBlock nextBlock) {
		setNextBlocks(Arrays.asList(nextBlock));
	}

	public void addPreviousBlocks(List<BasicBlock> prevBlocks) {
		List<BasicBlock> blocks = new ArrayList<>();
		blocks.addAll(getPreviousBlocks());
		blocks.addAll(prevBlocks);
		setPreviousBlocks(blocks);
	}

	public void addPreviousBlock(BasicBlock prevBlock) {
		addPreviousBlocks(Collections.singletonList(prevBlock));
	}

	public void addNextBlocks(List<BasicBlock> nextBlocks) {
		List<BasicBlock> blocks = new ArrayList<>();
		blocks.addAll(getNextBlocks());
		blocks.addAll(nextBlocks);
		setNextBlocks(blocks);
	}

	public void addNextBlock(BasicBlock nextBlock) {
		addNextBlocks(Collections.singletonList(nextBlock));
	}
	
	public CFG generateCFG(CFGContext context) {
		return this;
	}
	
	private void orderBlocks() {
		//TODO: after createPreheader, 
		// the same block is being put in orderedBlocks twice

		HashSet<BasicBlock> visited = new HashSet<BasicBlock>();
		Stack<BasicBlock> blockStack = new Stack<>();
		Stack<Stack<BasicBlock>> blockStackStack = new Stack<>();
		blockStack.add(getEntryBlock());
		blockStackStack.add(blockStack);

		Set<BasicBlock> searchSet = new HashSet<>();

		List<BasicBlock> orderedBlocks = new ArrayList<>();

		while(blockStackStack.size() > 0) {
			BasicBlock currentBlock = blockStackStack.peek().pop();
			searchSet.remove(currentBlock);
			if(blockStackStack.peek().isEmpty())
				blockStackStack.pop();

			orderedBlocks.add(currentBlock);

			//System.out.println(currentBlock + " added to orderedBlocks");

			if(currentBlock.getNextBlocks().size() > 1) {
				blockStack = new Stack<>();
				if(!visited.contains(currentBlock.getNextBlock(false))) {
					visited.add(currentBlock.getNextBlock(false));
					blockStack.add(currentBlock.getNextBlock(false));
					searchSet.add(currentBlock.getNextBlock(false));
				}

				if(!visited.contains(currentBlock.getNextBlock(true))) {
					visited.add(currentBlock.getNextBlock(true));
					blockStack.add(currentBlock.getNextBlock(true));
					searchSet.add(currentBlock.getNextBlock(true));
				}

				blockStackStack.add(blockStack);
			} else if(currentBlock.getNextBlocks().size() == 1) {
				blockStack = new Stack<>();
				BasicBlock nextBlock = currentBlock.getNextBlock();
				blockStack.add(nextBlock);
				if(searchSet.contains(nextBlock)) {}
				else if(nextBlock.getNextBlocks().size() > 1 && !visited.contains(nextBlock)) {
					visited.add(nextBlock);
					blockStackStack.add(blockStack);
					searchSet.add(nextBlock);
				} else if(nextBlock.getNextBlocks().size() > 1 && visited.contains(nextBlock)) {}
				else if(nextBlock.getPreviousBlocks().size() > 1 && !visited.contains(nextBlock)) {
					visited.add(nextBlock);
				} else if(nextBlock.getPreviousBlocks().size() > 1 && visited.contains(nextBlock)) {
					blockStackStack.add(blockStack);
					searchSet.add(nextBlock);
				}
				else if(visited.contains(nextBlock)) {}
				else {
					visited.add(nextBlock);
					blockStackStack.add(blockStack);
					searchSet.add(nextBlock);
				}
			}

		}

		this.orderedBlocks = orderedBlocks;
	}

	private void giveAllBlocksIds(){
		for(int blockNum = 0; blockNum < orderedBlocks.size(); blockNum++) {
			BasicBlock currentBlock = orderedBlocks.get(blockNum);
			currentBlock.setID("block" +  blockNum);
			currentBlock.setNumID(blockNum);
		}
	}

	public void clearPrevBlocks(){
		HashSet<BasicBlock> visited = new HashSet<BasicBlock>();
		Queue<BasicBlock> blockQueue = new ArrayDeque<>();
		blockQueue.add(getEntryBlock());

		while(blockQueue.size() > 0) {
			BasicBlock currentBlock = blockQueue.poll();
			if(visited.contains(currentBlock)) continue;
			else visited.add(currentBlock);

			currentBlock.setPreviousBlocks(new ArrayList<BasicBlock>());
			if(currentBlock.getNextBlocks().size() > 0){
				blockQueue.add(currentBlock.getNextBlock(true));
			}
			if(currentBlock.getNextBlocks().size() > 1) {
				blockQueue.add(currentBlock.getNextBlock(false));
			}
		}
	}

	public void genPrevBlocks(){
		HashSet<BasicBlock> visited = new HashSet<BasicBlock>();
		Queue<BasicBlock> blockQueue = new ArrayDeque<>();
		blockQueue.add(getEntryBlock());

		//System.out.println("---- in genPrevBlocks() ------");

		while(blockQueue.size() > 0) {
			BasicBlock currentBlock = blockQueue.poll();
			if(visited.contains(currentBlock)) continue;
			else visited.add(currentBlock);

			//System.out.println(currentBlock + " nextBlocks: " + currentBlock.getNextBlocks());

			if(currentBlock.getNextBlocks().size() > 0){
				//System.out.println(" - next block true: " + currentBlock.getNextBlock(true));

				currentBlock.getNextBlock(true).addPreviousBlock(currentBlock);
				blockQueue.add(currentBlock.getNextBlock(true));
			}
			if(currentBlock.getNextBlocks().size() > 1) {
				//System.out.println(" - next block true: " + currentBlock.getNextBlock(false));

				currentBlock.getNextBlock(false).addPreviousBlock(currentBlock);
				blockQueue.add(currentBlock.getNextBlock(false));
			}
		}
	}

	public void barbieOrderBlocks(){
		HashSet<BasicBlock> visited = new HashSet<BasicBlock>();
        LinkedList<BasicBlock> blockStack = new LinkedList<>();
        List<BasicBlock> ordered = new ArrayList<>();
        blockStack.push(getEntryBlock());
        int blockNum = 0;

		while(!blockStack.isEmpty()) {
			BasicBlock currentBlock = blockStack.pop();
			if(visited.contains(currentBlock)) continue;
			else visited.add(currentBlock);

			ordered.add(currentBlock);


			//push blocks in reverse order to pop in correct order
			if(currentBlock.getNextBlocks().size() > 1) {
				blockStack.add(currentBlock.getNextBlock(false));
			}
			if(currentBlock.getNextBlocks().size() > 0){
				blockStack.add(currentBlock.getNextBlock(true));
			}
		}
		this.orderedBlocks = ordered;
	}

	// Merge basic blocks optimization
	public void mergeBlocks(){
		List<BasicBlock> mergedBlocks = new ArrayList<>();
		for(int blockNum = 0; blockNum < orderedBlocks.size()-1; blockNum++) {
			BasicBlock b1 = orderedBlocks.get(blockNum);
			BasicBlock b2 = orderedBlocks.get(blockNum+1);

			if(b1.getNextBlocks().contains(b2) &&
					BasicBlock.canMerge(b1, b2)) {
				BasicBlock merged = BasicBlock.merge(b1, b2);
				if(b1 == entryBlock) entryBlock = merged;
				if(b2 == exitBlock) exitBlock = merged;

				for(BasicBlock block: merged.getPreviousBlocks()) {
					List<BasicBlock> list = block.getNextBlocks();
					list.set(list.indexOf(b1), merged);
				}
				for(BasicBlock block: merged.getNextBlocks()) {
					List<BasicBlock> list = block.getPreviousBlocks();
					list.set(list.indexOf(b2), merged);
				}
				orderedBlocks.set(blockNum, merged);
				orderedBlocks.set(blockNum+1, merged);
			}
			else {
				mergedBlocks.add(b1);
			}
		}

		mergedBlocks.add(orderedBlocks.get(orderedBlocks.size()-1));
		this.orderedBlocks = mergedBlocks;
	}

	public void pruneBlocks(){
		List<BasicBlock> prunedBlocks = new ArrayList<>();
		for(int blockNum = 0; blockNum < orderedBlocks.size()-1; blockNum++) {
			BasicBlock b1 = orderedBlocks.get(blockNum);

			if(b1.isEmpty() && b1.getNextBlocks().size() > 0) {
				BasicBlock b2 = b1.getNextBlock();
				if(b1 == entryBlock) entryBlock = b2;

				for(BasicBlock block: b1.getPreviousBlocks()) {
					List<BasicBlock> list = block.getNextBlocks();
					list.set(list.indexOf(b1), b2);
				}
				b2.addPreviousBlocks(new ArrayList<>(new HashSet<>(b1.getPreviousBlocks())));
			}
			else {
				prunedBlocks.add(b1);
			}
		}

		prunedBlocks.add(orderedBlocks.get(orderedBlocks.size()-1));
		this.orderedBlocks = prunedBlocks;
	}

	public void exportDOT(String fileName){
		DOTExporter<BasicBlock, DefaultEdge> exporter =
				new DOTExporter<BasicBlock, DefaultEdge>(
						new org.jgrapht.ext.VertexNameProvider<BasicBlock>() {
							@Override
							public String getVertexName(BasicBlock bb) {
								return bb.getID();
							}
						},
						new org.jgrapht.ext.VertexNameProvider<BasicBlock>() {
							@Override
							public String getVertexName(BasicBlock bb) {
								StringWriter sw = new StringWriter();
								PrintWriter pw = new PrintWriter(sw);
								bb.cfgPrint(pw, "");
								String s = sw.toString();

								s = s.replace("\\", "\\\\");
								s = s.replace("\r\n", "\\l");
								s = s.replace("\n", "\\l");
								s = s.replace("\"", "\\\"");
								s = s.replace("{", "\\{");
								s = s.replace("}", "\\}");
								s = s.replace("|", "\\|");
								s = s.replace("|", "\\|");
								s = s.replace("<", "\\<");
								s = s.replace(">", "\\>");
								s = s.replace("(", "\\(");
								s = s.replace(")", "\\)");
								s = s.replace(",", "\\,");
								s = s.replace(";", "\\;");
								s = s.replace(":", "\\:");
								s = s.replace(" ", "\\ ");

								return bb.getID() + ": " + s;
							}
						},
						null);


		DirectedPseudograph<BasicBlock, DefaultEdge> jgraphtGraph = createJGraphT();

		try {
			exporter.export(new FileWriter(fileName), jgraphtGraph);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public DirectedPseudograph<BasicBlock, DefaultEdge> createJGraphT() {
		DirectedPseudograph<BasicBlock, DefaultEdge> g = new DirectedPseudograph<>(DefaultEdge.class);
		return createJGraphT_Sub(null, getEntryBlock(), g);
	}

	public DirectedPseudograph<BasicBlock, DefaultEdge> createJGraphT_Sub(BasicBlock pred, BasicBlock bb, DirectedPseudograph<BasicBlock, DefaultEdge> g) {
		boolean visited = !g.addVertex(bb);
		if (!(pred == null))
			g.addEdge(pred, bb);

		if (visited)
			return g;

		for (BasicBlock succ : bb.getNextBlocks())
			g = createJGraphT_Sub(bb, succ, g);

		return g;
	}

	public long getNumStackAllocations() {
		HashSet<BasicBlock> visited = new HashSet<BasicBlock>();
		Queue<BasicBlock> blockQueue = new ArrayDeque<>();
		blockQueue.add(getEntryBlock());

		int numStackAllocations = 0;
		while(blockQueue.size() > 0) {
			BasicBlock currentBlock = blockQueue.poll();
			if(visited.contains(currentBlock)) continue;
			else visited.add(currentBlock);

			numStackAllocations += currentBlock.getNumStackAllocations();

			if(currentBlock.getNextBlocks().size() > 0){
				blockQueue.add(currentBlock.getNextBlock(true));
			}
			if(currentBlock.getNextBlocks().size() > 1) {
				blockQueue.add(currentBlock.getNextBlock(false));
			}
		}

		return numStackAllocations;
	}
	
	public static CFG createWithOptimizations(BasicBlock entryBlock, BasicBlock exitBlock) {
		CFG cfg = new CFG(entryBlock, exitBlock);
		cfg.clearPrevBlocks();
		cfg.genPrevBlocks();
		cfg.orderBlocks();
		cfg.mergeBlocks();
		cfg.pruneBlocks();
		cfg.giveAllBlocksIds();
		
		return cfg;
	}
}
