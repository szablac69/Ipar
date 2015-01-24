package net.ipar.mod.utilsPLC.LdIcons;

import java.util.ArrayList;
import java.util.List;

import net.ipar.mod.gui.GuiPLC;
import net.ipar.mod.tileEntity.TileEntityPLC;
import net.ipar.mod.utilsPLC.Address24bit;
import net.ipar.mod.utilsPLC.Address24bit.Groups;
import net.ipar.mod.utilsPLC.Instruction;
import net.ipar.mod.utilsPLC.Instruction.EnuStlCmd;

public class LdIconByteInput extends LdIconBase{

	public static final int cmd = 0x40;
	public static final int maskcmd = 0x40;
	public static final int maskFronEnd = 0x01;
	public static final int maskKonstansEnable = 0x02;
	public static final int maskEnable = 0x04;
	public static final int maskNotKonstansEnable = 0x08;
	public static final int textureXposFront = 9;
	public static final int textureYposFront = 0;
	public static final int textureXposEnd = 10;
	public static final int textureYposEnd = 0;
	
	public boolean isKonsatnsEnable;
	public boolean isNotKonsatnsEnable;
	public boolean isEnable;
	
	public LdIconByteInput(boolean isEnd, boolean isKonstansEnable, boolean isEnable, boolean isNotKonsatnsEnable, int x, int y) {
		super(isEnd ? textureXposEnd : textureXposFront, isEnd ? textureYposEnd : textureYposFront,
				(byte) ((cmd | (isEnd ? maskFronEnd : 0)) | (isKonstansEnable ? maskKonstansEnable : 0) | 
															(isEnable ? maskEnable : 0) | 
															(isNotKonsatnsEnable ? maskNotKonstansEnable : 0)),x,y);
		this.address = new Address24bit();
		this.isDeletable = false;
		this.isKonsatnsEnable = isKonstansEnable;
		this.isEnable = isEnable;
		this.isNotKonsatnsEnable = isNotKonsatnsEnable;
	}
	public LdIconByteInput(int value, int x, int y) {		
		super(((value >> 24) & maskFronEnd) == 0 ? textureXposFront : textureXposEnd,
				((value >> 24) & maskFronEnd) == 0 ? textureYposFront : textureYposEnd,value,x,y);
		this.isDeletable = false;
		this.isKonsatnsEnable = ((value >> 24) & maskKonstansEnable) != 0;
		this.isEnable = ((value >> 24) & maskEnable) != 0;
		this.isNotKonsatnsEnable = ((value >> 24) & maskNotKonstansEnable) != 0;
	}
	
	@Override
	public void addKey(char c){
		if(isEnable){
			c = Character.toUpperCase(c);
			if(Character.isDigit(c)) super.addKey(c);
			if(isNotKonsatnsEnable && (c == 'X' ||  c == 'Y' ||  c == 'R' ||  c == 'B' ||  c == 'D')) super.addKey(c);
			if(isKonsatnsEnable && c == 'K') super.addKey(c);
		}
	}
	
	@Override
	public boolean isValidIcon() {
		Groups group = address.getGroup();
		if(isKonsatnsEnable){
			if(group == Groups.K){
				int num = address.getNumber();
				if(num >= 0 && num < 255) return true;
			}
		}
		if(isNotKonsatnsEnable){
			if(group == Groups.BX || group == Groups.BY || group == Groups.BR || group == Groups.DT){
				int num = address.getNumber();
				if(num >= 0 && num < 64) return true;
			}	
		}
		return false;
	}
	
	@Override
	public void addIconToTheArray(GuiPLC gui) {
		if(gui.slotPLCEdit instanceof LdIconByteInput){
			super.addIconToTheArraySimple(gui);
		}
		
	}
	
	@Override
	public void delIconFromTheArray(GuiPLC gui) {
		super.delIconFromTheArraySimple(gui);
		
	}
	
	@Override
	public List<Instruction> getInstructionList(TileEntityPLC PLC, int posX,int posY) {
		List<Instruction> lstInst = new ArrayList<Instruction>();
		//lstInst.add(new net.ipar.mod.utilsPLC.Instruction(null, this.address));
		return lstInst;
	}
	
}
