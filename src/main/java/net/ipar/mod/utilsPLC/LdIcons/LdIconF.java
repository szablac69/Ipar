package net.ipar.mod.utilsPLC.LdIcons;

import java.util.ArrayList;
import java.util.List;

import net.ipar.mod.gui.GuiPLC;
import net.ipar.mod.tileEntity.TileEntityPLC;
import net.ipar.mod.utilsPLC.Address24bit;
import net.ipar.mod.utilsPLC.Instruction;
import net.ipar.mod.utilsPLC.Instruction.EnuStlCmd;

public class LdIconF extends LdIconBase{
	/**
	 *  1 - move
	 */
	public static final int cmd = 0x1C;
	public static final int textureXpos = 11;
	public static final int textureYpos = 0;
	
	public static final int maxItem = 200;
	
	public LdIconF(int x, int y) {
		super(textureXpos, textureYpos, (byte)cmd, x, y);
		this.address = new Address24bit();
	}
	public LdIconF(int value, int x, int y) {
		super(textureXpos, textureYpos,value, x, y);
	}
	
	@Override
	public void addKey(char c){
		c = Character.toUpperCase(c);
		if(Character.isDigit(c)) super.addKey(c);
	}
	
	@Override
	public String toString(){
		return "F" + super.toString();
	}
	
	@Override
	public boolean isValidIcon() {
		if(address.getGroup() == null){
			int i = address.getNumber();
			if(i == 1) return true;		//mov
			if(i == 35) return true;	//inc
		}
		return false;
	}
	
	@Override
	public void addIconToTheArray(GuiPLC gui) {
		int m = findIcon(gui);
		int i = m % 12;
		int j = m / 12;
		int fun = address.getNumber();
		this.x = i;
		this.y = j;
		if(fun == 1){
			if(i < 8){
				gui.PLC.ldIcon[i][j] = this;
				gui.PLC.ldIcon[i + 1][j] = new LdIconByteInput(false, true, true, false, i + 1, j);
				gui.PLC.ldIcon[i + 2][j] = new LdIconByteInput(true, false, false, false, i + 2, j);
				gui.PLC.ldIcon[i + 3][j] = new LdIconByteInput(false, false, true, true, i + 3, j);
				gui.PLC.ldIcon[i + 4][j] = new LdIconByteInput(true, false, false, false, i + 4, j);
				gui.changeSelection(gui.PLC.ldIcon[i+1][j]);
			}
		}
		if(fun == 35){
			if(i < 10){
				gui.PLC.ldIcon[i][j] = this;
				gui.PLC.ldIcon[i + 1][j] = new LdIconByteInput(false, false, true, true, i + 1, j);
				gui.PLC.ldIcon[i + 2][j] = new LdIconByteInput(true, false, false, false, i + 2, j);
				gui.changeSelection(gui.PLC.ldIcon[i+1][j]);
			}
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
		if(address.getNumber() == 1){
			lstInst.add(new net.ipar.mod.utilsPLC.Instruction(EnuStlCmd.FUN, this.address));
			lstInst.add(new net.ipar.mod.utilsPLC.Instruction(EnuStlCmd.ADR, PLC.ldIcon[posX + 1][posY].address));
			lstInst.add(new net.ipar.mod.utilsPLC.Instruction(EnuStlCmd.ADR, PLC.ldIcon[posX + 3][posY].address));
		}
		if(address.getNumber() == 35){
			lstInst.add(new net.ipar.mod.utilsPLC.Instruction(EnuStlCmd.FUN, this.address));
			lstInst.add(new net.ipar.mod.utilsPLC.Instruction(EnuStlCmd.ADR, PLC.ldIcon[posX + 1][posY].address));
		}
		return lstInst;
	}
}
