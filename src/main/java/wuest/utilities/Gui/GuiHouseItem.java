package wuest.utilities.Gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.GuiSlider;
import wuest.utilities.WuestUtilities;
import wuest.utilities.Proxy.HouseTagMessage;

public class GuiHouseItem extends GuiScreen
{
	public static final int GUI_ID = 549;
	protected GuiButtonExt btnCancel;
	protected GuiButtonExt btnBuild;

	protected GuiCheckBox btnAddTorches;
	protected GuiCheckBox btnAddBed;
	protected GuiCheckBox btnAddCraftingTable;
	protected GuiCheckBox btnAddChest;
	protected GuiCheckBox btnAddChestContents;
	protected GuiCheckBox btnAddFarm;
	protected GuiCheckBox btnAddMineShaft;
	protected GuiCheckBox btnIsCeilingFlat;

	protected GuiTextSlider btnFloorBlock;
	protected GuiTextSlider btnCeilingBlock;
	protected GuiTextSlider btnWallWoodType;

	protected int hitPosX;
	protected int hitPosY;
	protected int hitPosZ;

	public GuiHouseItem(int x, int y, int z) 
	{
		this.hitPosX = x;
		this.hitPosY = y;
		this.hitPosZ = z;
	}

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
	 */
	@Override
	public void drawScreen(int x, int y, float f) 
	{
		this.drawDefaultBackground();

		for (int i = 0; i < this.buttonList.size(); ++i)
		{
			((GuiButton)this.buttonList.get(i)).drawButton(this.mc, x, y);
		}

		for (int j = 0; j < this.labelList.size(); ++j)
		{
			((GuiLabel)this.labelList.get(j)).drawLabel(this.mc, x, y);
		}

		int labely = 20;
		int labelx = this.width - 140;

		// Create the sliders.
		int color = 14737632;
		this.drawString(this.mc.fontRendererObj, "Floor Block Type", labelx, labely, color);

		labely += 40;
		this.drawString(this.mc.fontRendererObj, "Ceiling Block Type", labelx, labely, color);

		labely += 40;
		this.drawString(this.mc.fontRendererObj, "Wall Wood Type", labelx, labely, color);
	}

	@Override
	public void initGui()
	{
		this.Initialize();
	}

	protected void Initialize()
	{
		int x = 20;
		int y = 20;

		this.btnAddTorches = new GuiCheckBox(1, x, y, HouseConfiguration.addTorchesName, true);
		this.buttonList.add(this.btnAddTorches);
		y += 20;

		this.btnAddBed = new GuiCheckBox(2, x, y, HouseConfiguration.addBedName, true);
		this.buttonList.add(this.btnAddBed);
		y += 20;

		this.btnAddChest = new GuiCheckBox(3, x, y, HouseConfiguration.addChestName, true);
		this.buttonList.add(this.btnAddChest);
		y += 20;

		this.btnAddChestContents = new GuiCheckBox(4, x, y, HouseConfiguration.addChestContentsName, true);
		this.buttonList.add(this.btnAddChestContents);
		y += 20;

		this.btnAddCraftingTable = new GuiCheckBox(5, x, y, HouseConfiguration.addCraftingTableName, true);
		this.buttonList.add(this.btnAddCraftingTable);
		y += 20;

		this.btnAddFarm = new GuiCheckBox(6,  x, y, HouseConfiguration.addFarmName, true);
		this.buttonList.add(this.btnAddFarm);
		y += 20;

		this.btnAddMineShaft = new GuiCheckBox(7, x, y, HouseConfiguration.addMineShaftName, true);
		this.buttonList.add(this.btnAddMineShaft);
		y += 20;

		this.btnIsCeilingFlat = new GuiCheckBox(8, x, y, HouseConfiguration.isCeilingFlatName, true);
		this.buttonList.add(this.btnIsCeilingFlat);

		x = this.width - 140;
		y = 35;

		// Create the slider labels (column 200);
		int color = 14737632;

		this.btnFloorBlock = new GuiTextSlider(11, x, y, 120, 20, 0, 2, 0, HouseConfiguration.floorBlockName);
		this.buttonList.add(this.btnFloorBlock);

		y += 40;
		this.btnCeilingBlock = new GuiTextSlider(12, x, y, 120, 20, 0, 2, 0, HouseConfiguration.ceilingBlockName);
		this.buttonList.add(this.btnCeilingBlock);

		y += 40;
		this.btnWallWoodType = new GuiTextSlider(13, x, y, 120, 20, 0, 5, 0, HouseConfiguration.wallWoodTypeName);
		this.buttonList.add(this.btnWallWoodType);

		// Create the done and cancel buttons.
		this.btnBuild = new GuiButtonExt(9, 20, 200, 120, 20, "Build!");
		this.buttonList.add(this.btnBuild);

		this.btnCancel = new GuiButtonExt(10, this.width - 140, 200, 120, 20, "Cancel");
		this.buttonList.add(this.btnCancel);

	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
	 */
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if (button == this.btnCancel)
		{
			this.mc.displayGuiScreen(null);
		}
		else if (button == this.btnBuild)
		{
			HouseConfiguration houseConfiguration = new HouseConfiguration();
			houseConfiguration.hitX = this.hitPosX;
			houseConfiguration.hitY = this.hitPosY;
			houseConfiguration.hitZ = this.hitPosZ;
			houseConfiguration.addBed = this.btnAddBed.isChecked();
			houseConfiguration.addChest = this.btnAddChest.isChecked();
			houseConfiguration.addChestContents = this.btnAddChestContents.isChecked();
			houseConfiguration.addCraftingTable = this.btnAddCraftingTable.isChecked();
			houseConfiguration.addFarm = this.btnAddFarm.isChecked();
			houseConfiguration.addMineShaft = this.btnAddMineShaft.isChecked();
			houseConfiguration.addTorches = this.btnAddTorches.isChecked();
			houseConfiguration.isCeilingFlat = this.btnIsCeilingFlat.isChecked();
			houseConfiguration.ceilingBlock = WuestConfiguration.CeilingFloorBlockType.ValueOf(this.btnCeilingBlock.getValueInt());
			houseConfiguration.floorBlock = WuestConfiguration.CeilingFloorBlockType.ValueOf(this.btnFloorBlock.getValueInt());
			houseConfiguration.wallWoodType = WuestConfiguration.WallBlockType.ValueOf(this.btnWallWoodType.getValueInt());

			WuestUtilities.network.sendToServer(new HouseTagMessage(houseConfiguration.WriteToNBTTagCompound()));

			// Close this screen when this is done.
			this.mc.displayGuiScreen(null);
		}
	}

	public class GuiTextSlider extends GuiSlider
	{
		protected String configName = "";

		/**
		 * Initializes a new instance of the GuiTextSlider class.
		 * @param id
		 * @param xPos
		 * @param yPos
		 * @param width
		 * @param height
		 * @param prefix
		 * @param suf
		 * @param minVal
		 * @param maxVal
		 * @param currentVal
		 * @param showDec
		 * @param drawStr
		 */
		public GuiTextSlider(int id, int xPos, int yPos, int width, int height, double minVal, double maxVal,
				double currentVal, String name) 
		{
			super(id, xPos, yPos, width, height, "", "", minVal, maxVal, currentVal,
					false, true);

			this.configName = name;
			this.updateSlider();
		}

		@Override
		public void updateSlider()
		{
			this.SetSuffix();

			super.updateSlider();
		}

		/**
		 * Fixes an issue where the integer value could be above the maximum value of this slider.
		 */
		@Override
		public int getValueInt()
		{
			int temp =(int)Math.round(sliderValue * (maxValue - minValue) + minValue);

			if (temp > this.maxValue)
			{
				temp = (int) Math.round(this.maxValue);
			}

			return temp; 
		}

		public void SetSuffix()
		{
			if (!this.showDecimal)
			{
				int currentValue = this.getValueInt();

				this.suffix = HouseConfiguration.GetIntegerOptionStringValue(this.configName, currentValue);
			}
		}

		public String getName()
		{
			return this.configName;
		}

		public void setName(String name)
		{
			this.configName = name;
		}
	}
}



















