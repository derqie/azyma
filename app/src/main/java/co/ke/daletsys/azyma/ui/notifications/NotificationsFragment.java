package co.ke.daletsys.azyma.ui.notifications;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import co.ke.daletsys.azyma.R;
import co.ke.daletsys.azyma.chat.ui.LoginActivity;

public class NotificationsFragment extends Fragment{

    View mRoot;
    Context mContext;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        mRoot = inflater.inflate(R.layout.fragment_notifications, container, false);
        mContext = mRoot.getContext();

        return mRoot;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}