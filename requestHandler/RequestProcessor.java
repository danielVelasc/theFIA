package requestHandler;

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

@Path("/")
public class RequestProcessor {
	@GET
	@Produces("application/json")
	public Response lookupFunction(@Context UriInfo ui) throws JSONException, UnirestException{
		MultivaluedMap<String, String> parameterMap = ui.getQueryParameters();
		String auroraString = new String("http://api.auroras.live/v1/?type=");
		auroraString = auroraString + parameterMap.get("type").get(0);
		
		Iterator<String> it = parameterMap.keySet().iterator();
		while(it.hasNext()){
			String theKey = (String)it.next();
			String theValue = parameterMap.get(theKey).get(0);
			
			if(!theKey.equals("type"))
			{
			auroraString = auroraString + "&" +theKey+"=";
			auroraString = auroraString + theValue;
			}
		}
		
		System.out.println(auroraString);
		JSONObject jsonObject = new JSONObject();
		HttpResponse<JsonNode> response = Unirest.get(auroraString).header("cookie", "PHPSESSID=MW2MMg7reEHx0vQPXaKen0").asJson();
		
		jsonObject = response.getBody().getObject();
		
		return Response.status(200).entity(response.getBody().toString()).build();
	}

}
