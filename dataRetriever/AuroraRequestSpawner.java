package dataRetriever;

import java.util.Iterator;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;


class AuroraRequestSpawner {
	
	
	protected Response createImageRequest(MultivaluedMap<String, String> parameterMap) throws UnirestException,JSONException {
		
		String auroraString = new String("http://api.auroras.live/v1/?type=");
		String type = parameterMap.get("type").get(0).toLowerCase();
		
		auroraString = auroraString + type;
		
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
		
		HttpResponse<java.io.InputStream> imgResponse = Unirest.get(auroraString).header("cookie", "PHPSESSID=MW2MMg7reEHx0vQPXaKen0").asBinary();
		
		if (imgResponse.getStatus() != 200)
			return Response.status(imgResponse.getStatus()).entity("ERROR " + imgResponse.getStatus()).type("application/json").build();
		
		return Response.status(imgResponse.getStatus()).entity(imgResponse.getBody()).type("image/png").build();

	}
	
	protected Response createStandardRequest(MultivaluedMap<String, String> parameterMap) throws UnirestException,JSONException {
		String auroraString = new String("http://api.auroras.live/v1/?type=");
		String type = parameterMap.get("type").get(0).toLowerCase();
		
		auroraString = auroraString + type;
		
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
		
		JSONObject jsonObject = new JSONObject();
		HttpResponse<JsonNode> response = Unirest.get(auroraString).header("cookie", "PHPSESSID=MW2MMg7reEHx0vQPXaKen0").asJson();
		jsonObject = response.getBody().getObject();
		return Response.status(response.getStatus()).entity(response.getBody().toString() + " Powered by Auroras.live").type("application/json").build();
	}
	

}
