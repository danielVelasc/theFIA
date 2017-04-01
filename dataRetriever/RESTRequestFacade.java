package dataRetriever;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.json.JSONException;

import com.mashape.unirest.http.exceptions.UnirestException;


public class RESTRequestFacade {
	
	/**
	 * These are the classes that are being abstracted by the facade.
	 */
	private AuroraRequestSpawner auroraRS = new AuroraRequestSpawner();
	private MapRequestSpawner mapRS = new MapRequestSpawner();
	
	/**
	 * Facilitates a call to the Auroras.live API by sending it to the AuroraRequestSpawner
	 * @param parameterMap The parameters from the call to the server.
	 * @return a response created by the Auroras.live API (json object)
	 * @throws UnirestException
	 * @throws JSONException
	 */
	public Response composeAuroraRequest(MultivaluedMap<String, String> parameterMap) throws UnirestException, JSONException {
		
		System.out.println("Get from API");
		
		return auroraRS.createStandardRequest(parameterMap);
		
	}
	
	/**
	 * Facilitates a call to the Auroras.live API by sending it to either the google API or the Auroras.live API.
	 * @param parameterMap The parameters from the call to the server.
	 * @return
	 * @throws UnirestException
	 * @throws JSONException
	 */
	public Response composeImageRequest(MultivaluedMap<String, String> parameterMap) throws UnirestException, JSONException {
		
		System.out.println("Get from API");
		
		String type = parameterMap.get("type").get(0);
		
		if (type.compareToIgnoreCase("map") == 0) {
			return mapRS.createMapRequest(parameterMap);
		}
		else {
			return auroraRS.createImageRequest(parameterMap);
		}
		
	}
	
	/**
	 * A function needed to return the byte array to be stored by the cache.
	 * @param parameterMap The parameters from the call to the server.
	 * @return a byte array containing an image to store in the cache.
	 * @throws JSONException
	 * @throws UnirestException
	 */
	public byte[] getCacheImage(MultivaluedMap<String, String> parameterMap) throws JSONException, UnirestException {
		
		System.out.println("Get from API");
		
		String type = parameterMap.get("type").get(0);
		
		if (type.compareToIgnoreCase("map") == 0) {
			return mapRS.createCacheMapRequest(parameterMap);
		}
		else {
			return auroraRS.createCacheImageRequest(parameterMap);
		}
	}
	
}
