package cacheController;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.json.JSONException;

import com.mashape.unirest.http.exceptions.UnirestException;

import cache.Cache;
import dataRetriever.RESTRequestFacade;

public class CacheManager {

	
	/**
	 * Used to store the administrator information for the admin users.
	 */
	private ArrayList<String[]> users = new ArrayList<>();
	
	/**
	 * Can be used to communicate with the auroras.live API.
	 */
	private RESTRequestFacade restFacade = new RESTRequestFacade();
	
	/**
	 * The constructor for the CacheManager object. This sets the usernames and passwords for
	 * configure command.
	 */
	public CacheManager() {
		
		String[] daniel = {"dvelasco", "papi"};
		String[] quinn = {"quinnbischoff", "queenb"};
		String[] eric = {"ericmatteucci", "AwkwardAardvark420"};
		
		users.add(daniel);
		users.add(eric);
		users.add(quinn);
		
	}
	
	/**
	 * The method used to make cache decisions and either call the Auroras.live API or the
	 * cache to get the needed information to be sent back as a Response.
	 * @param parameterMap The parameters from the call to the server.
	 * @return a formatted Response from either the Auroras.live server or the cache.
	 * @throws UnirestException
	 * @throws JSONException
	 */
	public Response cacheGet(MultivaluedMap<String, String> parameterMap) throws UnirestException, JSONException {
		
		// get the type of request
		String type = parameterMap.get("type").get(0);
		
		// generate the cache key
		int hashKey = createKey(parameterMap);
		
		boolean useCache = false;
		
		// check if the cache contains the specified cache key
		if (Cache.getData().lifeMap.containsKey(hashKey)) {
			
			String typeCondition = type.toLowerCase();
			
			long lifeInMillis = System.currentTimeMillis() - Cache.getData().lifeMap.get(hashKey);
			
			// determine if the time to live of the specific item in the cache has expired
			switch(typeCondition) {
			
			case "ace":
				if (lifeInMillis < Cache.getData().AceTTL || Cache.getData().AceTTL == -1)
					useCache = true;
				break;
			case "all":
				if (lifeInMillis < Cache.getData().AllTTL || Cache.getData().AllTTL == -1)
					useCache = true;
				break;
			case "archive":
				if (lifeInMillis < Cache.getData().ArchiveTTL || Cache.getData().ArchiveTTL == -1)
					useCache = true;
				break;
			case "embed":
				if (lifeInMillis < Cache.getData().EmbedTTL || Cache.getData().EmbedTTL == -1)
					useCache = true;
				break;
			case "images":
				if (lifeInMillis < Cache.getData().ImageTTL || Cache.getData().ImageTTL == -1)
					useCache = true;
				break;
			case "locations":
				if (lifeInMillis < Cache.getData().LocationTTL || Cache.getData().LocationTTL == -1)
					useCache = true;
				break;
			case "map":
				if (lifeInMillis < Cache.getData().MapTTL || Cache.getData().MapTTL == -1)
					useCache = true;
				break;
			case "weather":
				if (lifeInMillis < Cache.getData().WeatherTTL|| Cache.getData().WeatherTTL == -1)
					useCache = true;
				break;
			default:
				break;
					
			}
			
		}
		
		Response response = null;
		
		// if the time to live is valid or if item has an indefinite cache, retrieve the item from the cache 
		if (useCache) {
			// call the cache here
			System.out.println("Get from cache");
			
			if (type.equals("map") || type.equals("images")) {
				byte[] entityBody = Cache.getData().imageCacheMap.get(hashKey);
				
				InputStream stream = new ByteArrayInputStream(entityBody);
				
				return Response.status(200).entity(stream).type("image/jpg").build();
			}
				
			response = Cache.getData().cacheMap.get(hashKey);
			
		}
		else {

			
			byte[] imageStore = null;
			
			// call the needed function in the facade to delegate to the proper calls to the 
			// Auroras.live API
			if((type.equals("images" ) && !parameterMap.containsKey("action")) || type.equals("embed" ) || type.equals("map"))
			{
				response = restFacade.composeImageRequest(parameterMap);
				imageStore = restFacade.getCacheImage(parameterMap);
				
				// store the image in the cache
				if (imageStore != null)
					cacheStore(hashKey, imageStore);
			}
			else {
				response = restFacade.composeAuroraRequest(parameterMap);
				cacheStore(hashKey, response);
			}
			
		}
		
		// return the formatted response from the Auroras.live API
		return response;
		
	}
	
	/**
	 * Used to store the json application responses in the cache.
	 * @param hashKey The cache key for the specified request item. 
	 * @param response The formatted json response to be stored in the cache.
	 */
	private void cacheStore(int hashKey, Response response) {
		
		Cache.getData().cacheMap.put(hashKey, response);
		
		Cache.getData().lifeMap.put(hashKey, System.currentTimeMillis());
		
	}
	
	/**
	 * Used to store the image type objects in the cache.
	 * @param hashKey The cache key for the specified request item.
	 * @param byteArray The byte array containing the image to be stored in the cache.
	 */
	private void cacheStore(int hashKey, byte[] byteArray) {
	
		Cache.getData().imageCacheMap.put(hashKey, byteArray);
		
		Cache.getData().lifeMap.put(hashKey, System.currentTimeMillis());
		
	}
	
	/**
	 * The valid arguments that can be configured within the cache.
	 */
	private final String[] configureArguments = {"all", "ace", "archive", "embed", "images", "locations", "map", "weather" };
	
	private boolean isValidArgument(String str) {
		
		for (int i = 0; i < configureArguments.length; i++ ) {
			
			if (configureArguments[i].compareToIgnoreCase(str) == 0)
				return true;
			
		}
		
		return false;

	}
	
	/** 
	 * Use to configure the cache and set the times to live for the specific items in the cache.
	 * @param parameterMap The parameters from the call to the server.
	 * @return A response that signifies if the configure was successful or not.
	 */
	public Response configure(MultivaluedMap<String, String> parameterMap) {
		
		String username = null;
		String password = null;
		
		boolean validAdmin = false;
		
		// check if the username and password that were entered are valid.
		if (parameterMap.containsKey("username") && parameterMap.containsKey("password")) {
			username = parameterMap.get("username").get(0);
			password = parameterMap.get("password").get(0);
			
			for (int i = 0; i < users.size(); i++) {
				
				if (users.get(i)[0].equals(username) && users.get(i)[1].equals(password)) {
					validAdmin = true;
					break;
				}
			}	
		}
		
		// if the username and password were not valid, return with an error Response.
		if(!validAdmin)
			return Response.status(400).entity("Username and password are incorrect").type("application/json").build();
		
		// Instantiate the boolean array to determine which times to live need to be set to a default value.
		boolean[] setDefault = new boolean[8];
		for(int i = 0; i < setDefault.length; i++)
			setDefault[i] = true;
		
		// Cycle through the parameterMap and find out which times to live need to 
		// set to the specified values.
		Iterator<String> it = parameterMap.keySet().iterator();
		while(it.hasNext()){
			String theKey = (String)it.next();
			String theValue = parameterMap.get(theKey).get(0);
			
			if(isValidArgument(theKey)) {
				
				String switchArgument = theKey.toLowerCase();
				long timeInMillis = Long.parseLong(theValue);
				
				switch(switchArgument) {
				
				case "ace":
					Cache.getData().AceTTL = timeInMillis;
					setDefault[0] = false;
					break;
				case "archive":
					Cache.getData().ArchiveTTL = timeInMillis;
					setDefault[1] = false;
					break;
				case "embed":
					Cache.getData().EmbedTTL = timeInMillis;
					setDefault[2] = false;
					break;
				case "images":
					Cache.getData().ImageTTL = timeInMillis;
					setDefault[3] = false;
					break;
				case "locations":
					Cache.getData().LocationTTL = timeInMillis;
					setDefault[4] = false;
					break;
				case "map":
					Cache.getData().MapTTL = timeInMillis;
					setDefault[5] = false;
					break;
				case "weather":
					Cache.getData().WeatherTTL = timeInMillis;
					setDefault[6] = false;
					break;
				case "all":
					Cache.getData().AllTTL = timeInMillis;
					setDefault[7] = false;
					break;
				default:
					break;
						
				}
			}
			
		}
		
		// Set the rest of the times to live to the default values.
		if (parameterMap.containsKey("default")) {
			
			long timeInMillis = Long.parseLong(parameterMap.get("default").get(0));
			
			for (int i = 0; i < setDefault.length; i++) {
				
				if (!setDefault[i])
					continue;
				
				switch(i) {
				
				case 0:
					Cache.getData().AceTTL = timeInMillis;
					break;
				case 1:
					Cache.getData().ArchiveTTL = timeInMillis;
					break;
				case 2:
					Cache.getData().EmbedTTL = timeInMillis;
					break;
				case 3:
					Cache.getData().ImageTTL = timeInMillis;
					break;
				case 4:
					Cache.getData().LocationTTL = timeInMillis;
					break;
				case 5:
					Cache.getData().MapTTL = timeInMillis;
					break;
				case 6:
					Cache.getData().WeatherTTL = timeInMillis;
					break;
				case 7:
					Cache.getData().AllTTL = timeInMillis;
					break;
				default:
					break;
						
				}
			}
			
		}
		
		// return with a Response saying that the configuration was successful
		return Response.status(200).entity("Cache configured successfully").type("application/json").build();
		
	}
	
	/**
	 * Creates the cache key for the cache.
	 * @param parameterMap The parameters from the call to the server.
	 * @return The newly created cache key.
	 */
	private int createKey(MultivaluedMap<String, String> parameterMap) {
		
		int key = 0;
		
		// iterate through and find create the cache key from the parameters.
		Iterator<String> it = parameterMap.keySet().iterator();
		while(it.hasNext()){
			String theKey = (String)it.next();
			String theValue = parameterMap.get(theKey).get(0);
			
			key += theKey.hashCode();
			key += theValue.hashCode();

			
		}
		
		return key;
		
	}
	
	/**
	 * Use to clear the cache of all entries.
	 * @return A response stating that the purge was successful.
	 */
	public Response clearCache() {
		
		// clear all of the cache components.
		Cache.getData().lifeMap.clear();
		Cache.getData().cacheMap.clear();
		Cache.getData().imageCacheMap.clear();
		
		// return with a message stating that the clearing was successful.
		return Response.status(200).entity("Cache cleared successfully").type("application/json").build();
		
	}

}
