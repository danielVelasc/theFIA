package cache;

import java.util.HashMap;

import javax.ws.rs.core.Response;

public class Cache {
	
	/**
	 * Format for the HashMap key String is that the exact same as for
	 * the aurora.live string used for a query request except it starts at "type"
	 * eg. a key could be { type=locations }
	 */
	public HashMap<Integer, Long> lifeMap = new HashMap<>();
	
	/**
	 * 
	 * 
	 * PLACEHOLDER CACHE
	 * 
	 * 
	 * 
	 */
	public HashMap<Integer, Response> cacheMap = new HashMap<>();
	public HashMap<Integer, byte[]> imageCacheMap = new HashMap<>();
	
	private static Cache cache = new Cache();
	
	public static Cache getData() { return cache; }
	
	private Cache() {
		
		System.out.println("Cache Created");
		
	}
	
	

}
