package requestHandler;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.JSONException;

import com.mashape.unirest.http.exceptions.UnirestException;

import cacheController.CacheManager;
import dataRetriever.RESTRequestFacade;

@Path("/")
public class RequestProcessor {
	
	/**
	 * All of the valid types that may be used in a REST query
	 */
	private static final String[] types = {"all", "ace", "archive", "embed", "images", "locations", "map", "weather", "configure"}; 
	
	/**
	 * A facade that can be used to communicate with the objects that create the calls to Auroras.live
	 */
	private RESTRequestFacade restFacade = new RESTRequestFacade();
	
	/**
	 * The main function for calling recieving requests from a browser with a REST query.
	 * @param ui
	 * @return a response to the request. Either coming directly from Auroras.live or the cache.
	 * @throws JSONException
	 * @throws UnirestException
	 */
	@GET
	@Produces({"application/json", "image/png", "image/jpg"})
	public Response lookupFunction(@Context UriInfo ui) throws JSONException, UnirestException {
		MultivaluedMap<String, String> parameterMap = ui.getQueryParameters();
		
		String type = null;
		
		// check for the proper parameters. If they aren't present, return a 400 response.
		if (parameterMap.containsKey("type"))
			type = parameterMap.get("type").get(0);
		else if (parameterMap.containsKey("command")) {
			type = parameterMap.get("command").get(0);
			if (type.equals("configure"))
				return CacheManager.getCacheManager().configure(parameterMap);
			else if (type.equals("clear"))
				return CacheManager.getCacheManager().clearCache();
			else if (type.equals("getCacheStatus"))
				return CacheManager.getCacheManager().getCacheStatus(parameterMap);
		}
		else
			return Response.status(400).entity("ERROR 400: Bad request").type("application/json").build();
			
		
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
	
		// if no-caching=false call the cache 
		if (noCaching.equals("false")) {
			System.out.println("Cache get");
			return CacheManager.getCacheManager().cacheGet(parameterMap);
		} // else call the Auroras.live API
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
