package com.interview.search.view;
/**
 * Created by Vinit sharma on 11-06-2016.
 */
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.interview.search.R;
import com.interview.search.adapter.CustomRecycleListAdapter;
import com.interview.search.app.AppController;
import com.interview.search.model.ImageObject;
import com.interview.search.model.MySuggestionProvider;
import com.interview.search.model.Thumbnail;
import com.interview.search.util.NetworkUtil;
import com.interview.search.util.URL_CONFIG;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SearchableActivity extends AppCompatActivity {

    private  String url ;
    private ProgressDialog pDialog;
    private List<ImageObject> imageObjectList = new ArrayList<ImageObject>();
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private ImageView noResultView;
    private  URL_CONFIG urlConfig;
    private String TAG = SearchableActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        urlConfig = new URL_CONFIG.Builder().thumbSize(300).thumbnailLimit(50).build();
        url = urlConfig.getURL();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        noResultView = (ImageView) findViewById(R.id.no_result);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
            getSupportActionBar().setTitle(getResources().getString(R.string.search_title) + " " + query);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showSearchResult(String text) {

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        imageObjectList.clear();
        pDialog.show();

        // Creating volley request obj
        JsonObjectRequest imageRequest = new JsonObjectRequest(url + text,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();

                        // Parsing json
                            try {

                                JSONObject obj = response.getJSONObject("query").getJSONObject("pages");
                                Iterator<String> keys = obj.keys();
                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    JSONObject page = obj.getJSONObject(key);
                                    String title = page.getString("title");
                                    JSONObject thumbnailJSON = null;
                                    try {
                                        thumbnailJSON = page.getJSONObject("thumbnail");
                                    } catch (Exception e) {

                                    }
                                    Thumbnail thumbnail = new Thumbnail();
                                    if(thumbnailJSON != null) {

                                        thumbnail.setSource(thumbnailJSON.getString("source"));
                                        thumbnail.setWidth(thumbnailJSON.getInt("width"));
                                        thumbnail.setHeight(thumbnailJSON.getInt("height"));
                                    } else {
                                        thumbnail.setSource(null);
                                        thumbnail.setWidth(urlConfig.getThumbSize());
                                        thumbnail.setHeight(urlConfig.getThumbSize());
                                    }
                                    ImageObject imageObject = new ImageObject();
                                    imageObject.setTitle(title);
                                    imageObject.setThumbnail(thumbnail);
                                    imageObjectList.add(imageObject);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        if(imageObjectList.size()> 0){
                            recyclerView.setVisibility(View.VISIBLE);
                            noResultView.setVisibility(View.GONE);
                        }
                        else{
                            recyclerView.setVisibility(View.GONE);
                            noResultView.setVisibility(View.VISIBLE);
                        }
                        adapter = new CustomRecycleListAdapter(imageObjectList,SearchableActivity.this);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();
                recyclerView.setVisibility(View.GONE);
                noResultView.setVisibility(View.VISIBLE);
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(imageRequest);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
        imageObjectList.clear();
        imageObjectList=null;
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
}
