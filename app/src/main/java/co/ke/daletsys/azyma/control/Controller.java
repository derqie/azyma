package co.ke.daletsys.azyma.control;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.onesignal.Continue;
import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;

public class Controller extends Application {
    private static final String ONESIGNAL_APP_ID = "aaf85d5f-5491-4942-b60c-462e3f7e1ef9";
    private static final String TAG = Application.class.getSimpleName();
    private static Controller instance;
    RequestQueue mRequestQueue;


    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        FirebaseApp.initializeApp(getApplicationContext());

        OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID);
        OneSignal.getNotifications().requestPermission(false, Continue.none());
    }

    public static synchronized Controller getInstance(){
        return instance;
    }

    private RequestQueue getRequestQueue(){
        if (mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue (Request<T> req){
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelAllRequest(Object req){
        if (mRequestQueue != null){
            mRequestQueue.cancelAll(req);
        }
    }

}