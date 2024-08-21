package co.ke.daletsys.azyma.chat.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import co.ke.daletsys.azyma.R;
import co.ke.daletsys.azyma.control.Application;
import co.ke.daletsys.azyma.easychat.model.UserModel;
import co.ke.daletsys.azyma.easychat.ui.LoginPhoneNumberActivity;
import co.ke.daletsys.azyma.easychat.ui.LoginUsernameActivity;
import co.ke.daletsys.azyma.easychat.utils.FirebaseUtil;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import static android.text.TextUtils.isEmpty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;


public class RegisterActivity extends AppCompatActivity{
    String gName,gEmail,gNameSet,gEmailSet,gUrlImage;
    private static final String TAG = "RegisterActivity";
    private EditText mEmail, mPassword, mConfirmPassword;
    private ProgressBar mProgressBar;
    private FirebaseFirestore mDb;
    SharedPreferences pSettings;
    Context mContext;
    UserModel userModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mContext = getApplicationContext();

        pSettings = getSharedPreferences("GLOBAL", 0);
        gName = pSettings.getString("gName", "");
        gEmail = pSettings.getString("gEmail", "");
        gUrlImage = pSettings.getString("gUrlImage", "");

        Intent it = this.getIntent();
        gNameSet = it.getStringExtra("gName");
        gEmailSet = it.getStringExtra("gEmail");

        mEmail = (EditText) findViewById(R.id.input_email);
        mPassword = (EditText) findViewById(R.id.input_password);
        mConfirmPassword = (EditText) findViewById(R.id.input_confirm_password);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mEmail.setText(gEmail);
        mEmail.setEnabled(false);

        AppCompatImageView back = findViewById(R.id.legend);
        Glide.with(getApplicationContext())
                .load(R.drawable.legend)
                .error(R.drawable.legend)
                .into(back);

        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isEmpty(mEmail.getText().toString())
                        && !isEmpty(mPassword.getText().toString())
                        && !isEmpty(mConfirmPassword.getText().toString())){

                    if(Application.doStringsMatch(mPassword.getText().toString(), mConfirmPassword.getText().toString())){

                        registerNewEmail(mEmail.getText().toString(), mPassword.getText().toString());
                    }else{
                        Toast.makeText(RegisterActivity.this, "Passwords do not Match", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(RegisterActivity.this, "You must fill out all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mDb = FirebaseFirestore.getInstance();

        hideSoftKeyboard();
    }

    /**
     * Register a new email and password to Firebase Authentication
     * @param email
     * @param password
     */
    public void registerNewEmail(final String email, String password){

        showDialog();

        String username = gName;
        if (username.isEmpty() || username.length() < 3) {

            return;
        }

        if (userModel != null) {
            userModel.setUsername(username);
            userModel.setStatus("Online");
        } else {
            userModel = new UserModel("", username, Timestamp.now(), FirebaseUtil.currentUserId(), "Online");
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: AuthState: " + FirebaseAuth.getInstance().getCurrentUser().getUid());

                            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                                    .build();
                            mDb.setFirestoreSettings(settings);

                            DocumentReference newUserRef = mDb
                                    .collection(getString(R.string.collection_users))
                                    .document(FirebaseAuth.getInstance().getUid());

                            newUserRef.set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    hideDialog();

                                    if(task.isSuccessful()){
                                        showLoginOptions();
                                        //redirectLoginScreen();
                                    }else{
                                        View parentLayout = findViewById(android.R.id.content);
                                        Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                        else {
                            View parentLayout = findViewById(android.R.id.content);
                            Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show();
                            hideDialog();
                        }

                        // ...
                    }
                });
    }


    void showLoginOptions() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(RegisterActivity.this);
        View bsView = LayoutInflater.from(RegisterActivity.this).inflate(R.layout.logins_options, null);
        bottomSheetDialog.setContentView(bsView);
        bottomSheetDialog.show();
        CardView sEmail = bsView.findViewById(R.id.sEmail);
        CardView sPhone = bsView.findViewById(R.id.sPhone);
        CardView sUser = bsView.findViewById(R.id.sUser);

        sPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, LoginPhoneNumberActivity.class));
                finish();
            }
        });

        sEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, LoginActivity.class));
                finish();
            }
        });

        sUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, LoginUsernameActivity.class));
                finish();
            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });
    }

    private void showDialog(){
        mProgressBar.setVisibility(View.VISIBLE);

    }

    private void hideDialog(){
        if(mProgressBar.getVisibility() == View.VISIBLE){
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

}
