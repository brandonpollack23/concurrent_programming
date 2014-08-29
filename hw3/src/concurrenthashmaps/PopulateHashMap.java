package concurrenthashmaps;

import java.util.Iterator;
import java.util.Map.Entry;


public class PopulateHashMap implements Runnable 
{
	public volatile CopyOnWriteHashMap<Integer, Integer> map;
	private int startingNum;
	
	public PopulateHashMap(CopyOnWriteHashMap<Integer, Integer> m, int startingNum)
	{
		this.map = new CopyOnWriteHashMap<Integer, Integer>(m);
		this.startingNum = startingNum;
	}
	
	@Override
	public void run() 
	{		
		for(int i = startingNum; i < startingNum + 10; i++)
		{
			map.put(i, 2*i);
		}

		//now print my hash map for the test
		for(Iterator<Entry<Integer, Integer>> i = map.entrySet().iterator(); i.hasNext();)
		{
			System.out.println(i.next());
		}
	}
}
