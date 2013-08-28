package com.android.deskclock;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import java.util.ArrayList;

public class DeskClockAdapter extends FragmentPagerAdapter
  implements ActionBar.TabListener, ViewPager.OnPageChangeListener {
  private final ArrayList mArrayList = new ArrayList();
  private int mCurrpager;
  private final ActionBar mActionBar;
  private final Context mContext;
  private final ViewPager mViewPager;

  static private final int TAB_PAGE_WORLDCLOCK = 1;
  static private final int TAB_PAGE_ALARM = 0;
  static private final int TAB_PAGE_STOPWATCH = 2;
  static private final int TAB_PAGE_TIMER = 3;
  
  
  public DeskClockAdapter(Activity activity, ViewPager viewPager) {
    super(activity.getFragmentManager());
    this.mContext = activity;
    this.mActionBar = activity.getActionBar();
    this.mViewPager = viewPager;
    this.mViewPager.setAdapter(this);
    this.mViewPager.setOnPageChangeListener(this);
  }

  static public void DeskClockAdapterSetTab(DeskClockAdapter adapter, int index) {
	  adapter.mViewPager.setCurrentItem(index);
  }
  
  public void AddFragment(ActionBar.Tab tabItem, Class className, Bundle savedState) {
	DeskClockPagerItem item = new DeskClockPagerItem(className, savedState);
    tabItem.setTag(item);
    tabItem.setTabListener(this);
    this.mArrayList.add(item);
    this.mActionBar.addTab(tabItem);
    notifyDataSetChanged();
  }

  public int getCount()
  {
    return this.mArrayList.size();
  }

  @Override
  public Fragment getItem(int index) {

	DeskClockPagerItem item = (DeskClockPagerItem)this.mArrayList.get(index);
    if (DeskClockPagerItem.getFragment(item) == null)
    	DeskClockPagerItem.getFragment(item, Fragment.instantiate(this.mContext, 
    	DeskClockPagerItem.getClassName(item).getName(), 
    	DeskClockPagerItem.getBundle(item)));
    return (Fragment)DeskClockPagerItem.getFragment(item);
  }

  public void onPageScrollStateChanged(int paramInt)
  {
  }

  public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2)
  {
  }

  
  public void onPageSelected(int pageIndex)
  {
    if ((this.mActionBar.getNavigationMode() == ActionBar.NAVIGATION_MODE_TABS) && (pageIndex != this.mActionBar.getSelectedNavigationIndex()))
    	this.mActionBar.setSelectedNavigationItem(pageIndex);
    if (pageIndex == TAB_PAGE_TIMER) {
    	((TimerFragment)getItem(pageIndex)).onSelected();
    }
    
    if (this.mCurrpager == TAB_PAGE_TIMER) {
    	((TimerFragment)getItem(TAB_PAGE_TIMER)).onUnSelected();
    }
    
    ((DeskClockInterface)getItem(this.mCurrpager)).handler();
    this.mCurrpager = pageIndex;
    ((Activity)this.mContext).invalidateOptionsMenu();
    //((DeskClockTabActivity)this.mContext).o().trackEvent("tabIndex", pageIndex);
  }

  public void onTabReselected(ActionBar.Tab paramTab, FragmentTransaction paramFragmentTransaction)
  {
  }

  public void onTabSelected(ActionBar.Tab paramTab, FragmentTransaction paramFragmentTransaction)
  {
    Object localObject = paramTab.getTag();
    for (int i = 0; i < this.mArrayList.size(); ++i)
    {
      if (this.mArrayList.get(i) != localObject)
        continue;
      this.mViewPager.setCurrentItem(i);
    }
  }

  public void onTabUnselected(ActionBar.Tab paramTab, FragmentTransaction paramFragmentTransaction)
  {
  }
}
