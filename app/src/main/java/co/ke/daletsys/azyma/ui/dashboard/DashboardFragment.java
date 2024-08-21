package co.ke.daletsys.azyma.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import co.ke.daletsys.azyma.R;
import co.ke.daletsys.azyma.databinding.FragmentDashboardBinding;
import co.ke.daletsys.azyma.global.ApiConfig;
import co.ke.daletsys.azyma.ui.MapsActivity;
import co.ke.daletsys.azyma.ui.Users;
import co.ke.daletsys.azyma.ui.home.HomeHolder;
import co.ke.daletsys.azyma.ui.home.OfferAdapter;
import co.ke.daletsys.azyma.ui.home.OfferHolder;

public class DashboardFragment extends Fragment {

    Context context;
    View mRoot;
    private FragmentDashboardBinding binding;
    private RecyclerView cRecyclerView;
    private RecyclerView.Adapter hAdapter;
    private RecyclerView.LayoutManager dLayoutManager;
    private ArrayList<HomeHolder> dHolder;
    private Context mContext;
    SharedPreferences pSettings;
    SharedPreferences.Editor pEditor;
    String gName, gUrlImage, gEmail, gLat, gLong, gCategory;
    private RecyclerView oRecyclerView;
    private RecyclerView.Adapter oAdapter;
    private RecyclerView.LayoutManager oLayoutManager;
    private ArrayList<OfferHolder> oHolder;
    private RequestQueue rlQueue;
    private int rlCount = 1;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        mRoot = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mContext = mRoot.getContext();

        ImageView legend = mRoot.findViewById(R.id.legend);
        ImageView maps = mRoot.findViewById(R.id.maps);
        Glide.with(mRoot.getContext())
                .load(R.drawable.legend)
                .error(R.drawable.legend)
                .into(legend);
        Glide.with(mRoot.getContext())
                .load(R.drawable.dubai)
                .error(R.drawable.dubai)
                .into(maps);
        mRoot.findViewById(R.id.cMaps).setOnClickListener(view -> {
            Intent nIntent = new Intent(mContext, MapsActivity.class);
            Bundle extras = new Bundle();
            extras.putString("Category", "");
            nIntent.putExtras(extras);
            mContext.startActivity(nIntent);
        });
        mRoot.findViewById(R.id.go_maps).setOnClickListener(view -> {
            Intent nIntent = new Intent(mContext, MapsActivity.class);
            Bundle extras = new Bundle();
            extras.putString("Category", "");
            nIntent.putExtras(extras);
           mContext.startActivity(nIntent);
        });

        mRoot.findViewById(R.id.tvUsers).setOnClickListener(view -> {
            Intent nIntent = new Intent(mContext, Users.class);
            Bundle extras = new Bundle();
            extras.putString("Category", "Users");
            extras.putString("CategoryDetails", "Featured User Profiles");
            extras.putString("Url", ApiConfig.MISC);
            nIntent.putExtras(extras);
            mContext.startActivity(nIntent);
        });

        pSettings = mRoot.getContext().getSharedPreferences("GLOBAL", 0);
        gName = pSettings.getString("gName", "");
        gEmail = pSettings.getString("gEmail", "");
        gUrlImage = pSettings.getString("gUrlImage", "");
        gLat = pSettings.getString("Latitude", "");
        gLong = pSettings.getString("Longitude", "");
        gCategory = "";

        cRecyclerView = mRoot.findViewById(R.id.dList);
        dLayoutManager = new GridLayoutManager(mContext, 2);
        cRecyclerView.setLayoutManager(dLayoutManager);
        cRecyclerView.setNestedScrollingEnabled(false);
        dHolder = new ArrayList<HomeHolder>();
        dHolder.add(new HomeHolder("Trends", "We have more in the Pandora!", ApiConfig.TREND, R.drawable.ic_azyma_light));
        dHolder.add(new HomeHolder("Automobile", "Automobile | Cars, Motorbikes, Scooters, Lorries And Heavy Trucks...", ApiConfig.AUTO, R.drawable.ic_azyma_light));
        dHolder.add(new HomeHolder("Appliances", "Cookers, Fryers, Fridges, Heaters, Pans, Ovens...", ApiConfig.APPLIANCES, R.drawable.ic_azyma_light));
        dHolder.add(new HomeHolder("Food", "Grains, Flour, Desserts, Snacks, Beverages, Canned, Vegetable, Seeds...", ApiConfig.FOOD, R.drawable.ic_azyma_light));
        dHolder.add(new HomeHolder("Laundry", "Caps, Pullovers, Hoodies, Trousers, Pants, Cardigans, Shirts, Socks, Scarfs...", ApiConfig.LAUNDRY, R.drawable.ic_azyma_light));
        dHolder.add(new HomeHolder("Books", "Novels, Magazines, Text-books, Maps, Encyclopedias,", ApiConfig.BOOKS, R.drawable.ic_azyma_light));
        dHolder.add(new HomeHolder("Electronics", "Computers, Phones, Speakers, TVs, Cameras, Music Players... ", ApiConfig.ELECTRONICS, R.drawable.ic_azyma_light));
        dHolder.add(new HomeHolder("Furniture", "Sofa, Beds, Chairs, Drawers, Cabinets, Tables, Wall Units...", ApiConfig.FURNITURE, R.drawable.ic_azyma_light));
        dHolder.add(new HomeHolder("Skills", "Lawyers, Mechanics, Doctors, Engineers, Actors, Drivers, Techies, Teachers...", ApiConfig.SKILLS, R.drawable.ic_azyma_light));
        dHolder.add(new HomeHolder("Misc", "Anything else, Let your Mind get Creative!", ApiConfig.MISC, R.drawable.ic_azyma_light));
        hAdapter = new DashAdapter(mContext, dHolder);
        cRecyclerView.setAdapter(hAdapter);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                initLocal(mRoot);
                getLocal();
            }
        }, 1500);
        return mRoot;
    }


    void initLocal(View root) {
        oRecyclerView = root.findViewById(R.id.oList);
        oRecyclerView.setHasFixedSize(true);
        oLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        oRecyclerView.setLayoutManager(oLayoutManager);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(oRecyclerView);
        oRecyclerView.setNestedScrollingEnabled(true);
        oHolder = new ArrayList<>();
        rlQueue = Volley.newRequestQueue(mContext);
        oAdapter = new OfferAdapter(mContext, oHolder);
        oRecyclerView.setAdapter(oAdapter);
        oRecyclerView.hasFixedSize();
    }


    private JsonArrayRequest getLocalFromServer(int requestCount) {
        final ProgressBar progressBar = (ProgressBar) mRoot.findViewById(R.id.cProgress);
        progressBar.setVisibility(View.VISIBLE);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(ApiConfig.CATEGORIES_OFFER_LOCAL +
                String.valueOf(requestCount) + "&category=" + gCategory + "&user=" + gEmail
                + "&uLatitude=" + gLat + "&uLongitude=" + gLong,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        parseLocal(response);
                        progressBar.setVisibility(View.GONE);
                        mRoot.findViewById(R.id.fError).setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error.toString().equals("com.android.volley.TimeoutError")) {
                            progressBar.setVisibility(View.GONE);
                            mRoot.findViewById(R.id.fError).setVisibility(View.VISIBLE);
                            Log.e(ApiConfig.AZYMA_APP, error.toString());
                        } else if (error.toString().contains("com.android.volley.ParseError: org.json.JSONException: Value over of type java.lang.String cannot be converted to JSONArray")) {

                            progressBar.setVisibility(View.GONE);
                            mRoot.findViewById(R.id.fError).setVisibility(View.GONE);
                            Log.e(ApiConfig.AZYMA_APP, error.toString());

                        } else if (error.toString().contains("com.android.volley.ParseError: org.json.JSONException: End of input at character 0 of")) {

                            progressBar.setVisibility(View.GONE);
                            mRoot.findViewById(R.id.fError).setVisibility(View.GONE);
                            Log.e(ApiConfig.AZYMA_APP, error.toString());

                        } else {

                            progressBar.setVisibility(View.GONE);
                            mRoot.findViewById(R.id.fError).setVisibility(View.VISIBLE);
                            Log.e(ApiConfig.AZYMA_APP, error.toString());
                        }

                    }
                });

        return jsonArrayRequest;
    }

    private void getLocal() {
        rlQueue.add(getLocalFromServer(rlCount));
        rlCount++;
    }

    private void parseLocal(JSONArray markers) {

        TextView tCount = mRoot.findViewById(R.id.tCount);
        for (int i = 0; i < markers.length(); i++) {

            OfferHolder olHolder = new OfferHolder();
            JSONObject json = null;
            try {
                json = markers.getJSONObject(i);

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
        tCount.setText(oAdapter.getItemCount() + "");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}