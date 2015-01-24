package net.ipar.mod.utilsPLC.LdIcons;

import java.util.ArrayList;
import java.util.List;

import net.ipar.mod.gui.GuiPLC;
import net.ipar.mod.tileEntity.TileEntityPLC;
import net.ipar.mod.utilsPLC.Address24bit;
import net.ipar.mod.utilsPLC.Address24bit.Groups;
import net.ipar.mod.utilsPLC.Instruction;
import net.ipar.mod.utilsPLC.Instruction.EnuStlCmd;

public class LdIconOUT extends LdIconBase{

	public static final int cmd = 0x10;
	public static final int textureXpos = 4;
	public static final int textureYpos = 0;
	
	public static final int maxItem = 64;
	
	public LdIconOUT(int x, int y) {
		super(textureXpos, textureYpos, (byte)cmd, x, y);
		this.address = new Address24bit();
	}
	public LdIconOUT(int value, int x, int y) {
		super(textureXpos, textureYpos,value, x, y);
	}
	
	@Override
	public void addKey(char c){
		c = Character.toUpperCase(c);
		if(Character.isDigit(c) || c == 'Y' || c =='R') super.addKey(c);
	}
	
	@Override
	public boolean isValidIcon() {
		Groups group = address.getGroup();
		if(group == Groups.Y || group == Groups.R){
			int num = address.getNumber();
			if(num >= 0 && num < maxItem && (num % 10) <= 8) return true;
		}
		return false;
	}
	
	@Override
	public void addIconToTheArray(GuiPLC gui) {
		int m = super.findIcon(gui);
		int j = m / 12;

		for(int i = m % 12;i < 11; i++){
			gui.PLC.ldIcon[i][j] = new LdIconShort(i, j);
		}
		gui.slotSelected = gui.PLC.ldIcon[11][j];
		super.addIconToTheArraySimple(gui);
	}
	
	@Override
	public void delIconFromTheArray(GuiPLC gui) {
		super.delIconFromTheArraySimple(gui);
		
	}
	
	@Override
	public List<Instruction> getInstructionList(TileEntityPLC PLC, int posX,int posY) {
		List<Instruction> lstInst = new ArrayList<Instruction>();
		lstInst.add(new net.ipar.mod.utilsPLC.Instruction(EnuStlCmd.OT, this.address));
		return lstInst;
	}
}
