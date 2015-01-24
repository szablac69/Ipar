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
	//Kezdeti Node pozíció (kapcsolat az ldItem-el)
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
	 * A saját LdItems hivatkozásait cseréli ki Node-ra való hivatkozással
	 * @param lstNode - Az a node lista, amiben a a nodokat keressük
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
	 * csak elõrefelé tekint, ha õ csak az utánalévõvel kapcsolódik ÉS az utánalévõ csak vele kapcsolódik visszafelé akkor AND van köztük
	 * @return null - nem vonható össe
	 * 				  ha nem null, akkor a törlendõ elemet adja vissza
	 */
	public IlNode simplifyAnd(){
		if(lstNextNode.size() == 1){							//csak akkor lehet összavonni, ha csak egy mögötte lévõ elemmel csatlakozik
			if(lstNextNode.get(0).lstPrevNode.size() == 1){  	//csak akkor lehet összavonni, ha csak egy elõtte lévõ elemmel csatlakozik
				//if(lstNextNode.get(0).lstIstruction.get(0).getEnuStlCmd() == enuStlCmd.OT) return null;		//Ha kimenet nincs összevonás
				//if(lstNextNode.get(0).lstIstruction.get(0).cmd.isOutput()) return null;		//Ha kimenet nincs összevonás

				//Ez a kettõ ugyanaz?
				//if(lstNextNode.contains(lstNextNode.get(0).lstPrevNode.get(0))){
				if(this == lstNextNode.get(0).lstPrevNode.get(0)){
					//Na ez a kettõ AND kapcsolatban van egymással.
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
					//Egyszerüsítsük a proginkat
					Iterator<Instruction> i = lstIstruction.iterator();
					Instruction prevInst = i.next();
					while (i.hasNext()) {
						Instruction inst = i.next();
						if(inst.contractInstruction(prevInst)) {i.remove();}
						else{prevInst = inst;}
						
					}
					//Az utána való hivatkozás gatyába rázása
					
					//Az utána lévõ elemre mutatók helyreállítása (hmm...)
					for(IlNode node : nextNode.lstNextNode){		//Az össze második elehez csatlakozó node
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
	 * csak elõrefelé tekint, megnézi az utána lévõ elem elõzõ elemeit(kivéve magát) és ha ugyanaz az lstXXXNode mint az övé, akkor aoros kapcsolat van
	 * @return null - nem vonható össe
	 * 				  ha nem null, akkor a törlendõ elemet adja vissza
	 */
	public IlNode simplifyOr(){
		//Az összes utána lévõ elem
		for(IlNode nextNode : lstNextNode){
			//Az utána lévõ elem elõtte lévõ elemei
			for(IlNode compareNode : nextNode.lstPrevNode){
				//Ha ez én vagyok, akkor next
				if(compareNode.equals(this)) continue;
					if(haveSameElements(this.lstPrevNode, compareNode.lstPrevNode) && 
					 haveSameElements(this.lstNextNode, compareNode.lstNextNode)){
						//A compareNode-ot kell törölni
						lstIstruction.addAll(compareNode.lstIstruction);
						//lstIstruction.add(new Instruction((char)0xF605));
						lstIstruction.add(new Instruction(EnuStlCmd.ORS));
						//TODO Egyszerüséítés
						Iterator<Instruction> i = lstIstruction.iterator();
						Instruction prevInst = i.next();
						while (i.hasNext()) {
							Instruction inst = i.next();
							if(inst.contractInstruction(prevInst)) {i.remove();}
							else{prevInst = inst;}
							
						}
						//Hivatkozások rendbetétele
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
	 * Megkereis a következõ nodot, ami kapcsolódik a sourceNode-hoz
	 * Az irány lehet bárni, az aktuális Item-nek kell megnézni, hogy van-e kapcsolat a sourcal
	 * @param sourceNode 	- Amihez keressük a kötõdõ elemet
	 * @param sx,sy			- Source ldItems koordinát
	 * @param direction  	- Merre keressük a következõ elemet 
	 * 						- 0 - elõre
	 * 						- 1 - fel
	 * 						- 2 - hátra
	 * 						- 3 - le
	 * @param isForward		- a nextNode-ot kerresük (true) vagy a prevNode-ot
	 * @return				- a megtalált kapcsolódó node list (nagy null, ha nincs)
	 * http://stackoverflow.com/questions/11682212/java-returning-method-which-returns-arraylist
	 */
	public static ArrayList<LdIconBase> search(TileEntityPLC PLC, int sx, int sy, byte direction, boolean isForward, RowBounds rowBounds){
		ArrayList<LdIconBase> retVal = new ArrayList<LdIconBase>();
				
		if(direction == 0 && !isForward) return retVal;  //Ez meg mi?
		if(direction == 2 && isForward) return retVal;  //Ez meg megint mi?
		
		//Check the valid direction
		if(direction == 0 && sx > 11) return retVal;  // Ha ez az utolsó oszlop és jobbra mennénk
		if(direction == 1 && sy < 0) return retVal;  // Ha ez az elsõ sor és felfelé mennénk
		if(direction == 2 && sx < 0) return retVal;  // Ha ez az elsõ oszlop és balra mennénk
		if(direction == 3 && sy > TileEntityPLC.maxRow - 1) return retVal;  // Ha ez az utolsó sor és lelfelé mennénk
		
		//Check the connection between the source and the target. Forward and Backward the connection is ensured
		
		LdIconBase ldIcon = PLC.ldIcon[sx][sy];
		
		if(isForward){
			if(direction == 1 && !PLC.ldIcon[sx][sy + 1].isUp) return retVal;  	//Nincs felfelé összeköttetés
			if(direction == 3 && !ldIcon.isUp) return retVal;  //Nincs lefelé összeköttetés
		}else{
			if(direction == 1 && !PLC.ldIcon[sx + 1][sy + 1].isUp) return retVal;  	//Nincs felfelé összeköttetés
			if(direction == 3 && !PLC.ldIcon[sx + 1][sy].isUp) return retVal;  //Nincs lefelé összeköttetés
		}
		
		
		//Most már tuti, hogy van kapcsolat köztünk és a source között
		//Az aktuális ponton van végét jelzõ parancs?
		//Jobbra vagy balra csak akkor kell ellenõrizni, ha nincs érvényes parancs az aktuális helyen és abba az irányba megyünk
		//Fel és le mindenképp kell ellenõrizni (ha csak nem onnan jöttünk)
		if(ldIcon.isEcexutable){
			retVal.add(ldIcon);			//Konkrétan csak itt van a listához értékadás
			rowBounds.minY = Math.min(rowBounds.minY,sy);
			rowBounds.maxY = Math.max(rowBounds.maxY,sy);
		}
		//rekurzívam meghívja a többi irányt, amit kell
		boolean isShort = (ldIcon instanceof LdIconShort) || (ldIcon instanceof LdIconByteInput);
		if(direction == 0){  //Fel,Le, (jobbra)
			retVal.addAll(search(PLC, sx, sy - 1, (byte)1,isForward,rowBounds));											//Fel
			retVal.addAll(search(PLC, sx, sy + 1, (byte)3,isForward,rowBounds));											//Le
			if(isShort && isForward) retVal.addAll(search(PLC, sx + 1, sy, (byte)0,isForward,rowBounds));			//Elõre
		}else if(direction == 1){  //Fel (jobbra/balra)
			retVal.addAll(search(PLC, sx, sy - 1, (byte)1,isForward,rowBounds));											//Fel
			if(isShort && isForward) retVal.addAll(search(PLC, sx + 1, sy, (byte)0,isForward,rowBounds));			//Elõre
			if(isShort && !isForward) retVal.addAll(search(PLC, sx - 1, sy, (byte)2,isForward,rowBounds));			//Hátra
		}else if(direction == 2){//Fel,Le, (balra)
			retVal.addAll(search(PLC, sx, sy - 1, (byte)1,isForward,rowBounds));											//Fel
			retVal.addAll(search(PLC, sx, sy + 1, (byte)3,isForward,rowBounds));											//Le
			if(isShort && !isForward) retVal.addAll(search(PLC, sx - 1, sy, (byte)2,isForward,rowBounds));			//Hátra
		}else if(direction == 3){  //Le (jobbra/balra)
			retVal.addAll(search(PLC, sx, sy + 1, (byte)3,isForward,rowBounds));											//Le
			if(isShort && isForward) retVal.addAll(search(PLC, sx + 1, sy, (byte)0,isForward,rowBounds));			//Elõre
			if(isShort && !isForward) retVal.addAll(search(PLC, sx - 1, sy, (byte)2,isForward,rowBounds));			//Hátra
		}

		return retVal;
	}
	
	
	public static class RowBounds{
		public int minY = Integer.MAX_VALUE;
		public int maxY = Integer.MIN_VALUE;
	}
	
}
