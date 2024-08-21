package co.ke.daletsys.azyma.ui.home;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import co.ke.daletsys.azyma.R;
import co.ke.daletsys.azyma.chat.ui.LoginActivity;
import co.ke.daletsys.azyma.chat.ui.RegisterActivity;
import co.ke.daletsys.azyma.databinding.ActivitySplashBinding;
import co.ke.daletsys.azyma.easychat.ui.LoginOtpActivity;
import co.ke.daletsys.azyma.easychat.ui.SplashActivity;
import co.ke.daletsys.azyma.global.LinePagerIndicatorDecorationBottom;

public class Splash extends AppCompatActivity {

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private final Handler mHideHandler = new Handler(Looper.myLooper());

    SignInButton signup;
    Context mContext;
    GoogleSignInClient mGoogleSignInClient;
    int google_login = 100;
    int google_register = 99;
    private GoogleSignInAccount gAccount;

    SharedPreferences.Editor pEditor;
    SharedPreferences pSettings;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mManager;
    ArrayList<HomeHolder> mItems;
    ScrollingLinearLayoutManager scrollManager;
    SwipeTask swipeTask;
    Timer swipeTimer;


     private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            if (Build.VERSION.SDK_INT >= 30) {

            } else {

            }
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {

        }
    };

    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (AUTO_HIDE) {
                        delayedHide(AUTO_HIDE_DELAY_MILLIS);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
                default:
                    break;
            }
            return false;
        }
    };
    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mControlsView = binding.fullscreenContentControls;
        mContext = this.getApplicationContext();

        ConstraintLayout background = findViewById(R.id.background);
        mRecyclerView = findViewById(R.id.mList);
        scrollManager = new ScrollingLinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL, false, 100);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.addItemDecoration(new LinePagerIndicatorDecorationBottom());
        mRecyclerView.setLayoutManager(scrollManager);
        mItems = new ArrayList<>();
        mItems.add(new HomeHolder("Explore Azyma Now", "We have more in the Pandora!", "Explore Azyma Now",R.drawable.about));
        mItems.add(new HomeHolder("Get Social", "Our socials are active and we respond back.", "Get Social",R.drawable.engage));
        mItems.add(new HomeHolder("Report Something!", "Have your voice Heard.", "Report",R.drawable.speak));
        mItems.add(new HomeHolder("Coming Soon", "The Horizon has more for Azymians.", "Coming Soon",R.drawable.self));
        mAdapter = new SplashAdapter(mContext, mItems);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    int position = getCurrentItem();

                    if(position==0){

                        cColorAlpha(background);

                    }else if(position==1){

                        cColorBeta(background);

                    }else if(position==2){

                        cColorTheta(background);

                    }else if(position==3){

                        cColorDelta(background);

                    }
                }
            }
        });
        playCarousel();


        signup = findViewById(R.id.signup);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sInGoogle();
            }
        });

        findViewById(R.id.proxy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sUpGoogle();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sInGoogle();
            }
        });

        pSettings = getApplicationContext().getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        boolean gActive = pSettings.getBoolean("gActive", false);
        if (gActive) {
            finish();
            Intent myIntent = new Intent(Splash.this, Genesis.class);
            Splash.this.startActivity(myIntent);
        }

        AppCompatImageView back = findViewById(R.id.back);
        AppCompatImageView nGoogle = findViewById(R.id.nGoogle);
        AppCompatImageView sGoogle = findViewById(R.id.sGoogle);

        Glide.with(mContext)
                .load(R.drawable.legend)
                .error(R.drawable.legend)
                .into(back);
        Glide.with(mContext)
                .load(R.drawable.google_logo_icon)
                .error(R.drawable.google_logo_icon)
                .into(nGoogle);
        Glide.with(mContext)
                .load(R.drawable.google_logo_icon)
                .error(R.drawable.google_logo_icon)
                .into(sGoogle);

        findViewById(R.id.app).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nIntent = new Intent(Splash.this, SplashActivity.class);
                startActivity(nIntent);
            }
        });
    }


    public boolean hasPreview() {
        return getCurrentItem() > 0;
    }

    public boolean hasNext() {
        return mRecyclerView.getAdapter() != null &&
                getCurrentItem() < (mRecyclerView.getAdapter().getItemCount()- 1);
    }

    public void preview() {
        int position = getCurrentItem();
        if (position > 0)
            setCurrentItem(position -1, true);
    }

    public void next() {
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter == null)
            return;

        int position = getCurrentItem();
        int count = adapter.getItemCount();
        if (position < (count -1))
            setCurrentItem(position + 1, true);
    }

    private int getCurrentItem(){
        return ((LinearLayoutManager)mRecyclerView.getLayoutManager())
                .findFirstVisibleItemPosition();
    }

    private void setCurrentItem(int position, boolean smooth){
        if (smooth)
            mRecyclerView.smoothScrollToPosition(position);
        else
            mRecyclerView.scrollToPosition(position);
    }

    private void cColorAlpha(View view) {

        final int initialColor = getResources().getColor(R.color.retro);
        final int finalColor = getResources().getColor(R.color.retro);

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Use animation position to blend colors.
                float position = animation.getAnimatedFraction();
                int blended = blendColors(initialColor, finalColor, position);

                // Apply blended color to the view.
                view.setBackgroundColor(blended);
            }
        });

        anim.setDuration(500).start();
    }

    private void cColorBeta(View view) {

        final int initialColor = getResources().getColor(R.color.retro);
        final int finalColor = getResources().getColor(R.color.cancel_l);

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Use animation position to blend colors.
                float position = animation.getAnimatedFraction();
                int blended = blendColors(initialColor, finalColor, position);

                // Apply blended color to the view.
                view.setBackgroundColor(blended);
            }
        });

        anim.setDuration(500).start();
    }

    private void cColorTheta(View view) {

        final int initialColor = getResources().getColor(R.color.cancel_l);
        final int finalColor = getResources().getColor(R.color.lime_l);

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Use animation position to blend colors.
                float position = animation.getAnimatedFraction();
                int blended = blendColors(initialColor, finalColor, position);

                // Apply blended color to the view.
                view.setBackgroundColor(blended);
            }
        });

        anim.setDuration(500).start();
    }

    private void cColorDelta(View view) {

        final int initialColor = getResources().getColor(R.color.lime_l);
        final int finalColor = getResources().getColor(R.color.light_blue_600);

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Use animation position to blend colors.
                float position = animation.getAnimatedFraction();
                int blended = blendColors(initialColor, finalColor, position);

                // Apply blended color to the view.
                view.setBackgroundColor(blended);
            }
        });

        anim.setDuration(500).start();
    }

    private int blendColors(int from, int to, float ratio) {
        final float inverseRatio = 1f - ratio;

        final float r = Color.red(to) * ratio + Color.red(from) * inverseRatio;
        final float g = Color.green(to) * ratio + Color.green(from) * inverseRatio;
        final float b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio;

        return Color.rgb((int) r, (int) g, (int) b);
    }


    private class SmoothScroller extends LinearSmoothScroller {
        private final float distanceInPixels;
        private final float duration;

        public SmoothScroller(Context context, int distanceInPixels, int duration) {
            super(context);
            this.distanceInPixels = distanceInPixels;
            this.duration = duration;
        }

        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            return this.computeScrollVectorForPosition(targetPosition);
        }

        @Override
        protected int calculateTimeForScrolling(int dx) {
            float proportion = (float) dx / distanceInPixels;
            return (int) (duration * proportion);
        }
    }

    public class ScrollingLinearLayoutManager extends LinearLayoutManager {
        private final int duration;

        public ScrollingLinearLayoutManager(Context context, int orientation, boolean reverseLayout, int duration) {
            super(context, orientation, reverseLayout);
            this.duration = duration;
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state,
                                           int position) {
            View firstVisibleChild = recyclerView.getChildAt(0);
            int itemHeight = firstVisibleChild.getHeight();
            int currentPosition = recyclerView.getChildLayoutPosition(firstVisibleChild);
            int distanceInPixels = Math.abs((currentPosition - position) * itemHeight);
            if (distanceInPixels == 0) {
                distanceInPixels = (int) Math.abs(firstVisibleChild.getY());
            }
            SmoothScroller smoothScroller = new SmoothScroller(recyclerView.getContext(), distanceInPixels, duration);
            smoothScroller.setTargetPosition(position);
            startSmoothScroll(smoothScroller);
        }

        private class SmoothScroller extends LinearSmoothScroller {
            private final float distanceInPixels;
            private final float duration;

            public SmoothScroller(Context context, int distanceInPixels, int duration) {
                super(context);
                this.distanceInPixels = distanceInPixels;
                this.duration = duration;
            }

            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                return ScrollingLinearLayoutManager.this
                        .computeScrollVectorForPosition(targetPosition);
            }

            @Override
            protected int calculateTimeForScrolling(int dx) {
                float proportion = (float) dx / distanceInPixels;
                return (int) (duration * proportion);
            }
        }
    }
    private class SwipeTask extends TimerTask {
        public void run() {
            mRecyclerView.post(()->{
                int nextPage = (scrollManager.findFirstVisibleItemPosition() + 1) % mAdapter.getItemCount();
                mRecyclerView.smoothScrollToPosition(nextPage);
            });
        }
    }
    private void stopScrollTimer() {
        if (null != swipeTimer) {
            swipeTimer.cancel();
        }
        if (null != swipeTask) {
            swipeTask.cancel();
        }
    }

    private void resetScrollTimer() {
        stopScrollTimer();
        swipeTask = new SwipeTask();
        swipeTimer = new Timer();
    }

    private void playCarousel() {
        resetScrollTimer();
        swipeTimer.schedule(swipeTask,0, 4000);
    }

    private void sUpGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, google_register);
    }

    private void sInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, google_login);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("called", "called");
        if (requestCode == google_login) {
            Task task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }else if(requestCode == google_register){
            Task task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignUpResult(task);
        }
    }

    private void handleSignUpResult(Task completedTask) {
        try {
            gAccount = (GoogleSignInAccount) completedTask.getResult(ApiException.class);

            Log.e("email", gAccount.getEmail());
            Log.e("name", gAccount.getDisplayName());
            Log.e("id", gAccount.getId());

            pEditor = pSettings.edit();
            pEditor.putString("gEmail", gAccount.getEmail());
            pEditor.putString("gName", gAccount.getDisplayName());
            pEditor.putString("gId", gAccount.getId());
            pEditor.putString("gUrlImage", gAccount.getPhotoUrl().toString());
            pEditor.putBoolean("gActive", true);
            pEditor.putBoolean("gCache", true);
            pEditor.putBoolean("gSync", true);
            pEditor.putBoolean("gDark", false);
            pEditor.commit();
            pEditor.apply();

            /**Intent myIntent = new Intent(Splash.this, Genesis.class);
            myIntent.putExtra("Name", "Welcome " + gAccount.getDisplayName());
            Splash.this.startActivity(myIntent);*/

            Intent nIntent = new Intent(Splash.this, RegisterActivity.class);//Register New User
            Bundle extras = new Bundle();
            extras.putString("gEmail", gAccount.getEmail());
            extras.putString("gName", gAccount.getDisplayName());
            nIntent.putExtras(extras);
            startActivity(nIntent);

        } catch (ApiException e) {
            Log.e("signInResult", ":failed code=" + e.getStatusCode());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private void handleSignInResult(Task completedTask) {
        try {
            gAccount = (GoogleSignInAccount) completedTask.getResult(ApiException.class);

            Log.e("email", gAccount.getEmail());
            Log.e("name", gAccount.getDisplayName());
            Log.e("id", gAccount.getId());

            pEditor = pSettings.edit();
            pEditor.putString("gEmail", gAccount.getEmail());
            pEditor.putString("gName", gAccount.getDisplayName());
            pEditor.putString("gId", gAccount.getId());
            pEditor.putString("gUrlImage", gAccount.getPhotoUrl().toString());
            pEditor.putBoolean("gActive", true);
            pEditor.putBoolean("gCache", true);
            pEditor.putBoolean("gSync", true);
            pEditor.putBoolean("gDark", false);
            pEditor.commit();
            pEditor.apply();

            /**Intent myIntent = new Intent(Splash.this, Genesis.class);
             myIntent.putExtra("Name", "Welcome " + gAccount.getDisplayName());
             Splash.this.startActivity(myIntent);*/

            Intent nIntent = new Intent(Splash.this, LoginActivity.class);
            Bundle extras = new Bundle();
            extras.putString("gEmail", gAccount.getEmail());
            extras.putString("gName", gAccount.getDisplayName());
            nIntent.putExtras(extras);
            startActivity(nIntent);

        } catch (ApiException e) {
            Log.e("signInResult", ":failed code=" + e.getStatusCode());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}