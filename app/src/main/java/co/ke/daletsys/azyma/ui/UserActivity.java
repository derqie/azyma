package co.ke.daletsys.azyma.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.Query;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import co.ke.daletsys.azyma.R;
import co.ke.daletsys.azyma.easychat.adapter.SearchUserRecyclerAdapter;
import co.ke.daletsys.azyma.easychat.model.UserModel;
import co.ke.daletsys.azyma.easychat.utils.FirebaseUtil;
import co.ke.daletsys.azyma.easychat.utils.WrapContentLinearLayoutManager;
import co.ke.daletsys.azyma.global.ApiConfig;

public class UserActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Context mContext;
    SharedPreferences pSettings;
    SharedPreferences.Editor pEditor;
    String gName, gEmail;

    MapView mView;
    GoogleMap gMap;
    CameraUpdate cameraUpdate;
    String gLat,gLong,uName;


    RecyclerView recyclerView;
    SearchUserRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mContext = this.getApplicationContext();
        pSettings = this.getSharedPreferences(ApiConfig.GLOBAL, 0);
        gName = pSettings.getString("gName", "");
        gEmail = pSettings.getString("gEmail", "");

        Intent it = this.getIntent();
        uName = it.getStringExtra("Name");
        String uEmail = it.getStringExtra("Email");
        String uUrl = it.getStringExtra("Url");
        String uPhone = it.getStringExtra("Phone");
        String uSignal = it.getStringExtra("Signal");
        String uVerified = it.getStringExtra("Verified");
        String uRating = it.getStringExtra("Rating");
        String uLat = it.getStringExtra("Latitude");
        String uLong = it.getStringExtra("Longitude");
        gLat = uLat;
        gLong = uLong;
        AppCompatImageView legend = findViewById(R.id.legend);
        AppCompatImageView gImage = findViewById(R.id.gImage);
        TextView tName = findViewById(R.id.tName);
        TextView tEmail = findViewById(R.id.tEmail);
        TextView tDistance = findViewById(R.id.tDistance);
        Glide.with(mContext)
                .load(uUrl)
                .into(legend);
        Glide.with(mContext)
                .load(uUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(gImage);

        tName.setText(uName);
        tEmail.setText(uEmail);
        tDistance.setText(uSignal);

        mView = findViewById(R.id.gMap);
        mView.onCreate(savedInstanceState);
        mView.getMapAsync(this);


        recyclerView = findViewById(R.id.search_user_recycler_view);
        setupSearchRecyclerView(uName);

        findViewById(R.id.review).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showReviewDialog(uName, uEmail, gEmail, gName);
            }
        });

    }


    void setupSearchRecyclerView(String searchTerm) {

        Query query = FirebaseUtil.allUserCollectionReference()
                .whereGreaterThanOrEqualTo("username", searchTerm)
                .whereLessThanOrEqualTo("username", searchTerm + '\uf8ff');

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class).build();

        RecyclerView.LayoutManager sLayoutManager;
        sLayoutManager = new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        adapter = new SearchUserRecyclerAdapter(options, getApplicationContext());
        recyclerView.setLayoutManager(sLayoutManager);
        recyclerView.getRecycledViewPool().clear();
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();

    }

    void showReviewDialog(String rvName, String rvEmail, String uEmail, String uName) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(UserActivity.this);
        View bsView = LayoutInflater.from(UserActivity.this).inflate(R.layout.post_review, null);

        AppCompatButton bSync = bsView.findViewById(R.id.bSync);
        AppCompatButton bDismiss = bsView.findViewById(R.id.bDismiss);
        TextView oTitle = bsView.findViewById(R.id.oTitle);
        TextView oReview = bsView.findViewById(R.id.oReview);
        TextInputEditText gComment = bsView.findViewById(R.id.gComment);
        RadioGroup mRadioGr = bsView.findViewById(R.id.cReviews);

        bottomSheetDialog.setContentView(bsView);
        bottomSheetDialog.show();

        oTitle.setText(rvName);
        oReview.setText("5.0");

        mRadioGr.setOnCheckedChangeListener((radioGroup, i) -> {
            radioGroup = mRadioGr;
            int sReviews = radioGroup.getCheckedRadioButtonId();
            RadioButton mRadioBt = bsView.findViewById(sReviews);
            String cReviews = mRadioBt.getText().toString();
            oReview.setText(cReviews);

        });
        //A Japanese samurai Walking in a Pavement lit with realistic fire while it drizzles in the night with mild thunder and lighting
        bSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cReviews = oReview.getText().toString();
                String cComment = gComment.getText().toString();

                reviewed(gComment, bSync, rvEmail, cComment, cReviews, uEmail, uName);
            }
        });
        bDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });
    }

    private void reviewed(TextInputEditText gComment, AppCompatButton bSync, String rvEmail, String rvComment,
                          String rvDouble, String uEmail, String uName) {

        bSync.setEnabled(false);
        String url = ApiConfig.SYNC_REVIEWS;
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(mContext, jsonObject.getString("message").toString(), Toast.LENGTH_LONG).show();
                    if (jsonObject.getString("message").equals("Posted!")) {

                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                gComment.clearFocus();
                                gComment.setText("");
                                bSync.setEnabled(true);

                            }
                        }, 1500);

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, error -> {

        }) {
            @Override
            public String getBodyContentType() {

                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("rvEmail", rvEmail);
                params.put("rvComment", rvComment);
                params.put("rvDouble", rvDouble);
                params.put("uEmail", uEmail);
                params.put("uName", uName);
                return params;
            }
        };

        queue.add(request);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        gMap.getUiSettings().setMyLocationButtonEnabled(false);
        gMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            gMap.setMyLocationEnabled(true);
            return;
        }

        if (gLat.equals("") || gLong.equals("")) {
            Snackbar.make(findViewById(android.R.id.content),
                    "User Location Not Synced!", Snackbar.LENGTH_LONG).show();
        } else {
            LatLng nLocation = new LatLng(Double.parseDouble(gLat), Double.parseDouble(gLong));
            gMap.addMarker(new MarkerOptions().position(nLocation).title(uName).contentDescription(uName));
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(nLocation, 5);
            gMap.animateCamera(cameraUpdate);

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null)
            adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.stopListening();
        recyclerView.invalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.startListening();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null)
            adapter.stopListening();
        recyclerView.invalidate();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);

    }
}