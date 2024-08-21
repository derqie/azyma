package co.ke.daletsys.azyma.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import co.ke.daletsys.azyma.R;
import co.ke.daletsys.azyma.control.Controller;
import co.ke.daletsys.azyma.global.ApiConfig;
import co.ke.daletsys.azyma.global.ExpandCollapseExtention;
import co.ke.daletsys.azyma.global.LinePagerIndicatorDecoration;
import co.ke.daletsys.azyma.ui.home.ImageAdapter;
import co.ke.daletsys.azyma.ui.home.ImageHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Images extends AppCompatActivity {

    RecyclerView mRecyclerView;
    LinearLayout lDetails;
    TextView tSize,tCount;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mManager;
    ProgressBar cProgress;
    ArrayList<ImageHolder> mItems;
    String oSerial;
    SharedPreferences pSettings;
    SharedPreferences.Editor pEditor;
    boolean gCache;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Intent it = this.getIntent();
        oSerial = it.getStringExtra("Serial");

        pSettings = getSharedPreferences("GLOBAL", 0);
        gCache = pSettings.getBoolean("gCache", false);

        cProgress = findViewById(R.id.cProgress);
        tSize = findViewById(R.id.tSize);
        tCount = findViewById(R.id.tCount);
        lDetails = findViewById(R.id.lDetails);
        mRecyclerView = (RecyclerView) findViewById(R.id.mList);
        mItems = new ArrayList<>();
        mManager = new LinearLayoutManager(Images.this, LinearLayoutManager.HORIZONTAL, false);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.addItemDecoration(new LinePagerIndicatorDecoration());
        mRecyclerView.setLayoutManager(mManager);
        mAdapter = new ImageAdapter(Images.this, mItems);
        mRecyclerView.setAdapter(mAdapter);
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        loadJson();
        tCount.setText("1");

        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                ExpandCollapseExtention.expand(lDetails);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ExpandCollapseExtention.collapse(lDetails);
                    }
                }, 5000);

                int X = (int) motionEvent.getX();
                int Y = (int) motionEvent.getY();
                int eAction = motionEvent.getAction();
                switch (eAction) {
                    case MotionEvent.ACTION_DOWN:


                        break;

                    case MotionEvent.ACTION_MOVE:
                        break;

                    case MotionEvent.ACTION_UP:

                        break;
                }
                return false;
            }
        });


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    int position = getCurrentItem()+1;
                    tCount.setText(position+"");
                }
            }
        });

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                ExpandCollapseExtention.collapse(lDetails);
            }
        }, 1500);
    }

    public boolean hasPreview() {
        return getCurrentItem() > 0;
    }

    public boolean hasNext() {
        return mRecyclerView.getAdapter() != null &&
                getCurrentItem() < (mRecyclerView.getAdapter().getItemCount()- 1);
    }

    public void preview() {
        int position = getCurrentItem();
        if (position > 0)
            setCurrentItem(position -1, true);
    }

    public void next() {
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter == null)
            return;

        int position = getCurrentItem();
        int count = adapter.getItemCount();
        if (position < (count -1))
            setCurrentItem(position + 1, true);
    }

    private int getCurrentItem(){
        return ((LinearLayoutManager)mRecyclerView.getLayoutManager())
                .findFirstVisibleItemPosition();
    }

    private void setCurrentItem(int position, boolean smooth){
        if (smooth)
            mRecyclerView.smoothScrollToPosition(position);
        else
            mRecyclerView.scrollToPosition(position);
    }

    private void loadJson() {
        cProgress.setVisibility(View.VISIBLE);

        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, ApiConfig.SYNC_IMAGES + "?serial=" + oSerial, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                cProgress.setVisibility(View.GONE);
                Log.d("volley", "response : " + response.toString());
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject data = response.getJSONObject(i);
                        ImageHolder md = new ImageHolder();
                        md.setSerial(data.getString("iSerial"));
                        md.setIdentify(data.getString("iId"));
                        md.setUrl(data.getString("iName"));
                        mItems.add(md);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mAdapter.notifyDataSetChanged();
                tSize.setText(mAdapter.getItemCount()+".");
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                cProgress.setVisibility(View.GONE);

                Snackbar.make(findViewById(android.R.id.content),
                        "S'mthing Went Wrong!", Snackbar.LENGTH_LONG).show();

                Log.d(ApiConfig.AZYMA_APP, "error : " + error.getMessage());
            }
        });
        Controller.getInstance().addToRequestQueue(arrayRequest);
    }
}