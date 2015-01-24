package net.ipar.mod.utilsPLC.LdIcons;

import java.util.ArrayList;
import java.util.List;

import net.ipar.mod.gui.GuiPLC;
import net.ipar.mod.tileEntity.TileEntityPLC;
import net.ipar.mod.utilsPLC.Address24bit;
import net.ipar.mod.utilsPLC.Instruction;
import net.ipar.mod.utilsPLC.Instruction.EnuStlCmd;

public class LdIconDF extends LdIconBase{

	public static final int cmd = 0x04;
	public static final int textureXpos = 6;
	public static final int textureYpos = 0;
	
	
	public LdIconDF(int x, int y) {
		super(textureXpos, textureYpos, (byte)cmd, x, y);
		this.address = new Address24bit();
	}
	public LdIconDF(int value, int x, int y) {
		super(textureXpos, textureYpos,value, x, y);
	}
	
	@Override
	public void addKey(char c){	}
	
	@Override
	public boolean isValidIcon() {
		return true;
	}
	
	@Override
	public void addIconToTheArray(GuiPLC gui) {
		super.addIconToTheArraySimple(gui);
	}
	
	@Override
	public void delIconFromTheArray(GuiPLC gui) {
		super.delIconFromTheArraySimple(gui);	
	}
	
	@Override
	public List<Instruction> getInstructionList(TileEntityPLC PLC, int posX,int posY) {
		List<Instruction> lstInst = new ArrayList<Instruction>();
		lstInst.add(new net.ipar.mod.utilsPLC.Instruction(EnuStlCmd.DF, this.address));
		return lstInst;
	}
}
