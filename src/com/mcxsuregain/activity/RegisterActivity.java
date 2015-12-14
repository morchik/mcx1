package com.mcxsuregain.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.mcxsuregain.utils.Constants;
import com.mcxsuregain.utils.SoapRequests;
import com.mcxsuregain.utils.SpinPojo;
import com.mcxsuregain.utils.Utils;

public class RegisterActivity extends Activity {

	private EditText edtFirstname, edtLastName, edtEmail, edtMoNo,
			edtLandlineNo, edtCity;
	private Spinner spinStates;
	private Utils utils;
	private ArrayList<SpinPojo> arrayStates;
	String fName, lName, email, mono, landNo, city;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registraion);
		setViews();
		findViewById(R.id.btnRegisteration).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						fName = edtFirstname.getText().toString();
						lName = edtLastName.getText().toString();
						email = edtEmail.getText().toString();
						mono = edtMoNo.getText().toString();
						landNo = edtLandlineNo.getText().toString();
						city = edtCity.getText().toString();

						if (fName.equals("") || lName.equals("")
								|| email.equals("") || mono.equals("")
								|| city.equals("")) {
							utils.Toast("Fill all details...");
						} else if (mono.length() < 9) {
							utils.Toast("Enter proper mobile number");
						} else if (!utils.isEmailValid(email)) {
							utils.Toast("Please enter proper email address");
						} else {
							AlertDialog.Builder builder = new Builder(
									RegisterActivity.this);
							builder.setTitle("Please confirm");
							builder.setMessage("Your user id and password will be sent to : \n\n"
									+ email + "\n\nConfirm it or re enter");
							builder.setPositiveButton("Done",
									new OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											if (utils.isNetConnected()) {
												new synchRegisteration()
														.execute();
											} else {
												utils.Toast("Please check connection....");
											}
										}
									})
									.setNegativeButton("Cancel",
											new OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													dialog.dismiss();
												}
											}).show();
						}
					}
				});
	}

	private void setViews() {
		utils = new Utils(this);
		arrayStates = new ArrayList<>();
		edtFirstname = (EditText) findViewById(R.id.edtFirstname);
		edtLastName = (EditText) findViewById(R.id.edtlastname);
		edtEmail = (EditText) findViewById(R.id.edtEmail);
		edtMoNo = (EditText) findViewById(R.id.edtMono);
		edtMoNo.setRawInputType(InputType.TYPE_CLASS_NUMBER);
		edtLandlineNo = (EditText) findViewById(R.id.edtlanno);
		edtLandlineNo.setRawInputType(InputType.TYPE_CLASS_NUMBER);
		edtCity = (EditText) findViewById(R.id.edtcity);
		spinStates = (Spinner) findViewById(R.id.spinStates);
		try {
			new synchStates().execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class synchStates extends AsyncTask<Void, Void, String> {

		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			arrayStates.clear();
			dialog = new ProgressDialog(RegisterActivity.this);
			dialog.setMessage("Loading");
			dialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			String jsonString = "";
			try {
				jsonString = new SoapRequests().getStates();
				System.out.println(jsonString);
				String subStr = jsonString.substring(jsonString.indexOf("[{"),
						jsonString.indexOf(";"));
				JSONArray jsonArray = new JSONArray(subStr);
				SpinPojo pojo;
				for (int i = 0; i < jsonArray.length(); i++) {
					pojo = new SpinPojo();
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					String key = jsonObject.get("StateID").toString();
					String value = jsonObject.get("StateName").toString();
					pojo.setKey(key);
					pojo.setValue(value);
					arrayStates.add(pojo);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return jsonString;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result.contains("Error")) {
				utils.showAlertMessage("OK",
						"Getting server error \n or \n Your net connection is too slow...");
			} else {
				ArrayList<String> spinData = new ArrayList<>();
				for (SpinPojo string : arrayStates) {
					String str = string.getValue();
					spinData.add(str);
				}
				spinStates.setAdapter(new ArrayAdapter<>(RegisterActivity.this,
						android.R.layout.simple_spinner_item, spinData));
			}
			dialog.dismiss();
		}
	}

	private class synchRegisteration extends AsyncTask<Void, Void, String> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(RegisterActivity.this);
			dialog.setMessage("Registering....");
			dialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			String jsonString = "";
			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("Firstname", fName);
				jsonObject.put("Lastname", lName);
				jsonObject.put("Landline", landNo);
				jsonObject.put("MobileNo", mono);
				jsonObject.put("State",
						arrayStates.get(spinStates.getSelectedItemPosition())
								.getKey());
				jsonObject.put("City", city);
				jsonObject.put("EmailId", email);
				jsonObject.put("IMEINumber", utils.getImeiNo());

				jsonString = new SoapRequests().registerUser(jsonObject
						.toString());
				System.out.println(jsonString);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return jsonString;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result.contains("Error")) {
				utils.showAlertMessage("OK",
						"Getting server error \n or \n Your net connection is too slow...");
			} else {
				try {
					JSONObject jsonObject = new JSONObject(result);
					String statusId = jsonObject.getString("StatusId");
					if (statusId.equals("1")) {
						arrayStates.clear();
						utils.setBoolPrefrences(Constants.prefIsLogin, true);
						utils.setBoolPrefrences(Constants.prefIsRegLogin, true);
						startActivity(new Intent(RegisterActivity.this,
								MainTabActivity.class));
						finish();
					} else {
						utils.Toast("Registration unsuccessful.\nPlease contact administrator.");
						startActivity(new Intent(RegisterActivity.this,
								LoginActivity.class));
						finish();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			dialog.dismiss();
		}
	}
}
