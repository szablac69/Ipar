package net.ipar.mod.gui;

import io.netty.handler.codec.spdy.SpdyOrHttpChooser.SelectedProtocol;

import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.ipar.mod.Ipar;
import net.ipar.mod.container.ContainerPLC;
import net.ipar.mod.handler.MessagePLC;
import net.ipar.mod.tileEntity.TileEntityPLC;
import net.ipar.mod.utilsPLC.Address24bit;
import net.ipar.mod.utilsPLC.Address24bit.Groups;
import net.ipar.mod.utilsPLC.CompileLD2STL;
import net.ipar.mod.utilsPLC.IlNode;
import net.ipar.mod.utilsPLC.Instruction;
import net.ipar.mod.utilsPLC.IlNode.RowBounds;
import net.ipar.mod.utilsPLC.Instruction.EnuStlCmd;
import net.ipar.mod.utilsPLC.LdIcons.LdIconBase;
import net.ipar.mod.utilsPLC.LdIcons.LdIconByteInput;
import net.ipar.mod.utilsPLC.LdIcons.LdIconEmpty;
import net.ipar.mod.utilsPLC.LdIcons.LdIconShort;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;

public class GuiPLC extends GuiScreen{
public static final ResourceLocation bground = new ResourceLocation(Ipar.modid + ":" + "textures/gui/GuiPLC.png");
	
	public TileEntityPLC PLC;
	
    protected int xSize = 256 ;
    protected int ySize = 256 ;
    protected int editWindowX = 5;
    protected int editWindowY = 10;
    
    public final static int iconWidth = 20;
    public final static int iconHeight = 20;
    public final static int textureIconStartPosX = 5;
    public final static int textureIconStartPosY = 110;
    public final static int cmdIconStartPosX = 98/2;
    public final static int cmdIconStartPosY = 432/2;
    public final static int textureSelectionX = 25;
    public final static int textureSelectionY = 130;
    public final static int textureHightLightX = 5;
    public final static int textureHightLightY = 130;

    
    protected int guiLeft;
    protected int guiTop;
	

    //public List slotPLCItems = new ArrayList();
    //public slotPLCItem[][] slotPLCItems = new slotPLCItem[12][10];
    public LdIconBase slotSelected;
    public LdIconBase slotHighLight;
    public LdIconBase slotPLCEdit = new LdIconEmpty(false, -1, -1);
    public static final int slotEditX = 5;
    public static final int slotEditY = 213;

    //public int slotSelectedX, slotSelectedY;
  
    private int actFirstRow = 0;
    
    private GuiButton buttonCompile;
    
    public List<IlNode> lstNode = new ArrayList<IlNode>();
    
    public int lastMouseWheel;
   
    public GuiPLCCommandButton[][] buttonsCmd = new GuiPLCCommandButton[9][2];
    
    private boolean wasRepeatEventsEnabled;
    
	public GuiPLC(InventoryPlayer inventoryPlayer, TileEntityPLC entity) {
		super();
		this.PLC = entity;
		//request the LD software
		Ipar.network.sendToServer(new MessagePLC((TileEntityPLC) PLC,0x10));
	
		//iconSelect = new IconLD(1, 1);
		//changeSelection(PLC.ldIcon[0][0]);
	
	}

	
	public void initGui(){
		super.initGui();
		this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
		this.buttonList.add(this.buttonCompile = new GuiButton( 2, 20, 20, 60, 20, "Compile"));
        initButtonCmd();
        changeSelection(PLC.ldIcon[0][0]);
        wasRepeatEventsEnabled = Keyboard.areRepeatEventsEnabled();
        Keyboard.enableRepeatEvents(true);
	}
	
	@Override
	public void drawScreen(int x, int y, float f) {
		lastMouseWheel = Mouse.getDWheel();
		if(lastMouseWheel < 0){ScrollDwn();}
		if(lastMouseWheel > 0){ScrollUp();}
		
		//Ipar.network.sendToServer(new MessagePLC(PLC,0xC));
		//this.drawDefaultBackground();
		
		
		//Ez mi ez e?
		int k = this.guiLeft;
        int l = this.guiTop;
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        super.drawScreen(x, y, f);
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glPushMatrix();
        GL11.glTranslatef((float)k, (float)l, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        //Hmmm*/
        this.drawGuiBackgroundLayer(x, y, f);
        this.drawGuiForegroundLayer(x, y);

        GL11.glPopMatrix();
	}
	
    protected void drawGuiForegroundLayer(int x, int y) {
    	Minecraft.getMinecraft().getTextureManager().bindTexture(bground);
    	GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glColorMask(true, true, true, false);	
        GL11.glEnable(GL11.GL_BLEND);
        
        //FindHighLight
        x -= this.guiLeft;
		y -= this.guiTop;
		LdIconBase h = findIcon(x, y);
		if(h != null) slotHighLight = h;
		//Draw edit window Icons 
        for(int i = actFirstRow; i<actFirstRow + 10;i++){
	        for(int j = 0; j<12;j++){
	        	LdIconBase item = PLC.ldIcon[j][i];
	        	item.drawSlot(this,editWindowX +  j * iconWidth,editWindowY + (i - actFirstRow) * iconHeight);
	        	if(slotSelected == item){
	        		drawTexturedModalRect(editWindowX + j * iconWidth,editWindowY + (i - actFirstRow) * iconHeight, textureSelectionX, textureSelectionY, GuiPLC.iconWidth , GuiPLC.iconHeight);
	        	}
	        	if(slotHighLight == item){
	        		drawTexturedModalRect(editWindowX + j * iconWidth,editWindowY + (i - actFirstRow) * iconHeight, textureHightLightX, textureHightLightY, GuiPLC.iconWidth , GuiPLC.iconHeight);
	        	}
	        }
        }
       //Draw edit icon
        if(slotPLCEdit != null) slotPLCEdit.drawSlot(this,slotEditX,slotEditY);
        
        //Draw I/O
        for(short i = 0; i< 8; i++){
        	int colorOn = 0xFF119911;
        	int colorOff = 0x0FFAAAAAA;

        	drawRect(280, 50 + i * 20, 300, 50 + i * 20 + 15, Address24bit.getBit(Groups.X, 0, i, PLC) ? colorOn : colorOff);//X    	
        	drawRect(310, 50 + i * 20, 330, 50 + i * 20 + 15, Address24bit.getBit(Groups.Y, 0, i, PLC) ? colorOn : colorOff);//Y        	
        	this.fontRendererObj.drawString("X" + Integer.toString(i), 285, 54 + i * 20, 0xFF000000);
        	this.fontRendererObj.drawString("Y" + Integer.toString(i), 315, 54 + i * 20, 0xFF000000);
        }
        
        
        
        //DrawText (Because TEXTURE OVERLOAD)
        for(int i = actFirstRow; i<actFirstRow + 10;i++){
	        for(int j = 0; j<12;j++){
	        	// TODO slotPLCItems[j][i].drawText();  
        		fontRendererObj.drawString(PLC.ldIcon[j][i].toString(), editWindowX + j * iconWidth, editWindowY + (i - actFirstRow) * iconHeight, 4210752); 
        		fontRendererObj.drawString(PLC.ldIcon[j][i].toStringValue(PLC), editWindowX + j * iconWidth, editWindowY + (i - actFirstRow) * iconHeight, 0xF06000); 
	        }
        }
        //Draw edit icon text
        // TODO if(slotPLCEdit != null) slotPLCEdit.drawText();      
        if(slotPLCEdit != null) fontRendererObj.drawString(slotPLCEdit.toString(), slotEditX, slotEditY, 4210752); 
        
        this.fontRendererObj.drawString(Integer.toString(actFirstRow), 10, 1, 4210752);
               
        GL11.glDisable(GL11.GL_BLEND);
		GL11.glColorMask(true, true, true, true);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
  
    }
	
	protected void drawGuiBackgroundLayer(int x, int y, float f) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(bground);
		drawTexturedModalRect(0, 0, 0, 0, xSize , ySize );
		drawTexturedModalRect(5, 110, 5, 10, 240 , 100 );
		
		//Draw Command line
		drawCommandLine();
		/*for(IconLD icon : InitPLC.lstIconLD){
			if(icon.cmd != 0){
				drawTexturedModalRect(this.guiLeft + icon.getCmdX(), this.guiTop + icon.getCmdY(), icon.getX(), icon.getY(), slotPLCWidth , slotPLCHeight );
			}else{
				drawTexturedModalRect(this.guiLeft + icon.getCmdX() + 9, this.guiTop + icon.getCmdY() + 6, icon.getX(), icon.getY(), slotPLCWidth , slotPLCHeight - 6 );
			}
		}*/
	}
	private void initButtonCmd(){
		/*for(int i = 0;i<9;i++){
			buttonsCmd[i][0] = new GuiPLCCommandButton();
			buttonsCmd[i][1] = new GuiPLCCommandButton();
		}
		buttonsCmd[0][0] = new GuiPLCCommandButton(this, textureIconStartPosX, textureIconStartPosY + 5,new GuiPLCCommandButton.CommandNO());
		buttonsCmd[1][0] = new GuiPLCCommandButton(this, textureIconStartPosX + iconWidth * 1, textureIconStartPosY + 5,new GuiPLCCommandButton.CommandNC());
		buttonsCmd[2][0] = new GuiPLCCommandButton(this, textureIconStartPosX + iconWidth * 3 - 10, textureIconStartPosY + 5,null);
		buttonsCmd[3][0] = new GuiPLCCommandButton(this, textureIconStartPosX + iconWidth * 5, textureIconStartPosY + 5,new GuiPLCCommandButton.CommandShort());
	*/}
	public void drawCommandLine(){
		for(int i = 0;i<9;i++){
			buttonsCmd[i][0].draw(this,cmdIconStartPosX + (iconWidth + 2) * i,cmdIconStartPosY);
			buttonsCmd[i][1].draw(this,cmdIconStartPosX + (iconWidth + 2) * i,cmdIconStartPosY + 17);
		}
	}
	
	
	
	 /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char c, int keyCode)
    {
    	
    	//ESC + bindings
        if (keyCode == 1) // || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode())
        {
        	Keyboard.enableRepeatEvents(wasRepeatEventsEnabled);
        	Ipar.network.sendToServer(new MessagePLC(PLC,1));
        	this.mc.thePlayer.closeScreen();
        }

        //Function
        if(keyCode >= 59 && keyCode <= 68){
        	if(this.isShiftKeyDown()) buttonsCmd[keyCode - 59][1].execute(this);
        	else buttonsCmd[keyCode - 59][0].execute(this);
        }else{
        	if(slotPLCEdit != null) slotPLCEdit.addKey(c);
        }
        
        //Enter
        if(keyCode == 28 || keyCode == 156){	
        	if(slotPLCEdit.isValidIcon() && slotSelected != null){
        		slotPLCEdit.addIconToTheArray(this);
        	}
        }
        
        //Delete
        if(keyCode == 211){	
        	if(this.isShiftKeyDown()){
        		int sx = slotSelected.x;
            	int sy = slotSelected.y;
            	for(int row = sy; row < PLC.maxRow - 1;row++){
            		for(int col = 0; col < 12; col++){
            			PLC.ldIcon[col][row] = PLC.ldIcon[col][row + 1];
        				PLC.ldIcon[col][row].x = col;
        				PLC.ldIcon[col][row].y = row;
            		}
            	}
            	for(int col = 0; col < 12; col++){
        			PLC.ldIcon[col][PLC.maxRow - 1] = new LdIconEmpty(false, col, PLC.maxRow - 1);
        		}
            	changeSelection(PLC.ldIcon[sx][sy]);
        	}else{
        		if(slotSelected.isDeletable){
            		slotSelected.delIconFromTheArray(this);
            	}
        	}
        		
        	
        }
        
        //Arrows
        if(keyCode == 200 || keyCode == 203 || keyCode == 205 || keyCode == 208){
        	int m = LdIconBase.findIcon(this);
    		int i = m % 12;
    		int j = m / 12;
        	
    		if(keyCode == 200) j--;		//Up
    		if(keyCode == 208) j++;		//Down
    		if(keyCode == 203) i--;		//Left
    		if(keyCode == 205) i++;		//Right
    		
    		if(i >= 12) {i -= 12; j++;}
    		if(i < 0) {i += 12; j--;}
        	if(j < 0) j = 0;
        	if(j >= PLC.maxRow) j = PLC.maxRow - 1;
        	
        	if(j < actFirstRow) actFirstRow = j;
        	if(j >= actFirstRow + 10) actFirstRow = j - 9;
        	
        	changeSelection(PLC.ldIcon[i][j]);
        }
        
        //Insert
        if(keyCode == 210){
        	if(this.isShiftKeyDown()){
        		int sx = slotSelected.x;
            	int sy = slotSelected.y;
            	for(int row = PLC.maxRow - 1; row > sy;row--){
            		for(int col = 0; col < 12; col++){
            			PLC.ldIcon[col][row] = PLC.ldIcon[col][row - 1];
        				PLC.ldIcon[col][row].x = col;
        				PLC.ldIcon[col][row].y = row;
            		}
            	}
            	for(int col = 0; col < 12; col++){
        			PLC.ldIcon[col][sy] = new LdIconEmpty(false, col, sy);
        		}
            	changeSelection(PLC.ldIcon[sx][sy]);
        	}else{
        		int sx = slotSelected.x;
            	int sy = slotSelected.y;
            	if(!(slotSelected instanceof LdIconEmpty) && !(slotSelected instanceof LdIconShort) && !(slotSelected instanceof LdIconByteInput)){
            		for(int i = sx; i < 12; i++){
                		if(PLC.ldIcon[i][sy] instanceof LdIconEmpty || PLC.ldIcon[i][sy] instanceof LdIconShort){
                			for(int j = i; j > sx; j--){
                				PLC.ldIcon[j][sy] = PLC.ldIcon[j - 1][sy];
                				PLC.ldIcon[j][sy].x = j;
                				PLC.ldIcon[j][sy].y = sy;
                			}
                			PLC.ldIcon[sx][sy] = new LdIconShort(sx, sy);
                			changeSelection(PLC.ldIcon[sx][sy]);
                			break;
                		}
                	}
            	}
        	}
        	
        }
      //BackSpace
        if(keyCode == 14){
        	int sx = slotSelected.x;
        	int sy = slotSelected.y;
        	if(!(slotSelected instanceof LdIconEmpty) && !(slotSelected instanceof LdIconShort) && !(slotSelected instanceof LdIconByteInput)){
        		for(int i = sx; i >= 0; i--){
            		if(PLC.ldIcon[i][sy] instanceof LdIconEmpty || PLC.ldIcon[i][sy] instanceof LdIconShort){
            			for(int j = i; j < sx; j++){
            				PLC.ldIcon[j][sy] = PLC.ldIcon[j + 1][sy];
            				PLC.ldIcon[j][sy].x = j;
            				PLC.ldIcon[j][sy].y = sy;
            			}
            			//PLC.ldIcon[sx][sy] = new LdIconShort(sx, sy);
            			
            			int j = sx;
            			while(PLC.ldIcon[j+1][sy] instanceof LdIconByteInput){
            				PLC.ldIcon[j][sy] = PLC.ldIcon[j + 1][sy];
            				PLC.ldIcon[j][sy].x = j;
            				PLC.ldIcon[j][sy].y = sy;
            				j++;
            			}
            			PLC.ldIcon[j][sy] = new LdIconShort(j, sy);
            			changeSelection(PLC.ldIcon[sx][sy]);
            			break;
            		}
            	}
        	}
        }
        
        //TODO handle this
    }

    private void ScrollUp(){
    	actFirstRow--;
    	if(actFirstRow < 0) actFirstRow = 0;    
    }
    private void ScrollDwn(){
    	actFirstRow++;
    	if(actFirstRow > (PLC.maxRow - 10)) actFirstRow = PLC.maxRow - 10;
    }
    
    /**
     * Handles keyboard input.
     */
    //Override to remove system hotkeys
    @Override
    public void handleKeyboardInput()
    {
        if (Keyboard.getEventKeyState()) this.keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
    }
    
	protected void mouseClicked(int x, int y, int b){
		//slotPLCItem slotSelectedOriginal = slotSelected;
		super.mouseClicked(x, y, b);
		x -= this.guiLeft;
		y -= this.guiTop;
		//TODO handle selection
		
		//Check command button click
		if(x > cmdIconStartPosX && x < cmdIconStartPosX + 9 * (iconWidth +2) && y > cmdIconStartPosY && y < cmdIconStartPosY + 34){
			for(int i = 0; i < 9;i++){
				int left = cmdIconStartPosX + 2 + (iconWidth + 2) * i;
				int right = left + iconWidth;
				int top = cmdIconStartPosY + 2;
				int bottom = top + 15;
				if(x >= left && x <= right){
					if(y >= top && y <= bottom) buttonsCmd[i][0].execute(this);
					if(y >= top + 17 && y <= bottom + 17) buttonsCmd[i][1].execute(this);
				}
			}
		}
		changeSelection(findIcon(x, y));
		
	}

	
	public void changeSelection(LdIconBase newSelected){
		if(newSelected == null) return;
		slotSelected = newSelected;
		slotSelected.setCommadButtonBasic(this);
		slotPLCEdit = LdIconBase.load(slotSelected.getSaveInt(), -1, -1);
		slotPLCEdit.isUp = false;
	}
	
	private LdIconBase findIcon(int x, int y){
		int ix,iy;
		x -= editWindowX;
		y -= editWindowY;
		if(x >= 0 && x < 12 * iconWidth && y >= 0 && y < 10 * iconHeight){
			ix = x / iconWidth;
			iy = y / iconWidth;
			return PLC.ldIcon[ix][iy + actFirstRow];
		}
		
		return null;
	}
	
	public  void actionPerformed(GuiButton guiButton){
			
		if(guiButton.id == 2){		//Compile
			CompileLD2STL compiler = new CompileLD2STL(this);
			compiler.compile();
			
		}
		
		
	}
	
	@Override
	public boolean doesGuiPauseGame()
    {
        return false;
    }
}