package com.mcxsuregain.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mcxsuregain.utils.Constants;
import com.mcxsuregain.utils.SoapRequests;
import com.mcxsuregain.utils.Utils;

public class AccountContainer extends Fragment {

	private View v;
	private TextView imageLogOut;
	private Utils utils;
	private TextView textName, textMobile, textCity, textRegDate,
			textVaildDate, textDaysRem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		v = LayoutInflater.from(getActivity()).inflate(
				R.layout.account_fragment, null);
		utils = new Utils(getActivity());
		HomeFragment.comingFromOtherTab = true;
		setViews();

		try {

			String strmo = utils.getPreference(Constants.prefMobile);
			String strreg = utils.getPreference(Constants.prefRegDate).split(
					"T")[0];
			String strvalid = utils.getPreference(Constants.prefValidDate)
					.split("T")[0];
			String strcity = utils.getPreference(Constants.prefCity);
			String strunmae = utils.getPreference(Constants.prefUserName);
			String strlname = utils.getPreference(Constants.prefLastName);

			textName.setText(strunmae + " " + strlname);
			textMobile.setText(strmo);
			textCity.setText(strcity);
			textRegDate.setText(getconvertdate(strreg));
			textVaildDate.setText(getconvertdate(strvalid));

			textDaysRem.setText(String.valueOf(daysBetween(strvalid)));
		} catch (Exception e) {
			e.printStackTrace();
		}

		imageLogOut = (TextView) v.findViewById(R.id.imageLogout);
		imageLogOut.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setTitle("Do you want to logout");
				builder.setPositiveButton("Yes", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							new HomeFragment().killTimer();
							utils.setPreference(Constants.marketString, "0");
							utils.setBoolPrefrences(Constants.prefIsLogin,
									false);
							utils.setPreference(Constants.oldPref, "0");
							utils.setBoolPrefrences(
									Constants.prefIsNotifyEnable, false);
							getActivity().finish();
						} catch (Exception e) {
							e.printStackTrace();
							getActivity().finish();
						}
					}
				}).setNegativeButton("No", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create();
				builder.show();

			}
		});

		v.findViewById(R.id.btnChangePassword).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						passwordDialog();
					}
				});
		return v;
	}

	public String getconvertdate(String date) {
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd",
				Locale.ENGLISH);
		inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy",
				Locale.ENGLISH);
		Date parsed = new Date();
		try {
			parsed = inputFormat.parse(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String outputText = outputFormat.format(parsed);
		return outputText;
	}

	private void setViews() {
		textName = (TextView) v.findViewById(R.id.textProfileName);
		textMobile = (TextView) v.findViewById(R.id.textProfileMobile);
		textCity = (TextView) v.findViewById(R.id.textProfileCity);
		textRegDate = (TextView) v.findViewById(R.id.textProfileRegDate);
		textVaildDate = (TextView) v.findViewById(R.id.textProfileValidDate);
		textDaysRem = (TextView) v.findViewById(R.id.textProfileDaysRema);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public void passwordDialog() {
		final Dialog dialog = new Dialog(getActivity());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.row_change_password);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.gravity = Gravity.CENTER;
		dialog.getWindow().setAttributes(lp);
		Button btnOk = (Button) dialog.findViewById(R.id.btnPasswordDone);
		final EditText edtCurrentPass = (EditText) dialog
				.findViewById(R.id.edtCurrentPassword);
		final EditText edtNewPass = (EditText) dialog
				.findViewById(R.id.edtNewpassword);
		final EditText edtConfirmPass = (EditText) dialog
				.findViewById(R.id.edtRepass);

		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String strCurrentPass = edtCurrentPass.getText().toString();
				String strNewPass = edtNewPass.getText().toString();
				String strConf = edtConfirmPass.getText().toString();
				if (!strNewPass.equals(strConf)) {
					utils.Toast("Password not matched.");
				} else {
					dialog.dismiss();
					new synchpass(strCurrentPass, strConf).execute();
				}
			}
		});
		dialog.show();
	}

	private class synchpass extends AsyncTask<Void, Void, String> {
		ProgressDialog dialog;
		String cpass, Confirmpass;

		public synchpass(String cpass, String conPass) {
			this.cpass = cpass;
			Confirmpass = conPass;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(getActivity());
			dialog.setMessage("Please wait...");
			dialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			String jsonString = "";
			try {
				jsonString = new SoapRequests().changePassword(
						utils.getPreference(Constants.userdId), cpass,
						Confirmpass);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return jsonString;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result.equals("true")) {
				utils.Toast("Password change successfully");
			} else {
				utils.Toast("Failed to change password");
			}
			dialog.dismiss();
		}
	}

	public long daysBetween(String end) {
		Date startDate = new Date();
		Date endDate = new Date();
		long daysBetween = 0;

		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",
					Locale.ENGLISH);
			startDate = dateFormat.parse(new SimpleDateFormat("yyyy-MM-dd",
					Locale.ENGLISH).format(new Date()));
			endDate = dateFormat.parse(end);

			Calendar sDate = getDatePart(startDate);
			Calendar eDate = getDatePart(endDate);

			while (sDate.before(eDate)) {
				sDate.add(Calendar.DAY_OF_MONTH, 1);
				daysBetween++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return daysBetween;
	}

	public static Calendar getDatePart(Date date) {
		Calendar cal = Calendar.getInstance(); // get calendar instance
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0); // set hour to midnight
		cal.set(Calendar.MINUTE, 0); // set minute in hour
		cal.set(Calendar.SECOND, 0); // set second in minute
		cal.set(Calendar.MILLISECOND, 0); // set millisecond in second

		return cal; // return the date part
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		utils.setPreference(Constants.marketString, "0");
	}
}