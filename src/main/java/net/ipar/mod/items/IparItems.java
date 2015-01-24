package net.ipar.mod.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ipar.mod.Ipar;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;

public class IparItems extends Item {
	
	public IparItems(){
		setCreativeTab(Ipar.iparTab);
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister){
		this.itemIcon = iconRegister.registerIcon(Ipar.modid + ":" + this.getUnlocalizedName().substring(5));
	}
}
