package concurrenthashmaps;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BlockingConcurrentHashMap<K, V> implements Map<K,V>, Cloneable //For now, use caution when putting in mutable objects
{
	private volatile HashMap<K, V> hashMap;
	private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();;
	private ReentrantReadWriteLock.ReadLock r = rwl.readLock();
	private ReentrantReadWriteLock.WriteLock w = rwl.writeLock();
	
	public BlockingConcurrentHashMap()
	{
		hashMap = new HashMap<K,V>();
	}
	
	public BlockingConcurrentHashMap(int initialCapacity)
	{
		hashMap = new HashMap<K,V>(initialCapacity);
		
	}
	
	public BlockingConcurrentHashMap(int initialCapacity, float loadFactor)
	{
		hashMap = new HashMap<K,V>(initialCapacity,loadFactor);
	}
	
	public BlockingConcurrentHashMap(Map<? extends K,? extends V> m)
	{
		hashMap = new HashMap<K,V>(m);
	}

	@Override
	public void clear()
	{
		w.lock();
		try
		{
			hashMap.clear();
		}
		finally
		{
			w.unlock();
		}
	}

	@Override
	public boolean containsKey(Object key)
	{
		r.lock();
		
		boolean rvalue;
		
		try
		{
			rvalue = hashMap.containsKey(key);
		}
		finally
		{
			r.unlock();
		}
		return rvalue;
	}

	@Override
	public boolean containsValue(Object value)
	{
		r.lock();
		
		boolean rvalue;
		
		try
		{
			rvalue = hashMap.containsValue(value);
		}
		finally
		{
			r.unlock();
		}
		return rvalue;
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet()
	{
		r.lock();
		
		Set<java.util.Map.Entry<K, V>> rvalue;
		
		try
		{
			rvalue = hashMap.entrySet();
		}
		finally
		{
			r.unlock();
		}
		return rvalue;
	}

	@Override
	public V get(Object key)
	{
		synchronized(this) //atomic await
		{
			while(!containsKey(key)) //while I do not have this key
			{
				try
				{
					this.wait(); //wait until notified
				}
				catch(InterruptedException e)
				{
					//do nothing
				}
			}
		}
		r.lock(); //now that I have it, go ahead and do the rlock and continue as normal
		
		V rvalue;
		
		try
		{
			rvalue = hashMap.get(key);
		}
		finally
		{
			r.unlock();
		}
		return rvalue;
	}

	@Override
	public boolean isEmpty()
	{
		r.lock();
		
		boolean rvalue;
		
		try
		{
			rvalue = hashMap.isEmpty();
		}
		finally
		{
			r.unlock();
		}
		return rvalue;
	}

	@Override
	public Set<K> keySet()
	{
		r.lock();
		
		Set<K> rvalue;
		
		try
		{
			rvalue = hashMap.keySet();
		}
		finally
		{
			r.unlock();
		}
		return rvalue;
	}

	@Override
	public V put(K key, V value)
	{
		w.lock();
		
		V rvalue;
		
		try
		{
			rvalue = hashMap.put(key, value);
		}
		finally
		{
			w.unlock();
		}
		
		this.notifyAll(); //notify all waiting threads that are blocking in order to get
		
		return rvalue;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m)
	{
		w.lock();
		try
		{
			hashMap.putAll(m);
		}
		finally
		{
			w.unlock();
		}
		this.notifyAll(); //notify all waiting threads that are blocking in order to get
	}

	@Override
	public V remove(Object key)
	{
		w.lock();
		
		V rvalue;
		
		try
		{
			rvalue = hashMap.remove(key);
		}
		finally
		{
			w.unlock();
		}
		return rvalue;
	}

	@Override
	public int size()
	{
		r.lock();
		
		int rvalue;
		try
		{
			rvalue = hashMap.size();
		}
		finally
		{
			r.unlock();
		}
		return rvalue;
	}

	@Override
	public Collection<V> values()
	{
		r.lock();
		
		Collection<V> rvalue;
		try
		{
			rvalue = hashMap.values();
		}
		finally
		{
			r.unlock();
		}
		return rvalue;
	}	
}
