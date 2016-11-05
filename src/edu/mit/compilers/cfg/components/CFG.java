package edu.mit.compilers.cfg.components;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Stack;
import java.util.HashSet;

import edu.mit.compilers.highir.nodes.Expression;
import edu.mit.compilers.cfg.CFGAble;
import edu.mit.compilers.cfg.CFGContext;
import edu.mit.compilers.cfg.components.BasicBlock;
import edu.mit.compilers.lowir.AssemblyContext;
import edu.mit.compilers.lowir.ImmediateValue;
import edu.mit.compilers.lowir.instructions.Cmp;
import edu.mit.compilers.lowir.instructions.Instruction;
import edu.mit.compilers.lowir.instructions.Je;
import edu.mit.compilers.lowir.instructions.Jmp;
import edu.mit.compilers.lowir.instructions.Jne;
import edu.mit.compilers.lowir.instructions.Label;
import edu.mit.compilers.lowir.instructions.Mov;
import edu.mit.compilers.lowir.Memory;
import edu.mit.compilers.lowir.Register;
import edu.mit.compilers.lowir.Storage;

import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.StringEdgeNameProvider;
import org.jgrapht.ext.StringNameProvider;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.ext.EdgeNameProvider;


public class CFG implements CFGAble {
    protected BasicBlock entryBlock;
    protected BasicBlock exitBlock;

    public CFG(BasicBlock entryBlock, BasicBlock exitBlock) {
        this.entryBlock = entryBlock;
        this.exitBlock = exitBlock;
    }

    // For purposes of creating a NOP start and a NOP end
    // to link to, note that entry and exit block are not linked
    // when creating the actual interior CFG link to the NOPs appropriately
    public CFG() {
    }

    public BasicBlock getEntryBlock() {
        return entryBlock;
    }

    public BasicBlock getExitBlock() {
        return exitBlock;
    }

    public BasicBlock getPreviousBlock() {
        return getPreviousBlocks().get(0);
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

    @Override
    public CFG generateCFG(CFGContext context) {
        return this;
    }

    private void giveAllBlocksIds(){
    	 HashSet<BasicBlock> visited = new HashSet<BasicBlock>();
         Queue<BasicBlock> blockQueue = new ArrayDeque<>();
         blockQueue.add(getEntryBlock());
         int blockNum = 0;

         //step 1: give every BasicBlock an ID
         while(blockQueue.size() > 0) {
             BasicBlock currentBlock = blockQueue.poll();
             if(visited.contains(currentBlock)) continue;
             else visited.add(currentBlock);

             currentBlock.setID("block" +  blockNum);
             blockNum++;
             if(currentBlock.getNextBlocks().size() > 0){
                 blockQueue.add(currentBlock.getNextBlock(true));
             }
             if(currentBlock.getNextBlocks().size() > 1) {
                 blockQueue.add(currentBlock.getNextBlock(false));
             }
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

        while(blockQueue.size() > 0) {
            BasicBlock currentBlock = blockQueue.poll();
            if(visited.contains(currentBlock)) continue;
            else visited.add(currentBlock);

            if(currentBlock.getNextBlocks().size() > 0){
            	currentBlock.getNextBlock(true).addPreviousBlock(currentBlock);
                blockQueue.add(currentBlock.getNextBlock(true));
            }
            if(currentBlock.getNextBlocks().size() > 1) {
            	currentBlock.getNextBlock(false).addPreviousBlock(currentBlock);
                blockQueue.add(currentBlock.getNextBlock(false));
            }
        }
   }

    // Merge basic blocks optimization
    public void mergeBasicBlocks(){
    	HashSet<BasicBlock> visited = new HashSet<BasicBlock>();
        Queue<BasicBlock> blockQueue = new ArrayDeque<>();
        blockQueue.add(getEntryBlock());

        while(blockQueue.size() > 0) {
            BasicBlock currentBlock = blockQueue.poll();
            if(visited.contains(currentBlock)) continue;
            else visited.add(currentBlock);


            if(currentBlock.getNextBlocks().size() > 0 &&
            		BasicBlock.canMerge(currentBlock, currentBlock.getNextBlock())) {
            	BasicBlock b1 = currentBlock, b2 = currentBlock.getNextBlock();
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
            	blockQueue.add(merged);
            } else {
                if(currentBlock.getNextBlocks().size() > 0){
                    blockQueue.add(currentBlock.getNextBlock(true));
                }
            	if(currentBlock.getNextBlocks().size() > 1) {
                    blockQueue.add(currentBlock.getNextBlock(false));
                }
            }
        }
   }

//DO NOT UNCOMMENT unless you are sure it won't break codegen test 17 or any other codegen test
//    public void eliminateEmptyBlocks(){
//    	HashSet<BasicBlock> visited = new HashSet<BasicBlock>();
//        Queue<BasicBlock> blockQueue = new ArrayDeque<>();
//        blockQueue.add(getEntryBlock());
//
//        while(blockQueue.size() > 0) {
//            BasicBlock currentBlock = blockQueue.poll();
//            if(visited.contains(currentBlock)) continue;
//            else visited.add(currentBlock);
//
//            if(currentBlock.isEmpty() && currentBlock.getNextBlocks().size() > 0) {
//        		BasicBlock next = currentBlock.getNextBlock();
//            	for(BasicBlock block: currentBlock.getPreviousBlocks()) {
//            		List<BasicBlock> list = block.getNextBlocks();
//            		list.set(list.indexOf(currentBlock), next);
//            	}
//            	next.setPreviousBlocks(currentBlock.getPreviousBlocks());
//            	blockQueue.add(next);
//            } else {
//                if(currentBlock.getNextBlocks().size() > 0){
//                    blockQueue.add(currentBlock.getNextBlock(true));
//                }
//            	if(currentBlock.getNextBlocks().size() > 1) {
//                    blockQueue.add(currentBlock.getNextBlock(false));
//                }
//            }
//        }
//   }

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

    @Override
    public void cfgPrint(PrintWriter pw, String prefix) {
        HashSet<BasicBlock> visited = new HashSet<BasicBlock>();
        Queue<BasicBlock> blockQueue = new ArrayDeque<>();
        blockQueue.add(getEntryBlock());

        //step 1: give every BasicBlock an ID
        giveAllBlocksIds();

        //step 2: print stuff
        while(blockQueue.size() > 0) {
            BasicBlock currentBlock = blockQueue.poll();
            if(visited.contains(currentBlock)) continue;
            else visited.add(currentBlock);

            pw.println(prefix + "BasicBlock " + currentBlock.getID() + ":");
            currentBlock.cfgPrint(pw, prefix + "    ");
            pw.println(prefix + currentBlock.getID() + " points to:");

            for(BasicBlock block : currentBlock.getNextBlocks()){
            	pw.println(prefix + "    " + block.getID());
            }

            if(currentBlock.getNextBlocks().size() > 0){
                blockQueue.add(currentBlock.getNextBlock(true));
            }
            if(currentBlock.getNextBlocks().size() > 1) {
                blockQueue.add(currentBlock.getNextBlock(false));
            }
        }
    }

    @Override
    public void generateAssembly(AssemblyContext ctx) {
        HashSet<BasicBlock> visited = new HashSet<BasicBlock>();
        Queue<BasicBlock> blockQueue = new ArrayDeque<>();
        blockQueue.add(getEntryBlock());

        giveAllBlocksIds();

        while(blockQueue.size() > 0) {
            BasicBlock currentBlock = blockQueue.poll();
            if(visited.contains(currentBlock)) continue;
            else visited.add(currentBlock);

        	ctx.addInstruction(Label.create(currentBlock.getID()));
            currentBlock.generateAssembly(ctx);

            //push blocks in reverse order to pop in correct order
            if(currentBlock.getNextBlocks().size() > 1) {
                blockQueue.add(currentBlock.getNextBlock(true));
                blockQueue.add(currentBlock.getNextBlock(false));

                //get conditional value generated at the end of currentBlock
                Register val = ((Expression) currentBlock.getCondition()).allocateRegister(ctx);
                //compare to 1
                Storage temp = Register.create("%rax");
                Storage btrue = ImmediateValue.create(true);
                ctx.addInstruction(Mov.create(btrue, temp));
                ctx.addInstruction(new Cmp(val, temp));
               	ctx.deallocateRegister(val);
                //if its == 1, go down true branch
                ctx.addInstruction(Je.create(Memory.create(currentBlock.getNextBlock(true).getID())));
                //else, go down TRUE branch
                ctx.addInstruction(Jmp.create(Memory.create(currentBlock.getNextBlock(false).getID())));
            } else if(currentBlock.getNextBlocks().size() > 0){
                blockQueue.add(currentBlock.getNextBlock(true));
                ctx.addInstruction(Jmp.create(Memory.create(currentBlock.getNextBlock(true).getID())));
            }
        }
    }

    @Override
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

    @Override
    public String toString() {
    	StringWriter sw = new StringWriter();
    	cfgPrint(new PrintWriter(sw), "");
    	return sw.toString();
    }
}
