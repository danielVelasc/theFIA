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
import org.omg.CORBA.portable.InputStream;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

//google api key: AIzaSyCBPC1jeKHhkYOyZHgvX0wYJUORm4nN69E

@Path("/")
public class RequestProcessor {
	
	@GET
	@Produces({"application/json", "image/png"})
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
		
		if((parameterMap.get("type").get(0).equals("images" ) && !parameterMap.containsKey("action")) || parameterMap.get("type").get(0).equals("embed" ))
		{
			HttpResponse<java.io.InputStream> imgResponse = Unirest.get(auroraString).header("cookie", "PHPSESSID=MW2MMg7reEHx0vQPXaKen0").asBinary();
			return Response.status(200).entity(imgResponse.getBody()).type("image/png").build();
		}
		if(parameterMap.get("type").get(0).equals("googlemap"))
		{
			HttpResponse<java.io.InputStream> imgResponse = Unirest.get("https://maps.googleapis.com/maps/api/staticmap?center=Brooklyn+Bridge,New+York,NY&zoom=13&size=600x300&maptype=roadmap&markers=color:blue%7Clabel:S%7C40.702147,-74.015794&markers=color:green%7Clabel:G%7C40.711614,-74.012318%20&markers=color:red%7Clabel:C%7C40.718217,-73.998284&key=AIzaSyCBPC1jeKHhkYOyZHgvX0wYJUORm4nN69E").header("cookie", "PHPSESSID=MW2MMg7reEHx0vQPXaKen0").asBinary();
			return Response.status(200).entity(imgResponse.getBody()).type("image/png").build();
		}
		else{
		JSONObject jsonObject = new JSONObject();
		HttpResponse<JsonNode> response = Unirest.get(auroraString).header("cookie", "PHPSESSID=MW2MMg7reEHx0vQPXaKen0").asJson();
		jsonObject = response.getBody().getObject();
		return Response.status(200).entity(response.getBody().toString()).type("application/json").build();
		}
	}
}
