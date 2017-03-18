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

import dataRetriever.RESTRequestFacade;

@Path("/")
public class RequestProcessor {
	
	public static final String[] types = {"all", "ace", "archive", "embed", "images", "locations", "map", "weather"}; 
	
	private RESTRequestFacade restFacade = new RESTRequestFacade();
	
	@GET
	@Produces({"application/json", "image/png"})
	public Response lookupFunction(@Context UriInfo ui) throws JSONException, UnirestException{
		MultivaluedMap<String, String> parameterMap = ui.getQueryParameters();

		String type = parameterMap.get("type").get(0);
		
		for (int i = 0; i < types.length; i++ ) {
			
			if (types[i].compareToIgnoreCase(type) == 0)
				break;
				
			if (i == types.length - 1)
				return Response.status(400).entity("ERROR 400: " + type + " is not a valid type").type("application/json").build();
			
		}
		

		
		type = type.toLowerCase();
		
		if((type.equals("images" ) && !parameterMap.containsKey("action")) || type.equals("embed" ) || type.equals("map"))
		{
			return restFacade.composeImageRequest(parameterMap);
		}
		else {
			return restFacade.composeAuroraRequest(parameterMap);
		}
	}
}
