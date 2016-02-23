package wuest.utilities.Proxy;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import wuest.utilities.*;
import wuest.utilities.Blocks.RedstoneClock;
import wuest.utilities.Events.*;
import wuest.utilities.Gui.*;
import wuest.utilities.Items.*;
import wuest.utilities.Tiles.TileEntityRedstoneClock;

public class CommonProxy implements IGuiHandler 
{
	// This should be static in order for the events to be processed on server and client.
	private static WuestEventHandler eventHandler = new WuestEventHandler();
	
	public static WuestConfiguration proxyConfiguration;

	/*
	  * Methods for ClientProxy to Override
	  */
	public void registerRenderers() {}
	
	public void preInit(FMLPreInitializationEvent event)
	{
		WuestUtilities.config = new Configuration(event.getSuggestedConfigurationFile());
	    WuestUtilities.config.load();
		WuestConfiguration.syncConfig();
		
		WuestUtilities.network = NetworkRegistry.INSTANCE.newSimpleChannel("MyChannel");
		WuestUtilities.network.registerMessage(WuestHandler.class, TagMessage.class, 0, Side.SERVER);
		WuestUtilities.network.registerMessage(HouseHandler.class, HouseTagMessage.class, 1, Side.SERVER);
		WuestUtilities.network.registerMessage(RedstoneClockHandler.class, RedstoneClockMessage.class, 2, Side.SERVER);
	}
	
	public void init(FMLInitializationEvent event)
	{
		// Register items here.
		ItemStartHouse.RegisterItem();
		RedstoneClock.RegisterBlock();
		GeneralRecipes.LoadRecipies();

		NetworkRegistry.INSTANCE.registerGuiHandler(WuestUtilities.instance, WuestUtilities.proxy);
		this.RegisterEventListeners();
	}
	
	public void postinit(FMLPostInitializationEvent event)
	{
	}
	
	private void RegisterEventListeners()
	{
		// DEBUG
	    System.out.println("Registering event listeners");
	    
	    MinecraftForge.EVENT_BUS.register(eventHandler);
	}
	
	public IThreadListener getThreadFromContext(MessageContext ctx) 
	{
		return ctx.getServerHandler().playerEntity.getServerForPlayer();
	}
	
	/**
	 * Returns a side-appropriate EntityPlayer for use during message handling
	 */
	public EntityPlayer getPlayerEntity(MessageContext ctx) 
	{
		System.out.println("Retrieving player from CommonProxy for message on side " + ctx.side);
		return ctx.getServerHandler().playerEntity;
	}
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) 
	{
		return null;
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) 
	{
		if (ID == GuiHouseItem.GUI_ID)
		{
            return new GuiHouseItem(x, y, z);
		}
		else if (ID == GuiRedstoneClock.GUI_ID)
		{
			return new GuiRedstoneClock(x, y, z);
		}
		
		return null;
	}
}