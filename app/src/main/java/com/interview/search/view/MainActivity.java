package com.interview.search.view;
/**
 * Created by Vinit sharma on 11-06-2016.
 */
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.interview.search.R;
import com.interview.search.model.MySuggestionProvider;
import com.interview.search.util.NetworkUtil;

public class MainActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener {
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
            MenuItem searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                String suggestion = getSuggestion(position);
                searchView.setQuery(suggestion, true);
                return true;
            }
        });
        searchView.setOnQueryTextListener(this);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(this, SearchableActivity.class)));
        searchView.setIconifiedByDefault(false);

        return true;
    }
    private String getSuggestion(int position) {
        Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(
                position);
        String suggest1 = cursor.getString(cursor
                .getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
        return suggest1;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                clearCache();
               Toast.makeText(this, getResources().getString(R.string.searchclear_history),Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, getResources().getString(R.string.sugg_by)+ query, Toast.LENGTH_SHORT).show();

        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            String uri = intent.getDataString();
            Toast.makeText(this, getResources().getString(R.string.sugg)+ uri, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if(NetworkUtil.getConnectivityStatus(MainActivity.this)!=NetworkUtil.TYPE_NOT_CONNECTED){
            return false;
        }
        else{
            Toast.makeText(this, getResources().getString(R.string.nw_not_con), Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {

            return false;

    }

    private void clearCache(){
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
        suggestions.clearHistory();
    }
}
