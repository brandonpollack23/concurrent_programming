package concurrenthashmaps;

import java.util.Iterator;
import java.util.Map.Entry;

public class test 
{
	public static void main(String[] args) 
	{
		CopyOnWriteHashMap<Integer, Integer> copyOnWrHashMap = new CopyOnWriteHashMap<Integer,Integer>();
		
		for(int i = 0; i < 10; i++)
		{
			copyOnWrHashMap.put(i, 2*i);
		}
		
		for(Iterator<Entry<Integer, Integer>> i = copyOnWrHashMap.entrySet().iterator(); i.hasNext();)
		{
			System.out.println(i.next());
		}		
		//hash has 0 to 9 in it now
		
		System.out.println("DONE WITH INIT OF MAP");
		System.out.println();
		
		PopulateHashMap test = new PopulateHashMap(copyOnWrHashMap, 10);
		
		Thread one = new Thread(test);		
		one.start(); //start a new thread that adds 10 to 19
		
		try 
		{
			one.join(); //join that thread, which should now have printed all of its values 0-19
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		
		System.out.println("DONE WITH FIRST THREAD");
		System.out.println();
		
		Thread two = new Thread(new PopulateHashMap(copyOnWrHashMap, 20));
		two.start(); //and another thread that adds 20 to 29

		try 
		{
			two.join(); //it should all print with 0-9 and 20 to 29
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		
		System.out.println("DONE WITH SECOND THREAD");
		System.out.println();
		
		for(Iterator<Entry<Integer, Integer>> i = copyOnWrHashMap.entrySet().iterator(); i.hasNext();)
		{
			System.out.println(i.next()); //our home thread should still be 0 to 9
		}
	}
}
