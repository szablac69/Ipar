package net.ipar.mod.container;

import net.ipar.mod.items.CreditCard;
import net.ipar.mod.tileEntity.TileEntityATM;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntity;

public class ContainerATM extends Container{

	private TileEntityATM ATM;
	
	public ContainerATM(InventoryPlayer inventory, TileEntityATM tileEntity) {
		System.out.println("Container Started");
		this.ATM = tileEntity;
		
		this.addSlotToContainer(new Slot(tileEntity,0,45,35));
		this.addSlotToContainer(new CreditCardSlot(tileEntity,1,117,35));
		
		
		for(int i = 0;i < 3; i++){
			for(int j = 0; j< 9; j++){
				this.addSlotToContainer(new Slot(inventory,j + i*9 + 9, 8 + j*18, 84 + i*18));
			}
		}
		for(int i = 0;i < 9; i++){
			this.addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));
		}
		
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return true;
	}

	
    class CreditCardSlot extends Slot
    {
        private static final String __OBFID = "CL_00001736";

        public CreditCardSlot(IInventory p_i1801_2_, int p_i1801_3_, int p_i1801_4_, int p_i1801_5_)
        {
            super(p_i1801_2_, p_i1801_3_, p_i1801_4_, p_i1801_5_);
        }

        /**
         * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
         */
        public boolean isItemValid(ItemStack itemStack)
        {
            return itemStack != null && itemStack.getItem() != null && itemStack.getItem() instanceof CreditCard;
        }

        /**
         * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the
         * case of armor slots)
         */
        public int getSlotStackLimit()
        {
            return 1;
        }
    }
	
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(par2);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (par2 == 0) {
                if (!this.mergeItemStack(itemstack1, 2, 38, true)) {
                    return null;
                }
            }else if(par2 == 1){
            	if (!this.mergeItemStack(itemstack1, 2, 38, true)) {
                    return null;
                }
            
            }else {
            	if(TileEntityATM.isCreditCard(itemstack1)){
            		if (!this.mergeItemStack(itemstack1, 1, 2, false))
            		{
                        return null;
                    }
            	}else{
            		if (!this.mergeItemStack(itemstack1, 0, 1, false))
            		{
                        return null;
                    }
            	}
            }
            
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
}
