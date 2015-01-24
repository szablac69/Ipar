package net.ipar.mod.gui;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ipar.mod.Ipar;
import net.ipar.mod.container.ContainerATM;
import net.ipar.mod.handler.MyMessage;
import net.ipar.mod.tileEntity.TileEntityATM;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiATM extends GuiContainer{

	public static final ResourceLocation bground = new ResourceLocation(Ipar.modid + ":" + "textures/gui/GuiATM.png");
	
	private TileEntityATM ATM;
	private GuiButton buttonStart;
	
	public GuiATM(InventoryPlayer inventoryPlayer, TileEntityATM entity) {
		super(new ContainerATM(inventoryPlayer, entity));
		
		this.ATM = entity;
		
		this.xSize = 176;
		this.ySize = 166;
	}

	public void initGui(){
		super.initGui();
        this.buttonList.add(this.buttonStart = new GuiButton( 1, this.guiLeft + 73, this.guiTop + 38, 30, 10, "SELL"));
	}
	
	public void drawGuiContainerForegroundLayer(int par1, int par2){
		//String name = this.alabasterOven.hasCustomInventoryName() ? this.alabasterOven.getInventoryName() : I18n.format(this.alabasterOven.getInventoryName(), new Object[0]);
		String name =  "ATM";
				
		this.fontRendererObj.drawString(name, this.xSize / 2 - this.fontRendererObj.getStringWidth(name) / 2, 6, 4210752);
		//this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]),118,this.ySize - 96 + 2,4210752);
		this.fontRendererObj.drawString(ATM.getItemNameOnSlot(), 10, 16, 4210752);
		
		
		
	}
	
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		
		GL11.glColor4f(1F, 1F, 1F, 1F);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(bground);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
	}
	
	protected void actionPerformed(GuiButton guiButton){
		if(guiButton.id == 1){
			Ipar.network.sendToServer(new MyMessage("S",ATM));
		}
		
		
	}

}
