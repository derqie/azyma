package co.ke.daletsys.azyma.control;

import android.content.Context;
import android.net.ConnectivityManager;
import androidx.annotation.NonNull;

import java.util.Objects;


public class InternetConnection {

    public static boolean checkConnection(@NonNull Context context) {
        return ((ConnectivityManager) Objects.requireNonNull(context.getSystemService
                (Context.CONNECTIVITY_SERVICE))).getActiveNetworkInfo() != null;
    }
}