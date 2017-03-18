package dataRetriever;

import java.io.InputStream;
import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.json.JSONException;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;


public class RESTRequestFacade {
	
	/**
	 * These are the classes that are being abstracted by the facade.
	 */
	private AuroraRequestSpawner auroraRS = new AuroraRequestSpawner();
	private MapRequestSpawner mapRS = new MapRequestSpawner();
	
	
	
	public Response composeAuroraRequest(MultivaluedMap<String, String> parameterMap) throws UnirestException, JSONException{
		
		return auroraRS.createStandardRequest(parameterMap);
		
	}
	
	public Response composeImageRequest(MultivaluedMap<String, String> parameterMap) throws UnirestException, JSONException {
		
		String type = parameterMap.get("type").get(0);
		
		if (type.compareToIgnoreCase("map") == 0) {
			return mapRS.createMapRequest(parameterMap);
		}
		else {
			return auroraRS.createImageRequest(parameterMap);
		}
		
	}
	

}
