package net.ipar.mod.utilsPLC.LdIcons;


import java.util.ArrayList;
import java.util.List;

import net.ipar.mod.gui.GuiPLC;
import net.ipar.mod.tileEntity.TileEntityPLC;
import net.ipar.mod.utilsPLC.Address24bit;
import net.ipar.mod.utilsPLC.Address24bit.Groups;
import net.ipar.mod.utilsPLC.Instruction;
import net.ipar.mod.utilsPLC.Instruction.*;

public class LdIconCMP extends LdIconBase{

	public static final int cmd = 0x1D;
	public static final int textureXpos = 2;
	public static final int textureYpos = 1;
		
	public LdIconCMP(int x, int y) {
		super(textureXpos, textureYpos, (byte)cmd, x, y);
		this.address = new Address24bit();
	}
	public LdIconCMP(int value, int x, int y) {
		super(textureXpos, textureYpos,value, x, y);
	}
	
	@Override
	public void addKey(char c){
		c = Character.toUpperCase(c);
		if(c=='>') this.address.setAddress(1);
		if(c=='=') this.address.setAddress(2);
		if(c=='<') this.address.setAddress(3);
	}
	
	@Override
	public String toString(){
		if(address != null){
			if(address.getAddress() == 1) return " >";
			if(address.getAddress() == 2) return " =";
			if(address.getAddress() == 3) return " <";
		}
		return "";
	}
	
	@Override
	public boolean isValidIcon() {
		if(address.getAddress() >= 1 && address.getAddress() <= 3) return true;
		return false;
	}
	
	@Override
	public void addIconToTheArray(GuiPLC gui) {
		int m = findIcon(gui);
		int i = m % 12;
		int j = m / 12;
		this.x = i;
		this.y = j;
		if(i < 8){
			gui.PLC.ldIcon[i][j] = this;
			gui.PLC.ldIcon[i + 1][j] = new LdIconByteInput(false, true, true, true, i + 1, j);
			gui.PLC.ldIcon[i + 2][j] = new LdIconByteInput(true, false, false, false, i + 2, j);
			gui.PLC.ldIcon[i + 3][j] = new LdIconByteInput(false, true, true, true, i + 3, j);
			gui.PLC.ldIcon[i + 4][j] = new LdIconByteInput(true, false, false, false, i + 4, j);
			gui.changeSelection(gui.PLC.ldIcon[i+1][j]);
		}
	}
	
	@Override
	public void delIconFromTheArray(GuiPLC gui) {
		int m = findIcon(gui);
		int i = m % 12;
		int j = m / 12;
		int ii = i;
		gui.PLC.ldIcon[ii][j] = new LdIconEmpty(gui.PLC.ldIcon[ii][j].isUp, ii, j);
		ii++;
		while(gui.PLC.ldIcon[ii][j] instanceof LdIconByteInput){
			gui.PLC.ldIcon[ii][j] = new LdIconEmpty(false, ii, j);
			ii++;
		}
		gui.changeSelection(gui.PLC.ldIcon[i][j]);
	}
	
	@Override
	public List<Instruction> getInstructionList(TileEntityPLC PLC, int posX,int posY) {
		List<Instruction> lstInst = new ArrayList<Instruction>();
		lstInst.add(new net.ipar.mod.utilsPLC.Instruction(EnuStlCmd.CMP, this.address));
		lstInst.add(new net.ipar.mod.utilsPLC.Instruction(EnuStlCmd.ADR, PLC.ldIcon[posX + 1][posY].address));
		lstInst.add(new net.ipar.mod.utilsPLC.Instruction(EnuStlCmd.ADR, PLC.ldIcon[posX + 3][posY].address));
		return lstInst;
	}
	
}
