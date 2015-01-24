package net.ipar.mod.container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ipar.mod.tileEntity.TileEntityPLC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class ContainerPLC extends Container{

private TileEntityPLC PLC;
	
	public ContainerPLC(InventoryPlayer inventory, TileEntityPLC tileEntity) {
		System.out.println("Container Started");
		this.PLC = tileEntity;

		
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		return true;
	}

}
