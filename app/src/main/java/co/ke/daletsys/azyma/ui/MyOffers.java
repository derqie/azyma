package co.ke.daletsys.azyma.ui;

import static co.ke.daletsys.azyma.control.InternetConnection.checkConnection;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import co.ke.daletsys.azyma.ui.home.OfferAdapter;
import co.ke.daletsys.azyma.ui.home.OfferHolder;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MyOffers extends AppCompatActivity implements RecyclerView.OnScrollChangeListener {

    private SwipeRefreshLayout oSLay;
    Context cContext;
    private RequestQueue requestQueue;
    private int requestCount = 1;
    private RecyclerView.LayoutManager nLayoutManager;
    private RecyclerView oRecyclerView;
    private ArrayList<OfferHolder> oHolder;
    private RecyclerView.Adapter oAdapter;
    String gCategory, gCategoryDet, gUrl,gEmail,gLatitude,gLongitude;
    SharedPreferences pSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_details);

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
        oAdapter = new OfferAdapter(cContext, oHolder);
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

        NestedScrollView scroller = (NestedScrollView) findViewById(R.id.myScroll);

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
        CardView cHome = findViewById(R.id.cHome);
        boolean isViewVisible = cHome.isShown();
        if (scroller != null) {

            scroller.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    if (scrollY > oldScrollY) {
                        Log.i(ApiConfig.AZYMA_APP, "Scroll DOWN");
                    }

                    if (scrollY < oldScrollY) {
                        Log.i(ApiConfig.AZYMA_APP, "Scroll UP");
                    }

                    if (scrollY == 0) {
                        Log.i(ApiConfig.AZYMA_APP, "TOP SCROLL");
                    }

                    if (scrollY == (v.getMeasuredHeight() - v.getChildAt(0).getMeasuredHeight())) {
                        getData();
                        Log.i(ApiConfig.AZYMA_APP, "BOTTOM SCROLL");
                    }
                }
            });
        }
    }


    private JsonArrayRequest getDataFromServer(int requestCount) {

        findViewById(R.id.oProgress).setVisibility(View.VISIBLE);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(ApiConfig.MY_OFFERS_REQUESTED_URL +
                String.valueOf(requestCount) + "&category=" + gCategory+"&user=" + gEmail
                +"&uLatitude=" + gLatitude+"&uLongitude=" + gLongitude,

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
                                    "Som'thing Went Wrong!", Snackbar.LENGTH_LONG).show();

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

            OfferHolder olHolder = new OfferHolder();
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);

                olHolder.setId(json.getString(ApiConfig.OFF_ID));
                olHolder.setUrl(json.getString(ApiConfig.OFF_URL));
                olHolder.setName(json.getString(ApiConfig.OFF_NAME));
                olHolder.setDescription(json.getString(ApiConfig.OFF_DESCR));
                olHolder.setSerial(json.getString(ApiConfig.OFF_SERIAL));
                olHolder.setCount(json.getString(ApiConfig.OFF_COUNT));
                olHolder.setCurrency(json.getString(ApiConfig.OFF_CURRENCY));
                olHolder.setCategory(json.getString(ApiConfig.OFF_CATEGORY));
                olHolder.setReviews(json.getString(ApiConfig.OFF_REVIEWS));
                olHolder.setUploads(json.getString(ApiConfig.OFF_UPLOADS));
                olHolder.setLikes(json.getString(ApiConfig.OFF_LIKES));
                olHolder.setLiked(json.getString(ApiConfig.OFF_LIKED));
                olHolder.setShares(json.getString(ApiConfig.OFF_SHARES));
                olHolder.setPeeps(json.getString(ApiConfig.OFF_PEEPS));
                olHolder.setPeeped(json.getString(ApiConfig.OFF_PEEDED));
                olHolder.setRequests(json.getString(ApiConfig.OFF_REQUESTS));
                olHolder.setRequested(json.getString(ApiConfig.OFF_REQUESTED));
                olHolder.setCreated(json.getString(ApiConfig.CREATED));
                olHolder.setDistance(json.getString(ApiConfig.OFF_DISTANCE));
                olHolder.setUser(json.getString(ApiConfig.OFF_USER));
                olHolder.setPhone(json.getString(ApiConfig.OFF_PHONE));
                olHolder.setEmail(json.getString(ApiConfig.OFF_EMAIL));
                olHolder.setUrl(json.getString(ApiConfig.OFF_UX_URL));

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