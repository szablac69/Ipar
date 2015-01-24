package net.ipar.mod.utilsPLC.LdIcons;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.ipar.mod.gui.GuiPLC;
import net.ipar.mod.gui.GuiPLCCommandButton;
import net.ipar.mod.tileEntity.TileEntityPLC;
import net.ipar.mod.utilsPLC.Address24bit;
import net.ipar.mod.utilsPLC.Instruction;
import net.minecraft.nbt.NBTTagCompound;

public abstract class LdIconBase {
	
	protected final int textureXgridUp = 3,textureYgridUp = 0;
	protected final int textureXposUp,textureYposUp;
	
	protected final int textureXpos,textureYpos;
	protected final byte cmd;
	public boolean isDeletable = true;
	public boolean isEcexutable = true;
	public boolean isUp = false;
	protected Address24bit address;
	public int x,y;
	
	
	
	public LdIconBase(int textureXgrid, int textureYgrid, byte cmd, int x, int y){
		this.textureXpos = textureXgrid * GuiPLC.iconWidth + GuiPLC.textureIconStartPosX;
		this.textureYpos = textureYgrid * GuiPLC.iconHeight + GuiPLC.textureIconStartPosY;
		this.textureXposUp = textureXgridUp * GuiPLC.iconWidth + GuiPLC.textureIconStartPosX;
		this.textureYposUp = textureYgridUp * GuiPLC.iconHeight + GuiPLC.textureIconStartPosY;
		
		this.x = x;
		this.y = y;

		this.cmd = (byte) (cmd & 0x7F);
		if((cmd & 0x80) != 0) isUp = true;
	}
	
	public LdIconBase(int textureXgrid, int textureYgrid, int loadData, int x, int y){
		this.textureXpos = textureXgrid * GuiPLC.iconWidth + GuiPLC.textureIconStartPosX;
		this.textureYpos = textureYgrid * GuiPLC.iconHeight + GuiPLC.textureIconStartPosY;
		this.textureXposUp = textureXgridUp * GuiPLC.iconWidth + GuiPLC.textureIconStartPosX;
		this.textureYposUp = textureYgridUp * GuiPLC.iconHeight + GuiPLC.textureIconStartPosY;
		
		this.x = x;
		this.y = y;
	
		this.cmd = (byte) ((loadData >> 24) & 0x7F);
		if(((loadData >> 24) & 0x80) != 0) isUp = true;
		this.address = new Address24bit(loadData);
	}

	public void drawSlot(GuiPLC gui, int x, int y){
		if(isUp) gui.drawTexturedModalRect(x, y - 7, textureXposUp, textureYposUp, GuiPLC.iconWidth , GuiPLC.iconHeight);
		if(cmd == 0) return;
		if(cmd == LdIconNO.cmd || cmd == LdIconOUT.cmd){
			if(address.getBit(gui.PLC)) GL11.glColorMask(false, true, false, false);
		}
		if(cmd == LdIconNC.cmd){
			if(!address.getBit(gui.PLC)) GL11.glColorMask(false, true, false, false);
		}
		gui.drawTexturedModalRect(x, y, textureXpos, textureYpos, GuiPLC.iconWidth , GuiPLC.iconHeight);
		GL11.glColorMask(true, true, true, false);
		//if(item.isUp()) drawTexturedModalRect(this.x, this.y - 7, InitPLC.lstIconLD.get(2).getX(), InitPLC.lstIconLD.get(2).getY(), slotPLCWidth , slotPLCHeight);
	}
	
		
	public int getSaveInt(){
		return (cmd << 24) | (address == null ? 0 : address.address()) | (isUp ? 0x80000000 : 0) ;
	}
	
	public static LdIconBase load(int loadValue, int x, int y){
		byte cmd = (byte) ((loadValue >> 24) & 0x7F);
		
		if((cmd & LdIconByteInput.maskcmd) == LdIconByteInput.cmd) return new LdIconByteInput(loadValue, x, y);
		
		switch (cmd){
			case LdIconNO.cmd : return new LdIconNO(loadValue, x, y);
			case LdIconNC.cmd : return new LdIconNC(loadValue, x, y);
			case LdIconOUT.cmd : return new LdIconOUT(loadValue, x, y);
			case LdIconShort.cmd : return new LdIconShort(loadValue, x, y);
			case LdIconT.cmd : return new LdIconT(loadValue, x, y);
			case LdIconF.cmd : return new LdIconF(loadValue, x, y);
			case LdIconDF.cmd : return new LdIconDF(loadValue, x, y);
			case LdIconCMP.cmd : return new LdIconCMP(loadValue, x, y);
			default : return new LdIconEmpty(((loadValue >> 24) & 0x80) != 0, x, y);
		}
		
	}
	
	public void addKey(char c){
		if(Character.isDigit(c)) address.addNumber(Character.getNumericValue(c));
		if(Character.isAlphabetic(c)) address.addChar(c);
	}
	
	/*public void drawText(GuiPLC gui, int x, int y){
		String s = address.toString();
		gui.fontRendererObj.drawString(s, x, y, 4210752); 
	}*/
	
	public String toString(){
		if(address != null && address.getAddress() != 0) return address.toString();
		return "";
	}
	public String toStringValue(TileEntityPLC tilePLC){
		if(this.cmd == LdIconT.cmd){
			if(address != null) return address.toStringValueT(tilePLC);
		}else{
			if(address != null && address.getAddress() != 0) return address.toStringValue(tilePLC);
		}
		
		return "";
	}
	public void setCommadButtonBasic(GuiPLC gui){
		for(int i = 0;i<9;i++){
			gui.buttonsCmd[i][0] = new GuiPLCCommandButton();
			gui.buttonsCmd[i][1] = new GuiPLCCommandButton();
		}
		gui.buttonsCmd[0][0] = new GuiPLCCommandButton(gui.textureIconStartPosX, gui.textureIconStartPosY + 5, 1);							//NO
		gui.buttonsCmd[1][0] = new GuiPLCCommandButton(gui.textureIconStartPosX + gui.iconWidth * 1, gui.textureIconStartPosY + 5,2);			//NC
		gui.buttonsCmd[2][0] = new GuiPLCCommandButton(gui.textureIconStartPosX + gui.iconWidth * 3 - 10, gui.textureIconStartPosY + 5,3);	//Up
		gui.buttonsCmd[3][0] = new GuiPLCCommandButton(gui.textureIconStartPosX + gui.iconWidth * 4, gui.textureIconStartPosY + 5,4);			//OUT
		gui.buttonsCmd[7][0] = new GuiPLCCommandButton(gui.textureIconStartPosX + gui.iconWidth * 5, gui.textureIconStartPosY + 5,8);			//SHORT
		gui.buttonsCmd[2][1] = new GuiPLCCommandButton(gui.textureIconStartPosX + gui.iconWidth * 6, gui.textureIconStartPosY + 5,12);			//DF
		gui.buttonsCmd[4][0] = new GuiPLCCommandButton(gui.textureIconStartPosX + gui.iconWidth * 8, gui.textureIconStartPosY + 5,5);			//T
		gui.buttonsCmd[5][0] = new GuiPLCCommandButton(gui.textureIconStartPosX + gui.iconWidth * 11, gui.textureIconStartPosY + 5,6);			//F
		gui.buttonsCmd[3][1] = new GuiPLCCommandButton(gui.textureIconStartPosX + gui.iconWidth * 2, gui.textureIconStartPosY + 25,13);			//CMP
	}
	
	public void executeCommand(GuiPLC gui, int command){
		switch(command){
			case 1: gui.slotPLCEdit = new LdIconNO(-1, -1);break;
			case 2: gui.slotPLCEdit = new LdIconNC(-1, -1);break;
			case 3: gui.slotSelected.changeUp();break;
			case 4: gui.slotPLCEdit = new LdIconOUT(-1, -1);break;
			case 5: gui.slotPLCEdit = new LdIconT(-1, -1);break;
			case 6: gui.slotPLCEdit = new LdIconF(-1, -1);break;
			case 8: gui.slotPLCEdit = new LdIconShort(-1, -1);break;
			case 12: gui.slotPLCEdit = new LdIconDF(-1, -1);break;
			case 13: gui.slotPLCEdit = new LdIconCMP(-1, -1);break;
		}
	}
	
	public abstract boolean isValidIcon();
	
	public abstract void addIconToTheArray(GuiPLC gui);
	
	public abstract void delIconFromTheArray(GuiPLC gui);
		
	//return the row * col of the slotSelected
	public static int findIcon(GuiPLC gui){
		for(int i = 0; i<12; i++){
			for(int j = 0; j < gui.PLC.maxRow; j++){
				if(gui.PLC.ldIcon[i][j] == gui.slotSelected){
					return j * 12 + i;
				}
			}
		}
		return -1;
	}
	
	protected void addIconToTheArraySimple(GuiPLC gui){
		int m = findIcon(gui);
		int i = m % 12;
		int j = m / 12;
		
		addIconToTheArraySimple(gui, i, j);
	}
	
	protected void addIconToTheArraySimple(GuiPLC gui, int i, int j){
		this.isUp = gui.PLC.ldIcon[i][j].isUp;
		this.x = i;
		this.y = j;
		gui.PLC.ldIcon[i][j] = this;
		i++;
		if(i >= 12) {
			i -= 12;
			j++;
			if(j >= gui.PLC.maxRow) j = gui.PLC.maxRow;
		}
		gui.changeSelection(gui.PLC.ldIcon[i][j]);
		
	}
	
	protected void delIconFromTheArraySimple(GuiPLC gui){
		int m = findIcon(gui);
		int i = m % 12;
		int j = m / 12;
		
		gui.PLC.ldIcon[i][j] = new LdIconEmpty(gui.PLC.ldIcon[i][j].isUp, i, j);
		gui.changeSelection(gui.PLC.ldIcon[i][j]);
	}
	
	public void changeUp(){
		isUp = !isUp;
	}
	
	public abstract List<Instruction> getInstructionList(TileEntityPLC PLC, int posX,int posY);
	
}
