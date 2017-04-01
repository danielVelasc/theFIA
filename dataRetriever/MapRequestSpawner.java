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
	
	/**
	 * The google API key needed to contact the google maps API
	 */
	private final String googleAPIKey = "AIzaSyCBPC1jeKHhkYOyZHgvX0wYJUORm4nN69E";
	
	/**
	 * Creates a request to Auroras.live and the google map API to create a new map object and 
	 * encase it in a Response to be returned to the caller.
	 * @param parameterMap The parameters from the call to the server.
	 * @return The formatted Response containing either a google map object or an error Response (400 or 404)
	 * @throws UnirestException
	 * @throws JSONException
	 */
	protected Response createMapRequest(MultivaluedMap<String, String> parameterMap) throws UnirestException, JSONException {
		
		// start of the requests to the specific APIs
		String googleString = "https://maps.googleapis.com/maps/api/staticmap?center=";
		String auroraString = new String("http://api.auroras.live/v1/?type=locations");
		
		// check that the required id parameter is present, else return an error Response
		if (!parameterMap.containsKey("id"))
			return Response.status(400).entity("ERROR 400: Parameter 'id' is mandatory.").type("application/json").build();
		
		String location = parameterMap.get("id").get(0);
		
		// check that the location is not null or empty, else return an error Response
		if (location == null || location.isEmpty())
			return Response.status(400).entity("ERROR 400: No location ID was inputted").type("application/json").build();
		
		// call the Auroras.live API to check if the location is valid
		HttpResponse<String> locationsResponse = Unirest.get(auroraString).header("cookie", "PHPSESSID=MW2MMg7reEHx0vQPXaKen0").asString();
		
		// check if the location is valid, else return an error Response.
		if (!isValidLocation(locationsResponse.getBody().toString(), location))
			return Response.status(404).entity("ERROR 404: " + location + " is not a valid hunting location.").type("application/json").build();
		
		// Grab the first letter of the location to use as a label on the marker in the google map object.
		String label = location.substring(0, 1);
		label = label.toUpperCase();
		
		// Call the google map API to get an object with the specific location.
		googleString = googleString + location + "&zoom=13&size=600x300&maptype=roadmap&markers=color:red%7Clabel:" + label + "%7C" + location;
		googleString = googleString + "&key=" + googleAPIKey;
		
		// call the google maps API and return the Response to the caller
		HttpResponse<java.io.InputStream> imgResponse = Unirest.get(googleString).header("cookie", "PHPSESSID=MW2MMg7reEHx0vQPXaKen0").asBinary();
		return Response.status(200).entity(imgResponse.getBody()).type("image/png").build();
		
	}
	
	/**
	 * A function to check if the location is valid by parsing the returned locations call from Auroras.live
	 * and comparing the location inputted to the ones in the array.
	 * @param locationsArray The full String that came from the Auroras.live API.
	 * @param loc the location inputted from the call from the web browser
	 * @return true if the location is valid and false otherwise.
	 */
	private boolean isValidLocation(String locationsArray, String loc) {
		
		// parse the spring and extract all of the locations
		String[] splitA = locationsArray.split("\"id\":\"");
		ArrayList<String> validLocations = new ArrayList<>();
		
		// Split the string further
		for (String str : splitA) {
			String[] splitB = str.split("\",\"name\"");
			validLocations.add(splitB[0]);
		}
		
		// search through the array and determine if the location is valid.
		for (String str : validLocations) {
			if (str == null || str.isEmpty())
				continue;
			if (str.compareToIgnoreCase(loc) == 0)
				return true;
		}
		
		// otherwise, return false.
		return false;
		
	}
	
	/**
	 * Calls the google map API and convert the Response to a byte array that can be stored in the cache.
	 * @param parameterMap The parameters from the call to the server.
	 * @return The byte array that has been converted from the Response from the google maps API
	 * @throws UnirestException
	 * @throws JSONException
	 */
	protected byte[] createCacheMapRequest(MultivaluedMap<String, String> parameterMap) throws UnirestException, JSONException {
		
		// The start of a call to the google maps API and the Auroras.live API
		String googleString = "https://maps.googleapis.com/maps/api/staticmap?center=";
		String auroraString = new String("http://api.auroras.live/v1/?type=locations");
		
		// Check that the required parameters are in the request.
		if (!parameterMap.containsKey("id"))
			return null;
		
		String location = parameterMap.get("id").get(0);
		
		if (location == null || location.isEmpty())
			return null;
		
		// call the auroras.live API to get the valid locations.
		HttpResponse<String> locationsResponse = Unirest.get(auroraString).header("cookie", "PHPSESSID=MW2MMg7reEHx0vQPXaKen0").asString();
		
		// check if the inputted location is valid 
		if (!isValidLocation(locationsResponse.getBody().toString(), location))
			return null;
		
		// Create a label for the marker used in the google map object.
		String label = location.substring(0, 1);
		label = label.toUpperCase();
		
		// create the string for the call to the google map API.
		googleString = googleString + location + "&zoom=13&size=600x300&maptype=roadmap&markers=color:red%7Clabel:" + label + "%7C" + location;
		googleString = googleString + "&key=" + googleAPIKey;
		
		// call the google map API 
		HttpResponse<java.io.InputStream> imgResponse = Unirest.get(googleString).header("cookie", "PHPSESSID=MW2MMg7reEHx0vQPXaKen0").asBinary();
		
		// convert the returned Response to a byte array to be stored in the cache.
		byte[] byteRead = null;
		try {
			byteRead = new byte[imgResponse.getBody().available()];
			imgResponse.getBody().read(byteRead);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// return the byte array
		return byteRead;
		
	}

}
