package co.ke.daletsys.azyma.ui.home;

import static co.ke.daletsys.azyma.control.InternetConnection.checkConnection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import co.ke.daletsys.azyma.R;
import co.ke.daletsys.azyma.control.ApiService;
import co.ke.daletsys.azyma.control.FileUtils;
import co.ke.daletsys.azyma.control.MyAdapter;
import co.ke.daletsys.azyma.control.NetworkHandler;
import co.ke.daletsys.azyma.global.ApiConfig;
import co.ke.daletsys.azyma.global.ExpandCollapseExtention;
import co.ke.daletsys.azyma.global.HttpsTrustManager;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.model.CalendarEvent;
import devs.mulham.horizontalcalendar.utils.CalendarEventsPredicate;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Post extends AppCompatActivity {

    private String oSerial,cCategory, cCurrency, oUrl, oReviews, oCount, oDated, oStatus, oClaim, gName, gEmail, gId, gUrlImage, gPhone ,gLatitude, gLongitude;
    private static final String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";
    boolean gActive,gLocation;
    Context mContext;
    SharedPreferences pSettings;
    SharedPreferences.Editor pEditor;
    TextInputEditText oName, oDescription, pCost;
    ImageView gImage;
    MaterialButton mAdd,bPost;
    RadioGroup mRadioGr, mRadioCu;
    RadioButton mRadioBt, mRadioBtCur;
    SwitchCompat swVerify;
    private GridView mGrid;
    private ArrayList<Uri> mArray;
    private final int REQUEST_CODE_PERMISSIONS = 1;
    private final int REQUEST_CODE_READ_STORAGE = 2;
    int LOCATION_PERMISSION_REQUEST_CODE = 104;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        mContext = this.getApplicationContext();
        mRadioGr = findViewById(R.id.cCategories);
        mRadioCu = findViewById(R.id.cCurrency);
        int sCategory = mRadioGr.getCheckedRadioButtonId();
        int sCurrency = mRadioCu.getCheckedRadioButtonId();
        mRadioBt = findViewById(sCategory);
        mRadioBtCur = findViewById(sCurrency);
        cCategory = mRadioBt.getText().toString();
        cCurrency = mRadioBtCur.getText().toString();

        mGrid = findViewById(R.id.mGrid);
        mAdd = findViewById(R.id.mAdd);
        AppCompatImageView ivUpload = findViewById(R.id.ivUpload);
        AppCompatImageView ivCalendar = findViewById(R.id.ivCalendar);
        mArray = new ArrayList<>();
        ViewCompat.setNestedScrollingEnabled(mGrid, true);
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //checkPermission(Manifest.permission.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION, STORAGE_PERMISSION_CODE);
                    askForPermission();
                } else {
                    showChooser();
                }

            }
        });
        Glide.with(Post.this)
                .load(R.drawable.upload)
                .error(R.drawable.upload)
                .into(ivUpload);
        Glide.with(Post.this)
                .load(R.drawable.calendar)
                .error(R.drawable.calendar)
                .into(ivCalendar);

        swVerify = findViewById(R.id.swVerify);
        gImage = (ImageView) findViewById(R.id.gImage);

        pSettings = this.getSharedPreferences(ApiConfig.GLOBAL, 0);
        gName = pSettings.getString("gName", "");
        gEmail = pSettings.getString("gEmail", "");
        gPhone = pSettings.getString("gPhone", "");
        gUrlImage = pSettings.getString("gUrlImage", "");
        gActive = pSettings.getBoolean("gActive", true);
        gLatitude = pSettings.getString("Latitude", "");
        gLongitude = pSettings.getString("Longitude", "");
        gId = pSettings.getString("gId", "");
        gLocation = pSettings.getBoolean("gLocation", false);

        oName = findViewById(R.id.oName);
        oDescription = findViewById(R.id.oDescription);
        pCost = findViewById(R.id.pCost);
        oSerial = getRandomString(12);
        oUrl = "x";
        oReviews = "1";
        oClaim = "0";
        oStatus = "0";

        /* starts before 1 month from now */
        Date cDate = new Date();

        TextView sDate = findViewById(R.id.sDate);
        String todayDateStr = DateFormat.format("EEE, MMM d, yyyy", cDate).toString();
        sDate.setText(todayDateStr);
        oDated = todayDateStr;

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);

        /* ends after 1 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);
        final Calendar defaultSelectedDate = Calendar.getInstance();
        // on below line we are setting up our horizontal calendar view and passing id our calendar view to it.
        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                // on below line we are adding a range
                // as start date and end date to our calendar.
                .range(startDate, endDate)
                // on below line we are providing a number of dates
                // which will be visible on the screen at a time.
                .datesNumberOnScreen(5)
                .datesNumberOnScreen(5)
                .configure()
                .formatTopText("MMM")
                .formatMiddleText("dd")
                .formatBottomText("EEE")
                .showTopText(true)
                .showBottomText(true)
                .textColor(Color.LTGRAY, Color.WHITE)
                .colorTextMiddle(Color.LTGRAY, Color.parseColor("#ffd54f"))
                .end()
                .defaultSelectedDate(defaultSelectedDate)
                .addEvents(new CalendarEventsPredicate() {

                    Random rnd = new Random();
                    @Override
                    public List<CalendarEvent> events(Calendar date) {
                        List<CalendarEvent> events = new ArrayList<>();
                        int count = rnd.nextInt(6);

                        for (int i = 0; i <= count; i++){
                            events.add(new CalendarEvent(Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)), "event"));
                        }

                        return events;
                    }
                })
                .build();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                String selectedDateStr = DateFormat.format("EEE, MMM d, yyyy", date).toString();
                sDate.setText(selectedDateStr);
                oDated = selectedDateStr;
            }
        });

        TextView gUser = (TextView) findViewById(R.id.gUser);
        gUser.setText(gName + ",");
        bPost = findViewById(R.id.bPost);
        AppCompatButton bDismiss = findViewById(R.id.bDismiss);

        if (oName != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(oName.getWindowToken(), 0);
        }

        TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (filterLongEnough()) {

                    if (count > 12) {
                        oDescription.setTextColor(getResources().getColor(R.color.cheque_l));
                    } else if (count >= 254) {
                        oDescription.setTextColor(getResources().getColor(R.color.cancel_l));
                    }
                }
            }

            private boolean filterLongEnough() {
                return oDescription.getText().toString().trim().length() > 2;
            }
        };
        oDescription.addTextChangedListener(fieldValidatorTextWatcher);

        TextWatcher fieldNumber = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                if (count > 0) {
                    mRadioBtCur.setEnabled(true);
                } else {
                    mRadioBtCur.setEnabled(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (filterLongEnough()) {
                    mRadioBtCur.setEnabled(true);
                } else {
                    mRadioBtCur.setEnabled(false);
                }
            }

            private boolean filterLongEnough() {
                return pCost.getText().toString().trim().length() > 0;
            }
        };
        pCost.addTextChangedListener(fieldNumber);
        Glide.with(getApplicationContext())
                .load(gUrlImage)
                .apply(RequestOptions.circleCropTransform())
                .into(gImage);

        bDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });


        bPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int sCategory = mRadioGr.getCheckedRadioButtonId();
                int sCurrency = mRadioCu.getCheckedRadioButtonId();
                mRadioBt = findViewById(sCategory);
                mRadioBtCur = findViewById(sCurrency);
                cCategory = mRadioBt.getText().toString();
                cCurrency = mRadioBtCur.getText().toString();

                if (Objects.requireNonNull(oName.getText()).toString().isEmpty()) {
                    oName.setError("Please type some text");
                } else {

                    oCount = pCost.getText().toString();

                    if (cCategory.equals("")) {
                        Snackbar.make(findViewById(android.R.id.content),
                                "Category Was Not Selected!", Snackbar.LENGTH_LONG).show();
                    } else if (cCurrency.equals("")) {
                        Snackbar.make(findViewById(android.R.id.content),
                                "Currency Was Not Selected!", Snackbar.LENGTH_LONG).show();
                    } else {
                        syncOffer(String.valueOf(oSerial), oName.getText().toString().trim(),
                                oDescription.getText().toString().trim(), cCategory, oUrl,
                                oReviews, cCurrency, oCount, oDated, oClaim, oStatus, gName, gEmail,
                                gPhone, String.valueOf(gActive), gUrlImage,gLatitude,gLongitude);

                    }
                }
            }
        });

        swVerify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    uploadImagesToServer();
                }
            }
        });

        mScroller();
        if(gLocation){
            startLocationUpdates();
        }else {
            //...
        }

    }
    
    private void syncOffer(String oSerial, String oName, String oDescription, String cCategory, String oUrl, String oReviews,
                           String oCurrency, String oCount, String oDated, String oClaim, String oStatus, String uName, String uEmail, String uPhone, String uVerified,
                           String uUrl,String gLatitude,String gLongitude) {
        findViewById(R.id.cProgress).setVisibility(View.VISIBLE);

        String url = ApiConfig.SYNC_OFFER;
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(getApplicationContext(), jsonObject.getString("message").toString(), Toast.LENGTH_LONG).show();

                    if (jsonObject.getString("message").equals("Posted!")) {

                        HttpsTrustManager.allowAllSSL();
                        uploadImagesToServer();

                        findViewById(R.id.cProgress).setVisibility(View.GONE);

                    } else {
                        findViewById(R.id.cProgress).setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public String getBodyContentType() {

                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();

                params.put("oSerial", oSerial);
                params.put("oName", oName);
                params.put("oDescription", oDescription);
                params.put("cCategory", cCategory);
                params.put("oUrl", oUrl);
                params.put("oReviews", oReviews);
                params.put("oCurrency", oCurrency);
                params.put("oCount", oCount);
                params.put("oClaim", oClaim);
                params.put("oPick", oDated);
                params.put("oStatus", oStatus);
                params.put("uName", uName);
                params.put("uEmail", uEmail);
                params.put("uPhone", uPhone);
                params.put("uVerified", uVerified);
                params.put("uUrl", uUrl);
                params.put("gLatitude", gLatitude);
                params.put("gLongitude", gLongitude);

                return params;
            }
        };

        queue.add(request);
    }

    private void showChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE_READ_STORAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_READ_STORAGE) {
                if (resultData != null) {
                    if (resultData.getClipData() != null) {
                        int count = resultData.getClipData().getItemCount();
                        int currentItem = 0;
                        while (currentItem < count) {
                            Uri imageUri = resultData.getClipData().getItemAt(currentItem).getUri();
                            currentItem = currentItem + 1;

                            Log.d("Uri Selected", imageUri.toString());

                            try {
                                mArray.add(imageUri);
                                MyAdapter mAdapter = new MyAdapter(Post.this, mArray);
                                mGrid.setAdapter(mAdapter);

                                Snackbar.make(findViewById(android.R.id.content),
                                        mArray.size() +" Image(s) CLIPPED!", Snackbar.LENGTH_LONG).show();

                            } catch (Exception e) {
                                Log.e("TAG", "File select error", e);
                            }
                        }
                    } else if (resultData.getData() != null) {

                        final Uri uri = resultData.getData();
                        Log.i("TAG", "Uri = " + uri.toString());

                        try {
                            mArray.add(uri);
                            MyAdapter mAdapter = new MyAdapter(Post.this, mArray);
                            mGrid.setAdapter(mAdapter);

                            Snackbar.make(findViewById(android.R.id.content),
                                    mArray.size() +" Image(s) DATA!", Snackbar.LENGTH_LONG).show();

                        } catch (Exception e) {
                            Log.e("TAG", "File select error", e);
                        }
                    }
                }
            }
        }
    }


    private void uploadImagesToServer() {

        if (checkConnection(Post.this)) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiService.BASE_URL)
                    .client(getUnsafeOkHttpClient().build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            showProgress();

            List<MultipartBody.Part> parts = new ArrayList<>();
            ApiService service = retrofit.create(ApiService.class);

            if (mArray != null) {
                for (int i = 0; i < mArray.size(); i++) {
                    parts.add(prepareFilePart("image" + i, mArray.get(i)));
                }

                RequestBody description = createPartFromString("azyma.daletsys.co.ke");
                RequestBody size = createPartFromString("" + parts.size());
                RequestBody serial = createPartFromString(oSerial);

                Call<ResponseBody> call = service.uploadMultiple(description, size, serial, parts);
                call.enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        hideProgress();
                        if (response.isSuccessful()) {
                            Toast.makeText(Post.this,
                                    "Images successfully uploaded!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Snackbar.make(findViewById(android.R.id.content),
                                    response.message(), Snackbar.LENGTH_LONG).show();

                            Log.d(ApiConfig.AZYMA_APP, response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        showProgress();
                        Snackbar.make(findViewById(android.R.id.content),
                                "Attempting ReUpload", Snackbar.LENGTH_LONG).show();

                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                hideProgress();
                                if (response.isSuccessful()) {
                                    Toast.makeText(Post.this,
                                            "Images successfully uploaded!", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Snackbar.make(findViewById(android.R.id.content),
                                            response.message(), Snackbar.LENGTH_LONG).show();

                                    Log.d(ApiConfig.AZYMA_APP, response.message());
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                hideProgress();
                                Log.e("TAG", "Image ReUpload failed!", t);
                                Snackbar.make(findViewById(android.R.id.content),
                                        "Err:" + t, Snackbar.LENGTH_LONG).show();
                            }
                        });

                    }
                });

            }else {
                Snackbar.make(findViewById(android.R.id.content),
                        "Image List Not Read!", Snackbar.LENGTH_LONG).show();
            }

        } else {
            hideProgress();
            Toast.makeText(Post.this,
                    "Internet Unavailable!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showProgress() {
        findViewById(R.id.cProgress).setVisibility(View.VISIBLE);
        bPost.setEnabled(false);
        mAdd.setEnabled(false);
    }

    private void hideProgress() {
        findViewById(R.id.cProgress).setVisibility(View.GONE);
        mAdd.setEnabled(true);
        bPost.setEnabled(true);

    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(MediaType.parse(FileUtils.MIME_TYPE_TEXT), descriptionString);
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        // use the FileUtils to get the actual file by uri
        File file = FileUtils.getFile(this, fileUri);

        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse(FileUtils.MIME_TYPE_IMAGE), file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    private void askForPermission() {
        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE))
                != PackageManager.PERMISSION_GRANTED) {

            // need to request permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Snackbar.make(this.findViewById(android.R.id.content),
                        "Please grant permissions to write data in sdcard",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        v -> ActivityCompat.requestPermissions(Post.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_CODE_PERMISSIONS)).show();
            } else {
                /* Request for permission */
                ActivityCompat.requestPermissions(Post.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_PERMISSIONS);
            }

        } else {
            showChooser();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showChooser();
            } else {

                Toast.makeText(Post.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(mContext, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public static OkHttpClient.Builder getUnsafeOkHttpClient() {

        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String
                                authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String
                                authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    hostname = "https://azyma.daletsys.co.ke";
                    return true;
                }
            });
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Post.this);
        final AlertDialog dialog = builder.setMessage("You need to grant access to Read External Storage")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(arg0 -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                    ContextCompat.getColor(Post.this, android.R.color.holo_blue_light));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                    ContextCompat.getColor(Post.this, android.R.color.holo_red_light));
        });

        dialog.show();
    }

    void showDialog() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(Post.this);
        View view1 = LayoutInflater.from(Post.this).inflate(R.layout.media, null);
        bottomSheetDialog.setContentView(view1);
        bottomSheetDialog.show();
        CardView fPhoto = view1.findViewById(R.id.sPhoto);
        CardView fVideo = view1.findViewById(R.id.sVideo);
        CardView fFiles = view1.findViewById(R.id.sFiles);
        fPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        fVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        fFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });
    }

    private static String getRandomString(final int sizeOfRandomString) {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    public void mScroller() {
        NestedScrollView scroller = (NestedScrollView) findViewById(R.id.myScroll);

        scroller.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = (View) scroller.getChildAt(scroller.getChildCount() - 1);

                int diff = (view.getBottom() - (scroller.getHeight() + scroller
                        .getScrollY()));

                if (diff == 0) {

                }
            }
        });

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
                    Log.i(ApiConfig.AZYMA_APP, "BOTTOM SCROLL");
                }
            }
        });
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

                    updateLocationViews(getAddress(cLocation.latitude, cLocation.longitude));

                }
            }
        };

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.getFusedLocationProviderClient(mContext)
                    .requestLocationUpdates(locationRequest, locationCallback, null);
        }

    }

    void updateLocationViews(String address) {
        TextView  tInfo= findViewById(R.id.tInfo);
        tInfo.setText(address);
        ExpandCollapseExtention.expand(findViewById(R.id.notify));
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                ExpandCollapseExtention.collapse(findViewById(R.id.notify));
            }
        }, 10000);
    }

    public String getAddress(double lat, double lng) {
        String add = "";
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            add = obj.getAddressLine(0);
            add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            //add = add + "\n" + obj.getPostalCode();
            //add = add + "\n" + obj.getSubAdminArea();
            //add = add + "\n" + obj.getLocality();
            //add = add + "\n" + obj.getSubThoroughfare();

            Log.v("IGA", "Address" + add);
            /**Toast.makeText(this, "Address=>" + add,
             Toast.LENGTH_SHORT).show();*/
            return add;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return add;
    }


}
