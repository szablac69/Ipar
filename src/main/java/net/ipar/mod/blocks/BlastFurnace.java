package net.ipar.mod.blocks;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ipar.mod.Ipar;
import net.ipar.mod.tileEntity.TileEntityBlastFurnace;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlastFurnace extends BlockContainer{

	public BlastFurnace() {
		super(Material.iron);
		
		this.setHardness(3.0F);
		this.setResistance(5.0F);
		this.setStepSound(soundTypeStone);
		this.setCreativeTab(Ipar.iparTab);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		
		return new TileEntityBlastFurnace();
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		this.blockIcon = iconRegister.registerIcon(Ipar.modid + ":" + this.getUnlocalizedName().substring(5));
	}
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		if(!world.isRemote){
			FMLNetworkHandler.openGui(player, Ipar.instance, Ipar.guiIDBlastFurnace, world, x, y, z);
			//player.mc.displayGuiScreen(new GuiBeacon(this.inventory, p_146104_1_));
		}
		return true;
	}
}
