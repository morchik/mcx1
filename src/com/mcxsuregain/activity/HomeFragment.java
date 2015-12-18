package com.mcxsuregain.activity;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.mcxsuregain.utils.Constants;
import com.mcxsuregain.utils.ListModel;
import com.mcxsuregain.utils.SoapRequests;
import com.mcxsuregain.utils.SpinPojo;
import com.mcxsuregain.utils.Utils;

public class HomeFragment extends Fragment {

	private Button btnSearch;
	private ListView list;
	private View view;
	private ArrayList<SpinPojo> spinList;
	private String dateSet = "";
	private String fromdate = "", toDate = "";
	private int id = 0;
	private Utils utils;
	private SwipeRefreshLayout swipeLayout;
	private ArrayList<ListModel> listOfData;
	private TextView textSetDate, textSetToDate;
	private Spinner spinSymbols;
	private ImageView imageSettings;
	private TableRow imagefrom, imageTodate;
	public Handler timerHandler;
	public Runnable timerRunnable;
	private String stringCurrentDate = "";

	int oldSize = 0, newSize = 0;
	private boolean isOnPause = false;
	listAdapter adapter;
	public static boolean comingFromOtherTab = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = LayoutInflater.from(getActivity()).inflate(
				R.layout.home_fragment, container, false);
		setViews();
		// setting default dates
		if (!utils.getBoolPref(Constants.prefIsLogin)) {
			onDestroy();
			getActivity().finish();
		} else {
			setNewOldDateData();

			if (utils.isNetConnected()) {
				if (spinList.size() <= 0) {
					new synchSymbols().execute();
				}
			} else {
				utils.Toast("please check net connection");
			}

			imageSettings.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					settings();
				}
			});

			try {
				if (getActivity() != null) {
					timerHandler = new Handler();
					timerRunnable = new Runnable() {
						@Override
						public void run() {
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									System.out.println("Refreshing....");
									new synchMarket().execute();
								}
							});
							timerHandler.postDelayed(this, 20000); // run every 20 sec//??/?????? 
						}
					};
					timerRunnable.run();
					list.setAdapter(adapter);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return view;
	}

	private void setViews() {
		list = (ListView) view.findViewById(R.id.listView);
		list.setDividerHeight(5);
		list.setClickable(false);
		utils = new Utils(getActivity());

		if (comingFromOtherTab) {
			killTimer();
			comingFromOtherTab = false;
			listOfData = null;
			listOfData = new ArrayList<ListModel>();
			listOfData.clear();
		}

		utils.setBoolPrefrences(Constants.prefIsNotifyEnable, false);
		utils.setPreference(Constants.oldPref, "0");
		imageSettings = (ImageView) view.findViewById(R.id.imageSettings);
		swipeLayout = (SwipeRefreshLayout) view
				.findViewById(R.id.swipe_container);
		swipeLayout.setColorSchemeResources(R.color.holo_blue_bright,
				R.color.holo_blue_bright, R.color.holo_orange_light,
				R.color.holo_blue_bright);
		swipeLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				if (utils.isNetConnected()) {
					if (utils.getPreference(Constants.marketString).equals("0")) {
						utils.Toast("Please select new searching..");
						swipeLayout.setRefreshing(false);
					} else {
						// new synchMarket().execute();
					}
				} else {
					utils.Toast("please check net connection");
				}

			}
		});
		spinList = new ArrayList<>();
		adapter = new listAdapter();
	}

	public void settings() {
		final Dialog dialogSet = new Dialog(getActivity());
		dialogSet.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogSet.setContentView(R.layout.row_settings);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialogSet.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.gravity = Gravity.CENTER;
		dialogSet.getWindow().setAttributes(lp);
		imagefrom = (TableRow) dialogSet.findViewById(R.id.tableRow1);
		textSetDate = (TextView) dialogSet.findViewById(R.id.textSetFromDate);
		imageTodate = (TableRow) dialogSet.findViewById(R.id.tableRow2);
		textSetToDate = (TextView) dialogSet.findViewById(R.id.textSetToDate);

		btnSearch = (Button) dialogSet.findViewById(R.id.btnSearch);
		spinSymbols = (Spinner) dialogSet.findViewById(R.id.spinsymbols);
		textSetDate.setText(utils.getPrevDate());
		textSetToDate.setText(stringCurrentDate);

		ArrayList<String> spinData = new ArrayList<>();
		for (SpinPojo string : spinList) {
			String str = string.getValue();
			spinData.add(str);
		}
		spinSymbols.setAdapter(new ArrayAdapter<>(getActivity(),
				android.R.layout.simple_spinner_item, spinData));

		imagefrom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				id = 0;
				dateDialog();
			}
		});

		imageTodate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				id = 1;
				dateDialog();
			}
		});

		btnSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (fromdate.equals("")) {
					utils.Toast("Please select from date");
				} else if (toDate.equals("")) {
					utils.Toast("Please select to date");
				} else {
					String string = spinSymbols.getSelectedItem().toString();
					String symbolId = "";
					for (SpinPojo spinpojo : spinList) {
						if (spinpojo.getValue().equals(string)) {
							symbolId = spinpojo.getKey();
							break;
						}
					}
					try {
						if (utils.isNetConnected()) {
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("SymbolId", symbolId);
							jsonObject.put("ToDate", toDate);
							jsonObject.put("FromDate", fromdate);
							String str = jsonObject.toString()
									.replace("!", "/");
							utils.setPreference(Constants.marketString, str);
							// new synchMarket().execute();
						} else {
							utils.Toast("please check net connection");
						}
						dialogSet.dismiss();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		});
		dialogSet.show();
	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			if (isOnPause) {
				isOnPause = false;
				if (utils.getBoolPref(Constants.prefIsLogin)) {
					if (utils.isNetConnected()) {
						setNewOldDateData();
					} else {
						utils.Toast("please check net connection");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		isOnPause = true;
	}

	private class synchSymbols extends AsyncTask<Void, Void, String> {

		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(getActivity());
			dialog.setMessage("Loading");
			dialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			String jsonString = "";
			try {
				spinList = new ArrayList<>();
				jsonString = new SoapRequests().getSymbols();
				String subStr = jsonString.substring(jsonString.indexOf("[{"),
						jsonString.indexOf(";"));
				JSONArray jsonArray = new JSONArray(subStr);
				SpinPojo pojo;
				for (int i = 0; i < jsonArray.length(); i++) {
					pojo = new SpinPojo();
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					String key = jsonObject.get("SymbolID").toString();
					String value = jsonObject.get("Symbol").toString();
					pojo.setKey(key);
					pojo.setValue(value);
					spinList.add(pojo);
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
			}
			dialog.dismiss();
		}
	}

	public void dateDialog() {
		final Dialog dialog = new Dialog(getActivity());
		dialog.setTitle("Set date");
		dialog.setContentView(R.layout.custom_date);
		DatePicker datePicker = (DatePicker) dialog
				.findViewById(R.id.datePicker1);
		Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
		int day = datePicker.getDayOfMonth();
		int month = datePicker.getMonth();
		int year = datePicker.getYear();
		dateSet = day + "!" + utils.getMonth(month + 1) + "!" + year
				+ " 00:00:00";
		stringCurrentDate = day + "/" + utils.getMonth(month + 1) + "/" + year;
		datePicker.init(year, month, day, new OnDateChangedListener() {
			@Override
			public void onDateChanged(DatePicker view, int year,
					int monthOfYear, int dayOfMonth) {
				dateSet = dayOfMonth + "!" + utils.getMonth(monthOfYear + 1)
						+ "!" + year + " 00:00:00";
				stringCurrentDate = dayOfMonth + "/"
						+ utils.getMonth(monthOfYear + 1) + "/" + year;
			}
		});

		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println(dateSet);
				if (id == 0) {
					fromdate = dateSet;
					textSetDate.setText(stringCurrentDate);
				}
				if (id == 1) {
					toDate = dateSet;
					textSetToDate.setText(stringCurrentDate);
				}
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private class synchMarket extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			listOfData = null;
			listOfData = new ArrayList<>();
			swipeLayout.setRefreshing(true);
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				String str = utils.getPreference(Constants.marketString);
				String jsonString = new SoapRequests().serachMarketRequest(str);
				Log.w("", str+" "+jsonString);
				JSONArray jsonArray = new JSONArray(jsonString);
				ListModel listModel;
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					String callText = jsonObject.getString("CallText");
					String type = jsonObject.getString("Type");
					String pl = jsonObject.getString("PL");
					String time = jsonObject.getString("LatestTime");
					String isStopLoss = jsonObject.getString("IsStopLoss");
					listModel = new ListModel();
					listModel.setCalltext(callText);
					listModel.setType(type);
					listModel.setPL(pl);
					listModel.setLatestTime(time);
					listModel.setIsStopLoss(isStopLoss);
					listOfData.add(listModel);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							swipeLayout.setRefreshing(false);
							newSize = listOfData.size();
							oldSize = Integer.valueOf(utils
									.getPreference(Constants.oldPref));
							if (newSize > oldSize) {
								utils.setPreference(Constants.oldPref,
										String.valueOf(newSize));
								if (utils
										.getBoolPref(Constants.prefIsNotifyEnable)) {

									Log.w("test ", (listOfData.get(0).getLatestTime()));
									String[] strCheckNotify = listOfData.get(0)
											.getLatestTime().split("T");
									String serverDate = utils
											.getPreference(Constants.server_date);
									Log.w("test ", serverDate+" - "+strCheckNotify[0]); // ????
									Log.e("error ", "ttt "+serverDate+" - "+strCheckNotify[0]);
									if (strCheckNotify[0].equals(serverDate)) {
										utils.notification(listOfData.get(0)
												.getCalltext());
										utils.setBoolPrefrences(
												Constants.prefIsNotifyEnable,
												false);
									}
								}
							} else {
								utils.setPreference(Constants.oldPref,
										String.valueOf(newSize));
								utils.setBoolPrefrences(
										Constants.prefIsNotifyEnable, true);
							}
							if (!isOnPause) {
								adapter.notifyDataSetChanged();
								list.invalidateViews();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class listAdapter extends BaseAdapter {
		private LayoutInflater inflater = null;

		public listAdapter() {
			inflater = (LayoutInflater) getActivity().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return listOfData.size();
		}

		@Override
		public Object getItem(int position) {
			return listOfData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View vi = convertView;
			ViewHolder holder;
			if (convertView == null) {
				vi = inflater.inflate(R.layout.row_list_item, null);
				holder = new ViewHolder();
				holder.textCallType = (TextView) vi
						.findViewById(R.id.textCallType);
				holder.textDate = (TextView) vi.findViewById(R.id.textDate);
				holder.textTime = (TextView) vi.findViewById(R.id.textTime);
				holder.textCallText = (TextView) vi
						.findViewById(R.id.textCallText);
				holder.textpl = (TextView) vi.findViewById(R.id.textPL);
				holder.textplLabel = (TextView) vi
						.findViewById(R.id.textPLLabel);
				holder.layoutCall = (LinearLayout) vi
						.findViewById(R.id.callLayout);
				vi.setTag(holder);
			} else
				holder = (ViewHolder) vi.getTag();
			try {
				if (listOfData.size() != 0 && position < listOfData.size()) {
					String strCall = listOfData.get(position).getType();
					String strDateTime = listOfData.get(position)
							.getLatestTime();
					String strDetail = listOfData.get(position).getCalltext();
					String strPL = listOfData.get(position).getPL();
					String dateTime[] = null;
					if (strCall.equals("null")) {
						strCall = "";
					} else if (strDateTime.equals("null")) {
						strDateTime = "";
						holder.textDate.setText("");
						holder.textTime.setText("");
					} else {
						dateTime = strDateTime.split("T");
						holder.textDate.setText("Date : " + dateTime[0]);
						SimpleDateFormat formatter = new SimpleDateFormat(
								"hh:mm aa", Locale.ENGLISH);
						Time time = Time.valueOf(dateTime[1]);
						holder.textTime.setText("Time : "
								+ formatter.format(time));
					}
					if (strDetail.equals("null")) {
						strDetail = "";
					} else if (strPL.equals("null")) {
						strPL = "";
					}
					holder.textplLabel.setText("");
					if (strCall.toUpperCase().contains("buy".toUpperCase())) {
						holder.layoutCall
								.setBackgroundResource(R.color.green_vivek);
						holder.textCallText
								.setTextColor(Color.rgb(102, 153, 0));
					} else if (strCall.toUpperCase().contains(
							"sell".toUpperCase())) {
						holder.layoutCall
								.setBackgroundResource(R.color.red_vivek);
						holder.textCallText.setTextColor(Color.RED);
					} else if (strCall.toUpperCase().contains(
							"result".toUpperCase())) {
						if (strDetail.contains("P/L")) {
							String[] str = strDetail.split("P/L =");
							String first = str[0];
							String two = " " + str[1];
							holder.layoutCall
									.setBackgroundResource(R.color.orange_vivek);
							holder.textplLabel.setTextColor(Color.rgb(255, 136,
									0));
							holder.textplLabel.setText("P/L = ");
							holder.textCallText.setTextColor(Color.rgb(255,
									136, 0));
							if (strDetail.toUpperCase().contains(
									"target".toUpperCase())) {
								holder.textCallText.setText(first);
								holder.textpl.setTextColor(Color.BLACK);
								holder.textpl.setBackgroundColor(Color.GREEN);
								holder.textpl.setText(two);
							} else {
								holder.textCallText.setText(first);
								holder.textpl.setTextColor(Color.BLACK);
								holder.textpl.setBackgroundColor(Color.RED);
								holder.textpl.setText(two);
							}
						} else {
							holder.layoutCall
									.setBackgroundResource(R.color.orange_vivek);
							holder.textCallText.setTextColor(Color.rgb(255,
									136, 0));
						}
					} else {

					}
					holder.textCallType.setText(strCall
							.toUpperCase(Locale.ENGLISH));
					if (!strCall.toUpperCase().contains("result".toUpperCase())) {
						holder.textCallText.setText(strDetail);
						holder.textpl
								.setText(strPL.toUpperCase(Locale.ENGLISH));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return vi;
		}

		class ViewHolder {
			TextView textCallType, textCallText, textDate, textTime, textpl,
					textplLabel;
			LinearLayout layoutCall;
		}
	}

	public void killTimer() {
		try {
			if (timerHandler != null) {
				timerHandler.removeCallbacks(timerRunnable);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setNewOldDateData() {
		utils.setPreference(Constants.marketString, "0");
		stringCurrentDate = utils.getCurrentDate();
		fromdate = utils.getDefaultPrevDate() + " 00:00:00";
		toDate = utils.getDefaultCurrentDate() + " 00:00:00";
		if (utils.getPreference(Constants.marketString).equals("0")) {
			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("SymbolId", "0");
				jsonObject.put("ToDate", toDate);
				jsonObject.put("FromDate", fromdate);
				String str = jsonObject.toString().replace("!", "/");
				utils.setPreference(Constants.marketString, str);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		killTimer();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		System.err.println("detached view");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		killTimer();
		utils.setBoolPrefrences(Constants.prefIsNotifyEnable, false);
	}
}