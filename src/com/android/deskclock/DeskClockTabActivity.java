package com.android.deskclock;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.umeng.analytics.MobclickAgent;


public class DeskClockTabActivity extends Activity
{
  private DeskClockAdapter mAdapter;
  private ViewPager mViewPager;
  static private final int TAB_PAGE_WORLDCLOCK = 1;
  static private final int TAB_PAGE_ALARM = 0;
  static private final int TAB_PAGE_STOPWATCH = 2;
  static private final int TAB_PAGE_TIMER = 3;
  
  private static final int CHOOSEACTIVITYREQUESTCODE = 10;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
	  OutputLog.debug("DeskClockTabActivity:onCreate");
	  super.onCreate(savedInstanceState);
	  MobclickAgent.onError(this);
	  setContentView(R.layout.fragment_pager);
	  mViewPager = ((ViewPager)findViewById(R.id.pager));

	  ActionBar localActionBar = getActionBar();
	  localActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	  localActionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
	  mAdapter = new DeskClockAdapter(this, mViewPager);

	  mAdapter.AddFragment(localActionBar.newTab().setText(getString(R.string.alarm_list_title)), AlarmClock.class, null);
	  //mAdapter.AddFragment(localActionBar.newTab().setText(getString(R.string.worldclock_title)), WorldclockFragment.class, null);
	  mAdapter.AddFragment(localActionBar.newTab().setText(getString(R.string.worldclock_title)), WorldclockFragmentNew.class, null);
	  mAdapter.AddFragment(localActionBar.newTab().setText(getString(R.string.stopwatch_title)), StopwatchFragment.class, null);
	  mAdapter.AddFragment(localActionBar.newTab().setText(getString(R.string.timer_title)), TimerFragment.class, null);
	  
	  if (getIntent().hasExtra("navigation_tab")) {
		  int tabIndex;
		  tabIndex = PreferenceManager.getDefaultSharedPreferences(this).getInt("last_launched_tab_index_pref", 0);
		  if ((tabIndex > 3) || (tabIndex < 0))
			  tabIndex = 0;
		  localActionBar.setSelectedNavigationItem(tabIndex);
		  DeskClockAdapter.DeskClockAdapterSetTab(mAdapter, tabIndex);
    	
	  }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
	  OutputLog.debug("DeskClockTabActivity:onCreateOptionsMenu");
	  getMenuInflater().inflate(R.menu.alarm_list_menu, menu);
	  return super.onCreateOptionsMenu(menu);
  }

  
  
  @Override
  protected void onNewIntent(Intent intent) {
	  OutputLog.debug("DeskClockTabActivity:onNewIntent");
	  if (intent.hasExtra("navigation_tab")) {
		  OutputLog.debug("DeskClockTabActivity:onNewIntent:hasExtra");
		  int i = intent.getIntExtra("navigation_tab", 0);
		  getActionBar().setSelectedNavigationItem(i);
	  }
	  super.onNewIntent(intent);
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
	  boolean flag = true;
	  switch (item.getItemId()) {
	  	default:
	  		OutputLog.debug("DeskClockTabActivity:onOptionsItemSelected:default");
	  		flag = super.onOptionsItemSelected(item);
	  	case R.id.menu_item_settings:
	  		OutputLog.debug("DeskClockTabActivity:onOptionsItemSelected:menu_item_settings");
	  		//startActivity(new Intent(this, SettingsActivity.class));
	  		mAdapter.getItem(TAB_PAGE_ALARM).startActivity(new Intent(this, SettingsActivity.class));
	  		break;
	  	case R.id.action_edit_systemtime:
	  		OutputLog.debug("DeskClockTabActivity:onOptionsItemSelected:action_edit_systemtime");
	  		startActivity(new Intent("android.settings.DATE_SETTINGS"));
	  		break;
	  	case R.id.action_add_new_city:
	  		OutputLog.debug("DeskClockTabActivity:onOptionsItemSelected:action_add_new_city");
	  		//Intent intentAutoCompleteTextView = new Intent(this,
					//AutoCompleteTextViewActivity.class);
	  		//mAdapter.getItem(TAB_PAGE_WORLDCLOCK).startActivityForResult(
					//intentAutoCompleteTextView, CHOOSEACTIVITYREQUESTCODE);
	  		mAdapter.getItem(TAB_PAGE_WORLDCLOCK).startActivityForResult(new Intent(this, TimezoneSearchView.class), 100);
			
	  		break;
	  	case R.id.action_add_new_alarm:
	  		mAdapter.getItem(TAB_PAGE_ALARM).startActivity(new Intent(this, SetAlarm.class));
	  		break;
	  }

    return flag;
  }


@Override
	protected void onPause() {
		OutputLog.debug("DeskClockTabActivity:onPause");
    	PreferenceManager.getDefaultSharedPreferences(this).edit().putInt("last_launched_tab_index_pref", getActionBar().getSelectedNavigationIndex()).commit();
    	super.onPause();
    	MobclickAgent.onPause(this);
  }


  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    switch (getActionBar().getSelectedNavigationIndex()) {
    default:
    	OutputLog.debug("DeskClockTabActivity:onPrepareOptionsMenu:default");
    	menu.findItem(R.id.menu_item_settings).setVisible(false);
    	menu.findItem(R.id.menu_item_edit_alarm).setVisible(false);
    	menu.findItem(R.id.action_edit_worldclock).setVisible(false);
    	menu.findItem(R.id.action_add_new_city).setVisible(false);
    	menu.findItem(R.id.action_add_new_alarm).setVisible(false);
    	break;
    case TAB_PAGE_WORLDCLOCK:
    	OutputLog.debug("DeskClockTabActivity:onPrepareOptionsMenu:TAB_PAGE_WORLDCLOCK");
    	menu.findItem(R.id.menu_item_settings).setVisible(false);
    	menu.findItem(R.id.menu_item_edit_alarm).setVisible(false);
    	menu.findItem(R.id.action_edit_worldclock).setVisible(false);
    	menu.findItem(R.id.action_add_new_alarm).setVisible(false);
    	menu.findItem(R.id.action_add_new_city).setVisible(true);
    	break;
    case TAB_PAGE_ALARM:
    	OutputLog.debug("DeskClockTabActivity:onPrepareOptionsMenu:TAB_PAGE_ALARM");
    	menu.findItem(R.id.menu_item_settings).setVisible(true);
    	menu.findItem(R.id.menu_item_edit_alarm).setVisible(false);
    	menu.findItem(R.id.action_edit_worldclock).setVisible(false);
    	menu.findItem(R.id.action_add_new_city).setVisible(false);
    	menu.findItem(R.id.action_add_new_alarm).setVisible(true);
    	break;
    }
    while (true) {
    	return super.onPrepareOptionsMenu(menu);
    }
  }

  
  @Override
  protected void onResume() {
	  OutputLog.debug("DeskClockTabActivity:onResume");
	  super.onResume();
	  MobclickAgent.onResume(this);
  }
}

