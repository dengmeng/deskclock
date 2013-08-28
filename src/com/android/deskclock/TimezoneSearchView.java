package com.android.deskclock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import com.umeng.analytics.MobclickAgent;

public class TimezoneSearchView extends Activity
  implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {
  private ListView mListView;
  private SearchView mSearchView;
  private TimezoneSearchViewAdapter mAdapter;

  	private void setSearchView() {
	    mSearchView.setOnQueryTextListener(this);
	    mSearchView.setSubmitButtonEnabled(false);
	    mSearchView.setBackgroundResource(R.drawable.listview_bg);
	    mSearchView.setQueryHint(getString(R.string.timezone_search_hint));
	    mSearchView.setQuery(null, false);
	    mSearchView.onActionViewExpanded();
  	}

  
  	@Override
  	protected void onCreate(Bundle bundle) {
	    super.onCreate(bundle);
	    getWindow().requestFeature(1);
	    setContentView(R.layout.timezone_searchview);
	    mSearchView = ((SearchView)findViewById(R.id.search_view));
	    mListView = ((ListView)findViewById(R.id.list_view));
	    mAdapter = new TimezoneSearchViewAdapter(this, this, false);
	    mListView.setAdapter(mAdapter);
	    mListView.setOnItemClickListener(this);
	    setSearchView();
  	}

  	public void onItemClick(AdapterView paramAdapterView, View paramView, int paramInt, long paramLong) {
  		TimezoneInfo timezone = (TimezoneInfo)mAdapter.getItem(paramInt);
	    setResult(-1, new Intent().putExtra("android.intent.extra.TEXT", timezone.mId));
	    finish();
  	}

  public boolean onQueryTextChange(String str)
  {
    mAdapter.search(str);
    mAdapter.notifyDataSetChanged();
    return true;
  }

  public boolean onQueryTextSubmit(String str)
  {
    return false;
  }
  
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
}