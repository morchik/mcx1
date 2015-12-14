/**
 * @author Vivek Shah
 * 
 * This class is made as common or utility class where the all common methods and
 * also common variables are stored which can be used in whole development time.
 *  
 * You should declare this class  in every activity to use common things like alert and toast even , 
 * just need to pass context to constructor from there.
 * 
 */
package com.mcxsuregain.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mcxsuregain.activity.MainTabActivity;
import com.mcxsuregain.activity.R;

public class Utils extends Application {

	// This variable store the context of Application
	public static Context applicationContext;

	// private context made because no one can use ot store in this context
	private Context context = null;
	private String LOG_TAG = "JRJ_LOG";
	private SharedPreferences preferences = null;
	private SharedPreferences.Editor editor = null;

	/*
	 * Parameterized Constructor made because getting fresh context every time
	 * and to make methods easy.
	 */
	public Utils(Context con) {
		context = con;
		preferences = PreferenceManager.getDefaultSharedPreferences(con);
		editor = preferences.edit();
		// LOG_TAG = context.getPackageName().toString();
	}

	/**
	 * @param message
	 *            Pass message to show user
	 * @return It will return long toast message whatever you pass in your
	 *         application
	 */
	public void Toast(String message) {
		final String onTimeMsg = message;
		((Activity) context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View layout = inflater.inflate(R.layout.toast_layout,
						(ViewGroup) ((Activity) context)
								.findViewById(R.id.toast_layout_root));

				TextView text = (TextView) layout.findViewById(R.id.text);
				text.setText(onTimeMsg);
				Toast toast = new Toast(context);
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(Toast.LENGTH_LONG);
				toast.setView(layout);
				toast.show();
			}
		});
	}

	/**
	 * @param buttonName
	 *            set yes no or cancel
	 * @param message
	 *            message in alert box
	 * 
	 * @return AlertBox to use as user message
	 */
	public void showAlertMessage(String buttonName, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message);
		builder.setPositiveButton(buttonName,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	/**
	 * @param emailAddress
	 *            Passyour emiailaddress string to check
	 * @return It will return true if email address is valid or false in case
	 *         email is not valid
	 */
	public boolean isEmailValid(String emailAddress) {
		String regExpn = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
				+ "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
				+ "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
				+ "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
				+ "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
				+ "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

		Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(emailAddress);

		if (matcher.matches())
			return true;
		else
			return false;
	}

	/**
	 * @return It will check your Internet connection.True if any net connected.
	 */
	public boolean isNetConnected() {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

		}
		return false;
	}

	/**
	 * @return This method returns system current time , change format as per
	 *         your reuirement , Locale is also set as english so take care of
	 *         that also.It is HH:mm 24 hour format
	 */
	public String getCurrentTime() {
		return new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
				.format(new Date());
	}

	public String getCurrentTimePath() {
		return new SimpleDateFormat("HHmmss", Locale.ENGLISH)
				.format(new Date());
	}

	public String getPrevDate() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		int day = c.get(Calendar.DATE);
		int month = c.get(Calendar.MONTH) + 1;
		int year = c.get(Calendar.YEAR);
		String dateFormat = String.valueOf(day) + "/" + getMonth(month) + "/"
				+ String.valueOf(year);
		return dateFormat;
	}

	/**
	 * @return This method returns system current date in dd/MM/yyyy format
	 */
	public String getCurrentDate() {
		Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DATE);
		int month = c.get(Calendar.MONTH) + 1;
		int year = c.get(Calendar.YEAR);
		String dateFormat = String.valueOf(day) + "/" + getMonth(month) + "/"
				+ String.valueOf(year);
		return dateFormat;
	}

	public String getDefaultPrevDate() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		int day = c.get(Calendar.DATE);
		int month = c.get(Calendar.MONTH) + 1;
		int year = c.get(Calendar.YEAR);
		String dateFormat = String.valueOf(day) + "!" + getMonth(month) + "!"
				+ String.valueOf(year);
		return dateFormat;
	}

	public String getDefaultCurrentDate() {
		Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DATE);
		int month = c.get(Calendar.MONTH) + 1;
		int year = c.get(Calendar.YEAR);
		String dateFormat = String.valueOf(day) + "!" + getMonth(month) + "!"
				+ String.valueOf(year);
		return dateFormat;
	}

	public String getMonth(int month) {
		String months = "";
		switch (month) {
		case 1:
			months = "JAN";
			break;
		case 2:
			months = "FEB";
			break;
		case 3:
			months = "MAR";
			break;
		case 4:
			months = "APR";
			break;
		case 5:
			months = "MAY";
			break;
		case 6:
			months = "JUN";
			break;
		case 7:
			months = "JUL";
			break;
		case 8:
			months = "AUG";
			break;
		case 9:
			months = "SEP";
			break;
		case 10:
			months = "OCT";
			break;
		case 11:
			months = "NOV";
			break;
		case 12:
			months = "DEC";
			break;
		default:
			break;
		}
		return months;
	}

	/**
	 * set string Preference
	 */
	public void setPreference(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * Use to save string preferences
	 */
	public void setScriptPrefrences(String key, String msg) {
		editor.putString(key, msg);
		editor.commit();

	}

	/**
	 * Use to get string preference
	 */
	public String getScriptPrefrences(String key) {
		return preferences.getString(key, "");
	}

	/**
	 * get string preferences
	 */
	public String getPreference(String key) {
		return preferences.getString(key, "0");
	}

	/**
	 * Use to set boolean preference
	 */
	public void setBoolPrefrences(String key, boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}

	/**
	 * use to get boolean preference
	 */
	public boolean getBoolPref(String key) {
		return preferences.getBoolean(key, false);
	}

	/**
	 * @return This method kills all processes of application which is running
	 *         in back ground , we can also use
	 *         android.os.Process.killProcess(pid) to exit from application
	 */
	public void killProcess(Context context) {
		int pid = 0;
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> pids = am
				.getRunningAppProcesses();
		for (int i = 0; i < pids.size(); i++) {
			ActivityManager.RunningAppProcessInfo info = pids.get(i);
			if (info.processName.equalsIgnoreCase("com.dbb.activity")) {
				pid = info.pid;
			}
		}
		android.os.Process.killProcess(pid);
	}

	/**
	 * @return true is tablet resolution is there
	 */
	public boolean isTablet() {
		boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
		boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
		return (xlarge || large);
	}

	/* All logs */
	public void log(String message) {
		Log.d(LOG_TAG, message);
	}

	public void infoLog(String message) {
		Log.i(LOG_TAG, message);
	}

	public void errorLog(String message) {
		Log.e(LOG_TAG, message);
	}

	public void verboseLog(String message) {
		Log.v(LOG_TAG, message);
	}

	public void warnLog(String message) {
		Log.w(LOG_TAG, message);
	}

	public boolean isUsingMobileData() {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo mobileInfo = connectivity
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (mobileInfo.getState() == NetworkInfo.State.CONNECTED
				|| mobileInfo.getState() == NetworkInfo.State.CONNECTING) {
			return true;
		}
		return false;
	}

	/**
	 * @return IMEI NO
	 */
	public String getImeiNo() {
		TelephonyManager teleManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return teleManager.getDeviceId();
	}

	public boolean isUsingWiFi() {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiInfo = connectivity
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiInfo.getState() == NetworkInfo.State.CONNECTED
				|| wifiInfo.getState() == NetworkInfo.State.CONNECTING) {
			return true;
		}

		return false;
	}

	public boolean isMemorycard() {

		Boolean isSDPresent = android.os.Environment.getExternalStorageState()
				.equals(android.os.Environment.MEDIA_MOUNTED);

		if (isSDPresent) {
			// yes SD-card is present
			return true;
		} else {
			// Sorry
			return false;
		}

	}

	@SuppressWarnings("deprecation")
	public void notification(String msg) {
		// NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
		// context).setSmallIcon(R.drawable.ic_launcher)
		// .setContentTitle("MCX Suregain").setContentText(msg);
		// Intent resultIntent = new Intent(context, MainTabActivity.class);
		// TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// stackBuilder.addParentStack(MainTabActivity.class);
		// stackBuilder.addNextIntent(resultIntent);
		// PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
		// PendingIntent.FLAG_UPDATE_CURRENT);
		// mBuilder.setContentIntent(resultPendingIntent);
		// NotificationManager mNotificationManager = (NotificationManager)
		// context
		// .getSystemService(Context.NOTIFICATION_SERVICE);
		// mBuilder.setAutoCancel(true);
		// mBuilder.setDefaults(Notification.DEFAULT_SOUND
		// | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
		// mNotificationManager.notify(0, mBuilder.build());
		try {
			NotificationManager mManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			Intent intent1 = new Intent(context, MainTabActivity.class);
			intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
					| Intent.FLAG_ACTIVITY_CLEAR_TOP);
			Notification notification = new Notification(
					R.drawable.ic_launcher, "Mcx SureGain",
					System.currentTimeMillis());
			PendingIntent pendingNotificationIntent = PendingIntent
					.getActivity(context, 0, intent1,
							PendingIntent.FLAG_UPDATE_CURRENT);
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.defaults |= Notification.DEFAULT_ALL;
			notification.setLatestEventInfo(context, msg, null,
					pendingNotificationIntent);
			mManager.notify(0, notification);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public boolean isValidDate(String currentDate, String validDt) {
		boolean response = false;
		// 24 < 28
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
					Locale.ENGLISH);
			Date date1 = sdf.parse(currentDate);
			Date date2 = sdf.parse(validDt);
			if (date1.before(date2)) {
				response = true;
			} else {
				response = false;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return response;
	}
}
