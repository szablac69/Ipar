package net.ipar.mod.utilsPLC.LdIcons;

import java.util.ArrayList;
import java.util.List;

import net.ipar.mod.gui.GuiPLC;
import net.ipar.mod.tileEntity.TileEntityPLC;
import net.ipar.mod.utilsPLC.Instruction;
import net.ipar.mod.utilsPLC.Instruction.EnuStlCmd;

public class LdIconShort extends LdIconBase{
	public static final int cmd = 0x06;
	public static final int textureXpos = 5;
	public static final int textureYpos = 0;
	
	
	public LdIconShort(int x, int y) {
		super(textureXpos, textureYpos, (byte)cmd, x, y);
		this.isEcexutable = false;
	}
	public LdIconShort(int value, int x, int y) {
		super(textureXpos, textureYpos, value, x, y);
		this.isEcexutable = false;
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
		return null;
	}
	
}
