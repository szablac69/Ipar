package net.ipar.mod;

import net.ipar.mod.blocks.ATM;
import net.ipar.mod.blocks.BlastFurnace;
import net.ipar.mod.blocks.IparBlocks;
import net.ipar.mod.blocks.PLC;
import net.ipar.mod.handler.GuiHandler;
import net.ipar.mod.handler.MessagePLC;
import net.ipar.mod.handler.MyMessage;
import net.ipar.mod.items.CreditCard;
import net.ipar.mod.items.IparItems;
import net.ipar.mod.tileEntity.TileEntityATM;
import net.ipar.mod.tileEntity.TileEntityBlastFurnace;
import net.ipar.mod.tileEntity.TileEntityPLC;
import net.ipar.mod.utilsPLC.PLCrenderer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = Ipar.modid, version = Ipar.version)
public class Ipar {
	public static final String modid = "Ipar";
	public static final String version = "alpha v0.1";
	
	public static CreativeTabs iparTab;
	

	public static Item itemCreditCard;
	public static Item itemCoke;
	public static Item itemSlag;
	public static Item itemSteel;
	
	
	public static Block blockATM;
	public static final int guiIDATM = 0;
	public static Block blockBlastFurnace;
	public static final int guiIDBlastFurnace = 1;
	public static Block blockLimeStone;
	public static Block blockPLC;
	public static final int guiIDPLC = 2;
	
	
	public static SimpleNetworkWrapper network;
	
	@Instance(modid)
	public static Ipar instance;
	
	
	@EventHandler
	public void PreInit(FMLPreInitializationEvent preEvent){
		Configuration config = new Configuration(preEvent.getSuggestedConfigurationFile());
		
		iparTab = new CreativeTabs("Ipar") {
			@SideOnly(Side.CLIENT)
			public Item getTabIconItem(){
				return Items.iron_ingot;
			}		
		};
		
		//Initialization
		
		//Items
		itemCreditCard = new CreditCard().setUnlocalizedName("CreditCard");
		itemCoke = new IparItems().setUnlocalizedName("Coke");
		itemSlag = new IparItems().setUnlocalizedName("Slag");
		itemSteel = new IparItems().setUnlocalizedName("Steel");
		
		//Blocks
		blockATM = new ATM().setBlockName("ATM");
		blockBlastFurnace = new BlastFurnace().setBlockName("BlastFurnace");
		blockLimeStone = new IparBlocks(Material.rock).setBlockName("LimeStone");
		blockPLC = new PLC().setBlockName("PLC");
		//Registers
		
		//Items
		GameRegistry.registerItem(itemCreditCard,"CreditCard");
		GameRegistry.registerItem(itemCoke, "Coke");
		GameRegistry.registerItem(itemSlag, "Slag");
		GameRegistry.registerItem(itemSteel, "Steel");
		
		//Blocks
		GameRegistry.registerBlock(blockATM, "ATM");
		GameRegistry.registerBlock(blockBlastFurnace, "BlastFurnace");
		GameRegistry.registerBlock(blockLimeStone, "LimeStone");
		GameRegistry.registerBlock(blockPLC, "PLC");
		
		//Network
		network = NetworkRegistry.INSTANCE.newSimpleChannel(modid);
	    network.registerMessage(MyMessage.Handler.class, MyMessage.class, 0, Side.SERVER);
	    network.registerMessage(MessagePLC.Handler.class, MessagePLC.class, 1, Side.SERVER);
	    network.registerMessage(MessagePLC.Handler.class, MessagePLC.class, 2, Side.CLIENT);
	    
	    
	}
	
	@EventHandler
	public void Init(FMLInitializationEvent event){
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPLC.class, new PLCrenderer());
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		
		GameRegistry.registerTileEntity(TileEntityATM.class, "ATM");		
		GameRegistry.registerTileEntity(TileEntityBlastFurnace.class, "BlastFurnace");
		GameRegistry.registerTileEntity(TileEntityPLC.class, "PLC");
		
		//Smelting
		GameRegistry.addSmelting(Items.coal, new ItemStack(itemCoke), 0);
	}
	
	@EventHandler
	public void PostInit(FMLPostInitializationEvent postEvent){
		
	}
}
