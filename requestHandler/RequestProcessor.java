package requestHandler;

import java.util.HashMap;
import java.util.Iterator;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.JSONException;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import cacheController.CacheManager;
import dataRetriever.RESTRequestFacade;

@Path("/")
public class RequestProcessor {
	
	private static final String[] types = {"all", "ace", "archive", "embed", "images", "locations", "map", "weather", "configure"}; 
	
	private RESTRequestFacade restFacade = new RESTRequestFacade();
	
	private CacheManager cacheManager = new CacheManager();
	
	public RequestProcessor() { System.out.println("Created!!!!!!!!!!!!!!!!!!!!!!!!"); }
	
	@GET
	@Produces({"application/json", "image/png", "image/jpg"})
	public Response lookupFunction(@Context UriInfo ui) throws JSONException, UnirestException {
		MultivaluedMap<String, String> parameterMap = ui.getQueryParameters();

		System.out.println("Lookup Function");
		
		String type = parameterMap.get("type").get(0);
		
		for (int i = 0; i < types.length; i++ ) {
			
			if (types[i].compareToIgnoreCase(type) == 0)
				break;
				
			if (i == types.length - 1)
				return Response.status(400).entity("ERROR 400: " + type + " is not a valid type").type("application/json").build();
			
		}
		
		
		type = type.toLowerCase();
		
		String noCaching = null;
		
		// pull the no-caching variable from the map to determine if caching should occur
		// current default is to call auroras.live if the variable is absent.
		if (parameterMap.containsKey("no-caching"))
			noCaching = parameterMap.get("no-caching").get(0);
		else
			noCaching = "true";
		
		noCaching = noCaching.toLowerCase();
		
		System.out.println("cache decision");
		
		if (type.equals("configure")) {
			cacheManager.configure(parameterMap);
			System.out.println("Cache configured");
			return Response.status(200).entity("Cache configured successfully").type("application/json").build();
		}
		
		if (noCaching.equals("false")) {
			System.out.println("Cache get");
			return cacheManager.cacheGet(parameterMap);
		}
		else {
			if((type.equals("images" ) && !parameterMap.containsKey("action")) || type.equals("embed" ) || type.equals("map"))
			{
				return restFacade.composeImageRequest(parameterMap);
			}
			else {
				return restFacade.composeAuroraRequest(parameterMap);
			}
		}
	}
}
