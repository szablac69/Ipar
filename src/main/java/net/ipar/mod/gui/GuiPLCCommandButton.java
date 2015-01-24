package net.ipar.mod.gui;

import net.ipar.mod.utilsPLC.LdIcons.LdIconBase;
import net.ipar.mod.utilsPLC.LdIcons.LdIconNC;
import net.ipar.mod.utilsPLC.LdIcons.LdIconNO;
import net.ipar.mod.utilsPLC.LdIcons.LdIconShort;

public class GuiPLCCommandButton {
	private final int textureX,textureY;
	private static final int textureWidth = 20;
	private static final int textureHeight = 15;
	private final int command; 
	
	
	public GuiPLCCommandButton(int textureX, int textureY,int commandMethod){
		this.textureX = textureX;
		this.textureY = textureY;
		this.command = commandMethod;
	}
	public GuiPLCCommandButton(){
		this.textureX = 0;
		this.textureY = 0;
		this.command = 0;
	}
	
	public void draw(GuiPLC gui, int x, int y){
		if(command != 0) gui.drawTexturedModalRect(x,y,textureX,textureY,textureWidth,textureHeight);
	}
	
	public void execute(GuiPLC gui){
		gui.slotPLCEdit.executeCommand(gui, command);
		//if(commandMethod != 0) commandMethod.execute(data);
	}
	
}
