package co.ke.daletsys.azyma.ui;

import static co.ke.daletsys.azyma.control.InternetConnection.checkConnection;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import co.ke.daletsys.azyma.R;
import co.ke.daletsys.azyma.global.ApiConfig;
import co.ke.daletsys.azyma.global.ExpandCollapseExtention;
import co.ke.daletsys.azyma.global.HttpsTrustManager;
import co.ke.daletsys.azyma.ui.home.UserAdapter;
import co.ke.daletsys.azyma.ui.home.UserHolder;

@RequiresApi(api = Build.VERSION_CODES.M)
public class Users extends AppCompatActivity implements RecyclerView.OnScrollChangeListener {

    private SwipeRefreshLayout oSLay;
    Context cContext;
    private RequestQueue requestQueue;
    private int requestCount = 1;
    private RecyclerView.LayoutManager nLayoutManager;
    private RecyclerView oRecyclerView;
    private ArrayList<UserHolder> oHolder;
    private RecyclerView.Adapter oAdapter;
    String gCategory, gCategoryDet, gUrl,gEmail,gLatitude,gLongitude;
    SharedPreferences pSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        cContext = this.getApplicationContext();
        Intent it = this.getIntent();
        gCategory = it.getStringExtra("Category");
        gCategoryDet = it.getStringExtra("CategoryDetails");
        gUrl = it.getStringExtra("Url");

        pSettings = getSharedPreferences("GLOBAL", 0);
        gEmail = pSettings.getString("gEmail", "");
        gLatitude = pSettings.getString("Latitude", "");
        gLongitude = pSettings.getString("Longitude", "");

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView cName = findViewById(R.id.tCategory);
        TextView cNameAlt = findViewById(R.id.tCategoryAlt);
        TextView cDescription = findViewById(R.id.tCategoryDetails);
        ImageView cLegend = findViewById(R.id.legend);
        cName.setText(gCategory);
        cNameAlt.setText("All Offers on | " + gCategory);
        cDescription.setText(gCategoryDet);
        Glide.with(cContext)
                .load(gUrl)
                .into(cLegend);

        oSLay = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        oRecyclerView = findViewById(R.id.oList);
        nLayoutManager = new LinearLayoutManager(cContext);
        oRecyclerView.setLayoutManager(nLayoutManager);
        oRecyclerView.setNestedScrollingEnabled(false);
        oHolder = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(cContext);
        oAdapter = new UserAdapter(cContext, oHolder);
        oRecyclerView.setAdapter(oAdapter);
        oRecyclerView.hasFixedSize();

        if (!checkConnection(cContext)) {
            ExpandCollapseExtention.expand(findViewById(R.id.notify));
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    ExpandCollapseExtention.collapse(findViewById(R.id.notify));
                }
            }, 2500);
        }else{
            HttpsTrustManager.allowAllSSL();
            getData();
            findViewById(R.id.oRefresh).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getData();
                }
            });

            oSLay.setOnRefreshListener(
                    new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    requestCount = 1;
                                    oHolder.clear();
                                    oRecyclerView.invalidate();
                                    oRecyclerView.refreshDrawableState();
                                    getData();
                                }
                            }, 1000);

                        }
                    }
            );
        }

        NestedScrollView scroller = findViewById(R.id.myScroll);

        scroller.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = (View) scroller.getChildAt(scroller.getChildCount() - 1);

                int diff = (view.getBottom() - (scroller.getHeight() + scroller
                        .getScrollY()));

                if (diff == 0) {
                    getData();
                }
            }
        });

        LinearLayout cHome = findViewById(R.id.cHome);
        LinearLayout cCard = findViewById(R.id.cCard);

        scroller.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                Rect scrollBounds = new Rect();
                scroller.getHitRect(scrollBounds);

                if (cHome.getLocalVisibleRect(scrollBounds)) { // The View is Visible on the screen while scrolling
                    ExpandCollapseExtention.eCollapse(cCard);
                } else { // The View is not Visible on the screen while scrolling
                    ExpandCollapseExtention.eExpand(cCard);
                }

                if (scrollY > oldScrollY) {
                    Log.i(ApiConfig.AZYMA_APP, "Scroll DOWN");
                }if (scrollY < oldScrollY) {
                    Log.i(ApiConfig.AZYMA_APP, "Scroll UP");
                }if (scrollY == 0) {
                    Log.i(ApiConfig.AZYMA_APP, "TOP SCROLL");
                }if (scrollY == (v.getMeasuredHeight() - v.getChildAt(0).getMeasuredHeight())) {
                    Log.i(ApiConfig.AZYMA_APP, "BOTTOM SCROLL");
                }
            }
        });

        SearchView sSearch = findViewById(R.id.sSearch);
        sSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }


    private JsonArrayRequest getDataFromServer(int requestCount) {

        findViewById(R.id.oProgress).setVisibility(View.VISIBLE);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(ApiConfig.USERS_URL +
                String.valueOf(requestCount) +"&user=" + gEmail+"&uLatitude=" + gLatitude+"&uLongitude=" + gLongitude,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        parseData(response);
                        findViewById(R.id.oProgress).setVisibility(View.GONE);
                        oSLay.setRefreshing(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error.toString().equals("com.android.volley.TimeoutError")) {

                            findViewById(R.id.oProgress).setVisibility(View.GONE);
                            findViewById(R.id.fError).setVisibility(View.VISIBLE);
                            oSLay.setRefreshing(false);

                            Snackbar.make(findViewById(android.R.id.content),
                                    "Timeout Error!", Snackbar.LENGTH_LONG).show();

                            Log.e(ApiConfig.AZYMA_APP, error.toString());
                        } else if (error.toString().contains("com.android.volley.ParseError: org.json.JSONException: Value over of type java.lang.String cannot be converted to JSONArray")) {

                            findViewById(R.id.oProgress).setVisibility(View.GONE);
                            findViewById(R.id.fError).setVisibility(View.GONE);
                            oSLay.setRefreshing(false);

                            Log.e(ApiConfig.AZYMA_APP, error.toString());

                        }else if (error.toString().contains("com.android.volley.ParseError: org.json.JSONException: End of input at character 0 of")) {

                            findViewById(R.id.oProgress).setVisibility(View.GONE);
                            findViewById(R.id.fError).setVisibility(View.GONE);
                            oSLay.setRefreshing(false);

                            Log.e(ApiConfig.AZYMA_APP, error.toString());

                        } else {

                            findViewById(R.id.oProgress).setVisibility(View.GONE);
                            findViewById(R.id.fError).setVisibility(View.VISIBLE);
                            oSLay.setRefreshing(false);

                            Snackbar.make(findViewById(android.R.id.content),
                                    "So'thing Went Wrong!", Snackbar.LENGTH_LONG).show();

                            Log.e(ApiConfig.AZYMA_APP, error.toString());
                        }

                    }
                });

        return jsonArrayRequest;
    }

    private void getData() {
        requestQueue.add(getDataFromServer(requestCount));
        requestCount++;
    }

    private void parseData(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {

            UserHolder olHolder = new UserHolder();
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);

                olHolder.setId(json.getString(ApiConfig.US_ID));
                olHolder.setName(json.getString(ApiConfig.US_USER));
                olHolder.setEmail(json.getString(ApiConfig.US_EMAIL));
                olHolder.setPhone(json.getString(ApiConfig.US_PHONE));
                olHolder.setUrl(json.getString(ApiConfig.US_URL));
                olHolder.setSignal(json.getString(ApiConfig.US_SIG));
                olHolder.setVerified(json.getString(ApiConfig.US_VERIFIED));
                olHolder.setLatitude(json.getString(ApiConfig.US_LAT));
                olHolder.setLongitude(json.getString(ApiConfig.US_LONG));
                olHolder.setRating(json.getString(ApiConfig.US_RATE));

                Log.e(ApiConfig.AZYMA_APP, json.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            oHolder.add(olHolder);

        }

        oAdapter.notifyDataSetChanged();
    }

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (Objects.requireNonNull(recyclerView.getAdapter()).getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }

    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (isLastItemDisplaying(oRecyclerView)) {
            getData();
        }
    }

}