package net.ipar.mod.gui;

import org.lwjgl.opengl.GL11;

import net.ipar.mod.Ipar;
import net.ipar.mod.container.ContainerBlastFurnace;
import net.ipar.mod.tileEntity.TileEntityBlastFurnace;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiBlastFurnace extends GuiContainer{

	public static final ResourceLocation bground = new ResourceLocation(Ipar.modid + ":" + "textures/gui/GuiBlastFurnace.png");
	
	TileEntityBlastFurnace BlastFurnace;
	
	public GuiBlastFurnace(InventoryPlayer inventoryPlayer, TileEntityBlastFurnace entity) {
		super(new ContainerBlastFurnace(inventoryPlayer, entity));
		
		this.BlastFurnace = entity;
		
		this.xSize = 175;
		this.ySize = 179;
	}

	
	public void drawGuiContainerForegroundLayer(int par1, int par2){
		//String name = this.alabasterOven.hasCustomInventoryName() ? this.alabasterOven.getInventoryName() : I18n.format(this.alabasterOven.getInventoryName(), new Object[0]);
		String name =  String.format("%.2f",BlastFurnace.furnaceTemp);
				
		this.fontRendererObj.drawString(String.format("%.2f",BlastFurnace.furnaceTemp), 170 - this.fontRendererObj.getStringWidth(name), 9, 4210752);
		
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		GL11.glColor4f(1F, 1F, 1F, 1F);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(bground);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		//System.out.println("GUI : " + Integer.toString(this.BlastFurnace.burnTime));
		if(this.BlastFurnace.isBurning()){
			int k = this.BlastFurnace.getBurnTimeRemainingScaled(66);
			//int j = 66 - k;
			drawTexturedModalRect(guiLeft + 53, guiTop + 78, 176, 0, k, 10);
		}
		int k = this.BlastFurnace.getFurnaceTempScaled(62);
		//drawTexturedModalRect(guiLeft + 64, guiTop + 70, 176, 74, 47, -40);
		drawTexturedModalRect(guiLeft + 119, guiTop + 7, 225, 11, 10, 62 - k);
		
		
		k = this.BlastFurnace.getSmeltingTempScaled(64);
		if(k == 0) k= 64;
		drawTexturedModalRect(guiLeft + 64, guiTop + 6, 176, 10, 47, k);
	
	}
}
