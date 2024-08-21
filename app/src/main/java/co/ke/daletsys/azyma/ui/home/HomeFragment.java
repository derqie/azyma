package co.ke.daletsys.azyma.ui.home;

import static co.ke.daletsys.azyma.control.InternetConnection.checkConnection;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import co.ke.daletsys.azyma.R;
import co.ke.daletsys.azyma.global.ApiConfig;
import co.ke.daletsys.azyma.global.ExpandCollapseExtention;
import co.ke.daletsys.azyma.global.HttpsTrustManager;
import co.ke.daletsys.azyma.ui.MapsActivity;
import co.ke.daletsys.azyma.ui.Setup;

@RequiresApi(api = Build.VERSION_CODES.M)
public class HomeFragment extends Fragment implements RecyclerView.OnScrollChangeListener {

    View mRoot;
    Context mContext;
    TextView gUser;
    AppCompatImageView sSet, sUpdated,nUpdated;
    String gName, gUrlImage, gEmail, gLat, gLong, gCategory;
    boolean gUpdated;
    private RecyclerView hRecyclerView, cRecyclerView;
    private RecyclerView.Adapter myAdapter, cAdapter;
    private RecyclerView.LayoutManager gLayoutManager, nLayoutManager;
    private ArrayList<HomeHolder> hHolder;
    private ArrayList<CategoryHolder> clHolder;
    private RequestQueue rcQueue;
    private int rcCount = 1;

    SharedPreferences pSettings;
    SharedPreferences.Editor pEditor;
    int LOCATION_PERMISSION_REQUEST_CODE = 104;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mRoot = inflater.inflate(R.layout.fragment_home, container, false);
        mContext = mRoot.getContext();

        pSettings = mRoot.getContext().getSharedPreferences("GLOBAL", 0);
        gName = pSettings.getString("gName", "");
        gEmail = pSettings.getString("gEmail", "");
        gUrlImage = pSettings.getString("gUrlImage", "");
        gLat = pSettings.getString("Latitude", "");
        gLong = pSettings.getString("Longitude", "");
        gUpdated = pSettings.getBoolean("gUpdated", false);
        gCategory = "";

        hRecyclerView = mRoot.findViewById(R.id.hList);
        gUser = mRoot.findViewById(R.id.gUser);
        hRecyclerView.setHasFixedSize(true);
        gLayoutManager = new GridLayoutManager(mRoot.getContext(), 2);
        hRecyclerView.setLayoutManager(gLayoutManager);
        hRecyclerView.setNestedScrollingEnabled(true);
        hHolder = new ArrayList<HomeHolder>();
        hHolder.add(new HomeHolder("Explore Azyma Now", "We have more in the Pandora!", "Explore Azyma Now", R.drawable.about));
        hHolder.add(new HomeHolder("All Users", "Look at profiles Nearby.\nChat and Share Ideas.", "Get Social", R.drawable.engage));
        hHolder.add(new HomeHolder("Report Something!", "Have your voice Heard.", "Report", R.drawable.speak));
        hHolder.add(new HomeHolder("Coming Soon", "The Horizon has more for Azymians.", "Coming Soon", R.drawable.self));
        myAdapter = new HomeAdapter(mRoot.getContext(), hHolder);
        hRecyclerView.setAdapter(myAdapter);

        AppCompatImageView gLegend = mRoot.findViewById(R.id.gLegend);
        AppCompatImageView gContext = mRoot.findViewById(R.id.gContext);
        AppCompatImageView gImage = mRoot.findViewById(R.id.gImage);
        sSet = mRoot.findViewById(R.id.sSet);
        sUpdated = mRoot.findViewById(R.id.sUpdated);
        nUpdated = mRoot.findViewById(R.id.nUpdated);

        Glide.with(mRoot.getContext())
                .load(R.drawable.legend)
                .into(gLegend);

        TextView tContext = mRoot.findViewById(R.id.tContext);
        Calendar now = Calendar.getInstance();
        int a = now.get(Calendar.AM_PM);

        if (a == Calendar.AM) {
            Glide.with(mRoot.getContext())
                    .load(R.drawable.sun)
                    .into(gContext);
            tContext.setText("Good Day,");
        } else if (a == Calendar.PM) {
            Glide.with(mRoot.getContext())
                    .load(R.drawable.moon)
                    .into(gContext);
            tContext.setText("Good Evening,");
        } else {
            Glide.with(mRoot.getContext())
                    .load(R.drawable.moon)
                    .into(gContext);
            tContext.setText("Good Evening,");
        }

        Glide.with(mRoot.getContext())
                .load(gUrlImage)
                .apply(RequestOptions.circleCropTransform())
                .into(gImage);
        gUser.setText(gName);

        SearchView sHome = mRoot.findViewById(R.id.sHome);
        sHome.setQueryHint("Search Things...");
        sHome.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        cRecyclerView = mRoot.findViewById(R.id.cList);
        nLayoutManager = new LinearLayoutManager(mRoot.getContext());
        cRecyclerView.setHasFixedSize(true);
        cRecyclerView.setLayoutManager(nLayoutManager);
        cRecyclerView.setNestedScrollingEnabled(true);
        clHolder = new ArrayList<>();
        rcQueue = Volley.newRequestQueue(mRoot.getContext());
        cAdapter = new CategoryAdapter(mRoot.getContext(), clHolder);
        cRecyclerView.setAdapter(cAdapter);

        if (!checkConnection(mContext)) {
            ExpandCollapseExtention.expand(mRoot.findViewById(R.id.notify));
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    ExpandCollapseExtention.collapse(mRoot.findViewById(R.id.notify));
                }
            }, 2500);
        } else {
            HttpsTrustManager.allowAllSSL();
            getData();
            checkPermissions();
            mRoot.findViewById(R.id.oRefresh).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getData();
                }
            });


        }

        mRoot.findViewById(R.id.sSet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mRoot.getContext(), Setup.class);
                startActivity(i);
            }
        });

        mRoot.findViewById(R.id.cNear).setOnClickListener(view -> {
            Intent nIntent = new Intent(mContext, MapsActivity.class);
            Bundle extras = new Bundle();
            extras.putString("Category", "");
            nIntent.putExtras(extras);
            mRoot.getContext().startActivity(nIntent);
        });
        return mRoot;
    }

    public void nConnection(String message) {
        final int initialColor = getResources().getColor(R.color.retro);
        RelativeLayout rInfo = mRoot.findViewById(R.id.rInfo);
        TextView tInfo = mRoot.findViewById(R.id.tInfo);
        AppCompatImageView oClose = mRoot.findViewById(R.id.oClose);
        tInfo.setText(message);
        rInfo.setBackgroundColor(initialColor);
        oClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExpandCollapseExtention.collapse(mRoot.findViewById(R.id.notify));
            }
        });
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    LatLng cLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    pEditor = pSettings.edit();
                    pEditor.putString("Latitude", "" + cLocation.latitude);
                    pEditor.putString("Longitude", "" + cLocation.longitude);
                    pEditor.commit();
                    pEditor.apply();

                    updateLocationViews();

                }
            }
        };

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.getFusedLocationProviderClient(mContext)
                    .requestLocationUpdates(locationRequest, locationCallback, null);
        }

    }

    void updateLocationViews() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {

                Toast.makeText(mContext, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private JsonArrayRequest getDataFromServer(int requestCount) {
        final ProgressBar progressBar = (ProgressBar) mRoot.findViewById(R.id.cProgress);
        progressBar.setVisibility(View.VISIBLE);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(ApiConfig.CATEGORIES_URL + String.valueOf(requestCount),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        parseData(response);
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

    private void getData() {
        rcQueue.add(getDataFromServer(rcCount));
        rcCount++;
    }

    private void parseData(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {

            CategoryHolder cHolder = new CategoryHolder();
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);

                cHolder.setUrl(json.getString(ApiConfig.TAG_IMAGE_URL));
                cHolder.setName(json.getString(ApiConfig.TAG_NAME));
                cHolder.setDescription(json.getString(ApiConfig.TAG_DESCRIPTION));
                cHolder.setCount(json.getString(ApiConfig.TAG_COUNT));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            clHolder.add(cHolder);
        }

        cAdapter.notifyDataSetChanged();
    }


    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }

    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (isLastItemDisplaying(cRecyclerView)) {
            getData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rcCount = 1;

    }

    @Override
    public void onStart() {
        super.onStart();
        rcCount = 1;
    }
}