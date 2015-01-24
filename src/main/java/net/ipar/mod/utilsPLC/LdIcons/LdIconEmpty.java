package net.ipar.mod.utilsPLC.LdIcons;

import java.util.List;

import net.ipar.mod.gui.GuiPLC;
import net.ipar.mod.tileEntity.TileEntityPLC;
import net.ipar.mod.utilsPLC.Instruction;

public class LdIconEmpty extends LdIconBase{

	public static final int cmd = 0;
	
	public LdIconEmpty(boolean isUp, int x, int y) {
		super(0, 0, cmd, x, y);
		this.isEcexutable = false;
		this.isUp = isUp;
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
	public void delIconFromTheArray(GuiPLC gui) {}
	
	@Override
	public List<Instruction> getInstructionList(TileEntityPLC PLC, int posX,int posY) {
		return null;
	}
}
