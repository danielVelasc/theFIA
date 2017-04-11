package cache;

import java.util.HashMap;

import javax.ws.rs.core.Response;

import org.json.JSONObject;

public class Cache {
	
	// A map storing the time at which the current item was placed into the cache
	public HashMap<Integer, Long> lifeMap = new HashMap<>();
	
	// Two hashmaps. One for json responses and one for storing image responses as byte arrays.
	public HashMap<Integer, Response> cacheMap = new HashMap<>();
	public HashMap<Integer, byte[]> imageCacheMap = new HashMap<>();
	
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
	
	/**
	 * Constructs a cache with a default time to live
	 * @param defaultTTL
	 */
	public Cache(long defaultTTL) {
		AceTTL = defaultTTL;
		AllTTL = defaultTTL;
		ArchiveTTL = defaultTTL;
		EmbedTTL = defaultTTL;
		ImageTTL = defaultTTL;
		LocationTTL = defaultTTL;
		MapTTL = defaultTTL;
		WeatherTTL = defaultTTL;
	}
	
	/**
	 * Returns a JSONObject containing the time to live for all different types held
	 * @return
	 */
	public JSONObject getTTLs() {
		JSONObject TTLs = new JSONObject();
		
		TTLs.put("Ace", AceTTL);
		TTLs.put("All", AllTTL);
		TTLs.put("Archive", ArchiveTTL);
		TTLs.put("Embed", EmbedTTL);
		TTLs.put("Image", ImageTTL);
		TTLs.put("Location", LocationTTL);
		TTLs.put("Map", MapTTL);
		TTLs.put("Weather", WeatherTTL);
		
		return TTLs;
	}
	


}
