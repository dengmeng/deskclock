package com.android.deskclock;

import android.app.Fragment;
import android.os.Bundle;

final class DeskClockPagerItem {
  private final Class mClassName;
  private final Bundle mSaveBundle;
  private Fragment mFragment;

  DeskClockPagerItem(Class className, Bundle saveBundle) {
    this.mClassName = className;
    this.mSaveBundle = saveBundle;
  }
  
  public static Fragment getFragment(DeskClockPagerItem item) {
	  return item.mFragment;
  }
  
  public static Fragment getFragment(DeskClockPagerItem item, Fragment fragmentInstance) {
	  item.mFragment = fragmentInstance;
	  return item.mFragment;
  }
  
  public static Class getClassName(DeskClockPagerItem item) {
	  return item.mClassName;
  }
  
  public static Bundle getBundle(DeskClockPagerItem item) {
	  return item.mSaveBundle;
  }
}
