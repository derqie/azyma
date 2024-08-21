package co.ke.daletsys.azyma.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.Objects;

import co.ke.daletsys.azyma.R;
import co.ke.daletsys.azyma.global.HttpsTrustManager;
import co.ke.daletsys.azyma.ui.Setup;
import co.ke.daletsys.azyma.ui.home.OfferAdapter;
import co.ke.daletsys.azyma.ui.home.OfferHolder;
import co.ke.daletsys.azyma.global.ApiConfig;

@RequiresApi(api = Build.VERSION_CODES.M)
public class Profile extends Fragment implements RecyclerView.OnScrollChangeListener{

    View mRoot;
    ImageView legend, gImage,sRank;
    AppCompatImageView sSet, sUpdated,nUpdated;

    int google_login = 100;
    private ProfileViewModel mViewModel;
    private TextView gUser;

    private String gName, gEmail, gId, gUrlImage, gPhone;
    boolean gActive,gUpdated;
    SharedPreferences pSettings;
    SharedPreferences.Editor pEditor;

    Context cContext;
    private RequestQueue requestQueue;
    private int requestCount = 1;
    private RecyclerView.LayoutManager nLayoutManager;
    private ArrayList<OfferHolder> oHolder;
    private RecyclerView.Adapter oAdapter;
    private RecyclerView oRecyclerView;

    public static Profile newInstance() {
        return new Profile();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_profile, container, false);
        cContext = mRoot.getContext();

        gUser = mRoot.findViewById(R.id.gUser);
        gImage = mRoot.findViewById(R.id.gImage);
        sRank = mRoot.findViewById(R.id.sRank);

        pSettings = mRoot.getContext().getSharedPreferences("GLOBAL", 0);
        gName = pSettings.getString("gName", "");
        gEmail = pSettings.getString("gEmail", "");
        gPhone = pSettings.getString("gPhone", "");
        gUrlImage = pSettings.getString("gUrlImage", "");
        gActive = pSettings.getBoolean("gActive", true);
        gUpdated = pSettings.getBoolean("gUpdated", false);
        gId = pSettings.getString("gId", "");

        gUser.setText(gName);
        legend = (ImageView) mRoot.findViewById(R.id.legend);
        sSet = mRoot.findViewById(R.id.sSet);
        sUpdated = mRoot.findViewById(R.id.sUpdated);
        nUpdated = mRoot.findViewById(R.id.nUpdated);

        Glide.with(mRoot.getContext())
                .load(R.drawable.legend)
                .into(legend);

        Glide.with(mRoot.getContext())
                .load(gUrlImage)
                .apply(RequestOptions.circleCropTransform())
                .into(gImage);

        Glide.with(mRoot.getContext())
                .load(R.drawable.silver)
                .apply(RequestOptions.circleCropTransform())
                .into(sRank);

        mRoot.findViewById(R.id.sSet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mRoot.getContext(), Setup.class);
                startActivity(i);
            }
        });

        oRecyclerView = mRoot.findViewById(R.id.oList);
        nLayoutManager = new LinearLayoutManager(cContext);
        oRecyclerView.setLayoutManager(nLayoutManager);
        oRecyclerView.setNestedScrollingEnabled(false);
        oHolder = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(cContext);
        oAdapter = new OfferAdapter(cContext, oHolder);
        oRecyclerView.setAdapter(oAdapter);
        oRecyclerView.hasFixedSize();
        HttpsTrustManager.allowAllSSL();
        getData();

        mRoot.findViewById(R.id.oRefresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
            }
        });

        NestedScrollView scroller = mRoot.findViewById(R.id.myScroll);
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
                        //getData();
                        Log.i(ApiConfig.AZYMA_APP, "BOTTOM SCROLL");
                    }
                }
            });
        }

        if (gUpdated) {
            sUpdated.setVisibility(View.VISIBLE);
            nUpdated.setVisibility(View.GONE);
            Glide.with(mRoot.getContext())
                    .load(R.drawable.secondary)
                    .error(R.drawable.secondary)
                    .into(sUpdated);
        } else {
            sUpdated.setVisibility(View.GONE);
            nUpdated.setVisibility(View.VISIBLE);
            Glide.with(mRoot.getContext())
                    .load(R.drawable.pending)
                    .error(R.drawable.pending)
                    .into(sUpdated);
        }

        // [ONE]
        OneSignal.getUser().addEmail(gEmail);

        return mRoot;
    }


    private JsonArrayRequest getDataFromServer(int requestCount) {

        mRoot.findViewById(R.id.oProgress).setVisibility(View.VISIBLE);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(ApiConfig.MY_OFFER_URL + String.valueOf(requestCount) + "&uEmail=" + gEmail,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        parseData(response);
                        mRoot.findViewById(R.id.oProgress).setVisibility(View.GONE);

                        if(response.equals("over")){
                            mRoot. findViewById(R.id.fError).setVisibility(View.GONE);
                        }

                     }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                        if(error.toString().equals("com.android.volley.TimeoutError")){

                            mRoot.findViewById(R.id.oProgress).setVisibility(View.GONE);
                            mRoot. findViewById(R.id.fError).setVisibility(View.VISIBLE);

                            Log.e(ApiConfig.AZYMA_APP,error.toString());
                        }else if(error.toString().contains("com.android.volley.ParseError: org.json.JSONException: Value over of type java.lang.String cannot be converted to JSONArray")) {

                            mRoot.findViewById(R.id.oProgress).setVisibility(View.GONE);
                            mRoot. findViewById(R.id.fError).setVisibility(View.GONE);
                            Log.e(ApiConfig.AZYMA_APP,error.toString());
                        }else {

                            mRoot.findViewById(R.id.oProgress).setVisibility(View.GONE);
                            mRoot.findViewById(R.id.fError).setVisibility(View.VISIBLE);
                            Toast.makeText(cContext, "Som'thing went Wrong!", Toast.LENGTH_SHORT).show();
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

                olHolder.setUrl(json.getString(ApiConfig.OFF_URL));
                olHolder.setName(json.getString(ApiConfig.OFF_NAME));
                olHolder.setDescription(json.getString(ApiConfig.OFF_DESCR));
                olHolder.setSerial(json.getString(ApiConfig.OFF_SERIAL));
                olHolder.setCurrency(json.getString(ApiConfig.OFF_CURRENCY));
                olHolder.setCount(json.getString(ApiConfig.OFF_COUNT));
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        // TODO: Use the ViewModel
    }


    @Override
    public void onResume() {
        super.onResume();

        if(gUpdated){
            Glide.with(mRoot.getContext())
                    .load(R.drawable.secondary)
                    .apply(RequestOptions.circleCropTransform())
                    .error(R.drawable.secondary)
                    .into(sUpdated);
        }else {
            Glide.with(mRoot.getContext())
                    .load(R.drawable.pending)
                    .apply(RequestOptions.circleCropTransform())
                    .error(R.drawable.pending)
                    .into(sUpdated);
        }
    }
}