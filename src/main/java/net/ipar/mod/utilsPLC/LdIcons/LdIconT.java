package net.ipar.mod.utilsPLC.LdIcons;

import java.util.ArrayList;
import java.util.List;

import net.ipar.mod.gui.GuiPLC;
import net.ipar.mod.tileEntity.TileEntityPLC;
import net.ipar.mod.utilsPLC.Address24bit;
import net.ipar.mod.utilsPLC.Instruction;
import net.ipar.mod.utilsPLC.Instruction.EnuStlCmd;

public class LdIconT extends LdIconBase{

	public static final int cmd = 0x18;
	public static final int textureXpos = 8;
	public static final int textureYpos = 0;
	
	public static final int maxItem = 64;
	
	public LdIconT(int x, int y) {
		super(textureXpos, textureYpos, (byte)cmd, x, y);
		this.address = new Address24bit();
	}
	public LdIconT(int value, int x, int y) {
		super(textureXpos, textureYpos,value, x, y);
	}
	
	@Override
	public void addKey(char c){
		c = Character.toUpperCase(c);
		if(Character.isDigit(c)) super.addKey(c);
	}
	
	@Override
	public String toString(){
		return "T" + super.toString();
	}
	
	@Override
	public boolean isValidIcon() {
		if(address.getGroup() == null && address.getNumber() >=0 && address.getNumber() < maxItem) return true;
		return false;
	}
	
	@Override
	public void addIconToTheArray(GuiPLC gui) {
		int m = findIcon(gui);
		int i = m % 12;
		int j = m / 12;
		this.x = i;
		this.y = j;
		if(i < 10){
			gui.PLC.ldIcon[i][j] = this;
			gui.PLC.ldIcon[i + 1][j] = new LdIconByteInput(false, true, true, false, i + 1, j);
			gui.PLC.ldIcon[i + 2][j] = new LdIconByteInput(true, true, false, false, i + 1, j);
			gui.changeSelection(gui.PLC.ldIcon[i + 1][j]);
		}
	}
	
	@Override
	public void delIconFromTheArray(GuiPLC gui) {
		int m = findIcon(gui);
		int i = m % 12;
		int j = m / 12;
		
		gui.PLC.ldIcon[i][j] = new LdIconEmpty(gui.PLC.ldIcon[i][j].isUp, i, j);
		gui.PLC.ldIcon[i + 1][j] = new LdIconEmpty(false, i + 1, j);
		gui.PLC.ldIcon[i + 2][j] = new LdIconEmpty(false, i + 2, j);
		gui.changeSelection(gui.PLC.ldIcon[i][j]);
	}
	
	@Override
	public List<Instruction> getInstructionList(TileEntityPLC PLC, int posX,int posY) {
		List<Instruction> lstInst = new ArrayList<Instruction>();
		lstInst.add(new net.ipar.mod.utilsPLC.Instruction(EnuStlCmd.TMX, this.address));
		lstInst.add(new net.ipar.mod.utilsPLC.Instruction(EnuStlCmd.ADR, PLC.ldIcon[posX + 1][posY].address));
		return lstInst;
	}
	
}
