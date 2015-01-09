package org.tgi.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.compiere.util.CLogger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * cf. https://developers.google.com/maps/documentation/geocoding/
 * @author mjourdan, nmicoud - TGI
 */
public class GoogleMaps {

	protected CLogger	log = CLogger.getCLogger (getClass());
	protected String address;
	private int statusOK = 200;
	protected String latitude;
	protected String longitude;
	private String googleMapWebscript;

	public GoogleMaps(){
		address="";
		longitude= "";
		latitude = "";
		googleMapWebscript="";
	}

	public GoogleMaps(String pAddress) {
		address = pAddress;
		longitude= "";
		latitude = "";
		googleMapWebscript="http://maps.googleapis.com/maps/api/geocode/json";
		getCoordonnessGPS(address);
	}

	/**
	 * @return the longitude
	 */
	public String getLongitude(){
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the latitude
	 */
	public String getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddresse(String address) {
		this.address = address;
	}

	/**
	 * @return the googleMapWebscript
	 */
	public String getGoogleMapWebscript() {
		return googleMapWebscript;
	}

	/**
	 * @param googleMapWebscript the googleMapWebscript to set
	 */
	public void setGoogleMapWebscript(String googleMapWebscript) {
		this.googleMapWebscript = googleMapWebscript;
	}

	public void getCoordonnessGPS(String adresse) {

		HttpClient httpClient = new HttpClient();
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);

		GetMethod method = new GetMethod(this.getGoogleMapWebscript()+"?address="+this.getAddress().replaceAll(" ", "+")+"&sensor=false");
		int status = 0;
		String resultString="";

		try {

			status = httpClient.executeMethod(method);
			resultString = method.getResponseBodyAsString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}
		if (status == statusOK) {
			JSONObject obj = this.parseJSON(resultString);
			try {

				String statusGoogle= obj.getString("status");
				if(statusGoogle.equals("OK")){
					JSONArray result= obj.getJSONArray("results");
					if(result.length()==1){
						JSONObject location = result.getJSONObject(0);
						location = location.getJSONObject("geometry").getJSONObject("location");
						longitude = location.getString("lng");
						latitude = location.getString("lat");
					}
				}
			} catch (Exception e) {	
				e.printStackTrace();
			}
		} else
			log.warning(status + " : " + resultString);			
	}

	private JSONObject parseJSON(String resultString)
	{
		JSONObject obj = null;
		try {
			obj = new JSONObject(resultString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
}
