package co.ke.daletsys.azyma.ui;


import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import co.ke.daletsys.azyma.R;
import co.ke.daletsys.azyma.global.ApiConfig;
import co.ke.daletsys.azyma.global.ExpandCollapseExtention;
import co.ke.daletsys.azyma.global.HttpsTrustManager;
import co.ke.daletsys.azyma.ui.home.OfferAdapter;
import co.ke.daletsys.azyma.ui.home.OfferHolder;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap gMap;
    Context mContext;
    int increment = 4;
    CameraUpdate cameraUpdate;
    int LOCATION_PERMISSION_REQUEST_CODE = 104;
    SharedPreferences pSettings;
    SharedPreferences.Editor pEditor;
    String gLat, gLong, gUrlImage, gEmail, gName, gCategory;
    boolean gCache;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    AppCompatImageView mAlert, legend, gImage;
    View vAlert;
    TextView uLocation,oLocation;
    private RequestQueue requestQueue;
    private int requestCount = 1;
    private ArrayList<OfferHolder> oHolder;
    private RecyclerView.Adapter oAdapter;
    private RecyclerView.LayoutManager nLayoutManager;
    private RecyclerView oRecyclerView;
    CardView cAlert;
    boolean sClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_maps);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.gMap);
        mapFragment.getMapAsync(this);

        mContext = getApplicationContext();

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mAlert = findViewById(R.id.mAlert);
        legend = findViewById(R.id.legend);
        gImage = findViewById(R.id.gImage);
        vAlert = findViewById(R.id.vAlert);
        uLocation = findViewById(R.id.uLocation);
        oLocation = findViewById(R.id.oLocation);
        cAlert = findViewById(R.id.cAlert);

        oRecyclerView = findViewById(R.id.oList);
        nLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        oRecyclerView.setLayoutManager(nLayoutManager);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(oRecyclerView);
        oRecyclerView.setNestedScrollingEnabled(false);
        oHolder = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(mContext);
        oAdapter = new OfferAdapter(mContext, oHolder);
        oRecyclerView.setAdapter(oAdapter);
        oRecyclerView.hasFixedSize();

        checkPermissions();

        pSettings = mContext.getSharedPreferences("GLOBAL", 0);
        gLat = pSettings.getString("Longitude", "");
        gLong = pSettings.getString("Latitude", "");
        gEmail = pSettings.getString("gEmail", "");
        gName = pSettings.getString("gName", "");
        gUrlImage = pSettings.getString("gUrlImage", "");
        gCategory = "";
        gCache = pSettings.getBoolean("gCache", false);

        sMaps(mapFragment);
        aAnimator(mAlert, mContext);
        HttpsTrustManager.allowAllSSL();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 1500);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        if (gMap == null) {
            Snackbar.make(findViewById(android.R.id.content),
                    "Null Maps!", Snackbar.LENGTH_LONG).show();
        } else {
            gMap.getUiSettings().setMyLocationButtonEnabled(false);
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                gMap.setMyLocationEnabled(true);
                return;
            }

            MapsInitializer.initialize(mContext);
            checkPermissions();

            if (gLat.equals("") || gLong.equals("")) {
                Snackbar.make(findViewById(android.R.id.content),
                        "S'mthing Went Wrong!", Snackbar.LENGTH_LONG).show();
            } else {
                LatLng nLocation = new LatLng(Double.parseDouble(gLat), Double.parseDouble(gLong));
                cameraUpdate = CameraUpdateFactory.newLatLngZoom(nLocation, 15);
                gMap.animateCamera(cameraUpdate);
                gMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(gLat), Double.parseDouble(gLong))));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        //mView.onSaveInstanceState(mapViewBundle);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void aAnimator(View mView, Context mContext) {
        uLocation.setText("Fetching Nearby.");
        uLocation.setBackgroundResource(R.drawable.fetching);
        Glide.with(mContext)
                .load(gUrlImage)
                .apply(RequestOptions.circleCropTransform())
                .into(gImage);

        findViewById(R.id.gImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSettingsPop(gName);
            }
        });

        findViewById(R.id.oRefresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
            }
        });

        Glide.with(mContext)
                .load(R.drawable.ic_minimize)
                .error(R.drawable.ic_minimize)
                .into(legend);


        //...
        sClicked = true;
        findViewById(R.id.lAlert).setVisibility(View.GONE);
        findViewById(R.id.rAlert).setVisibility(View.GONE);
        findViewById(R.id.oList).setVisibility(View.GONE);
        int newHeight = 42;
        int newHeightPx = convertDpToPixel(newHeight);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cAlert.getLayoutParams();
        layoutParams.height = newHeightPx;
        cAlert.setLayoutParams(layoutParams);
        Glide.with(mContext)
                .load(R.drawable.ic_maximize)
                .error(R.drawable.ic_maximize)
                .into(legend);

        legend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sClicked) {
                    sClicked = false;

                    findViewById(R.id.lAlert).setVisibility(View.VISIBLE);
                    findViewById(R.id.rAlert).setVisibility(View.VISIBLE);
                    findViewById(R.id.oList).setVisibility(View.VISIBLE);
                    int newHeight = 262;
                    int newHeightPx = convertDpToPixel(newHeight);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cAlert.getLayoutParams();
                    layoutParams.height = newHeightPx;
                    cAlert.setLayoutParams(layoutParams);
                    Glide.with(mContext)
                            .load(R.drawable.ic_minimize)
                            .error(R.drawable.ic_minimize)
                            .into(legend);
                } else {
                    sClicked = true;

                    findViewById(R.id.lAlert).setVisibility(View.GONE);
                    findViewById(R.id.rAlert).setVisibility(View.GONE);
                    findViewById(R.id.oList).setVisibility(View.GONE);
                    int newHeight = 42;
                    int newHeightPx = convertDpToPixel(newHeight);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cAlert.getLayoutParams();
                    layoutParams.height = newHeightPx;
                    cAlert.setLayoutParams(layoutParams);
                    Glide.with(mContext)
                            .load(R.drawable.ic_maximize)
                            .error(R.drawable.ic_maximize)
                            .into(legend);

                }
            }
        });
        //...

        Glide.with(mContext)
                .load(R.drawable.amber)
                .error(R.drawable.amber)
                .into(mAlert);

        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                mView,
                PropertyValuesHolder.ofFloat("scaleX", 0.5f),
                PropertyValuesHolder.ofFloat("scaleY", 0.5f)
        );
        scaleDown.setDuration(2000);
        scaleDown.setRepeatMode(ValueAnimator.REVERSE);
        scaleDown.setRepeatCount(ValueAnimator.INFINITE);
        scaleDown.start();
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public int convertDpToPixel(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void updateLocationViews() {
        Glide.with(mContext)
                .load(R.drawable.green)
                .error(R.drawable.green)
                .into(mAlert);
        vAlert.setBackgroundColor(getResources().getColor(R.color.lime_l));
        uLocation.setText("Location Updated");
        uLocation.setTextColor(getResources().getColor(R.color.lime_l));
        uLocation.setBackgroundResource(R.drawable.successful);
    }

    private void startLocationUpdates() {
        View mCustomMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);

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

                    /**gMap.addMarker(new MarkerOptions().position(cLocation).title("Home")
                            .icon(BitmapFromVector(getApplicationContext(),R.drawable.green)));**/

                    addCustomMarkerFromDrawable(gMap,cLocation,mCustomMarkerView);

                    cameraUpdate = CameraUpdateFactory.newLatLngZoom(cLocation, 15);
                    gMap.animateCamera(cameraUpdate);

                    pEditor = pSettings.edit();
                    pEditor.putString("Latitude", "" + cLocation.latitude);
                    pEditor.putString("Longitude", "" + cLocation.longitude);
                    pEditor.commit();
                    pEditor.apply();

                    updateLocationViews();

                }
            }
        };

        if (gCache) {
            // Request location updates
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                LocationServices.getFusedLocationProviderClient(this)
                        .requestLocationUpdates(locationRequest, locationCallback, null);
            }
        } else {
            // Cancel Request location updates
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {

                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private JsonArrayRequest getDataFromServer(int requestCount) {
        findViewById(R.id.oProgress).setVisibility(View.VISIBLE);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(ApiConfig.CATEGORIES_OFFER_LOCAL +
                String.valueOf(requestCount) + "&category=" + gCategory + "&user=" + gEmail
                + "&uLatitude=" + gLat + "&uLongitude=" + gLong,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        parseData(response);
                        findViewById(R.id.oProgress).setVisibility(View.GONE);
                        findViewById(R.id.fError).setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error.toString().equals("com.android.volley.TimeoutError")) {
                            Snackbar.make(findViewById(android.R.id.content),
                                    "Timeout Error!", Snackbar.LENGTH_LONG).show();
                            findViewById(R.id.oProgress).setVisibility(View.GONE);
                            findViewById(R.id.fError).setVisibility(View.VISIBLE);
                            Log.e(ApiConfig.AZYMA_APP, error.toString());
                        } else if (error.toString().contains("com.android.volley.ParseError: org.json.JSONException: Value over of type java.lang.String cannot be converted to JSONArray")) {

                            findViewById(R.id.oProgress).setVisibility(View.GONE);
                            findViewById(R.id.fError).setVisibility(View.GONE);
                            Log.e(ApiConfig.AZYMA_APP, error.toString());

                        } else if (error.toString().contains("com.android.volley.ParseError: org.json.JSONException: End of input at character 0 of")) {

                            findViewById(R.id.oProgress).setVisibility(View.GONE);
                            findViewById(R.id.fError).setVisibility(View.GONE);
                            Log.e(ApiConfig.AZYMA_APP, error.toString());

                        } else {

                            findViewById(R.id.oProgress).setVisibility(View.GONE);
                            findViewById(R.id.fError).setVisibility(View.VISIBLE);
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

    private void parseData(JSONArray markers) {

        for (int i = 0; i < markers.length(); i++) {

            try {
                JSONObject markerData = markers.getJSONObject(i);
                double lt = Double.parseDouble(markerData.getString("uLatitude"));
                double ln = Double.parseDouble(markerData.getString("uLongitude"));
                LatLng position = new LatLng(lt, ln);
                String title = markerData.getString("oName");
                String user = markerData.getString("uName");
                String iconUrl = markerData.getString("oUrl");
                String description = markerData.getString("oDescription");
                gMap.addMarker(new MarkerOptions().position(position).title(title).contentDescription(iconUrl));
                //gMap.addMarker(createMarker(mContext,position,title));
                gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        showPopupWindow(marker, user, getAddress(lt, ln), iconUrl);

                        return true;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

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
        oLocation.setText(oAdapter.getItemCount()+" Found.");
        oLocation.setTextColor(getResources().getColor(R.color.lime_l));
        oLocation.setBackgroundResource(R.drawable.successful);
    }

    private void showPopupWindow(Marker marker, String user, String description, String iconUrl) {

        View popupView = getLayoutInflater().inflate(R.layout.popup, null);
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.showAtLocation(findViewById(R.id.gMap), Gravity.CENTER, 0, 0);

        TextView uName = popupView.findViewById(R.id.uName);
        TextView mTitle = popupView.findViewById(R.id.mTitle);
        TextView mDescription = popupView.findViewById(R.id.mDescription);
        AppCompatImageView mImage = popupView.findViewById(R.id.mImage);
        Button popupButton = popupView.findViewById(R.id.mButton);
        mTitle.setText(marker.getTitle());
        mDescription.setText(description);
        uName.setText(user);

        Glide.with(mContext).load(iconUrl)
                .apply(RequestOptions.circleCropTransform())
                .error(R.drawable.ic_azyma_light)
                .into(mImage);

        popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }


    private void showSettingsPop(String user) {

    }

    public String sCities(){
        String[] cities = {"Nairobi","Bujumbura","Cairo","Denver","Entebbe"};
        Random random = new Random();
        int index = random.nextInt(cities.length);
        String sCity = cities[index];

        return sCity;
    }

    void sMaps(SupportMapFragment mapFragment) {
        FloatingActionButton sFab = findViewById(R.id.sFab);
        SearchView searchView = findViewById(R.id.sMaps);
        hideKeyboardFrom(mContext, searchView);
        searchView.clearFocus();

        searchView.setQueryHint(sCities());
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;
                if (location != null || location.equals("")) {
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    gMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));


                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        sFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String location = searchView.getQuery().toString();
                if (location.equals("")) {
                    Snackbar.make(findViewById(android.R.id.content),
                            "Null Search Not Allowed!", Snackbar.LENGTH_LONG).show();
                } else {
                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            String location = searchView.getQuery().toString();
                            List<Address> addressList = null;
                            if (location != null || location.equals("")) {
                                Geocoder geocoder = new Geocoder(MapsActivity.this);
                                try {
                                    addressList = geocoder.getFromLocationName(location, 1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Address address = addressList.get(0);
                                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                                gMap.addMarker(new MarkerOptions().position(latLng).title(location));
                                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                            }
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            return false;
                        }
                    });
                }
            }
        });

        mapFragment.getMapAsync(this);
    }

    public String getAddress(double lat, double lng) {
        String add = "";
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            add = obj.getAddressLine(0);
            //add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            //add = add + "\n" + obj.getPostalCode();
            //add = add + "\n" + obj.getSubAdminArea();
            //add = add + "\n" + obj.getLocality();
            //add = add + "\n" + obj.getSubThoroughfare();

            Log.v("IGA", "Address" + add);

            return add;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return add;
    }

    public static MarkerOptions createMarker(Context context, LatLng point, String content) {
        MarkerOptions marker = new MarkerOptions();
        marker.position(point);
        int px = context.getResources().getDimensionPixelSize(R.dimen.txtBig);
        View markerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker, null);
        markerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        markerView.layout(0, 0, px, px);
        markerView.buildDrawingCache();
        TextView bedNumberTextView = (TextView) markerView.findViewById(R.id.mTitle);
        Bitmap mDotMarkerBitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mDotMarkerBitmap);
        bedNumberTextView.setText(content);
        markerView.draw(canvas);
        marker.icon(BitmapDescriptorFactory.fromBitmap(mDotMarkerBitmap));

        return marker;
    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(
                context, vectorResId);

        vectorDrawable.setBounds(
                0, 0, vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight());

        Bitmap bitmap = Bitmap.createBitmap(
                vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_4444);

        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    /**
     * @param view is custom marker layout which we will convert into bitmap.
     * @param resId is the drawable which you want to show in marker.
     * @return
     */
    private Bitmap getMarkerBitmapFromView(View view, @DrawableRes int resId) {
        View mCustomMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
        ImageView mMarkerImageView = mCustomMarkerView.findViewById(R.id.profile_image);
        mMarkerImageView.setImageResource(resId);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = view.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        view.draw(canvas);
        return returnedBitmap;
    }


    private void addCustomMarkerFromDrawable(GoogleMap mGoogleMap,LatLng mDummyLatLng, View mCustomMarkerView) {
        if (mGoogleMap == null) {
            return;
        }
        mGoogleMap.addMarker(new MarkerOptions()
                .position(mDummyLatLng)
                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(mCustomMarkerView,R.drawable.heart))));

    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

    }
}