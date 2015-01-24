package net.ipar.mod.utilsPLC;

import java.util.*;

import net.ipar.mod.tileEntity.TileEntityPLC;
import net.ipar.mod.utilsPLC.Instruction.EnuStlCmd;
import net.ipar.mod.utilsPLC.LdIcons.LdIconBase;
import net.ipar.mod.utilsPLC.LdIcons.LdIconByteInput;
import net.ipar.mod.utilsPLC.LdIcons.LdIconShort;

public class IlNode {
	public List<Instruction> lstIstruction = new ArrayList<Instruction>();
	public List<IlNode> lstPrevNode = new ArrayList<IlNode>();
	public List<IlNode> lstNextNode = new ArrayList<IlNode>();
	
	public List<LdIconBase> lstPrevLdIcon = new ArrayList<LdIconBase>();
	public List<LdIconBase> lstNextLdIcon = new ArrayList<LdIconBase>();
	
	public RowBounds rowBound = new RowBounds();
	
	//private LdItems ldItem;
	//Kezdeti Node poz�ci� (kapcsolat az ldItem-el)
	public int posX,posY;
	
	private TileEntityPLC PLC;
	
	public IlNode(TileEntityPLC PLC, int posX,int posY){
		this.PLC = PLC;
		this.posX = posX;
		this.posY = posY;
		
		LdIconBase ldIcon = this.PLC.ldIcon[this.posX][this.posY];
		if(ldIcon.isEcexutable){
			lstIstruction.addAll(ldIcon.getInstructionList(PLC, posX, posY));
			lstNextLdIcon = search(PLC, posX + 1, posY, (byte)0, true, rowBound);
			lstPrevLdIcon = search(PLC, posX - 1, posY, (byte)2, false, rowBound);		
		}
		
	}
	/**
	 * A saj�t LdItems hivatkoz�sait cser�li ki Node-ra val� hivatkoz�ssal
	 * @param lstNode - Az a node lista, amiben a a nodokat keress�k
	 */
	public void makeConnections(List<IlNode> lstNode){
		for(LdIconBase ldIcon : lstPrevLdIcon){
			for(IlNode ilNode : lstNode){
				if(ilNode.posX == ldIcon.x && ilNode.posY == ldIcon.y){lstPrevNode.add(ilNode); break;}
			}
		}
		for(LdIconBase ldIcon : lstNextLdIcon){
			for(IlNode ilNode : lstNode){
				if(ilNode.posX == ldIcon.x && ilNode.posY == ldIcon.y){lstNextNode.add(ilNode); break;}
			}
		}
	}
	/**
	 * csak el�refel� tekint, ha � csak az ut�nal�v�vel kapcsol�dik �S az ut�nal�v� csak vele kapcsol�dik visszafel� akkor AND van k�zt�k
	 * @return null - nem vonhat� �sse
	 * 				  ha nem null, akkor a t�rlend� elemet adja vissza
	 */
	public IlNode simplifyAnd(){
		if(lstNextNode.size() == 1){							//csak akkor lehet �sszavonni, ha csak egy m�g�tte l�v� elemmel csatlakozik
			if(lstNextNode.get(0).lstPrevNode.size() == 1){  	//csak akkor lehet �sszavonni, ha csak egy el�tte l�v� elemmel csatlakozik
				//if(lstNextNode.get(0).lstIstruction.get(0).getEnuStlCmd() == enuStlCmd.OT) return null;		//Ha kimenet nincs �sszevon�s
				//if(lstNextNode.get(0).lstIstruction.get(0).cmd.isOutput()) return null;		//Ha kimenet nincs �sszevon�s

				//Ez a kett� ugyanaz?
				//if(lstNextNode.contains(lstNextNode.get(0).lstPrevNode.get(0))){
				if(this == lstNextNode.get(0).lstPrevNode.get(0)){
					//Na ez a kett� AND kapcsolatban van egym�ssal.
					IlNode nextNode = lstNextNode.get(0);
					lstIstruction.addAll(nextNode.lstIstruction);

					if(!lstNextNode.get(0).lstIstruction.get(0).cmd.isOutput() &&
							lstNextNode.get(0).lstIstruction.get(0).cmd != EnuStlCmd.CMP)
							lstIstruction.add(new Instruction(EnuStlCmd.ANS));
					//Ha DF, akkor nem kell ANS
					if(nextNode.lstIstruction.size() == 1 && nextNode.lstIstruction.get(0).cmd == EnuStlCmd.DF){
						lstIstruction.remove(lstIstruction.size() - 1);
					}
					if(nextNode.lstIstruction.size() == 1 && nextNode.lstIstruction.get(0).cmd == EnuStlCmd.DFN){
						lstIstruction.remove(lstIstruction.size() - 1);
					}
					//Egyszer�s�ts�k a proginkat
					Iterator<Instruction> i = lstIstruction.iterator();
					Instruction prevInst = i.next();
					while (i.hasNext()) {
						Instruction inst = i.next();
						if(inst.contractInstruction(prevInst)) {i.remove();}
						else{prevInst = inst;}
						
					}
					//Az ut�na val� hivatkoz�s gaty�ba r�z�sa
					
					//Az ut�na l�v� elemre mutat�k helyre�ll�t�sa (hmm...)
					for(IlNode node : nextNode.lstNextNode){		//Az �ssze m�sodik elehez csatlakoz� node
						int index = node.lstPrevNode.indexOf(nextNode);
						node.lstPrevNode.set(index, this);				
					}
					lstNextNode = nextNode.lstNextNode;
					
					return nextNode;
				}
			}
		}
		return null;
	}
	/**
	 * csak el�refel� tekint, megn�zi az ut�na l�v� elem el�z� elemeit(kiv�ve mag�t) �s ha ugyanaz az lstXXXNode mint az �v�, akkor aoros kapcsolat van
	 * @return null - nem vonhat� �sse
	 * 				  ha nem null, akkor a t�rlend� elemet adja vissza
	 */
	public IlNode simplifyOr(){
		//Az �sszes ut�na l�v� elem
		for(IlNode nextNode : lstNextNode){
			//Az ut�na l�v� elem el�tte l�v� elemei
			for(IlNode compareNode : nextNode.lstPrevNode){
				//Ha ez �n vagyok, akkor next
				if(compareNode.equals(this)) continue;
					if(haveSameElements(this.lstPrevNode, compareNode.lstPrevNode) && 
					 haveSameElements(this.lstNextNode, compareNode.lstNextNode)){
						//A compareNode-ot kell t�r�lni
						lstIstruction.addAll(compareNode.lstIstruction);
						//lstIstruction.add(new Instruction((char)0xF605));
						lstIstruction.add(new Instruction(EnuStlCmd.ORS));
						//TODO Egyszer�s��t�s
						Iterator<Instruction> i = lstIstruction.iterator();
						Instruction prevInst = i.next();
						while (i.hasNext()) {
							Instruction inst = i.next();
							if(inst.contractInstruction(prevInst)) {i.remove();}
							else{prevInst = inst;}
							
						}
						//Hivatkoz�sok rendbet�tele
						for(IlNode nodeLink : compareNode.lstNextNode) nodeLink.lstPrevNode.remove(compareNode);
						for(IlNode nodeLink : compareNode.lstPrevNode) nodeLink.lstNextNode.remove(compareNode);
						return compareNode;
					}
				
			}
		}
		
		
		
		return null;
	}
	
	/**
	 * Returns if both {@link Collection Collections} contains the same elements, in the same quantities, regardless of order and collection type.
	 * <p>
	 * Empty collections and {@code null} are regarded as equal.
	 */
	@SuppressWarnings("hiding")
	public static <IlNode> boolean haveSameElements(Collection<IlNode> col1, Collection<IlNode> col2) {
	    if (col1 == col2)
	        return true;

	    // If either list is null, return whether the other is empty
	    if (col1 == null)
	        return col2.isEmpty();
	    if (col2 == null)
	        return col1.isEmpty();

	    // If lengths are not equal, they can't possibly match
	    if (col1.size() != col2.size())
	        return false;

	    // Helper class, so we don't have to do a whole lot of autoboxing
	    class Count
	    {
	        // Initialize as 1, as we would increment it anyway
	        public int count = 1;
	    }

	    final Map<IlNode, Count> counts = new HashMap();

	    // Count the items in list1
	    for (final IlNode item : col1) {
	        final Count count = counts.get(item);
	        if (count != null)
	            count.count++;
	        else
	            // If the map doesn't contain the item, put a new count
	            counts.put(item, new Count());
	    }

	    // Subtract the count of items in list2
	    for (final IlNode item : col2) {
	        final Count count = counts.get(item);
	        // If the map doesn't contain the item, or the count is already reduced to 0, the lists are unequal 
	        if (count == null || count.count == 0)
	            return false;
	        count.count--;
	    }

	    // If any count is nonzero at this point, then the two lists don't match
	    for (final Count count : counts.values())
	        if (count.count != 0)
	            return false;

	    return true;
	}
	
	
	/**
	 * Megkereis a k�vetkez� nodot, ami kapcsol�dik a sourceNode-hoz
	 * Az ir�ny lehet b�rni, az aktu�lis Item-nek kell megn�zni, hogy van-e kapcsolat a sourcal
	 * @param sourceNode 	- Amihez keress�k a k�t�d� elemet
	 * @param sx,sy			- Source ldItems koordin�t
	 * @param direction  	- Merre keress�k a k�vetkez� elemet 
	 * 						- 0 - el�re
	 * 						- 1 - fel
	 * 						- 2 - h�tra
	 * 						- 3 - le
	 * @param isForward		- a nextNode-ot kerres�k (true) vagy a prevNode-ot
	 * @return				- a megtal�lt kapcsol�d� node list (nagy null, ha nincs)
	 * http://stackoverflow.com/questions/11682212/java-returning-method-which-returns-arraylist
	 */
	public static ArrayList<LdIconBase> search(TileEntityPLC PLC, int sx, int sy, byte direction, boolean isForward, RowBounds rowBounds){
		ArrayList<LdIconBase> retVal = new ArrayList<LdIconBase>();
				
		if(direction == 0 && !isForward) return retVal;  //Ez meg mi?
		if(direction == 2 && isForward) return retVal;  //Ez meg megint mi?
		
		//Check the valid direction
		if(direction == 0 && sx > 11) return retVal;  // Ha ez az utols� oszlop �s jobbra menn�nk
		if(direction == 1 && sy < 0) return retVal;  // Ha ez az els� sor �s felfel� menn�nk
		if(direction == 2 && sx < 0) return retVal;  // Ha ez az els� oszlop �s balra menn�nk
		if(direction == 3 && sy > TileEntityPLC.maxRow - 1) return retVal;  // Ha ez az utols� sor �s lelfel� menn�nk
		
		//Check the connection between the source and the target. Forward and Backward the connection is ensured
		
		LdIconBase ldIcon = PLC.ldIcon[sx][sy];
		
		if(isForward){
			if(direction == 1 && !PLC.ldIcon[sx][sy + 1].isUp) return retVal;  	//Nincs felfel� �sszek�ttet�s
			if(direction == 3 && !ldIcon.isUp) return retVal;  //Nincs lefel� �sszek�ttet�s
		}else{
			if(direction == 1 && !PLC.ldIcon[sx + 1][sy + 1].isUp) return retVal;  	//Nincs felfel� �sszek�ttet�s
			if(direction == 3 && !PLC.ldIcon[sx + 1][sy].isUp) return retVal;  //Nincs lefel� �sszek�ttet�s
		}
		
		
		//Most m�r tuti, hogy van kapcsolat k�zt�nk �s a source k�z�tt
		//Az aktu�lis ponton van v�g�t jelz� parancs?
		//Jobbra vagy balra csak akkor kell ellen�rizni, ha nincs �rv�nyes parancs az aktu�lis helyen �s abba az ir�nyba megy�nk
		//Fel �s le mindenk�pp kell ellen�rizni (ha csak nem onnan j�tt�nk)
		if(ldIcon.isEcexutable){
			retVal.add(ldIcon);			//Konkr�tan csak itt van a list�hoz �rt�kad�s
			rowBounds.minY = Math.min(rowBounds.minY,sy);
			rowBounds.maxY = Math.max(rowBounds.maxY,sy);
		}
		//rekurz�vam megh�vja a t�bbi ir�nyt, amit kell
		boolean isShort = (ldIcon instanceof LdIconShort) || (ldIcon instanceof LdIconByteInput);
		if(direction == 0){  //Fel,Le, (jobbra)
			retVal.addAll(search(PLC, sx, sy - 1, (byte)1,isForward,rowBounds));											//Fel
			retVal.addAll(search(PLC, sx, sy + 1, (byte)3,isForward,rowBounds));											//Le
			if(isShort && isForward) retVal.addAll(search(PLC, sx + 1, sy, (byte)0,isForward,rowBounds));			//El�re
		}else if(direction == 1){  //Fel (jobbra/balra)
			retVal.addAll(search(PLC, sx, sy - 1, (byte)1,isForward,rowBounds));											//Fel
			if(isShort && isForward) retVal.addAll(search(PLC, sx + 1, sy, (byte)0,isForward,rowBounds));			//El�re
			if(isShort && !isForward) retVal.addAll(search(PLC, sx - 1, sy, (byte)2,isForward,rowBounds));			//H�tra
		}else if(direction == 2){//Fel,Le, (balra)
			retVal.addAll(search(PLC, sx, sy - 1, (byte)1,isForward,rowBounds));											//Fel
			retVal.addAll(search(PLC, sx, sy + 1, (byte)3,isForward,rowBounds));											//Le
			if(isShort && !isForward) retVal.addAll(search(PLC, sx - 1, sy, (byte)2,isForward,rowBounds));			//H�tra
		}else if(direction == 3){  //Le (jobbra/balra)
			retVal.addAll(search(PLC, sx, sy + 1, (byte)3,isForward,rowBounds));											//Le
			if(isShort && isForward) retVal.addAll(search(PLC, sx + 1, sy, (byte)0,isForward,rowBounds));			//El�re
			if(isShort && !isForward) retVal.addAll(search(PLC, sx - 1, sy, (byte)2,isForward,rowBounds));			//H�tra
		}

		return retVal;
	}
	
	
	public static class RowBounds{
		public int minY = Integer.MAX_VALUE;
		public int maxY = Integer.MIN_VALUE;
	}
	
}
