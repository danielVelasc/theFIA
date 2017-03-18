package dataRetriever;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.json.JSONException;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

class MapRequestSpawner {
	
	//google api key: AIzaSyCBPC1jeKHhkYOyZHgvX0wYJUORm4nN69E
	
	private final String googleAPIKey = "AIzaSyCBPC1jeKHhkYOyZHgvX0wYJUORm4nN69E";
	
	protected Response createMapRequest(MultivaluedMap<String, String> parameterMap) throws UnirestException, JSONException {
		
		String googleString = "https://maps.googleapis.com/maps/api/staticmap?center=";
		
		if (!parameterMap.containsKey("id"))
			return Response.status(400).entity("ERROR 400: Parameter 'id' is mandatory.").type("application/json").build();
		
		String location = parameterMap.get("id").get(0);
		
		if (location == null || location.isEmpty())
			return Response.status(400).entity("ERROR 400: No location ID was inputted").type("application/json").build();
		
		String label = location.substring(0, 1);
		label = label.toUpperCase();
		
		googleString = googleString + location + "&zoom=13&size=600x300&maptype=roadmap&markers=color:red%7Clabel:" + label + "%7C" + location;
		googleString = googleString + "&key=" + googleAPIKey;
		
		HttpResponse<java.io.InputStream> imgResponse = Unirest.get(googleString).header("cookie", "PHPSESSID=MW2MMg7reEHx0vQPXaKen0").asBinary();
		return Response.status(200).entity(imgResponse.getBody()).type("image/png").build();
		
	}

}
