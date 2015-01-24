package net.ipar.mod.handler;

import cpw.mods.fml.common.network.IGuiHandler;
import net.ipar.mod.Ipar;
import net.ipar.mod.container.ContainerATM;
import net.ipar.mod.container.ContainerBlastFurnace;
import net.ipar.mod.container.ContainerPLC;
import net.ipar.mod.gui.GuiATM;
import net.ipar.mod.gui.GuiBlastFurnace;
import net.ipar.mod.gui.GuiPLC;
import net.ipar.mod.tileEntity.TileEntityATM;
import net.ipar.mod.tileEntity.TileEntityBlastFurnace;
import net.ipar.mod.tileEntity.TileEntityPLC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler{
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,	int x, int y, int z) {
		TileEntity entity = world.getTileEntity(x, y, z);		
		
		if(entity != null){
			switch(ID){
			case Ipar.guiIDATM:
				if(entity instanceof TileEntityATM){
					return  new ContainerATM(player.inventory, (TileEntityATM)entity);
				}
				return null;
			case Ipar.guiIDBlastFurnace:
				if(entity instanceof TileEntityBlastFurnace){
					return  new ContainerBlastFurnace(player.inventory, (TileEntityBlastFurnace)entity);
				}
				return null;
			case Ipar.guiIDPLC:
				if(entity instanceof TileEntityPLC){
					return  new ContainerPLC(player.inventory, (TileEntityPLC)entity);
				}
				return null;
			}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,	int x, int y, int z) {
		TileEntity entity = world.getTileEntity(x, y, z);
		
		if(entity != null){
			switch(ID){
			case Ipar.guiIDATM:
				if(entity instanceof TileEntityATM){
					return new GuiATM(player.inventory, (TileEntityATM)entity);
				}
				return null;
			case Ipar.guiIDBlastFurnace:
				if(entity instanceof TileEntityBlastFurnace){
					return new GuiBlastFurnace(player.inventory, (TileEntityBlastFurnace)entity);
				}
				return null;

			case Ipar.guiIDPLC:
				if(entity instanceof TileEntityPLC){
					return new GuiPLC(player.inventory, (TileEntityPLC)entity);
				}
				return null;
			}
		}
		return null;
	}

}
