package com.mcxsuregain.activity;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mcxsuregain.utils.Constants;
import com.mcxsuregain.utils.SoapRequests;
import com.mcxsuregain.utils.Utils;

public class LoginActivity extends Activity {

	private EditText editUserName, editPassword;
	private Button btnLogin;
	private Utils utils;
	private String userName, passWord;
	private SoapRequests soap;
	private TextView textAdmin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setViews();
		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				userName = editUserName.getText().toString();
				passWord = editPassword.getText().toString();
				if (userName.equals("") || passWord.equals("")) {
					utils.Toast("Please fill credentials...");
				} else {
					if (utils.isNetConnected()) {
						new synchLogin().execute();
					} else {
						utils.Toast("please check net connection");
					}
				}
			}
		});

		findViewById(R.id.textSignUp).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (utils.isNetConnected()) {
							startActivity(new Intent(LoginActivity.this,
									RegisterActivity.class));
						} else {
							utils.Toast("You will need net connection...");
						}
					}
				});
	}

	private void setViews() {
		utils = new Utils(LoginActivity.this);
		editUserName = (EditText) findViewById(R.id.editUserName);
		editPassword = (EditText) findViewById(R.id.editPassword);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		textAdmin = (TextView) findViewById(R.id.adminDetail);
		soap = new SoapRequests();
	}

	private class synchLogin extends AsyncTask<Void, Void, String> {

		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(LoginActivity.this);
			dialog.setMessage("Please wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			return soap.requestLogin(userName, passWord, utils.getImeiNo());
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result.equals("") || result.equals("null")) {
				utils.Toast("Login unsuccessful.\nPlease contact administrator");
				textAdmin.setVisibility(View.VISIBLE);
				textAdmin.setText("ccare@mcxsuregain.com\n+91-9555455557\n+91-9350222220\n+91-129-6570000");
			} else {
				try {
					JSONObject jsonObject = new JSONObject(result);
					String userId = jsonObject.getString("UserID");
					// String password = jsonObject.getString("Password");
					String mobileno = jsonObject.getString("MobileNo");
					// String email = jsonObject.getString("EmailId");
					// String landline = jsonObject.getString("Landline");
					// String state = jsonObject.getString("State");
					// String mac = jsonObject.getString("MACAddress");
					String imei = jsonObject.getString("IMEINumber");
					// String ispcuser = jsonObject.getString("IsPCUser");
					String isappuser = jsonObject.getString("IsAppUser");
					// String ismsguser = jsonObject.getString("IsMsgUser");
					// String subScriptionId = jsonObject
					// .getString("SubscriptionId");
					String regdt = jsonObject.getString("RegDT");
					String validdt = jsonObject.getString("ValidDT");
					// 2015-01-09T09:49:17
					String[] validDate = validdt.split("T");
					String statusid = jsonObject.getString("StatusId");
					String City = jsonObject.getString("City");
					String username = jsonObject.getString("Firstname");
					String lastname = jsonObject.getString("Lastname");
					String serverDate = utils
							.getPreference(Constants.server_date);

					if (!username.equals("null") || !userId.equals("null")
							&& Integer.valueOf(userId) > 0) {
						int stId = Integer.valueOf(statusid);
						boolean appUser = Boolean.valueOf(isappuser);
						if (stId != 1) {
							utils.Toast("Your account is disabled, please contact administrator.");
							textAdmin.setVisibility(View.VISIBLE);
							textAdmin.setText("ccare@mcxsuregain.com\n+91-9555455557\n+91-9350222220\n+91-129-6570000");
						} else if (!appUser) {
							utils.Toast("Your account does not have mobile application rights");
							textAdmin.setVisibility(View.VISIBLE);
							textAdmin.setText("ccare@mcxsuregain.com\n+91-9555455557\n+91-9350222220\n+91-129-6570000");
						} else if (!imei.equals(utils.getImeiNo())) {
							utils.Toast("You can login only from your registered mobile");
							textAdmin.setVisibility(View.VISIBLE);
							textAdmin.setText("ccare@mcxsuregain.com\n+91-9555455557\n+91-9350222220\n+91-129-6570000");
						} else if (!utils.isValidDate(serverDate, validDate[0])) {
							utils.showAlertMessage("OK",
									"Having licening problem , please contact to provider");
							textAdmin.setVisibility(View.VISIBLE);
							textAdmin.setText("ccare@mcxsuregain.com\n+91-9555455557\n+91-9350222220\n+91-129-6570000");
						} else {
							utils.setPreference(Constants.userdId, userId);
							utils.setPreference(Constants.prefMobile, mobileno);
							utils.setPreference(Constants.prefRegDate, regdt);
							utils.setPreference(Constants.prefValidDate,
									validdt);
							utils.setPreference(Constants.prefCity, City);
							utils.setPreference(Constants.prefUserName,
									username);
							utils.setPreference(Constants.prefLastName,
									lastname);

							utils.setBoolPrefrences(Constants.prefIsLogin, true);
							startActivity(new Intent(LoginActivity.this,
									MainTabActivity.class));
							finish();
						}
					} else {
						utils.Toast("Incorrect Username or Password");
					}
				} catch (Exception e) {
					e.printStackTrace();
					utils.Toast("Login error or net connection error");
				}
			}
			dialog.dismiss();
		}
	}
}
