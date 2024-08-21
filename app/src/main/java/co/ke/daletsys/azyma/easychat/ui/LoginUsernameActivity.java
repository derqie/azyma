package co.ke.daletsys.azyma.easychat.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import co.ke.daletsys.azyma.R;
import co.ke.daletsys.azyma.easychat.model.UserModel;
import co.ke.daletsys.azyma.easychat.utils.FirebaseUtil;
import co.ke.daletsys.azyma.ui.home.Genesis;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginUsernameActivity extends AppCompatActivity {


    EditText usernameInput;
    AppCompatButton letMeInBtn;
    ProgressBar progressBar;
    String phoneNumber, gName, gEmail, gNameSet, gEmailSet, gUrlImage;
    UserModel userModel;
    SharedPreferences pSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_username);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        pSettings = getSharedPreferences("GLOBAL", 0);
        gName = pSettings.getString("gName", "");
        gEmail = pSettings.getString("gEmail", "");
        gUrlImage = pSettings.getString("gUrlImage", "");

        usernameInput = findViewById(R.id.login_username);
        letMeInBtn = findViewById(R.id.login_let_me_in_btn);
        progressBar = findViewById(R.id.login_progress_bar);

        phoneNumber = getIntent().getExtras().getString("phone");
        getUsername();

        letMeInBtn.setOnClickListener((v -> {
            setUsername();
        }));

        AppCompatImageView back = findViewById(R.id.legend);
        Glide.with(getApplicationContext())
                .load(R.drawable.legend)
                .error(R.drawable.legend)
                .into(back);
    }

    void setUsername() {

        String username = usernameInput.getText().toString();
        if (username.isEmpty() || username.length() < 3) {
            usernameInput.setError("Username length should be at least 3 chars");
            return;
        }
        setInProgress(true);

        if (userModel != null) {
            userModel.setUsername(username);
            userModel.setStatus("Online");
        } else {
            userModel = new UserModel(phoneNumber, username, Timestamp.now(), FirebaseUtil.currentUserId(), "Online");
        }

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setInProgress(false);
                if (task.isSuccessful()) {
                    Intent intent = new Intent(LoginUsernameActivity.this, Genesis.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
            }
        });

    }

    void getUsername() {
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);
                if (task.isSuccessful()) {
                    userModel = task.getResult().toObject(UserModel.class);
                    if (userModel != null) {
                        //usernameInput.setText(userModel.getUsername());
                        usernameInput.setText(gName);
                    }
                }
            }
        });
    }

    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            letMeInBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            letMeInBtn.setVisibility(View.VISIBLE);
        }
    }
}