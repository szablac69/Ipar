package net.ipar.mod.utilsPLC;

import java.util.ArrayList;
import java.util.List;

import net.ipar.mod.Ipar;
import net.ipar.mod.gui.GuiPLC;
import net.ipar.mod.handler.MessagePLC;
import net.ipar.mod.utilsPLC.IlNode.RowBounds;

public class CompileLD2STL {
	
	public List<IlNode> lstNode = new ArrayList<IlNode>();
	private GuiPLC gui;
	
	public CompileLD2STL(GuiPLC gui){
		this.gui = gui;
	}
	
	public void compile(){
		
		int y = 0;
		gui.PLC.lstIstruction.clear();
		while(gui.PLC.ldIcon[0][y].isEcexutable){
			lstNode.clear();
			
			RowBounds rowBound = new RowBounds();
			do{
				for(int x = 0; x<12; x++){
					IlNode node = new IlNode(gui.PLC, x, y);
					if(node.lstIstruction.isEmpty()) continue;
					rowBound.minY = Math.min(rowBound.minY,node.rowBound.minY);
					rowBound.maxY = Math.max(rowBound.maxY,node.rowBound.maxY);
					lstNode.add(node);
				}
			} while(y++ < rowBound.maxY);
			System.out.println("++++++++++++++++++++++");
			System.out.println("\nMinRow : " + rowBound.minY + "\nMaxRow : " + rowBound.maxY + "\nNodeCount : " + lstNode.size());
			//Megvannak a Nodeok, most a hivatkozásokat kell gatyába rázni
			for(IlNode ilNode : lstNode) ilNode.makeConnections(lstNode);
			printNodes();
			//Most jöhet az egyszerûsítés
	
			boolean wasSimplifyAnd;
			boolean wasSimplifyOr;
			do{
				wasSimplifyOr = wasSimplifyAnd = false;
				for(IlNode ilNode : lstNode){
					IlNode nodeToDel = ilNode.simplifyOr();
					if(nodeToDel != null){
						lstNode.remove(nodeToDel);
						wasSimplifyOr = true;
						break;
					}
					nodeToDel = ilNode.simplifyAnd();
					if(nodeToDel != null){
						lstNode.remove(nodeToDel);
						wasSimplifyAnd = true;
						break;
					}
				}
			}while(wasSimplifyAnd || wasSimplifyOr);
			if(lstNode.size() == 1){
			//if(lstNode.size() == 2  && lstNode.get(1).lstIstruction.get(0).cmd.isOutput()){
			//if(lstNode.size() == 2 && lstNode.get(1).lstIstruction.size() == 1 && lstNode.get(1).lstIstruction.get(0).getEnuStlCmd() == enuStlCmd.OT){
			//if(lstNode.size() == 2 && lstNode.get(1).lstIstruction.size() == 1 && lstNode.get(1).lstIstruction.get(0).getCommand() == 0x7000){
				printNodes();
				//lstNode.get(0).lstIstruction.addAll(lstNode.get(1).lstIstruction);
				gui.PLC.lstIstruction.addAll(lstNode.get(0).lstIstruction);
				gui.PLC.isValidProgram = true;
				lstNode.clear();
				printProgram();
				Ipar.network.sendToServer(new MessagePLC(gui.PLC,3));
				
			}else{
				System.out.println("/////////Compiling ERROR\\\\\\\\\\\\\\");
				printNodes();
				lstNode.clear();
				gui.PLC.isValidProgram = false;
				break;
			}
		};
	}
	public void printNodes(){
		System.out.println("##########################################");
		for(IlNode ilNode : lstNode){
			System.out.println("+++++++++++++++++++++++++");
			System.out.println("ME");
			System.out.println("X : " + ilNode.posX + "\tY : " + ilNode.posY);
			for(Instruction ins : ilNode.lstIstruction){
				System.out.println(ins.toString());
			}
			System.out.println("----Prev Nodes-----------");
			for(IlNode ilNodePrev : ilNode.lstPrevNode){
				System.out.println("X : " + ilNodePrev.posX + "\tY : " + ilNodePrev.posY);
			}
			System.out.println("----Next Nodes-----------");
			for(IlNode ilNodePrev : ilNode.lstNextNode){
				System.out.println("X : " + ilNodePrev.posX + "\tY : " + ilNodePrev.posY);
			}
		}
	}
	public void printProgram(){
		System.out.println("#####  Compiling Success  ###################");
		for(Instruction ins : gui.PLC.lstIstruction){
			System.out.println(ins.toString());
		}
	}
}
