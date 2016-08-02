package com.wuest.utilities.Items;

import java.util.List;

import com.wuest.utilities.ModRegistry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This item is just used to create a "Silk Touch" enchantment via the Anvil. 
 * @author WuestMan
 */
public class ItemFluffyFabric extends Item
{
	/**
	 * Initializes a new instance of the ItemFluffyFabric class.
	 */
	public ItemFluffyFabric(String name)
	{
		super();
		
		this.setCreativeTab(CreativeTabs.MISC);
		ModRegistry.setItemName(this, name);
	}
	
    /**
     * Allow or forbid the specific book/item combination as an anvil enchant
     *
     * @param stack The item
     * @param book The book
     * @return if the enchantment is allowed
     */
    @Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book)
    {
        return false;
    }
    
    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
    	tooltip.add("Enchantment Item: Feels silky");
    }
}