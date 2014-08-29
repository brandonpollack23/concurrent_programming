package concurrenthashmaps;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class CopyOnWriteHashMap<K, V> implements Map<K,V>, Cloneable //For now, use caution when putting in mutable objects
{
	private volatile HashMap<K, V> hashMap;
	private int initialCapacity;
	private float loadFactor;
	private boolean isNewCopy = true;
	
	public CopyOnWriteHashMap()
	{
		hashMap = new HashMap<K,V>();
		this.initialCapacity = 16;
		this.loadFactor = (float) .75;
	}
	
	public CopyOnWriteHashMap(int initialCapacity)
	{
		hashMap = new HashMap<K,V>(initialCapacity);
		this.loadFactor = (float) .75;
	}
	
	public CopyOnWriteHashMap(int initialCapacity, float loadFactor)
	{
		hashMap = new HashMap<K,V>(initialCapacity,loadFactor);
		this.initialCapacity = initialCapacity;
		this.loadFactor = loadFactor;
	}
	
	public CopyOnWriteHashMap(Map<? extends K,? extends V> m)
	{
		hashMap = new HashMap<K,V>(m);
	}
	
	@SuppressWarnings("unchecked")
	public CopyOnWriteHashMap(HashMap<? extends K,? extends V> m)
	{
		hashMap = (HashMap<K, V>) m;
		isNewCopy = false;
	}
	
	public synchronized void clear() //this is a write op, have to copy
	{
		if(isNewCopy) hashMap.clear();
		else
		{
			hashMap = new HashMap<K,V>(initialCapacity,loadFactor); //empty hashmap with same parameters
		}
	}
	
	public Object clone() //just call the super class clone
	{
		try
		{
			return super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			throw new InternalError();
		}
	}
	
	public boolean containsKey(Object key)
	{
		return hashMap.containsKey(key);
	}
	
	public boolean containsValue(Object value)
	{
		return hashMap.containsValue(value);
	}
	
	public Set<Map.Entry<K,V>> entrySet()
	{
		return hashMap.entrySet();
	}
	
	public V get(Object key)
	{
		return hashMap.get(key);
	}
	
	public boolean isEmpty()
	{
		return hashMap.isEmpty();
	}
	
	public Set<K> keySet()
	{
		return hashMap.keySet();
	}
	
	public synchronized V put(K key, V value) //THIS OP IS A WRITE
	{
		if(isNewCopy) return hashMap.put(key, value);
		else
		{
			isNewCopy = true;
			
			HashMap<K,V> newHashMap = new HashMap<K,V>(hashMap); //make a copy of hashmap
			
			V rvalue = newHashMap.put(key,value); //put returns previous value, and puts this new value into the new array for us
			
			hashMap = newHashMap; //set the new reference to the copied array
			
			return rvalue;
		}
	}
	
	public synchronized void putAll(Map<? extends K, ? extends V> m)
	{
		if(isNewCopy) hashMap.putAll(m);
		else
		{
			isNewCopy = true;
			
			HashMap<K, V> newHashMap = new HashMap<K,V>(hashMap); //save old entries
			
			newHashMap.putAll(m);
			
			hashMap = newHashMap;
		}
	}
	
	public synchronized V remove(Object key)
	{
		if(isNewCopy) return hashMap.remove(key);
		else
		{
			isNewCopy = true;
			
			HashMap<K,V> newHashMap = new HashMap<K,V>(hashMap);
			
			V rvalue = newHashMap.remove(key); //this returns the value deleted
			
			hashMap = newHashMap;
			
			return rvalue;
		}		
	}
	
	public int size()
	{
		return hashMap.size();
	}
	
	public Collection<V> values()
	{
		return hashMap.values();
	}
	
	
}
