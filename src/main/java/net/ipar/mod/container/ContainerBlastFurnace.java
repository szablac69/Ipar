package net.ipar.mod.container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ipar.mod.Ipar;
import net.ipar.mod.tileEntity.TileEntityBlastFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

public class ContainerBlastFurnace extends Container{

	TileEntityBlastFurnace blastFurnace;
	
	public float lastFurnaceTemp;
	//The remaining time of the fuel
	public int lastBurnTime;
	//The actual burning fuel max burnTime (Because scale)
	public int lastCurrentItemBurnTime;
	//The remaining smelting time
	public int lastSmeltingTime;
	
	public ContainerBlastFurnace(InventoryPlayer inventory, TileEntityBlastFurnace tileEntity) {
		System.out.println("Container Started");
		this.blastFurnace = tileEntity;
		
		this.addSlotToContainer(new IronSlot(tileEntity,TileEntityBlastFurnace.slotNrIron,8,9));
		this.addSlotToContainer(new CokeSlot(tileEntity,TileEntityBlastFurnace.slotNrCoke,8,31));
		this.addSlotToContainer(new LimeStoneSlot(tileEntity,TileEntityBlastFurnace.slotNrLimeStone,8,53));
		this.addSlotToContainer(new FuelSlot(tileEntity,TileEntityBlastFurnace.slotNrFuel,8,75));
		this.addSlotToContainer(new SlotFurnace(inventory.player, tileEntity,TileEntityBlastFurnace.slotNrSlag,152,28));
		this.addSlotToContainer(new SlotFurnace(inventory.player,tileEntity,TileEntityBlastFurnace.slotNrSteel,152,58));

		
		for(int i = 0;i < 3; i++){
			for(int j = 0; j< 9; j++){
				this.addSlotToContainer(new Slot(inventory,j + i*9 + 9, 8 + j*18, 98 + i*18));
			}
		}
		for(int i = 0;i < 9; i++){
			this.addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 156));
		}	
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		return true;
	}
	
	public void addCraftingToCrafters(ICrafting icrafting){
		super.addCraftingToCrafters(icrafting);
		icrafting.sendProgressBarUpdate(this, 0, Float.floatToIntBits(this.blastFurnace.furnaceTemp));
		icrafting.sendProgressBarUpdate(this, 1, this.blastFurnace.smeltingTime);
		icrafting.sendProgressBarUpdate(this, 2, this.blastFurnace.burnTime);
		icrafting.sendProgressBarUpdate(this, 3, this.blastFurnace.currentItemBurnTime);
	}
	
	public void detectAndSendChanges(){
		super.detectAndSendChanges();
		for(int i = 0; i < this.crafters.size() ; i++){
			ICrafting icrafting = (ICrafting)this.crafters.get(i);
			
			if(this.lastFurnaceTemp != this.blastFurnace.furnaceTemp){
				icrafting.sendProgressBarUpdate(this, 0, Float.floatToIntBits(this.blastFurnace.furnaceTemp));
			}
			if(this.lastSmeltingTime != this.blastFurnace.smeltingTime){
				icrafting.sendProgressBarUpdate(this, 1, this.blastFurnace.smeltingTime);
			}
			if(this.lastBurnTime != this.blastFurnace.burnTime){
				icrafting.sendProgressBarUpdate(this, 2, this.blastFurnace.burnTime);
			}
			if(this.lastCurrentItemBurnTime != this.blastFurnace.currentItemBurnTime){
				icrafting.sendProgressBarUpdate(this, 3, this.blastFurnace.currentItemBurnTime);
			}
			
		}
		this.lastFurnaceTemp = this.blastFurnace.furnaceTemp;
		this.lastSmeltingTime = this.blastFurnace.smeltingTime;
		this.lastBurnTime = this.blastFurnace.burnTime;
		this.lastCurrentItemBurnTime = this.blastFurnace.currentItemBurnTime;
		
	}
	
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int par1, int par2)
    {
		if (par1 == 0) {
            this.blastFurnace.furnaceTemp = Float.intBitsToFloat(par2);
        }
		
        if (par1 == 1) {
            this.blastFurnace.smeltingTime = par2;
        }

        if (par1 == 2) {
            this.blastFurnace.burnTime = par2;
        }

        if (par1 == 3) {
            this.blastFurnace.currentItemBurnTime = par2;
        }
    }
	
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(par2);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if(par2 >= 0 && par2 <= 5){
            	if (!this.mergeItemStack(itemstack1, 6, 42, true)) {
                    return null;
                }
            }
            else if(par2 >= 6 && par2 <= 42){
            	if(itemstack1.getItem() == Item.getItemFromBlock(Blocks.iron_ore)){
            		if (!this.mergeItemStack(itemstack1, TileEntityBlastFurnace.slotNrIron, TileEntityBlastFurnace.slotNrIron + 1, false))
                    {
                        return null;
                    }
            	}else if(itemstack1.getItem() == Ipar.itemCoke){
            		if (!this.mergeItemStack(itemstack1, TileEntityBlastFurnace.slotNrCoke - 1, TileEntityBlastFurnace.slotNrCoke, false))
                    {
                        return null;
                    }
            	}else if(itemstack1.getItem() == Item.getItemFromBlock(Ipar.blockLimeStone)){
            		if (!this.mergeItemStack(itemstack1, TileEntityBlastFurnace.slotNrLimeStone + 1, TileEntityBlastFurnace.slotNrLimeStone + 2, false))
                    {
                        return null;
                    }
            	}else if(TileEntityBlastFurnace.isItemFuel(itemstack)){
            		if (!this.mergeItemStack(itemstack1, TileEntityBlastFurnace.slotNrFuel, TileEntityBlastFurnace.slotNrFuel + 1, false))
                    {
                        return null;
                    }
            	}
            }
            /*if (par2 == 2) {
                if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (par2 != 1 && par2 != 0) {
                if (FurnaceRecipes.smelting().getSmeltingResult(itemstack1) != null) {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false))
                    {
                        return null;
                    }
                }else if (TileEntityAlabasterOven.isItemFuel(itemstack1)) {
                    if (!this.mergeItemStack(itemstack1, 1, 2, false))
                    {
                        return null;
                    }
                }else if (par2 >= 3 && par2 < 30){
                    if (!this.mergeItemStack(itemstack1, 30, 39, false)){
                        return null;
                    }
                }else if (par2 >= 30 && par2 < 39 && !this.mergeItemStack(itemstack1, 3, 30, false)) {
                    return null;
                }
            } else if (!this.mergeItemStack(itemstack1, 3, 39, false)) {
                return null;
            }*/

            if (itemstack1.stackSize == 0) {
                slot.putStack((ItemStack)null);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
        }

        return itemstack;
    }
	
	
	class IronSlot extends Slot
    {
        public IronSlot(IInventory p_i1801_2_, int p_i1801_3_, int p_i1801_4_, int p_i1801_5_) {super(p_i1801_2_, p_i1801_3_, p_i1801_4_, p_i1801_5_);}
        public boolean isItemValid(ItemStack itemStack){return itemStack != null && itemStack.getItem() != null && itemStack.getItem() ==  Item.getItemFromBlock(Blocks.iron_ore);}
    }
	class CokeSlot extends Slot
    {
        public CokeSlot(IInventory p_i1801_2_, int p_i1801_3_, int p_i1801_4_, int p_i1801_5_) {super(p_i1801_2_, p_i1801_3_, p_i1801_4_, p_i1801_5_);}
        public boolean isItemValid(ItemStack itemStack){return itemStack != null && itemStack.getItem() != null && itemStack.getItem() == Ipar.itemCoke;}
    }	
	class LimeStoneSlot extends Slot
    {
        public LimeStoneSlot(IInventory p_i1801_2_, int p_i1801_3_, int p_i1801_4_, int p_i1801_5_) {super(p_i1801_2_, p_i1801_3_, p_i1801_4_, p_i1801_5_);}
        public boolean isItemValid(ItemStack itemStack){return itemStack != null && itemStack.getItem() != null && itemStack.getItem() == Item.getItemFromBlock(Ipar.blockLimeStone);}
    }
	class FuelSlot extends Slot
    {
        public FuelSlot(IInventory p_i1801_2_, int p_i1801_3_, int p_i1801_4_, int p_i1801_5_) {super(p_i1801_2_, p_i1801_3_, p_i1801_4_, p_i1801_5_);}
        public boolean isItemValid(ItemStack itemStack){return itemStack != null && itemStack.getItem() != null &&  TileEntityBlastFurnace.isItemFuel(itemStack);}
    }
}
