package com.mcxsuregain.activity;

import com.mcxsuregain.utils.Constants;
import com.mcxsuregain.utils.Utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainTabActivity extends FragmentActivity {

	private FragmentTabHost mTabHost;
	private Utils utils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		utils = new Utils(this);
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		mTabHost.setBackgroundColor(getResources().getColor(
				R.color.material_color));

		// home
		Bundle b = new Bundle();
		b.putString("key", "Home");
		mTabHost.addTab(
				mTabHost.newTabSpec("Home").setIndicator(
						createTabView(R.drawable.ic_home, getResources()
								.getString(R.string.home))),
				HomeFragment.class, b);

		// profit
		b = new Bundle();
		b.putString("key", "TodaysProfit");
		mTabHost.addTab(
				mTabHost.newTabSpec("TodaysProfit").setIndicator(
						createTabView(R.drawable.ic_profit, getResources()
								.getString(R.string.today_profit))),
				TodayProfitContainer.class, b);

		// monthly profit
		b = new Bundle();
		b.putString("key", "MonthlyProfit");
		mTabHost.addTab(
				mTabHost.newTabSpec("MonthlyProfit").setIndicator(
						createTabView(R.drawable.monthly_profit, getResources()
								.getString(R.string.monthly_profit))),
				MonthlyProfitContainer.class, b);

		// form
		b = new Bundle();
		b.putString("key", "SubscriptionForm");
		mTabHost.addTab(
				mTabHost.newTabSpec("SubscriptionForm").setIndicator(
						createTabView(R.drawable.ic_form, getResources()
								.getString(R.string.form))),
				QueryContainer.class, b);

		// account
		b = new Bundle();
		b.putString("key", "MyAccount");
		mTabHost.addTab(
				mTabHost.newTabSpec("MyAccount").setIndicator(
						createTabView(R.drawable.myaccount, getResources()
								.getString(R.string.account))),
				AccountContainer.class, b);
	}

	private View createTabView(final int id, final String text) {
		View view = LayoutInflater.from(this).inflate(R.layout.tag_icon, null);
		ImageView imageView = (ImageView) view.findViewById(R.id.tab_icon);
		imageView.setImageDrawable(getResources().getDrawable(id));
		TextView textView = (TextView) view.findViewById(R.id.tab_text);
		textView.setText(text);
		return view;
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				MainTabActivity.this);
		builder.setTitle("Do you want to Exit?");
		builder.setPositiveButton("Yes", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				utils.setPreference(Constants.marketString, "0");
				utils.setBoolPrefrences(Constants.prefIsNotifyEnable, false);
				if (utils.getBoolPref(Constants.prefIsRegLogin)) {
					utils.setBoolPrefrences(Constants.prefIsLogin, false);
					utils.setBoolPrefrences(Constants.prefIsRegLogin, false);
				}
				new HomeFragment().killTimer();
				finish();
			}
		}).setNegativeButton("No", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).create();
		builder.show();
	}
}