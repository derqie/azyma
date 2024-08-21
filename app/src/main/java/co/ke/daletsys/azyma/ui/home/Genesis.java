package co.ke.daletsys.azyma.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import co.ke.daletsys.azyma.R;
import co.ke.daletsys.azyma.databinding.ActivityGenesisBinding;
import co.ke.daletsys.azyma.databinding.ActivityMainBinding;
import co.ke.daletsys.azyma.easychat.model.UserModel;
import co.ke.daletsys.azyma.easychat.ui.LoginUsernameActivity;
import co.ke.daletsys.azyma.easychat.utils.FirebaseUtil;

public class Genesis extends AppCompatActivity {

    private ActivityGenesisBinding binding;
    SharedPreferences pSettings;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        binding = ActivityGenesisBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        pSettings = getApplicationContext().getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        boolean gDark = pSettings.getBoolean("gDark", false);

        if (gDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setBackground(null);
        navView.getMenu().getItem(2).isEnabled();

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_placeholder, R.id.navigation_conversations, R.id.navigation_profile)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        FloatingActionButton fButton = findViewById(R.id.fButton);
        fButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(Genesis.this, Post.class);
                startActivity(myIntent);

                /*BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
                View view1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.post_offer, null);
                bottomSheetDialog.setContentView(view1);
                bottomSheetDialog.show();

                bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {

                    }
                });*/
            }
        });
        setOnlineStatus();
    }

    void setOnlineStatus(){
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    userModel = task.getResult().toObject(UserModel.class);
                    if(userModel!=null){
                        userModel.setStatus("Online");

                        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){

                                }
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    userModel = task.getResult().toObject(UserModel.class);
                    if(userModel!=null){
                        userModel.setStatus("Offline");

                        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){

                                }
                            }
                        });
                    }
                }
            }
        });
    }
}