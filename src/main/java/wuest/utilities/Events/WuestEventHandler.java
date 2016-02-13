package wuest.utilities.Events;

import java.util.List;
import java.util.Random;

import wuest.utilities.*;
import wuest.utilities.Gui.WuestConfiguration;
import wuest.utilities.Items.ItemStartHouse;
import wuest.utilities.Proxy.WuestMessage;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockPotato;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class WuestEventHandler 
{
	public static final String GIVEN_HOUSEBUILDER_TAG = "givenHousebuilder";
	public static boolean sentConfiguration = false;
	
	@SubscribeEvent
	public void PlayerRightClicked(PlayerInteractEvent event)
	{
		// This only happens during the right-click event.
		// Can use the proxies configuration since this is on the client.
		if (event.action == Action.RIGHT_CLICK_BLOCK && WuestUtilities.proxy.proxyConfiguration.rightClickCropHarvest
				&& !event.world.isRemote)
		{
			EntityPlayer p = event.entityPlayer;
			
			ItemStack currentStack = p.inventory.getCurrentItem();
			
			if (currentStack != null)
			{
				Item currentItem = currentStack.getItem();
				ItemStack boneMealStack = new ItemStack(Items.dye, 1, 15);
				Item boneMeal = boneMealStack.getItem();
				
				if (currentItem != null && currentItem == boneMeal)
				{
					return;
				}
			}
			
			IBlockState cropState = event.world.getBlockState(event.pos);
			Block crop = cropState.getBlock();
			
			// Only re-plant when this is a fully grown plant.
			if (crop instanceof BlockCrops && (Integer)cropState.getValue(BlockCrops.AGE) == 7)
			{
				// Get the farmland below the crop.
				BlockPos farmlandPosition = event.pos.down();
				
				// Get the drops from this crop and add it to the inventory.
				List<ItemStack> drops = crop.getDrops(event.world, event.pos, cropState, 1);
				
				// Break the original crop block.
				event.world.setBlockToAir(event.pos);
				
				Boolean replanted = false;
				
				for (ItemStack drop : drops)
				{
					Item dropItem = drop.getItem();
					
					if (!replanted)
					{
						replanted = dropItem.onItemUse(new ItemStack(dropItem), p, event.world, farmlandPosition, event.face, 0, 0, 0);
						
						if (replanted)
						{
							continue;
						}
					}
					
					boolean addedItem = p.inventory.addItemStackToInventory(drop);
					p.inventoryContainer.detectAndSendChanges();
					
					if (addedItem)
					{
						continue;
					}
				}
				
				if (!replanted)
				{
					// The only reason why we wouldn't have re-planted at this point is because the wheat didn't drop a seed. Check the player inventory for a seed and plant it.
					// This should work with other plants that override BlockCrops.GetItem with their own seed.
					BlockCrops blockCrop = (BlockCrops)crop;
					
					// Make sure to re-set the age to 0 to get the seed.
					IBlockState tempState = cropState.withProperty(BlockCrops.AGE, 0);
					
					// Get the seed item and check to see fi the player has this in their inventory. If they do we can use it to replant.
					Item seed = blockCrop.getItemDropped(tempState, new Random(), 0);
					
					if (seed != null && p.inventory.hasItem(seed))
					{
						seed.onItemUse(new ItemStack(seed), p, event.world, farmlandPosition, event.face, 0, 0, 0);
						p.inventory.consumeInventoryItem(seed);
						p.inventoryContainer.detectAndSendChanges();
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void PlayerJoinedWorld(EntityJoinWorldEvent event)
	{
		if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayerMP) 
		{
			System.out.println("Player joined world, checking to see if the house builder should be provided.");
			
		    EntityPlayer player = (EntityPlayer)event.entity;
		    NBTTagCompound persistTag = this.getModIsPlayerNewTag(player);
		    
		    // Get the opposite of the value, if the bool doesn't exist then we can add the house to the inventory, otherwise the player isn't new and shouldn't get the item.
		    boolean shouldGiveHousebuilder = !persistTag.getBoolean(WuestEventHandler.GIVEN_HOUSEBUILDER_TAG);
		      
		    if (shouldGiveHousebuilder)
		    {
		        ItemStack stack = new ItemStack(ItemStartHouse.RegisteredItem);
		        player.inventory.addItemStackToInventory(stack);
		        
		        // Make sure to set the tag for this player so they don't get the item again.
		        persistTag.setBoolean(WuestEventHandler.GIVEN_HOUSEBUILDER_TAG, true);
		    }
		}
		
		if (event.entity.worldObj.isRemote && !WuestEventHandler.sentConfiguration)
		{
			WuestUtilities.network.sendToServer(new WuestMessage(WuestUtilities.proxy.proxyConfiguration.WriteToNBTTagCompound()));
			WuestEventHandler.sentConfiguration = true;
		}
	}
	
	@SubscribeEvent
	public void onClone(PlayerEvent.Clone event) 
	{
		// Don't add the tag unless the house item was added. This way it can be added if the feature is turned on.
		// When the player is cloned, make sure to copy the tag. If this is not done the item can be given to the player again if they die before the log out and log back in.
	    NBTTagCompound originalTag = event.original.getEntityData();
	    
	    if (originalTag.hasKey(WuestConfiguration.tagKey))
	    {
	    	WuestConfiguration config = WuestConfiguration.ReadFromNBTTagCompound((NBTTagCompound)originalTag.getTag(WuestConfiguration.tagKey));
	    
	    	if (config.addHouseItem)
	    	{
			    if (originalTag.hasKey("IsPlayerNew"))
			    {
			    	NBTTagCompound newPlayerTag = event.entityPlayer.getEntityData();
			    	newPlayerTag.setTag("IsPlayerNew", originalTag.getTag("IsPlayerNew"));
			    }
	    	}
	    	
	    	// Save the configuration tag.
	    	NBTTagCompound newPlayerTag = event.entityPlayer.getEntityData();
	    	newPlayerTag.setTag(WuestConfiguration.tagKey, originalTag.getTag(WuestConfiguration.tagKey));
	    }
	}

	private NBTTagCompound getModIsPlayerNewTag(EntityPlayer player)
	{
		NBTTagCompound tag = player.getEntityData();
		
		// Get/create a tag used to determine if this is a new player.
		NBTTagCompound newPlayerTag = null;
		
		if (tag.hasKey("IsPlayerNew"))
		{
			newPlayerTag = tag.getCompoundTag("IsPlayerNew");
		}
		else
		{
			newPlayerTag = new NBTTagCompound();
			tag.setTag("IsPlayerNew", newPlayerTag);
		}
		
		return newPlayerTag;
	}

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent onConfigChangedEvent)
    {
        if(onConfigChangedEvent.modID.equals("wuestUtilities"))
        {
            WuestConfiguration.syncConfig();
            WuestEventHandler.sentConfiguration = false;
            
            // Don't send the message if the configuration was changed in the main menu.
            if (onConfigChangedEvent.isWorldRunning)
            {
	            // Re-send the message when the configuration changes.
	            WuestUtilities.network.sendToServer(new WuestMessage(WuestUtilities.proxy.proxyConfiguration.WriteToNBTTagCompound()));
	            WuestEventHandler.sentConfiguration = true;
            }
        }
    }
}