package net.ipar.mod.blocks;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ipar.mod.Ipar;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class IparBlocks extends Block{

	public IparBlocks(Material material) {
		super(material);
		
		this.setHardness(3.0F);
		this.setResistance(5.0F);
		this.setStepSound(soundTypeStone);
		this.setCreativeTab(Ipar.iparTab);
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		this.blockIcon = iconRegister.registerIcon(Ipar.modid + ":" + this.getUnlocalizedName().substring(5));
	}

}
