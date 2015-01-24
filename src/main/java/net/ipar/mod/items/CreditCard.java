package net.ipar.mod.items;

import net.ipar.mod.Ipar;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class CreditCard extends IparItems{
	private final static int maxCredit = 1000;
	
	public CreditCard(){
		this.setMaxStackSize(1);
		this.setMaxDamage(maxCredit);
		//this.setDamage(new ItemStack(Ipar.itemCreditCard),1);
	}
	public static void setCredit(ItemStack itemStack, int newValue){
		if(itemStack != null){
			if((itemStack.getItem() instanceof CreditCard)) {
				if(newValue > maxCredit) newValue = maxCredit;
				itemStack.setItemDamage(newValue);
			}
		}
		
	}

	public static int getCredit(ItemStack itemStack){
		if(itemStack == null) return 0;
		if(itemStack.getItem() instanceof CreditCard){
			return itemStack.getItemDamage();
		}
		
		return 0;
	}
	
	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer p_77648_2_, World p_77648_3_, int p_77648_4_, int p_77648_5_, int p_77648_6_, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_)
    {
		this.setDamage(itemStack, this.getDamage(itemStack) + 1);
        return false;
    }
	
	@Override
	public String getItemStackDisplayName(ItemStack itemStack)
    {
        return ("" + StatCollector.translateToLocal(this.getUnlocalizedNameInefficiently(itemStack) + ".name")).trim() + "     Credit : " + itemStack.getItemDamage();
    }
	
	@Override
    public int getDisplayDamage(ItemStack stack)
    {
        return stack.getMaxDamage() - stack.getItemDamage();
    }
}
