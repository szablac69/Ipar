package net.ipar.mod.tileEntity;

import cpw.mods.fml.common.registry.GameRegistry;
import net.ipar.mod.Ipar;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class TileEntityBlastFurnace extends TileEntity implements ISidedInventory{

	private ItemStack[] slots = new ItemStack [6];
	
	public static final int slotNrIron = 0;
	public static final int slotNrLimeStone = 1;
	public static final int slotNrCoke = 2;
	public static final int slotNrFuel = 3;
	public static final int slotNrSlag = 4;
	public static final int slotNrSteel = 5;
	
	//How many time need to smelt
	public static final int furnaceSpeed = 1000;
	public static float environmentTemp = 20.0f;
	public static float maxfurnaceTemp = 2000.0f;
	public static float minSmeltingTemp = 1600.0f;
	public static float heatTrasferfastor = 0.002f;
	public static int speed = 5;
	
	public float furnaceTemp = environmentTemp;
	//The remaining time of the fuel
	public int burnTime;
	//The actual burning fuel max burnTime (Because scale)
	public int currentItemBurnTime;
	//The remaining smelting time
	public int smeltingTime = 0;
	//Already removed the source material
	
	public void updateEntity(){
		boolean flag = this.burnTime > 0;
		boolean flagDirty = false;
		
		if(this.isBurning()){
			this.burnTime  -= speed;
			if(this.burnTime < 0) this.burnTime = 0;
			//System.out.println("Time : " + Integer.toString(this.burnTime));
		}
		
		if(!this.worldObj.isRemote){
			
			if(this.burnTime == 0){
				this.currentItemBurnTime = this.burnTime = getItemBurnTime(slots[slotNrFuel]);
				//System.out.println("Time : " + Integer.toString(this.currentItemBurnTime));
				if(this.isBurning()){
					flagDirty = true;
					
					if(this.slots[slotNrFuel] != null){
						this.slots[slotNrFuel].stackSize--;
						
						if(this.slots[slotNrFuel].stackSize == 0){
							this.slots[slotNrFuel] = this.slots[slotNrFuel].getItem().getContainerItem(this.slots[slotNrFuel]);
						}
					}
				}
			
			}
			if(isBurning()){
				furnaceTemp += speed;
				if(furnaceTemp > maxfurnaceTemp) furnaceTemp = maxfurnaceTemp;
			}else{
				//furnaceTemp -=speed;
				//if(furnaceTemp < environmentTemp) furnaceTemp = environmentTemp;
			}
			FurnaceHeatTransfer();
			//Smelting start
			if(!isSmelting() && furnaceTemp >= minSmeltingTemp){
				if(canSmelt()){
					this.slots[slotNrIron].stackSize -= 20;
					this.slots[slotNrCoke].stackSize -= 5;
					this.slots[slotNrLimeStone].stackSize -= 1;
					
					if(this.slots[slotNrIron].stackSize == 0) this.slots[slotNrIron] = null;
					if(this.slots[slotNrCoke].stackSize == 0) this.slots[slotNrCoke] = null;
					if(this.slots[slotNrLimeStone].stackSize == 0) this.slots[slotNrLimeStone] = null;
					smeltingTime = furnaceSpeed;
					System.out.println("Smelting start end");
				}
			}
			if(isSmelting()){
				//Bacause Coke, it is self-heating
				furnaceTemp += speed;
				if(furnaceTemp > maxfurnaceTemp) furnaceTemp = maxfurnaceTemp;
				smeltingTime -= speed;
				if(smeltingTime <= 0){
					System.out.println("Smelting...");
					smeltingTime = 0;
					smelt();

				}
				
			}
			if(flag != this.isBurning()){
				flagDirty = true;
			}
		}
		
		if(flagDirty){
			this.markDirty();
		}
		
	}
	private void FurnaceHeatTransfer(){
		float dt = furnaceTemp - environmentTemp;
		furnaceTemp -= dt * heatTrasferfastor;
	}
	
	public boolean canSmelt(){
		if(slots[slotNrIron] == null || slots[slotNrCoke] == null || slots[slotNrLimeStone] == null) return false;
		
		if(slots[slotNrIron].stackSize >= 20 && slots[slotNrCoke].stackSize >= 5 && slots[slotNrLimeStone].stackSize >= 1){
			boolean sSteel = true;
			boolean sSlag = true;
			
			if(slots[slotNrSteel] != null && slots[slotNrSteel].stackSize > (64 - 10)) sSteel = false;
			if(slots[slotNrSlag] != null && slots[slotNrSlag].stackSize > (64 - 12)) sSlag = false;
			if(sSteel && sSlag) return true;
		}
		return false;
	}
	public void smelt(){
		if(this.slots[slotNrSteel] == null){
			this.slots[slotNrSteel] = new ItemStack(Ipar.itemSteel,10);
		}else{
			this.slots[slotNrSteel].stackSize += 10;
		}
		if(this.slots[slotNrSlag] == null){
			this.slots[slotNrSlag] = new ItemStack(Ipar.itemSlag,12);
		}else{
			this.slots[slotNrSlag].stackSize += 12;
		}
	}
	
	
	public int getBurnTimeRemainingScaled(int i){
		if(this.currentItemBurnTime == 0){
			this.currentItemBurnTime = this.furnaceSpeed;
		}
		return this.burnTime * i / this.currentItemBurnTime;
	}
	public int getFurnaceTempScaled(int i){
		return (int) (this.furnaceTemp * i / this.maxfurnaceTemp);
	}
	public int getSmeltingTempScaled(int i){
		return this.smeltingTime * i / this.furnaceSpeed;
	}
	
	public boolean isBurning(){
		return this.burnTime > 0;
	}
	public boolean isSmelting(){
		return this.smeltingTime > 0;
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
		}else return null;
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
		this.slots[i] = itemStack;
		
		if(itemStack != null && itemStack.stackSize > this.getInventoryStackLimit()){
			itemStack.stackSize = this.getInventoryStackLimit();
		}
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
	public void openInventory() {}
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_,
			int p_102007_3_) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_,
			int p_102008_3_) {
		// TODO Auto-generated method stub
		return false;
	}

	public static boolean isItemFuel(ItemStack itemStack) {
		return getItemBurnTime(itemStack) > 0;
	}
	
	private static int getItemBurnTime(ItemStack itemStack) {
		if(itemStack == null){
			return 0;
		}else{
			Item item = itemStack.getItem();
			
			if(item instanceof ItemBlock && Block.getBlockFromItem(item) != Blocks.air){
				Block block = Block.getBlockFromItem(item);
				
				if(block == Blocks.coal_block) return 14400;
			}
			if(item == Items.coal) return 1600;

		}
		return GameRegistry.getFuelValue(itemStack);
	}
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		System.out.println("BlastFurnance NBT read started");
		NBTTagList list = nbt.getTagList("Items", 10);
		this.slots = new ItemStack[this.getSizeInventory()];
		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound compound = (NBTTagCompound) list.getCompoundTagAt(i);
			byte b = compound.getByte("Slot");
			if(b >= 0 && b < this.slots.length) {
				this.slots[b] = ItemStack.loadItemStackFromNBT(compound);
			}
		}
		
		this.burnTime = (int)nbt.getInteger("BurnTime");
		this.smeltingTime = (int)nbt.getInteger("SmeltingTime");
		this.currentItemBurnTime = (int)nbt.getInteger("CurrentBurnTime");
		this.furnaceTemp = nbt.getFloat("FurnaceTemp");
		
	}
	
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		
		nbt.setInteger("BurnTime", this.burnTime);
		nbt.setInteger("SmeltingTime", this.smeltingTime);
		nbt.setInteger("CurrentBurnTime", this.currentItemBurnTime);
		nbt.setFloat("FurnaceTemp", this.furnaceTemp);
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
	}
}
