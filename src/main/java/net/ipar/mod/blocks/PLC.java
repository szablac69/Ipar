package net.ipar.mod.blocks;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mrtjp.projectred.api.IBundledEmitter;
import mrtjp.projectred.api.IBundledTileInteraction;
import mrtjp.projectred.api.IConnectable;
import mrtjp.projectred.api.ITransmissionAPI;
import net.ipar.mod.Ipar;
import net.ipar.mod.handler.MessagePLC;
import net.ipar.mod.tileEntity.TileEntityPLC;
import net.ipar.mod.utilsPLC.Instruction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class PLC extends BlockContainer  {
	
	@SideOnly(Side.CLIENT)
	private IIcon iconFront;
	
	@SideOnly(Side.CLIENT)
	private IIcon iconLeft;
	
	@SideOnly(Side.CLIENT)
	private IIcon iconEmpty;
	
	public PLC() {
		super(Material.iron);
		
		this.setHardness(3.0F);
		this.setResistance(5.0F);
		this.setStepSound(soundTypeStone);
		this.setCreativeTab(Ipar.iparTab);
	
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		
		return new TileEntityPLC();
	}

	
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		this.blockIcon = iconRegister.registerIcon(Ipar.modid + ":" + "PLC_base");
		this.iconFront = iconRegister.registerIcon(Ipar.modid + ":" + "PLC_front");
		this.iconLeft = iconRegister.registerIcon(Ipar.modid + ":" + "PLC_left");
		this.iconEmpty = iconRegister.registerIcon(Ipar.modid + ":" + "PLC_empty");
	}
	/**
	 * 0 - Bottom
	 * 1 - TOP
	 * 3 - Front?
	 */
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int metadata){
		int metaLeft = metadata == 2 ? 5 : metadata == 5 ? 3 : metadata == 3 ? 4 : metadata == 4 ? 2 :0;
		if(metadata == 0 && side == 3) return this.iconFront;
		if(metadata == 0 && side == 4) return this.iconLeft;
		if(side == metadata){
			return iconEmpty;//this.iconFront;
		}else if(side == metaLeft){
			return this.iconLeft;
		}else{
			return this.blockIcon;
		}
		
		/*switch(side){
			case 2: return this.iconFront;
			default: return this.blockIcon;
		}*/
		
		
		//return metadata == 0 && side == 3 ? this.iconFront : side == 1 ? this.iconTop : (side == 0 ? this.iconTop : (side == metadata ? this.iconFront : this.blockIcon));

	}

	public void onBlockAdded(World world, int x, int y, int z){
		//System.out.println("onBlockAdded");
		super.onBlockAdded(world, x, y, z);
		//this.setDefaultDirection(world,x,y,z);
		
	}
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityplayer, ItemStack itemStack){
		int l = MathHelper.floor_double((double)(entityplayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
	
		if(l == 0) world.setBlockMetadataWithNotify(x, y, z, 2, 2);
		if(l == 1) world.setBlockMetadataWithNotify(x, y, z, 5, 2);
		if(l == 2) world.setBlockMetadataWithNotify(x, y, z, 3, 2);
		if(l == 3) world.setBlockMetadataWithNotify(x, y, z, 4, 2);
		
		
	}
	
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		if(!world.isRemote){
			//Ipar.network.sendToServer(new MessagePLC((TileEntityPLC) world.getTileEntity(x, y, z),9));
			FMLNetworkHandler.openGui(player, Ipar.instance, Ipar.guiIDPLC, world, x, y, z);
			
			//player.mc.displayGuiScreen(new GuiBeacon(this.inventory, p_146104_1_));
		}
		return true;
	}
	
	public void onNeighborBlockChange(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_)
    {
        if (!p_149695_1_.isRemote)
        {
        	p_149695_1_.addBlockEvent(p_149695_2_, p_149695_3_, p_149695_4_, this, 1, 1);
        }
    }
	
	
	private boolean isIndirectlyPowered(World p_150072_1_, int p_150072_2_, int p_150072_3_, int p_150072_4_, int p_150072_5_)
    {
        return p_150072_5_ != 0 && p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_ - 1, p_150072_4_, 0) ? true : (p_150072_5_ != 1 && p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_ + 1, p_150072_4_, 1) ? true : (p_150072_5_ != 2 && p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_, p_150072_4_ - 1, 2) ? true : (p_150072_5_ != 3 && p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_, p_150072_4_ + 1, 3) ? true : (p_150072_5_ != 5 && p_150072_1_.getIndirectPowerOutput(p_150072_2_ + 1, p_150072_3_, p_150072_4_, 5) ? true : (p_150072_5_ != 4 && p_150072_1_.getIndirectPowerOutput(p_150072_2_ - 1, p_150072_3_, p_150072_4_, 4) ? true : (p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_, p_150072_4_, 0) ? true : (p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_ + 2, p_150072_4_, 1) ? true : (p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_ + 1, p_150072_4_ - 1, 2) ? true : (p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_ + 1, p_150072_4_ + 1, 3) ? true : (p_150072_1_.getIndirectPowerOutput(p_150072_2_ - 1, p_150072_3_ + 1, p_150072_4_, 4) ? true : p_150072_1_.getIndirectPowerOutput(p_150072_2_ + 1, p_150072_3_ + 1, p_150072_4_, 5)))))))))));
    }
	
	private boolean isPowered(World world,int x,int y,int z){
		
        return world.getBlockPowerInput(x - 1, y, z) == 0 ? false : true;
		
	}
	@Override
    public boolean isOpaqueCube(){
        return false;
    }
	
	@Override
	public boolean renderAsNormalBlock()
    {
        return false;
    }

}
