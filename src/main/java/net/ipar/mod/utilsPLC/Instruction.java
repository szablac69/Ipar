package net.ipar.mod.utilsPLC;

import net.ipar.mod.tileEntity.TileEntityPLC;
import net.ipar.mod.utilsPLC.LdIcons.LdIconBase;


public class Instruction {
	public static enum EnuStlCmd {
    	ST(1,"ST",false),STN(2,"ST/",false),AN(3,"AN",false),ANN(4,"AN/",false),OR(5,"OR",false),ORN(6,"OR/",false),OT(7,"OT",true),
    	DF(16,"DF",false),DF_ON(17,"DF",false),DFN(18,"DF/",false),DFN_ON(19,"DF/",false),
    	ANS(20,"ANS",false),ORS(21,"ORS",false),
    	TMX(10,"T",true),TMX_ON(11,"T",true),
    	FUN(99,"F",true),
    	ADR(98,"ADR",false),
    	CMP(97,"CMP",false);
    	
    	final short value;
    	private final String name;
    	private final boolean isOutput;
    	
    	private EnuStlCmd(int value, String name,boolean isOutput) {
    		this.value = (short) value;
    		this.name = name;
    		this.isOutput = isOutput;
    	}
    	public short value(){ return this.value;}
    	public String toString(){return name;}
    	public boolean isOutput(){return isOutput;}
    };

	public static boolean timerFlag = false;
	//private char instruction;
	public Address24bit adr;
	public EnuStlCmd cmd;
	
	public static Instruction prevInst1;
	public static Instruction prevInst2;
	
	public Instruction(EnuStlCmd cmd){
		this.adr = new Address24bit();
		this.cmd = cmd;
	}
	
	public Instruction(EnuStlCmd cmd, Address24bit adr){
		this.cmd = cmd;
		this.adr = adr;
	}
	
	public Instruction(int adr, short cmd){
		this.cmd = findEnuCmd(cmd);
		this.adr = new Address24bit(adr);
	}
	
	
	public EnuStlCmd findEnuCmd(short cmd) {
		for(EnuStlCmd e: EnuStlCmd.values()) {
			if(e.value == cmd) {
		      return e;
		    }
		 }
		 return null;// not found
	}
	
	@SuppressWarnings("incomplete-switch")
	public void executeInstruction(TileEntityPLC PLC){
		//enuStlCmd stlCmd = getEnuStlCmd();
		if(cmd != null  && cmd != EnuStlCmd.ADR){
			prevInst1 = null;
			prevInst2 = null;
			switch(cmd){
				case ST: {
					PLC.accu <<= 1;
					PLC.accu |= adr.getBit(PLC) ? 1 : 0;
					break;
				}
				case STN:{
					PLC.accu <<= 1;
					PLC.accu |= adr.getBit(PLC) ? 0 : 1;
					break;
				}
				case AN:{
					PLC.accu &= adr.getBit(PLC) ? 0xFFFF : 0xFFFE;
					break;
				}
				case ANN:{
					PLC.accu &= adr.getBit(PLC) ? 0xFFFE : 0xFFFF;
					break;
				}
				case OR:{
					PLC.accu |= adr.getBit(PLC) ? 1 : 0;
					break;
				}
				case ORN:{
					PLC.accu |= adr.getBit(PLC) ? 0 : 1;
					break;
				}
				case OT:{
					adr.setBit(PLC, (PLC.accu & 0x01) == 1);
					PLC.accu >>= 1;
					break;
				}
				case DF:{
					boolean v = (PLC.accu & 0x01) == 1;
					if(v) cmd = EnuStlCmd.DF_ON;
					break;
				}
				case DF_ON:{
					boolean v = (PLC.accu & 0x01) == 1;
					if(!v) cmd = EnuStlCmd.DF;
					PLC.accu &= 0xFFFFFFFE;
					break;
				}
				case DFN:{
					boolean v = (PLC.accu & 0x01) == 1;
					if(v) cmd = EnuStlCmd.DFN_ON;
					PLC.accu &= 0xFFFFFFFE;
					break;
				}
				case DFN_ON:{
					boolean v = (PLC.accu & 0x01) == 1;
					if(v) PLC.accu &= 0xFFFFFFFE;
					else{
						cmd = EnuStlCmd.DFN;
						PLC.accu |= 0x1;
					}
					break;
				}
				case ANS:{
					boolean flag = (PLC.accu & 1) == 1;
					PLC.accu >>= 1;
					PLC.accu &= flag ? 0xFFFF : 0xFFFE;	
					break;
				}
				case ORS:{
					boolean flag = (PLC.accu & 1) == 1;
					PLC.accu >>= 1;
					PLC.accu |= flag ? 1 : 0;	
					break;
				}
				case TMX:{
					adr.setBitT(PLC, false);		//Elõsször is kapcsoljuk ki
					boolean v = (PLC.accu & 0x01) == 1;
					PLC.accu >>= 1;
					if(v) {
						cmd = EnuStlCmd.TMX_ON;
						timerFlag = true;
					}
					prevInst1 = this;
					break;
				}
				case TMX_ON:{
					boolean flag = (PLC.accu & 1) == 1;
					PLC.accu >>= 1;
					if(!flag) {
						cmd = EnuStlCmd.TMX;
						adr.setBitT(PLC, false);	
						break;
					}
					prevInst1 = this;
					break;
				}
				case FUN:{
					prevInst1 = this;
				}
				case CMP:{
					prevInst1 = this;
				}
			}
		}else{
			if(prevInst1 != null && prevInst1.cmd == EnuStlCmd.TMX_ON){
				if (timerFlag){
					prevInst1.adr.setByteT(PLC, adr.getByte(PLC));
					timerFlag = false;
				}
				byte value = prevInst1.adr.getByteT(PLC);
				if(value == 0){
					prevInst1.adr.setBitT(PLC, true);
					PLC.accu <<= 1;
					PLC.accu |= 1;
				}else{
					value--;
					prevInst1.adr.setByteT(PLC,value);
					PLC.accu <<= 1;
				}
				prevInst1 = null;
		//Funk
			}else if(prevInst1 != null && prevInst1.cmd == EnuStlCmd.CMP){
				if(prevInst2 == null) prevInst2 = this;
				else{
					boolean flag = false;
					if((PLC.accu & 0x01) == 1){
						if(prevInst1.adr.getAddress() == 1) flag = (prevInst2.adr.getByte(PLC) > this.adr.getByte(PLC));
						if(prevInst1.adr.getAddress() == 2) flag = (prevInst2.adr.getByte(PLC) == this.adr.getByte(PLC));
						if(prevInst1.adr.getAddress() == 3) flag = (prevInst2.adr.getByte(PLC) < this.adr.getByte(PLC));
					}
					PLC.accu &= flag ? 0xFFFF : 0xFFFE;	
					prevInst1 = prevInst2 = null;
				}
			}else if(prevInst1 != null && prevInst1.cmd == EnuStlCmd.FUN){
				if(prevInst2 == null){
					if(prevInst1.adr.getNumber() == 1) prevInst2 = this;
					if(prevInst1.adr.getNumber() == 35){
						if((PLC.accu & 0x01) == 1) this.adr.setByte(PLC, (byte) (this.adr.getByte(PLC) + 1));
						prevInst1 = prevInst2 = null;
					}
				}else{
					if(prevInst1.adr.getNumber() == 1){
						if((PLC.accu & 0x01) == 1) this.adr.setByte(PLC, prevInst2.adr.getByte(PLC));
						prevInst1 = prevInst2 = null;
					}
					
				}
				
				
			}
			
			/*else if(prevInst.cmd == EnuStlCmd.FUN && prevInst.adr.getSave() == 0x1C23){		//Fun35
				if((PLC.accu & 1) == 1)	this.adr.setByte(PLC, (byte) (this.adr.getByte(PLC) + 1));
			}else if(prevInst.getEnuStlCmd() == enuStlCmd.FUN && prevInst.adr.getSave() == 0x1C06){  //F6
				if(prevInst2 == null) {prevInst2 = this;}
				else{
					if((PLC.accu & 1) == 1)	this.adr.setByte(PLC, (byte) (prevInst2.adr.getByte(PLC)));
					prevInst = null;prevInst2 = null;
				}
			}*/
		}
		
 
	}
	
	/**
	 * 4(cmd) - 2(space) - 4(group/rigth justified - address
 	 * 
	 */
	public String toString(){
		String retVal;
		retVal = cmd.toString();
		if(adr != null) retVal += "\t" + adr.toString();
		
		return retVal;
		
	}
	
	/**
	 * 
	 * @param prevInst  A következõ utasítás
	 * @return true, ha lehet törölni az elemet (Az összevonás megtörtént)
	 */
	public boolean contractInstruction(Instruction prevInst){
		//ST és ANS összevonás
		if((prevInst.cmd == EnuStlCmd.ST) && (this.cmd == EnuStlCmd.ANS)){prevInst.cmd = EnuStlCmd.AN; return true;}
		if((prevInst.cmd == EnuStlCmd.STN) && (this.cmd == EnuStlCmd.ANS)){prevInst.cmd = EnuStlCmd.ANN; return true;}
		//ST és ORS összevonás
		if((prevInst.cmd == EnuStlCmd.ST) && (this.cmd == EnuStlCmd.ORS)){prevInst.cmd = EnuStlCmd.OR; return true;}
		if((prevInst.cmd == EnuStlCmd.STN) && (this.cmd == EnuStlCmd.ORS)){prevInst.cmd = EnuStlCmd.ORN; return true;}
		return false;
	}
	
	/*private void setAddress(Address adr){
		this.adr = adr;
	}*/
		
}
