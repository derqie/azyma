package co.ke.daletsys.azyma.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import co.ke.daletsys.azyma.R;
import co.ke.daletsys.azyma.chat.ui.LoginActivity;
import co.ke.daletsys.azyma.easychat.model.UserModel;
import co.ke.daletsys.azyma.easychat.utils.AndroidUtil;
import co.ke.daletsys.azyma.easychat.utils.FirebaseUtil;
import co.ke.daletsys.azyma.global.ApiConfig;
import co.ke.daletsys.azyma.global.HttpsTrustManager;
import co.ke.daletsys.azyma.ui.home.Post;
import co.ke.daletsys.azyma.ui.home.Splash;
import okhttp3.OkHttpClient;

public class Setup extends AppCompatActivity {

    private String gLongitude, gLatitude, gName, gEmail, gId, gSubscription, gUrlImage, gPhone;
    boolean gActive, gUpdated;
    private AppCompatButton bLogout, bSync;
    private TextView gOnesignal, gOnesignalSub;
    SharedPreferences pSettings;
    SharedPreferences.Editor pEditor;
    Context mContext;
    GoogleApiClient mGoogleApiClient;
    TextInputEditText nPhone;
    UserModel currentUserModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mContext = this.getApplicationContext();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google))
                .requestEmail()
                .build();

        pSettings = getSharedPreferences("GLOBAL", 0);
        gOnesignal = findViewById(R.id.gOnesignal);
        gOnesignalSub = findViewById(R.id.gOnesignalSub);
        bLogout = findViewById(R.id.bLogout);
        bSync = findViewById(R.id.bSync);
        gName = pSettings.getString("gName", "");
        gId = pSettings.getString("gId", "");
        gEmail = pSettings.getString("gEmail", "");
        gPhone = pSettings.getString("gPhone", "");
        gUrlImage = pSettings.getString("gUrlImage", "");
        gActive = pSettings.getBoolean("gActive", true);
        gUpdated = pSettings.getBoolean("gUpdated", false);
        gSubscription = pSettings.getString("gSubscription", "");
        gLatitude = pSettings.getString("Latitude", "");
        gLongitude = pSettings.getString("Longitude", "");
        HttpsTrustManager.allowAllSSL();

        bLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(mContext, gso);
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pEditor = pSettings.edit();
                        pEditor.clear();
                        pEditor.commit();
                        signOut();
                        finish();
                        Intent i = new Intent(mContext, Splash.class);
                        startActivity(i);

                    }
                });
            }
        });

        sClickables();
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void sClickables() {
        String externalId = gId;
        OneSignal.login(externalId);
        gOnesignal.setText(OneSignal.getUser().getPushSubscription().getToken());
        gOnesignalSub.setText(OneSignal.getUser().getPushSubscription().getId());
        gSubscription = OneSignal.getUser().getPushSubscription().getId();

        pEditor = pSettings.edit();
        pEditor.putString("gSubscription", gSubscription);
        pEditor.commit();
        pEditor.apply();

        gOnesignal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", gOnesignal.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(mContext, "Copied", Toast.LENGTH_SHORT).show();
            }
        });
        gOnesignalSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", gOnesignalSub.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(mContext, "Copied", Toast.LENGTH_SHORT).show();
            }
        });

        pSettings = getApplicationContext().getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        boolean gCache = pSettings.getBoolean("gCache", false);
        boolean gSync = pSettings.getBoolean("gSync", false);
        boolean gDark = pSettings.getBoolean("gDark", false);
        boolean gLocation = pSettings.getBoolean("gLocation", false);


        SwitchCompat swCache = findViewById(R.id.swCache);
        SwitchCompat swSync = findViewById(R.id.swSync);
        SwitchCompat swDark = findViewById(R.id.swDark);
        SwitchCompat swLocation = findViewById(R.id.swLocation);
        RelativeLayout sText = findViewById(R.id.sText);
        RelativeLayout sOffer = findViewById(R.id.sOffer);
        RelativeLayout sTheme = findViewById(R.id.sTheme);
        RelativeLayout sLocation = findViewById(R.id.sLocation);
        nPhone = findViewById(R.id.gPhone);
        getUserData();

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        bSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                syncUser(gName, gEmail, gPhone, gActive + "", gUrlImage, gId, gId, gLatitude, gLongitude);
            }
        });
        if (gCache) {
            swCache.setChecked(true);
        } else {
            swCache.setChecked(false);
        }

        if (gDark) {
            swDark.setChecked(true);
        } else {
            swDark.setChecked(false);
        }

        if (gSync) {
            swSync.setChecked(true);
        } else {
            swSync.setChecked(false);
        }

        if (gLocation) {
            swLocation.setChecked(true);
        } else {
            swLocation.setChecked(false);
        }

        swCache.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sText.performClick();
            }
        });
        sText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gCache) {
                    swCache.setChecked(false);
                    pEditor = pSettings.edit();
                    pEditor.putBoolean("gCache", false);
                    pEditor.commit();
                    pEditor.apply();
                } else {
                    swCache.setChecked(true);
                    pEditor = pSettings.edit();
                    pEditor.putBoolean("gCache", true);
                    pEditor.commit();
                    pEditor.apply();
                }
            }
        });


        swSync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sOffer.performClick();
            }
        });
        sOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gSync) {
                    swSync.setChecked(false);
                    pEditor = pSettings.edit();
                    pEditor.putBoolean("gSync", false);
                    pEditor.commit();
                    pEditor.apply();
                } else {
                    swSync.setChecked(true);
                    pEditor = pSettings.edit();
                    pEditor.putBoolean("gSync", true);
                    pEditor.commit();
                    pEditor.apply();
                }
            }
        });

        swDark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sTheme.performClick();
            }
        });
        sTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (gDark) {
                    swDark.setChecked(false);
                    pEditor = pSettings.edit();
                    pEditor.putBoolean("gDark", false);
                    pEditor.commit();
                    pEditor.apply();

                } else {
                    swDark.setChecked(true);
                    pEditor = pSettings.edit();
                    pEditor.putBoolean("gDark", true);
                    pEditor.commit();
                    pEditor.apply();

                }
            }
        });

        swLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sLocation.performClick();
            }
        });
        sLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (gLocation) {
                    swLocation.setChecked(false);
                    pEditor = pSettings.edit();
                    pEditor.putBoolean("gLocation", false);
                    pEditor.commit();
                    pEditor.apply();

                } else {
                    swLocation.setChecked(true);
                    pEditor = pSettings.edit();
                    pEditor.putBoolean("gLocation", true);
                    pEditor.commit();
                    pEditor.apply();

                }
            }
        });

        findViewById(R.id.sVersion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

    }

    void getUserData() {
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            currentUserModel = task.getResult().toObject(UserModel.class);
            nPhone.setText(currentUserModel.getPhone());
        });
    }

    private void syncUser(String uName, String uEmail, String uPhone, String uVerified, String uUrl,
                          String gId, String gSignal, String gLatitude, String gLongitude) {
        TextInputEditText nPhone = findViewById(R.id.gPhone);
        TextInputLayout tPhoneLayout = findViewById(R.id.tPhoneLayout);
        String url = ApiConfig.SYNC_USER;
        RequestQueue queue = Volley.newRequestQueue(mContext);

        uPhone = nPhone.getText().toString();

        if (Objects.requireNonNull(nPhone.getText()).toString().isEmpty()) {
            tPhoneLayout.setError("Please type some text");

        } else if (uPhone.length() > 8) {

            StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("message").equals("Synced!")) {

                            pEditor = pSettings.edit();
                            pEditor.putString("gPhone", nPhone.getText().toString());
                            pEditor.putBoolean("gUpdated", true);
                            pEditor.commit();
                            pEditor.apply();

                            findViewById(R.id.cProgress).setVisibility(View.GONE);
                        } else if (jsonObject.getString("message").equals("Updated!")) {

                            pEditor = pSettings.edit();
                            pEditor.putString("gPhone", nPhone.getText().toString());
                            pEditor.putBoolean("gUpdated", true);
                            pEditor.commit();
                            pEditor.apply();

                        } else {

                            findViewById(R.id.cProgress).setVisibility(View.GONE);
                            Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            Log.e("TAG", "RESPONSE IS " + response);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(mContext, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public String getBodyContentType() {

                    return "application/x-www-form-urlencoded; charset=UTF-8";
                }

                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();

                    params.put("uName", uName);
                    params.put("uEmail", uEmail);
                    params.put("uPhone", nPhone.getText().toString());
                    params.put("uVerified", uVerified);
                    params.put("uUrl", uUrl);
                    params.put("gId", gId);
                    params.put("gSignal", gSubscription);
                    params.put("uLongitude", gLongitude);
                    params.put("uLatitude", gLatitude);

                    return params;
                }
            };

            queue.add(request);

        } else {
            Toast.makeText(mContext, "Insufficient Text", Toast.LENGTH_SHORT).show();

        }
    }


    private void syncMpesa(TextInputEditText pPhone, TextInputEditText pAmount,
                           TextView tPhone, TextView tAmount) {

        String url = ApiConfig.MPESA_REQUEST;
        RequestQueue queue = Volley.newRequestQueue(mContext);

        String phone = pPhone.getText().toString();
        String amount = pAmount.getText().toString();

        if (Objects.requireNonNull(pPhone.getText()).toString().isEmpty()) {


        } else if (Objects.requireNonNull(pAmount.getText()).toString().isEmpty()) {


        } else if (phone.length() > 8) {

            StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("message").equals("Synced!")) {

                        }

                        Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        Log.e("TAG", "RESPONSE IS " + response);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(mContext, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public String getBodyContentType() {

                    return "application/x-www-form-urlencoded; charset=UTF-8";
                }

                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();

                    params.put("phone", phone);
                    params.put("amount", amount);
                    return params;
                }
            };

            queue.add(request);

        } else {
            Toast.makeText(mContext, "Insufficient Text", Toast.LENGTH_SHORT).show();

        }
    }


    void showDialog() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bsView = LayoutInflater.from(this).inflate(R.layout.simplepayment, null);
        bottomSheetDialog.setContentView(bsView);
        bottomSheetDialog.show();

        AppCompatImageView close = bsView.findViewById(R.id.close);
        AppCompatImageView mLogo = bsView.findViewById(R.id.mLogo);
        TextView tAmount = bsView.findViewById(R.id.tAmountLayout);
        TextView tPhone = bsView.findViewById(R.id.tPhoneLayout);
        TextInputEditText gPhone = bsView.findViewById(R.id.gPhone);
        TextInputEditText gAmount = bsView.findViewById(R.id.gAmount);
        AppCompatButton bDismiss = bsView.findViewById(R.id.bDismiss);
        MaterialButton bSync = bsView.findViewById(R.id.bSync);

        tPhone.setText("+254");
        Glide.with(mContext)
                .load(ApiConfig.MPESA_LOGO)
                .error(R.drawable.ic_azyma_light)
                .into(mLogo);

        bSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                syncMpesa(gPhone, gAmount, tPhone, tAmount);
            }
        });

        bDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();

        super.onStart();
    }
}