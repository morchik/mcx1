package com.mcxsuregain.utils;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * User: Vivek Date: 03/7/14
 */
public class SoapRequests extends ServiceData {

	public String requestLogin(String username, String password, String imei) {
		String response = "";

		SoapObject request = new SoapObject(NAMESPACE, MethodLogin);
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("IMEINumber", imei);
			jsonObject.put("Password", password);
			jsonObject.put("UserID", username);
			request.addProperty("userInfo", jsonObject.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;

		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
			androidHttpTransport.call(ActionLogin, envelope);
			SoapObject result = (SoapObject) envelope.bodyIn;

			if (result != null) {
				SoapPrimitive obj = (SoapPrimitive) result.getProperty(0);
				response = obj.toString().trim();
			} else {
				response = "Connection Or Server Error";
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = "Server error";
		}
		return response;
	}

	public String changePassword(String uId, String curPass, String confPass) {
		String response = "";

		SoapObject request = new SoapObject(NAMESPACE, MethodPass);
		try {
			request.addProperty("UserId", Integer.valueOf(uId));
			request.addProperty("tbCurrPwd", curPass);
			request.addProperty("tbNewPwd", confPass);
		} catch (Exception e) {
			e.printStackTrace();
		}

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;

		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
			androidHttpTransport.call(ActionPass, envelope);
			SoapObject result = (SoapObject) envelope.bodyIn;

			if (result != null) {
				SoapPrimitive obj = (SoapPrimitive) result.getProperty(0);
				response = obj.toString().trim();
			} else {
				response = "Connection Or Server Error";
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = "Server error";
		}
		return response;
	}

	public String getSymbols() {
		String response = "";
		SoapObject request = new SoapObject(NAMESPACE, MethodSymbols);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;

		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
			androidHttpTransport.call(ActionSymbols, envelope);
			SoapObject result = (SoapObject) envelope.bodyIn;
			response = result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			response = "Server error";
		}
		return response;
	}

	public String getStates() {
		String response = "";
		SoapObject request = new SoapObject(NAMESPACE, MethodGetStates);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;

		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
			androidHttpTransport.call(ActionGetStates, envelope);
			SoapObject result = (SoapObject) envelope.bodyIn;
			response = result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			response = "Server error";
		}
		return response;
	}

	public String getSeverdate() {
		String response = "";
		SoapObject request = new SoapObject(NAMESPACE, MethodGetInfo);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;

		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
			androidHttpTransport.call(ActionGetinfo, envelope);
			SoapObject result = (SoapObject) envelope.bodyIn;
			response = result.toString();
			// GetInfoResponse{GetInfoResult=2014-11-24T10:36:46.5675527+05:30;
			// }
			String[] str = response.split("=");
			String[] subDate = str[1].split("T");
			response = subDate[0];
		} catch (Exception e) {
			e.printStackTrace();
			response = "Server error";
		}
		return response;
	}

	public String serachMarketRequest(String jsonString) {
		String response = "";
		SoapObject request = new SoapObject(NAMESPACE, MethodMarket);
		try {
			request.addProperty("searchInfo", jsonString);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;

		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
			androidHttpTransport.call(ActionMarket, envelope);
			SoapPrimitive result = (SoapPrimitive) envelope.getResponse();

			if (result != null) {
				response = result.toString();
			} else {
				response = "Connection Or Server Error";
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = "Server error";
		}
		return response;
	}

	public String registerUser(String jsonString) {
		String response = "";
		SoapObject request = new SoapObject(NAMESPACE, MethodRegisteration);
		try {
			request.addProperty("userInfo", jsonString.trim());
		} catch (Exception e) {
			e.printStackTrace();
		}

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;

		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
			androidHttpTransport.call(ActionRegisteration, envelope);
			SoapObject result = (SoapObject) envelope.bodyIn;

			if (result != null) {
				SoapPrimitive obj = (SoapPrimitive) result.getProperty(0);
				response = obj.toString().trim();
			} else {
				response = "Connection Or Server Error";
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = "Server error";
		}
		return response;
	}
}
