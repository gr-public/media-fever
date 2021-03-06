package com.mediafever.api.controller.parser;

import org.json.JSONException;
import com.jdroid.java.parser.json.JsonObjectWrapper;
import com.jdroid.java.parser.json.JsonParser;

/**
 * 
 * @author Maxi Rosson
 */
public class DeviceParser extends JsonParser<JsonObjectWrapper> {
	
	private static final String REGISTRATION_ID = "registrationId";
	
	private String registrationId;
	
	/**
	 * @see com.jdroid.java.parser.json.JsonParser#parse(java.lang.Object)
	 */
	@Override
	public Object parse(JsonObjectWrapper json) throws JSONException {
		registrationId = json.optString(REGISTRATION_ID);
		return null;
	}
	
	public String getRegistrationId() {
		return registrationId;
	}
	
}
