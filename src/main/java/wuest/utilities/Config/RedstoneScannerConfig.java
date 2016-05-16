package wuest.utilities.Config;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/**
 * This is the class responsible for holding the configuration options for the redstone scanner.
 * @author WuestMan
 *
 */
public class RedstoneScannerConfig
{
	private ArrayList<FacingConfig> facingConfigs;
	private int tickDelay;
	private BlockPos pos;
	
	/**
	 * Initializes a new instance of the RedstoneScannerConfig class.
	 */
	public RedstoneScannerConfig()
	{
		this.Initialize();
	}
	
	/**
	 * @return Gets the facing configs associated with this class.
	 */
	public ArrayList<FacingConfig> getFacingConfigs()
	{
		return this.facingConfigs;
	}

	/**
	 * @return The tick delay for this class.
	 */
	public int getTickDelay()
	{
		return this.tickDelay;
	}
	
	/**
	 * The new value of the tick delay.
	 * @param value The new tick delay.
	 */
	public void setTickDelay(int value)
	{
		if (value < 1)
		{
			value = 1;
		}
		
		this.tickDelay = value;
	}

	/**
	 * Gets the block pos of this class.
	 * @return The block pos saved in this class.
	 */
	public BlockPos getBlockPos()
	{
		return this.pos;
	}
	
	/**
	 * Sets the block pos for this class.
	 * @param value The new block pos for this class.
	 */
	public void setBlockPos(BlockPos value)
	{
		this.pos = value;
	}
	
	/**
	 * Sets the block pos for this class.
	 * @param x The X-Coordinate for this block.
	 * @param y The Y-Coordinate for this block.
	 * @param z The Z=Coordinate for this block.
	 */
	public void setBlockPos(int x, int y, int z)
	{
		this.pos = new BlockPos(x, y, z);
	}
	
	/**
	 * Adds a pre-configured facing configuration to the arraylist.
	 * @param value the pre-configured value to add.
	 */
 	public void AddFacingConfig(FacingConfig value)
	{
		this.facingConfigs.add(value);
	}
	
	/**
	 * Adds a facing to the arraylist.
	 * @param facing The facing to add.
	 * @param active Determines if the facing is active.
	 * @param scanLength The distance for the scan for this facing.
	 */
	public void AddFacingConfig(EnumFacing facing, boolean active, int scanLength)
	{
		FacingConfig config = new FacingConfig();
		this.AddFacingConfig(config.setFacing(facing).setActive(active).setScanLength(scanLength));
	}
	
	/**
	 * Sets the active flag for the facing's config.
	 * @param facing The facing to look for in the facing config.
	 * @param active The new value of the active flag.
	 */
	public void SetFacingConfig(EnumFacing facing, boolean active)
	{
		this.SetFacingConfig(facing, active, -1);
	}
	
	/**
	 * Sets the scan length flag for the facing's config.
	 * @param facing The facing to look for in the facing config.
	 * @param scanLength The distance for the scan for this facing.
	 */
	public void SetFacingConfig(EnumFacing facing, int scanLength)
	{
		for (FacingConfig config : this.facingConfigs)
		{
			if (config.facing == facing)
			{
				config.scanLength = scanLength;
				break;
			}
		}
	}
	
	/**
	 * Sets the active and scan length properties of the facing's config.
	 * @param facing The facing to look for in the facing config.
	 * @param active The new value of the active flag.
	 * @param scanLength The distance for the scan for this facing.
	 */
	public void SetFacingConfig(EnumFacing facing, boolean active, int scanLength)
	{
		for (FacingConfig config : this.facingConfigs)
		{
			if (config.facing == facing)
			{
				config.active = active;
				
				if (scanLength >= 0)
				{
					config.scanLength = scanLength;
				}
				
				break;
			}
		}
	}
	
	/**
	 * Initializes the properties of this class.
	 */
 	public void Initialize()
	{
		this.facingConfigs = new ArrayList<FacingConfig>();
		this.tickDelay = 5;
		
		for (EnumFacing facing : EnumFacing.HORIZONTALS)
		{
			this.SetFacingConfig(facing, true, 1);
		}
	}

 	/**
 	 * Writes the values of this class to an NBTTagCompound.
 	 * @param compound The compound to write the tag values too.
 	 */
	public void WriteToNBTCompound(NBTTagCompound compound)
	{
		// TODO Auto-generated method stub
		NBTTagCompound configCompound = new NBTTagCompound();
		configCompound.setInteger("tickDelay", this.tickDelay);
		configCompound.setInteger("x", this.pos.getX());
		configCompound.setInteger("y", this.pos.getY());
		configCompound.setInteger("z", this.pos.getZ());
		
		for (FacingConfig config : this.facingConfigs)
		{
			NBTTagCompound facing = new NBTTagCompound();
			facing.setString("facing", config.facing.getName2());
			facing.setBoolean("active", config.active);
			facing.setInteger("scanLength", config.scanLength);
			
			configCompound.setTag(config.facing.getName2(), facing);
		}
	}

	/**
	 * Builds a RedstoneScannerConfig from an NBTTagCompound.
	 * @param compound The compound to build the class from.
	 * @return A new instance of the RedstoneScannerConfig build with all of the values loaded from the tag (if any).
	 */
	public static RedstoneScannerConfig ReadFromNBTTagCompound(NBTTagCompound compound)
	{
		RedstoneScannerConfig config = new RedstoneScannerConfig();
		
		if (compound.hasKey("tickDelay"))
		{
			config.tickDelay = compound.getInteger("tickDelay");
		}
		
		if (compound.hasKey("x") && compound.hasKey("y") && compound.hasKey("z"))
		{
			config.pos = new BlockPos(compound.getInteger("x"), compound.getInteger("y"), compound.getInteger("z"));
		}
		
		for (EnumFacing facing : EnumFacing.HORIZONTALS)
		{
			if (compound.hasKey(facing.getName2()))
			{
				NBTTagCompound tag = compound.getCompoundTag(facing.getName2());
				FacingConfig facingConfig = new FacingConfig();
				
				if (tag.hasKey("facing"))
				{
					facingConfig.facing = EnumFacing.byName(tag.getString("facing"));
				}
				
				if (tag.hasKey("active"))
				{
					facingConfig.active = tag.getBoolean("active");
				}
				
				if (tag.hasKey("scanLength"))
				{
					facingConfig.scanLength = tag.getInteger("scanLength");
				}
			}
		}
		
		return config;
	}
 	
	/**
	 * This class is used to define the scanning configuration for a particular side. 
	 * @author WuestMan
	 *
	 */
	public static class FacingConfig
	{
		private EnumFacing facing;
		private boolean active;
		private int scanLength;
		
		/**
		 * Initializes a new instance of the FacingConfig class.
		 */
		public FacingConfig()
		{
			this.Initialize();
		}
		
		/**
		 * Gets the facing value.
		 * @return Gets the EnumFacing value of this class.
		 */
		public EnumFacing getFacing()
		{
			return this.facing;
		}
		
		/**
		 * Set the facing value.
		 * @param value The new value of the facing.
		 * @return The updated facing config for ease of setup.
		 */
		public FacingConfig setFacing(EnumFacing value)
		{
			this.facing = value;
			return this;
		}
		
		/**
		 * Gets whether this facing is active.
		 * @return A bool representing the status of this facing.
		 */
		public boolean getActive()
		{
			return this.active;
		}
		
		/**
		 * Sets the facing active status.
		 * @param value The new status of this facing.
		 * @return The updated facing config for ease of setup.
		 */
		public FacingConfig setActive(boolean value)
		{
			this.active = value;
			return this;
		}
	
		/**
		 * Gets the length (in blocks) that this scan will cover. 
		 * @return The number of blocks to scan.
		 */
		public int getScanLength()
		{
			return this.scanLength;
		}
		
		/**
		 * Sets the number of blocks to scan.
		 * @param value The number of blocks to scan for this facing when active.
		 * @return The updated facing config for ease of setup.
		 */
		public FacingConfig setScanLength(int value)
		{
			this.scanLength = value;
			return this;
		}
	
		/**
		 * Initializes the properties of this class.
		 */
		public void Initialize()
		{
			this.facing = EnumFacing.NORTH;
			this.active = false;
			this.scanLength = 1;
		}
	}

}