package com.mcxsuregain.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
// import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.mcxsuregain.utils.Constants;
import com.mcxsuregain.utils.SoapRequests;
import com.mcxsuregain.utils.Utils;

public class SplashActivity extends Activity {

	Utils utils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		utils = new Utils(SplashActivity.this);
		if (utils.isNetConnected()) {
			new synchInfo().execute();
		} else {
			utils.showAlertMessage("OK", "Please check connection");
		}
	}

	private class synchInfo extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			return new SoapRequests().getSeverdate();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			utils.setPreference(Constants.server_date, result);
			new CountDownTimer(1000, 3000) { // ??????
				@Override
				public void onTick(long millisUntilFinished) {

				}

				@Override
				public void onFinish() {
					utils.setBoolPrefrences(
							Constants.prefIsNotifyEnable, false);
					if (utils.getBoolPref(Constants.prefIsLogin)
							&& !utils.getPreference(Constants.userdId).equals(
									"")) {
						startActivity(new Intent(SplashActivity.this,
								MainTabActivity.class));
						finish();
					} else {
						startActivity(new Intent(SplashActivity.this,
								LoginActivity.class));
						finish();
					}
				}
			}.start();
		}
	}
}
