package co.ke.daletsys.azyma.chat.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import co.ke.daletsys.azyma.R;
import co.ke.daletsys.azyma.control.Controller;
import co.ke.daletsys.azyma.easychat.ui.LoginPhoneNumberActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import static android.text.TextUtils.isEmpty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SwitchCompat;

public class LoginActivity extends AppCompatActivity{

    String gName,gEmail,gNameSet,gEmailSet,gUrlImage;
    private static final String TAG = "LoginActivity";
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText mEmail, mPassword;
    private ProgressBar mProgressBar;
    SharedPreferences pSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        pSettings = getSharedPreferences("GLOBAL", 0);
        gName = pSettings.getString("gName", "");
        gEmail = pSettings.getString("gEmail", "");
        gUrlImage = pSettings.getString("gUrlImage", "");

        Intent it = this.getIntent();
        gNameSet = it.getStringExtra("gName");
        gEmailSet = it.getStringExtra("gEmail");

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mProgressBar = findViewById(R.id.progressBar);

        mEmail.setText(gEmail);

        AppCompatImageView back = findViewById(R.id.legend);
        Glide.with(getApplicationContext())
                .load(R.drawable.legend)
                .error(R.drawable.legend)
                .into(back);

        setupFirebaseAuth();
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        SwitchCompat swShow = findViewById(R.id.swShow);
        swShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            int start,end;
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            start=mPassword.getSelectionStart();
                            end=mPassword.getSelectionEnd();
                            mPassword.setTransformationMethod(new PasswordTransformationMethod());;
                            mPassword.setSelection(start,end);
                            swShow.setChecked(false);
                        }
                    }, 4000);
                }else{
                    start=mPassword.getSelectionStart();
                    end=mPassword.getSelectionEnd();
                    mPassword.setTransformationMethod(null);
                    mPassword.setSelection(start,end);
                }
            }
        });


        hideSoftKeyboard();
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

    private void setupFirebaseAuth(){

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                            .build();
                    db.setFirestoreSettings(settings);

                    DocumentReference userRef = db.collection(getString(R.string.collection_users)).document(user.getUid());

                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                Log.d(TAG, "onComplete: successfully set the user client.");
                                Intent intent = new Intent(LoginActivity.this, LoginPhoneNumberActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });

                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

    private void signIn(){
        //check if the fields are filled out
        if(!isEmpty(mEmail.getText().toString())
                && !isEmpty(mPassword.getText().toString())){
            Log.d(TAG, "onClick: attempting to authenticate.");

            showDialog();

            FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmail.getText().toString(),
                    mPassword.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            hideDialog();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    hideDialog();
                }
            });
        }else{
            Toast.makeText(LoginActivity.this, "You didn't fill in all the fields.", Toast.LENGTH_SHORT).show();
        }
    }

}