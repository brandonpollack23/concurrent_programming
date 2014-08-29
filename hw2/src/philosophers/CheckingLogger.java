package philosophers;

import java.util.ArrayList;

public class CheckingLogger implements Logger 
{
	ArrayList<Entry> log;
	int numphils;
	int[] timesLeft;
	
	public CheckingLogger(int numPhils, int timesToEat)
	{
		this.numphils = numPhils;
		this.log = new ArrayList<>();
		
		timesLeft = new int[numPhils];
		
		for(int i = 0; i < numPhils; i++)
		{
			timesLeft[i] = timesToEat;
		}
	}

	@Override
	public boolean isCorrect()
	{
		for(int i = 0; i < numphils; i++)
		{
			if(log.get(i).didFail()) //if any log entry was illegal
			{
				return false; //we failed
			}
			if(timesLeft[i] != 0) //if any phil didn't eat the number of times specified
			{
				return false; //we failed
			}
		}
		return true; //otherwise we are golden
	}

	@Override
	public synchronized void eats(int seat) 
	{
		boolean failed = false;
		boolean leftEating = true;
		boolean rightEating = true;
				
		//TODO check for failure
		/* General idea is that if the last thing both the phil's to left and right did 
		 * was think then you don't fail
		 * otherwise you failed
		 */
		
		// dec the tiems this guy has to eat
		timesLeft[seat]--;
		
		for(int i = log.size()-1; i >= 0; i--) //iterate in reverse through entire log, trying to find entries of left and right phils
		{
			int currentPhil = log.get(i).getSeat();
			if(currentPhil == nextSeat(seat) || currentPhil == prevSeat(seat)) //if this is a phil on either side
			{
				if(log.get(currentPhil).wasEating() && timesLeft[currentPhil] != 0) //if that phil was eating and it wasn't his last time
				{
					failed = true; //failure, there is a problem in general with the last time eating, no way to check if they were still eating or not (they don't think before they leave table)
				}
				else if(currentPhil == nextSeat(seat)) //if that phil was right
				{
					rightEating = false; //he wasn't eating
				}
				else if(currentPhil == prevSeat(seat)) //repeat for left
				{
					leftEating = false;
				}
			}			
			if(failed == true || (leftEating == false && rightEating == false)) //if we found that both left and right were not eating
			{
				break; //exit condition, we affirm the last thing both of them did was think or one of them ate
			}
			//exit condition, we iterated to the 0th log entry and one or more of the surrounding phils has not ate or thought yet
			//if one ate we would have exited
		}
				
		log.add(new Entry(seat,true,failed));		
	}

	@Override
	public synchronized void thinks(int seat) 
	{
		log.add((new Entry(seat,false,false))); //no way to fail at thinking, does not need a lock
	}
	
	private int nextSeat(int seat)
	{
		if(seat == numphils - 1)
		{
			return 0;
		}
		else return seat + 1;
	}
	
	private int prevSeat(int seat)
	{
		if(seat == 0)
		{
			return numphils - 1;
		}
		else return seat - 1;
	}

}


/*Problems in table class that could cause this to still work incorrectly:
*As mentioned above, the last thing a phil does is eat, so there is no way to really know (with this interface of logger) when they are done eating
*An easy solutionw ould be to add a method to the interface that lets the logger know when a phil is done eating
*and then that can be taken into account.
*
*As it stands, the very last time a phil eats (and the amount of time between him starting to think and finish eating each time, very small window)
*has undefined behaviour.  This could(unlikely but could) cause the logger to return a failure even though the program ran successfully.
*/