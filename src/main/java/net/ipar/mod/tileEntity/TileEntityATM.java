package net.ipar.mod.tileEntity;

import io.netty.buffer.ByteBuf;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.google.common.base.Charsets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ipar.mod.Ipar;
import net.ipar.mod.handler.MyMessage;
import net.ipar.mod.items.CreditCard;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.tileentity.TileEntity;
import static io.netty.buffer.Unpooled.*;
import cpw.mods.fml.common.FMLCommonHandler;

public class TileEntityATM extends TileEntity implements ISidedInventory{

	public TileEntityATM(){
		super();
		System.out.println("ATM entyty sreated");

	}
	private ItemStack[] slots = new ItemStack[2];
	
	public String getItemNameOnSlot(){
		if(slots[1] == null){
			return "No Item Inserted";
		}else{
			//Item item = slots[0].getItem();

			if(isCreditCard(slots[1])) {
				return "Credit : " + Integer.toString(CreditCard.getCredit(slots[1]));//Integer.toString(((CreditCard)item).credit);
			}
			return "No Cradit Card inserted";
		}
	}
	
	@Override
	public int getSizeInventory() {
		return this.slots.length;
	}

	@Override
	public ItemStack getStackInSlot(int var1) {
		return this.slots[var1];
	}

	@Override
	public ItemStack decrStackSize(int var1, int var2) {
		
		if(this.slots[var1] != null){
			ItemStack itemStack;
			
			if(this.slots[var1].stackSize <= var2){
				itemStack = this.slots[var1]; 
				this.slots[var1] = null;
				return itemStack;
			}else{
				itemStack = this.slots[var1].splitStack(var2);
				
				if(this.slots[var1].stackSize == 0) this.slots[var1] = null;
				return itemStack;
			}
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		if(this.slots[i] != null){
			ItemStack itemStack = this.slots[i];
			this.slots[i] = null;
			return itemStack;
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemStack) {
		System.out.println("Valimka");
		
		this.slots[i] = itemStack;
		
		if(itemStack != null && itemStack.stackSize > this.getInventoryStackLimit()){
			itemStack.stackSize = this.getInventoryStackLimit();
		}
		
		
	}
	
	public void sell(){
		//Elvileg csak a SERVER oldalon hajtódik végre
		if(this.slots[0] == null || this.slots[1] == null) return;
		int sellValue = 0;
		if(this.slots[1].getItem() instanceof CreditCard){
			if(this.slots[0].getItem() == Items.iron_ingot) sellValue = 1;
			
			if(sellValue > 0){
				CreditCard.setCredit(this.slots[1], CreditCard.getCredit(this.slots[1]) +  sellValue * this.slots[0].stackSize);
				this.slots[0]= null;
			}

		}

	}
	
	 
	 
	public static boolean isCreditCard(ItemStack itemStack){
		if(itemStack == null) return false;
		Item item =itemStack.getItem();
		return isCreditCard(item);
	}
	public static boolean isCreditCard(Item item){
		if(item instanceof CreditCard) return true;
		return false;
	}
	
	@Override
	public String getInventoryName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasCustomInventoryName() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : entityPlayer.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeInventory() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemStack) {

		return true;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemStack, int j) {
		return this.isItemValidForSlot(i, itemStack);
	}

	@Override
	public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_,
			int p_102008_3_) {
		// TODO Auto-generated method stub
		return false;
	}
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        NBTTagList list = nbt.getTagList("Items", 10);
		this.slots = new ItemStack[this.getSizeInventory()];
		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound compound = (NBTTagCompound) list.getCompoundTagAt(i);
			byte b = compound.getByte("Slot");
			if(b >= 0 && b < this.slots.length) {
				this.slots[b] = ItemStack.loadItemStackFromNBT(compound);
			}
		}
        
    }

    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        
        NBTTagList list = new NBTTagList();
		for (int i = 0; i < this.slots.length; i++) {
			if(this.slots[i] != null) {
				NBTTagCompound compound = new NBTTagCompound();
				compound.setByte("Slot", (byte)i);
				this.slots[i].writeToNBT(compound);
				list.appendTag(compound);
			}
		}
		nbt.setTag("Items", list);
		if (this.hasCustomInventoryName()) {
			//nbt.setString("CustomName", this.localizedName);
		}
    }
}
