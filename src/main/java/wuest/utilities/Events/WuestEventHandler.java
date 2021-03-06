package wuest.utilities.Events;

import java.awt.Color;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.time.*;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import wuest.utilities.WuestUtilities;
import wuest.utilities.Gui.WuestConfiguration;
import wuest.utilities.Items.ItemStartHouse;
import wuest.utilities.Proxy.BedLocationMessage;
import wuest.utilities.Proxy.RedstoneClockMessage;

public class WuestEventHandler
{
	public static final String GIVEN_HOUSEBUILDER_TAG = "givenHousebuilder";
	
	private static HashMap<String, BlockPos> playerBedLocation;

	@SubscribeEvent(receiveCanceled = true)
	public void PlayerRightClicked(PlayerInteractEvent event)
	{
		// This only happens during the right-click event.
		// Can use the proxy's configuration.
		if (event.action == Action.RIGHT_CLICK_BLOCK && WuestUtilities.proxy.proxyConfiguration.rightClickCropHarvest
				&& !event.world.isRemote
				&& !event.isCanceled())
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

				EnumActionResult replanted = EnumActionResult.FAIL;

				for (ItemStack drop : drops)
				{
					Item dropItem = drop.getItem();

					if (replanted != EnumActionResult.PASS)
					{
						replanted = dropItem.onItemUse(new ItemStack(dropItem), p, event.world, farmlandPosition, null, event.face, 0, 0, 0);

						if (replanted == EnumActionResult.PASS)
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

				if (replanted != EnumActionResult.PASS)
				{
					// The only reason why we wouldn't have re-planted at this point is because the wheat didn't drop a seed. Check the player inventory for a seed and plant it.
					// This should work with other plants that override BlockCrops.GetItem with their own seed.
					BlockCrops blockCrop = (BlockCrops)crop;

					// Make sure to re-set the age to 0 to get the seed.
					IBlockState tempState = cropState.withProperty(BlockCrops.AGE, 0);

					// Get the seed item and check to see if the player has this in their inventory. If they do we can use it to re-plant.
					ItemStack seed = new ItemStack(blockCrop.getItemDropped(tempState, new Random(), 0));

					if (seed != null && p.inventory.hasItemStack(seed))
					{
						seed.onItemUse(p, event.world, farmlandPosition, null, event.face, 0, 0, 0);
						ItemStack stackInSlot = p.inventory.getStackInSlot(p.inventory.getSlotFor(seed));
						stackInSlot.stackSize = stackInSlot.stackSize - 1;
						
						if (stackInSlot.stackSize <= 0)
						{
							p.inventory.deleteStack(stackInSlot);
						}
						else
						{
							p.inventory.setInventorySlotContents(p.inventory.getSlotFor(seed), stackInSlot);
						}

						p.inventoryContainer.detectAndSendChanges();
					}
				}

				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void PlayerJoinedWorld(EntityJoinWorldEvent event)
	{
		if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer) 
		{
			System.out.println("Player joined world, checking to see if the house builder should be provided.");

			EntityPlayer player = (EntityPlayer)event.entity;
			NBTTagCompound persistTag = this.getModIsPlayerNewTag(player);

			// Get the opposite of the value, if the bool doesn't exist then we can add the house to the inventory, otherwise the player isn't new and shouldn't get the item.
			boolean shouldGiveHousebuilder = !persistTag.getBoolean(WuestEventHandler.GIVEN_HOUSEBUILDER_TAG);

			if (shouldGiveHousebuilder && WuestUtilities.proxy.proxyConfiguration.addHouseItem)
			{
				ItemStack stack = new ItemStack(ItemStartHouse.RegisteredItem);
				player.inventory.addItemStackToInventory(stack);

				// Make sure to set the tag for this player so they don't get the item again.
				persistTag.setBoolean(WuestEventHandler.GIVEN_HOUSEBUILDER_TAG, true);
			}
		}
	}
	
	@SubscribeEvent
	public void PlayerTickEvent(TickEvent.PlayerTickEvent event)
	{
		if (event.side.isServer())
		{
			// Send the player's actual bed location to the client for the bed compass object.
			// This is needed as the client doesn't properly store the bed location.
			this.sendPlayerBedLocation(event);
		}
	}

	@SubscribeEvent
	public void onClone(PlayerEvent.Clone event) 
	{
		// Don't add the tag unless the house item was added. This way it can be added if the feature is turned on.
		// When the player is cloned, make sure to copy the tag. If this is not done the item can be given to the player again if they die before the log out and log back in.
		NBTTagCompound originalTag = event.original.getEntityData();

		// Use the server configuration to determine if the house should be added for this player.
		if (WuestUtilities.proxy.proxyConfiguration.addHouseItem)
		{
			if (originalTag.hasKey("IsPlayerNew"))
			{
				NBTTagCompound newPlayerTag = event.entityPlayer.getEntityData();
				newPlayerTag.setTag("IsPlayerNew", originalTag.getTag("IsPlayerNew"));
			}
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
		}
	}

	private void sendPlayerBedLocation(TickEvent.PlayerTickEvent event)
	{
		if (WuestEventHandler.playerBedLocation == null)
		{
			WuestEventHandler.playerBedLocation = new HashMap<String, BlockPos>();
		}
		
		// Send the updated bed information to the client.
		BedLocationMessage message = new BedLocationMessage();
		NBTTagCompound tag = new NBTTagCompound();
		EntityPlayerMP player = (EntityPlayerMP) event.player;
		BlockPos bedPosition = player.getBedLocation();

		if (bedPosition != null)
		{
			tag.setInteger("bedX", bedPosition.getX());
			tag.setInteger("bedY", bedPosition.getY());
			tag.setInteger("bedZ", bedPosition.getZ());
		}
		
		message.setMessageTag(tag);
		BlockPos existingBedPosition = null;
		
		if (WuestEventHandler.playerBedLocation.containsKey(player.getName()))
		{
			existingBedPosition = WuestEventHandler.playerBedLocation.get(player.getName());
		}
		else
		{
			WuestEventHandler.playerBedLocation.put(player.getName(), bedPosition);
		}
		
		if (existingBedPosition != bedPosition)
		{
			// Only send the message to the client if the bed position changes.
			WuestUtilities.network.sendTo(message, player);
		}
	}
}
