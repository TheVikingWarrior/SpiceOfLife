package squeek.spiceoflife.foodtracker;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import squeek.spiceoflife.ModConfig;
import squeek.spiceoflife.proxy.ProxyAgriculture;
import squeek.spiceoflife.proxy.ProxyHungerOverhaul;
import squeek.spiceoflife.proxy.ProxyMariculture;
import squeek.spiceoflife.proxy.ProxyTiC;

public class FoodValues
{
	public int hunger;
	public float saturationModifier;

	public FoodValues(int hunger, float saturationModifier)
	{
		this.hunger = hunger;
		this.saturationModifier = saturationModifier;
	}

	public float getSaturationIncrement()
	{
		return Math.min(20, hunger * saturationModifier * 2f);
	}

	public FoodValues modify(float modifier)
	{
		hunger = (int) ModConfig.FOOD_HUNGER_ROUNDING_MODE.round((float) hunger * modifier);

		if (ModConfig.AFFECT_FOOD_SATURATION_MODIFIERS)
		{
			if (saturationModifier < 0 && ModConfig.AFFECT_NEGATIVE_FOOD_SATURATION_MODIFIERS)
				saturationModifier *= 2 - modifier;
			else if (saturationModifier > 0)
				saturationModifier *= modifier;
		}

		return this;
	}

	public FoodValues getModified(float modifier)
	{
		return new FoodValues(hunger, saturationModifier).modify(modifier);
	}

	public static FoodValues get(ItemStack food)
	{
		if (food.getItem() instanceof ItemFood)
		{
			ItemFood itemFood = (ItemFood) food.getItem();

			if (ProxyTiC.isSpecialFood(itemFood))
				return ProxyTiC.getSpecialFoodValues(food);
			else if (ProxyHungerOverhaul.foodValuesWillBeModified(food))
				return ProxyHungerOverhaul.getModifiedFoodValues(itemFood);
			else
				return new FoodValues(itemFood.getHealAmount(), itemFood.getSaturationModifier());
		}
		else if (ProxyAgriculture.isFood(food))
			return ProxyAgriculture.getFoodValues(food);
		else if (ProxyMariculture.isFood(food))
			return ProxyMariculture.getFoodValues(food);

		return null;
	}

	public static FoodValues getModified(ItemStack food, float modifier)
	{
		return FoodValues.get(food).modify(modifier);
	}

	public static FoodValues getModified(ItemStack food, EntityPlayer player)
	{
		FoodValues defaultFoodValues = FoodValues.get(food);
		return defaultFoodValues.modify(FoodModifier.getFoodModifier(player, food, player.getFoodStats(), defaultFoodValues.hunger, defaultFoodValues.saturationModifier));
	}

	public static float getSaturationModifierFromIncrement(float saturationIncrement, int hunger)
	{
		return hunger != 0 ? saturationIncrement / (hunger * 2f) : 0f;
	}
}
