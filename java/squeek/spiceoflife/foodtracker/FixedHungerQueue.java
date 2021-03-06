package squeek.spiceoflife.foodtracker;

public class FixedHungerQueue extends FixedFoodQueue
{
	private static final long serialVersionUID = -1347372098150405272L;
	protected int hunger;
	protected int hungerOverflow;

	public FixedHungerQueue(int limit)
	{
		super(limit);
	}
	
	@Override
	public boolean add(FoodEaten foodEaten)
	{
		boolean added = super.add(foodEaten);
		if (added)
		{
			hunger += foodEaten.hungerRestored;
			trimToMaxSize();
		}
		return added;
	}
	
	@Override
	public void clear()
	{
		super.clear();
		hunger = hungerOverflow = 0;
	}
	
	public int hunger()
	{
		return hunger;
	}
	
	@Override
	protected void trimToMaxSize()
	{
		while (hunger > limit)
		{
			hunger -= 1;
			hungerOverflow += 1;
			
			while (hungerOverflow >= peekFirst().hungerRestored)
			{
				hungerOverflow -= removeFirst().hungerRestored;
			}
		}
	}
}
