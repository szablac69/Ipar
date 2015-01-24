package net.ipar.mod.utilsPLC;

import java.util.EnumSet;

import net.ipar.mod.tileEntity.TileEntityPLC;

public class Address24bit {
	
	 public static enum Groups {
	    	X("X",0x400,0),Y("Y",0x800,64),R("R",0xC00,128),T("T",0x2000,192),K("K",0x2800,0),B("B",0x1000,0),
	    	BX("BX",0x1400,0),BY("BY",0x1800,64),BR("BR",0x1C00,128),DT("DT",0x2400,328),F("F",0x2800,0);
	    	
	    	private final String value;
	    	private final int adrPrefix; 
	    	private final int startAdr;
	    	Groups(String value, int adrPrefix,int startAdr){
	    		this.value = value; 
	    		this.adrPrefix = adrPrefix;
	    		this.startAdr = startAdr;
	    	}
	    	public String value() {return value;}
	    	public int adrPrefix() {return adrPrefix;}
	    	public int startAdr() {return startAdr;}
	    };
	
	
	private int address;		//Max 24 bit
	private final static int maskNumber = 0x3FF;
	private final static int maskNotNumber = 0x00FFFC00;
	/*public final static int groupX = 0x400;
	public final static int groupY = 0x800;
	public final static int groupR = 0xC00;
	public final static int groupB = 0x1000;
	public final static int groupT = 0x2000;
	public final static int groupK = 0x2800;*/
	
	public Address24bit(int loadData) {
		address = loadData & 0x00FFFFFF;
	}
	public Address24bit() {
		address = 0;
	}

	public int address(){
		return address & 0x00FFFFFF;
	}
	
	public void addNumber(int i){
		if(i >= 0 && i <= 9){
			address = (((address & maskNumber) * 10 + i) % 1000) | (address & maskNotNumber);
		}
	}
	public void setAddress(int i){
		address = i;
	}
	
	public void addChar(char c){
		c = Character.toUpperCase(c);
		switch(c){
			case 'X':	address = Groups.X.adrPrefix | (address & maskNumber); break;
			case 'Y':	address = Groups.Y.adrPrefix | (address & maskNumber); break;
			case 'R':	address = Groups.R.adrPrefix | (address & maskNumber); break;
			case 'T':	address = Groups.T.adrPrefix | (address & maskNumber); break;
			case 'K':	address = Groups.K.adrPrefix | (address & maskNumber); break;
			case 'D':	address = Groups.DT.adrPrefix | (address & maskNumber); break;
			case 'B':	
				Groups g = this.getGroup();
				if(g == Groups.X ||  g == Groups.Y || g == Groups.R) address = address ^ Groups.B.adrPrefix;
				break;
		}
	}
	
	
	public String toString(){
		String s = "";
		Groups g = findGroup();
		if(g != null) s = g.toString(); 
		s += Integer.toString(address & maskNumber);
		return s;
	}
	
	public String toStringValue(TileEntityPLC tilePLC){
		String s = "";
		Groups g = findGroup();
		if(g != null && (g == Groups.BX || g == Groups.BY || g == Groups.BR || g == Groups.DT)) s = String.format("%9d", this.getByte(tilePLC));  //Integer.toString(this.getByte(tilePLC)); 
		return s;
	}
	public String toStringValueT(TileEntityPLC tilePLC){
		String s = "";
		s = String.format("%14d", this.getByteT(tilePLC));  //Integer.toString(this.getByte(tilePLC)); 
		return s;
	}
	
	public int getAddress(){return address & 0x00FFFFFF;}
	
	//public int getGroup(){return address & maskNotNumber;}
	public Groups getGroup(){return findGroup();}
	
	
	public int getNumber(){return address & maskNumber;}
	
	public Groups findGroup(){
		int adr = address & maskNotNumber;
		for(Groups g : EnumSet.allOf(Groups.class)){
			if(g.adrPrefix() == adr){
				return g;
			}
		}
		return null;
	}
	
	public boolean getBit(TileEntityPLC tilePLC){
		Groups g = findGroup();
		if (g != null && (g == Groups.X || g == Groups.Y || g == Groups.R || g == Groups.T)){
			int bitPos = address & 0x07;
			int bytePos = (address & 0x7F) >> 3;
			return (tilePLC.memoryMap[bytePos + g.startAdr()] & (1 << bitPos)) != 0;	
		}
		return false;
	}
	public static boolean getBit(Groups group, int byteAdr, int bitAdr, TileEntityPLC tilePLC){
		if(bitAdr > 7 || bitAdr < 0) return false;
		if(byteAdr > 63 || byteAdr < 0) return false;
		return new Address24bit(group.adrPrefix | (byteAdr << 3) | bitAdr).getBit(tilePLC);		
	}
	
	public void setBit(TileEntityPLC tilePLC, boolean newValue){
		Groups g = findGroup();
		if(g != null && (g == Groups.X || g == Groups.Y || g == Groups.R || g == Groups.T)){
			int bitPos = address & 0x07;
			int bytePos = (address & 0x7F) >> 3;
			if(newValue){
				tilePLC.memoryMap[bytePos + g.startAdr()] |= (byte) (1 << bitPos);
				return;
			}else{			
				tilePLC.memoryMap[bytePos + g.startAdr()] &= ~((byte) (1 << bitPos));
				return;			
			}
		}
		
	}
	public void setBitT(TileEntityPLC tilePLC, boolean newValue){
		int bitPos = address & 0x07;
		int bytePos = (address & 0x7F) >> 3;
		if(newValue){
			tilePLC.memoryMap[bytePos + Groups.T.startAdr()] |= (byte) (1 << bitPos);
			return;
		}else{			
			tilePLC.memoryMap[bytePos + Groups.T.startAdr()] &= ~((byte) (1 << bitPos));
			return;			
		}
	}
	
	public static void setBit(Groups group, short byteAdr, short bitAdr, TileEntityPLC tilePLC, boolean newValue){
		if(bitAdr > 7 || bitAdr < 0) return;
		if(byteAdr > 63 || byteAdr < 0) return;
		new Address24bit(group.adrPrefix | (byteAdr << 3)| bitAdr).setBit(tilePLC, newValue);		
	}
	
	public byte getByte(TileEntityPLC tilePLC){
		Groups g = findGroup();
		/*if (g != null && (g == Groups.T)){
			int bytePos = (address & 0x7F) * 2;
			return tilePLC.memoryMap[bytePos + 200];
		}*/
		if (g != null && (g == Groups.K)){
			return (byte) (address & 0xFF);
		}
		if (g != null && (g == Groups.BX || g == Groups.BY || g == Groups.BR || g == Groups.DT)){
			int bytePos = (address & 0x7F);
			return tilePLC.memoryMap[bytePos + g.startAdr()];
		}
		return 0;
	}
	public static byte getByte(Groups group, short byteAdr, TileEntityPLC tilePLC){	
		if(byteAdr > 63 || byteAdr < 0) return 0;
		return new Address24bit(group.adrPrefix | (byteAdr)).getByte(tilePLC);	
	}
	
	public byte getByteT(TileEntityPLC tilePLC){
		int bytePos = (address & 0x7F) * 2;
		return tilePLC.memoryMap[bytePos + 200];
	}
	
	public void setByte(TileEntityPLC tilePLC, byte newValue){
		Groups g = findGroup();
		if (g != null && (g == Groups.BX || g == Groups.BY || g == Groups.BR || g == Groups.DT)){
			int bytePos = (address & 0x7F);
			tilePLC.memoryMap[bytePos + g.startAdr()] = newValue;
		}
	}
	public static void setByte(Groups group, short byteAdr, TileEntityPLC tilePLC, byte newValue){
		if(byteAdr > 63 || byteAdr < 0) return;
		new Address24bit(group.adrPrefix | (byteAdr)).setByte(tilePLC, newValue);		
	}
	public void setByteT(TileEntityPLC tilePLC, byte newValue){
		int bytePos = (address & 0x7F) * 2;
		tilePLC.memoryMap[bytePos + 200] = newValue;
	}
	
}
