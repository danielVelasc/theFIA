package dataRetriever;

import java.io.IOException;
import java.util.Iterator;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;


class AuroraRequestSpawner {
	
	/**
	 * 
	 * @param parameterMap The parameters from the call to the server.
	 * @return The byte array containing the bytes for the image to be stored in the cache.
	 * @throws UnirestException
	 * @throws JSONException
	 */
	protected byte[] createCacheImageRequest(MultivaluedMap<String, String> parameterMap) throws UnirestException,JSONException {
		
		// the start of the call to the Auroras.live API
		String auroraString = new String("http://api.auroras.live/v1/?type=");
		String type = parameterMap.get("type").get(0).toLowerCase();
		
		auroraString = auroraString + type;
		
		// iterate throught the request call and add the parameters to the Auroras.live request
		Iterator<String> it = parameterMap.keySet().iterator();
		while(it.hasNext()){
			String theKey = (String)it.next();
			String theValue = parameterMap.get(theKey).get(0);
			
			if(!theKey.equals("type") || !theKey.equals("no-caching"))
			{
				auroraString = auroraString + "&" +theKey+"=";
				auroraString = auroraString + theValue;
			}
			
		}
		
		// call the Auroras.live API to return a Response
		HttpResponse<java.io.InputStream> imgResponse = Unirest.get(auroraString).header("cookie", "PHPSESSID=MW2MMg7reEHx0vQPXaKen0").asBinary();
		
		if (imgResponse.getStatus() != 200)
			return null;
		
		byte[] byteRead = null;
		
		// convert the returned Response to a byte array that can be stored in the cache.
		try {
			byteRead = new byte[imgResponse.getBody().available()];
			imgResponse.getBody().read(byteRead);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		// return the byte array to be stored in the cache
		return byteRead;

	}
	
	/**
	 * A function to call the Auroras.live API and return an image object to the caller.
	 * @param parameterMap The parameters from the call to the server.
	 * @return The Response containing an image object or an error Response.
	 * @throws UnirestException
	 * @throws JSONException
	 */
	protected Response createImageRequest(MultivaluedMap<String, String> parameterMap) throws UnirestException,JSONException {
		
		// The start for the Auroras.live request 
		String auroraString = new String("http://api.auroras.live/v1/?type=");
		String type = parameterMap.get("type").get(0).toLowerCase();
		
		auroraString = auroraString + type;
		
		// iterate through the parameterMap and add the parameters to the request for the Auroras.live API
		Iterator<String> it = parameterMap.keySet().iterator();
		while(it.hasNext()){
			String theKey = (String)it.next();
			String theValue = parameterMap.get(theKey).get(0);
			
			if(!theKey.equals("type") || !theKey.equals("no-caching"))
			{
				auroraString = auroraString + "&" +theKey+"=";
				auroraString = auroraString + theValue;
			}
			
		}
		
		// call the Auroras.live API to get the image Response
		HttpResponse<java.io.InputStream> imgResponse = Unirest.get(auroraString).header("cookie", "PHPSESSID=MW2MMg7reEHx0vQPXaKen0").asBinary();
		
		// return either the image Response or an error message if there was an error with the call to the Auroras.live API
		if (imgResponse.getStatus() != 200)
			return Response.status(imgResponse.getStatus()).entity("ERROR " + imgResponse.getStatus()).type("application/json").build();
		
		return Response.status(imgResponse.getStatus()).entity(imgResponse.getBody()).type("image/png").build();

	}
	
	/**
	 * A funtion to call the Auroras.live API to return a json application Response.
	 * @param parameterMap The parameters from the call to the server.
	 * @return A Response either containing a valid json object or an error Response
	 * @throws UnirestException
	 * @throws JSONException
	 */
	protected Response createStandardRequest(MultivaluedMap<String, String> parameterMap) throws UnirestException,JSONException {
		
		// The start of the call to the Auroras.live API
		String auroraString = new String("http://api.auroras.live/v1/?type=");
		String type = parameterMap.get("type").get(0).toLowerCase();
		
		auroraString = auroraString + type;
		
		// iterate through and add the parameters to the request string
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
		
		// create an attribution to auroras.live and add it to the json object
		String att = "Powered by Auroras.live";
		JSONObject jsonObject = new JSONObject();
		
		// call the Auroras.live API and obtain a response.
		HttpResponse<JsonNode> response = Unirest.get(auroraString).header("cookie", "PHPSESSID=MW2MMg7reEHx0vQPXaKen0").asJson();
		
		// put the attibution in the return map
		if(!type.equalsIgnoreCase("locations")) {
			jsonObject = response.getBody().getObject();
			jsonObject.put("Attribution", att);
		}
		else {
			JSONArray responseArray = response.getBody().getArray();
			JSONObject jsonAttributionObject = new JSONObject();
			jsonAttributionObject.put("Attribution", att);
			responseArray.put(jsonAttributionObject);
		}
		
		// return the Response to the caller
		return Response.status(response.getStatus()).entity(response.getBody().toString()).type("application/json").build();
	}
	

}
