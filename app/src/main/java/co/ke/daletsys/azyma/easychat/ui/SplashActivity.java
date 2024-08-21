package co.ke.daletsys.azyma.easychat.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.bumptech.glide.Glide;

import co.ke.daletsys.azyma.R;
import co.ke.daletsys.azyma.easychat.model.UserModel;
import co.ke.daletsys.azyma.easychat.utils.AndroidUtil;
import co.ke.daletsys.azyma.easychat.utils.FirebaseUtil;
import co.ke.daletsys.azyma.ui.home.Genesis;
import co.ke.daletsys.azyma.ui.home.Splash;

public class SplashActivity extends AppCompatActivity {
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_chat);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        mContext = getApplicationContext();

        if(getIntent().getExtras()!=null){
            //from notification
            String userId = getIntent().getExtras().getString("userId");
            if(userId != null){
                FirebaseUtil.allUserCollectionReference().document(userId).get()
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                UserModel model = task.getResult().toObject(UserModel.class);

                                Intent mainIntent = new Intent(this,MainActivity.class);
                                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(mainIntent);

                                Intent intent = new Intent(this, ChatActivity.class);
                                AndroidUtil.passUserModelAsIntent(intent,model);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
            }else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(FirebaseUtil.isLoggedIn()){
                            startActivity(new Intent(SplashActivity.this, Genesis.class));
                        }else{
                            startActivity(new Intent(SplashActivity.this, Splash.class));
                        }
                        finish();
                    }
                },2000);
            }
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(FirebaseUtil.isLoggedIn()){
                        startActivity(new Intent(SplashActivity.this, Genesis.class));
                    }else{
                        startActivity(new Intent(SplashActivity.this, Splash.class));
                    }
                    finish();
                }
            },2000);
        }

        AppCompatImageView back = findViewById(R.id.back);
        Glide.with(mContext)
                .load(R.drawable.legend)
                .error(R.drawable.legend)
                .into(back);
    }
}