package wuest.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class GeneralRecipes 
{
	public static void LoadRecipies()
	{
		if (WuestUtilities.proxy.proxyConfiguration.addMetalRecipes)
		{
			GeneralRecipes.LoadMetalRecipes();
		}

		if (WuestUtilities.proxy.proxyConfiguration.addWoodRecipes)
		{
			GeneralRecipes.LoadWoodRecipes();
		}

		if (WuestUtilities.proxy.proxyConfiguration.addStoneRecipes)
		{
			GeneralRecipes.LoadStoneRecipes();
		}

		if (WuestUtilities.proxy.proxyConfiguration.addArmorRecipes)
		{
			GeneralRecipes.LoadArmorRecipes();
		}

		if (WuestUtilities.proxy.proxyConfiguration.addMiscRecipes)
		{
			GeneralRecipes.LoadMiscRecipes();
		}

		if (WuestUtilities.proxy.proxyConfiguration.addNetherStarRecipe)
		{
			GeneralRecipes.LoadNetherStarRecipe();
		}
	}

	private static void LoadMetalRecipes()
	{
		ArrayList<String> ingots = new ArrayList<String>();

		ingots.add("ingotCopper");
		ingots.add("ingotTin");
		ingots.add("ingotAluminum");

		for (String ingot : ingots)
		{
			// Add recipes for each type of basic metal.
			// Since multiple ores can register this metal, get all instances of that from the dictionary and create recipies for it.
			for (ItemStack stack : OreDictionary.getOres(ingot))
			{
				GeneralRecipes.LoadBucketRecipes(stack);

				GeneralRecipes.LoadShearsRecipes(stack);
			}
		}
	}

	private static void LoadBucketRecipes(ItemStack stack)
	{
		GameRegistry.addRecipe(new ItemStack(Items.bucket),
				"x x",
				" x ",
				'x', stack);

		GameRegistry.addRecipe(new ItemStack(Items.bucket),
				"   ",
				"x x",
				" x ",
				'x', stack);
	}

	private static void LoadShearsRecipes(ItemStack stack)
	{
		GameRegistry.addShapelessRecipe(new ItemStack(Items.shears), stack, stack);
	}

	private static void LoadWoodRecipes()
	{
		for (int i = 0; i < 6; i++)
		{
			// wood slabs into sticks.
			GameRegistry.addRecipe(new ItemStack(Items.stick, 4), 
					"xx",
					"xx", 
					'x', 
					new ItemStack(Blocks.wooden_slab, 1, i));

			// Wooden slabs into planks.
			GameRegistry.addRecipe(new ItemStack(Blocks.planks, 1, i),
					"x",
					"x",
					'x',
					new ItemStack(Blocks.wooden_slab, 1, i));
		}

		// Stairs to planks
		HashMap<Block, ItemStack> stairs = new HashMap<Block, ItemStack>();
		stairs.put(Blocks.oak_stairs, new ItemStack(Blocks.planks, 3, 0));
		stairs.put(Blocks.stone_stairs, new ItemStack(Blocks.cobblestone, 3));
		stairs.put(Blocks.brick_stairs, new ItemStack(Blocks.brick_block, 3));
		stairs.put(Blocks.stone_brick_stairs, new ItemStack(Blocks.stonebrick, 3));
		stairs.put(Blocks.nether_brick_stairs, new ItemStack(Blocks.nether_brick, 3));
		stairs.put(Blocks.sandstone_stairs, new ItemStack(Blocks.sandstone, 3));
		stairs.put(Blocks.red_sandstone_stairs, new ItemStack(Blocks.red_sandstone, 3));
		stairs.put(Blocks.spruce_stairs, new ItemStack(Blocks.planks, 3,1));
		stairs.put(Blocks.birch_stairs, new ItemStack(Blocks.planks, 3, 2));
		stairs.put(Blocks.jungle_stairs, new ItemStack(Blocks.planks, 3, 3));
		stairs.put(Blocks.quartz_stairs, new ItemStack(Blocks.quartz_block, 3));
		stairs.put(Blocks.acacia_stairs, new ItemStack(Blocks.planks, 3, 4));
		stairs.put(Blocks.dark_oak_stairs, new ItemStack(Blocks.planks, 3, 5));

		for(Map.Entry<Block, ItemStack> set : stairs.entrySet())
		{
			// Add recipe to turn 2 stairs into 3 blocks. This gets us back to 6 blocks used to make 4 stairs.
			GameRegistry.addRecipe(set.getValue(),
					"x",
					"x",
					'x', new ItemStack(set.getKey()));
		}
	}

	private static void LoadStoneRecipes()
	{
		// Make slabs back into full blocks.
		for (int i = 0; i < 8; i++)
		{
			ItemStack currentBlock = null;
			ItemStack currentSlab = new ItemStack(Blocks.stone_slab, 1, i);

			// 2 is stone wood and is no longer craftable.
			if (i == 2)
			{
				continue;
			}

			switch (i)
			{
			case (0):
			{
				currentBlock = new ItemStack(Item.getItemFromBlock(Blocks.stone));
				break;
			}

			case (1):
			{
				currentBlock = new ItemStack(Item.getItemFromBlock(Blocks.sandstone));
				break;
			}

			case (3):
			{
				currentBlock = new ItemStack(Item.getItemFromBlock(Blocks.cobblestone));
				break;
			}

			case (4):
			{
				currentBlock = new ItemStack(Item.getItemFromBlock(Blocks.brick_block));
				break;
			}

			case (5):
			{
				currentBlock = new ItemStack(Item.getItemFromBlock(Blocks.stonebrick));
				break;
			}

			case (6):
			{
				currentBlock = new ItemStack(Item.getItemFromBlock(Blocks.nether_brick));
				break;
			}

			case (7):
			{
				currentBlock = new ItemStack(Item.getItemFromBlock(Blocks.quartz_block));
				break;
			}
			}

			GeneralRecipes.AddSlabRecipe(currentBlock, currentSlab, i);
		}

		GeneralRecipes.AddSlabRecipe(new ItemStack(Item.getItemFromBlock(Blocks.red_sandstone)), new ItemStack(Item.getItemFromBlock(Blocks.stone_slab2)), 1);
	}

	private static void AddSlabRecipe(ItemStack currentBlock, ItemStack currentSlab, int i)
	{
		// Need a different recipe for stone brick as this will interfere with the chiseled stone.
		if (i == 5 || i == 1 || i == 7)
		{
			GameRegistry.addRecipe(currentBlock, 
					"xx",
					"xx",
					'x', currentSlab);

			return;
		}

		GameRegistry.addRecipe(currentBlock, 
				"x",
				"x",
				'x', currentSlab);
	}

	private static void LoadArmorRecipes()
	{
		// Start with leather armor back to leather.
		GameRegistry.addShapelessRecipe(new ItemStack(Items.leather, 5), new ItemStack(Items.leather_helmet, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(Items.leather, 8), new ItemStack(Items.leather_chestplate, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(Items.leather, 7), new ItemStack(Items.leather_leggings, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(Items.leather, 4), new ItemStack(Items.leather_boots, 1, 0));

		// Smelt iron armor to ingots.
		GameRegistry.addSmelting(Items.iron_helmet, new ItemStack(Items.iron_ingot, 5), 1f);
		GameRegistry.addSmelting(Items.iron_chestplate, new ItemStack(Items.iron_ingot, 8), 1f);
		GameRegistry.addSmelting(Items.iron_leggings, new ItemStack(Items.iron_ingot, 7), 1f);
		GameRegistry.addSmelting(Items.iron_boots, new ItemStack(Items.iron_ingot, 4), 1f);

		// Smelt gold armor to ingots.
		GameRegistry.addSmelting(Items.golden_helmet, new ItemStack(Items.gold_ingot, 5), 1f);
		GameRegistry.addSmelting(Items.golden_chestplate, new ItemStack(Items.gold_ingot, 8), 1f);
		GameRegistry.addSmelting(Items.golden_leggings, new ItemStack(Items.gold_ingot, 7), 1f);
		GameRegistry.addSmelting(Items.golden_boots, new ItemStack(Items.gold_ingot, 4), 1f);

		// Smelt diamond armor to diamonds.
		GameRegistry.addSmelting(Items.diamond_helmet, new ItemStack(Items.diamond, 5), 1f);
		GameRegistry.addSmelting(Items.diamond_chestplate, new ItemStack(Items.diamond, 8), 1f);
		GameRegistry.addSmelting(Items.diamond_leggings, new ItemStack(Items.diamond, 7), 1f);
		GameRegistry.addSmelting(Items.diamond_boots, new ItemStack(Items.diamond, 4), 1f);

		// Chainmail recipes.
		GameRegistry.addRecipe(new ItemStack(Items.chainmail_helmet, 1), 
				"xxx",
				"y y",
				"   ",
				'x', Items.iron_ingot, 
				'y', Items.leather);

		GameRegistry.addRecipe(new ItemStack(Items.chainmail_chestplate, 1), 
				"y y",
				"yxy",
				"xxx",
				'x', Items.iron_ingot, 
				'y', Items.leather);

		GameRegistry.addRecipe(new ItemStack(Items.chainmail_leggings, 1), 
				"xxx",
				"y y",
				"y y",
				'x', Items.iron_ingot, 
				'y', Items.leather);

		GameRegistry.addRecipe(new ItemStack(Items.chainmail_boots, 1), 
				"   ",
				"y y",
				"x x",
				'x', Items.iron_ingot, 
				'y', Items.leather);
	}

	private static void LoadMiscRecipes()
	{
		// Rotten Flesh to leather.
		GameRegistry.addSmelting(Items.rotten_flesh, new ItemStack(Items.leather), 1f);

		// 4 Feathers and 1 string to 1 wool.
		GameRegistry.addRecipe(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1), 
				" x ",
				"xyx",
				" x ",
				'x', 
				Items.feather, 'y', Items.string);

		// Make a recipe for Clay: Sand + Water Bucket = Clay.
		GameRegistry.addShapelessRecipe(new ItemStack(Item.getItemFromBlock(Blocks.clay), 2), 
				new ItemStack(Items.water_bucket),
				new ItemStack(Item.getItemFromBlock(Blocks.sand)),
				new ItemStack(Item.getItemFromBlock(Blocks.gravel)));

		// Make a recipe for glowstone: redstone + gun powder + yellow dye.
		GameRegistry.addShapelessRecipe(new ItemStack(Items.glowstone_dust, 2), 
				new ItemStack(Items.redstone),
				new ItemStack(Items.gunpowder),
				new ItemStack(Items.dye, 1, 11));
	}

	private static void LoadNetherStarRecipe()
	{
		// 4 Quartz Blocks + 4 wither skulls + 1 Diamond Block = Nether Star
		GameRegistry.addRecipe(new ItemStack(Items.nether_star, 1), 
				"yxy",
				"xzx",
				"yxy",
				'x', new ItemStack(Item.getItemFromBlock(Blocks.quartz_block)), 
				'y', new ItemStack(Items.skull, 1, 1), 
				'z',  new ItemStack(Item.getItemFromBlock(Blocks.diamond_block)));
	}
}
