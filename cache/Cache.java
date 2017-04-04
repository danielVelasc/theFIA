package cache;

import java.util.HashMap;

import javax.ws.rs.core.Response;

public class Cache {
	
	// A map storing the time at which the current item was placed into the cache
	public HashMap<Integer, Long> lifeMap = new HashMap<>();
	
	// Two hashmaps. One for json responses and one for storing image responses as byte arrays.
	public HashMap<Integer, Response> cacheMap = new HashMap<>();
	public HashMap<Integer, byte[]> imageCacheMap = new HashMap<>();
	
	public Cache() { }
	
	/**
	 * The time to live for the specific types of objects stored inside the cache.
	 * These are stored in milliseconds.
	 * If the information is supposed to be stored indefinitely, this will be set -1.
	 */
	public long AceTTL;
	public long AllTTL;
	public long ArchiveTTL;
	public long EmbedTTL;
	public long ImageTTL;
	public long LocationTTL;
	public long MapTTL;
	public long WeatherTTL;

}
