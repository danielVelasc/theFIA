package dataRetriever;

import java.io.IOException;
import java.util.ArrayList;

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
		String auroraString = new String("http://api.auroras.live/v1/?type=locations");
		
		if (!parameterMap.containsKey("id"))
			return Response.status(400).entity("ERROR 400: Parameter 'id' is mandatory.").type("application/json").build();
		
		String location = parameterMap.get("id").get(0);
		
		if (location == null || location.isEmpty())
			return Response.status(400).entity("ERROR 400: No location ID was inputted").type("application/json").build();
		
		HttpResponse<String> locationsResponse = Unirest.get(auroraString).header("cookie", "PHPSESSID=MW2MMg7reEHx0vQPXaKen0").asString();
		
		System.out.println(locationsResponse.getBody().toString());
		
		if (!isValidLocation(locationsResponse.getBody().toString(), location))
			return Response.status(404).entity("ERROR 404: " + location + " is not a valid hunting location.").type("application/json").build();
		
		String label = location.substring(0, 1);
		label = label.toUpperCase();
		
		googleString = googleString + location + "&zoom=13&size=600x300&maptype=roadmap&markers=color:red%7Clabel:" + label + "%7C" + location;
		googleString = googleString + "&key=" + googleAPIKey;
		
		HttpResponse<java.io.InputStream> imgResponse = Unirest.get(googleString).header("cookie", "PHPSESSID=MW2MMg7reEHx0vQPXaKen0").asBinary();
		return Response.status(200).entity(imgResponse.getBody()).type("image/png").build();
		
	}
	
	private boolean isValidLocation(String locationsArray, String loc) {
		
		String[] splitA = locationsArray.split("\"id\":\"");
		ArrayList<String> validLocations = new ArrayList<>();
		
		for (String str : splitA) {
			String[] splitB = str.split("\",\"name\"");
			validLocations.add(splitB[0]);
		}
		
		for (String str : validLocations) {
			if (str == null || str.isEmpty())
				continue;
			if (str.compareToIgnoreCase(loc) == 0)
				return true;
		}
		
		return false;
		
	}
	
	protected byte[] createCacheMapRequest(MultivaluedMap<String, String> parameterMap) throws UnirestException, JSONException {
		
		String googleString = "https://maps.googleapis.com/maps/api/staticmap?center=";
		String auroraString = new String("http://api.auroras.live/v1/?type=locations");
		
		if (!parameterMap.containsKey("id"))
			return null;
		
		String location = parameterMap.get("id").get(0);
		
		if (location == null || location.isEmpty())
			return null;
		
		HttpResponse<String> locationsResponse = Unirest.get(auroraString).header("cookie", "PHPSESSID=MW2MMg7reEHx0vQPXaKen0").asString();
		
		System.out.println(locationsResponse.getBody().toString());
		
		if (!isValidLocation(locationsResponse.getBody().toString(), location))
			return null;
		
		String label = location.substring(0, 1);
		label = label.toUpperCase();
		
		googleString = googleString + location + "&zoom=13&size=600x300&maptype=roadmap&markers=color:red%7Clabel:" + label + "%7C" + location;
		googleString = googleString + "&key=" + googleAPIKey;
		
		HttpResponse<java.io.InputStream> imgResponse = Unirest.get(googleString).header("cookie", "PHPSESSID=MW2MMg7reEHx0vQPXaKen0").asBinary();
		
		byte[] byteRead = null;
		
		int length = 1;
		while(true) {
			
			byteRead = new byte[length];
			System.out.println("---------------------------" + length + "------------------------");
			try {
				int check = imgResponse.getBody().read(byteRead, 0, length);
				if (check < length) {
					length--;
					byteRead = new byte[length];
					imgResponse.getBody().read(byteRead, 0, length);
					break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			length++;
			
		}
		
		return byteRead;
		
	}

}
