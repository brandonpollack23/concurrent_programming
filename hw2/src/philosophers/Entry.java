package philosophers;

public class Entry 
{
	private int seat;
	private boolean failed,isEating;
	
	public Entry(int seat, boolean isEating, boolean failed)
	{
		this.seat = seat;
		this.failed = failed;
	}
	
	public int getSeat()
	{
		return seat;
	}
	
	public boolean wasEating()
	{
		return isEating;
	}
	
	public boolean didFail()
	{
		return failed;
	}
}
