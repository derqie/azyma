package co.ke.daletsys.azyma.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import co.ke.daletsys.azyma.R;
import co.ke.daletsys.azyma.easychat.ui.ChatFragment;
import co.ke.daletsys.azyma.easychat.ui.ProfileFragment;
import co.ke.daletsys.azyma.easychat.ui.SearchUserActivity;
import co.ke.daletsys.azyma.ui.notifications.NotificationsFragment;

public class Conversations extends Fragment {

    private ConversationsViewModel mViewModel;
    private View mRoot;
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    Context mContext;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        ConversationsViewModel conversationsViewModel =
                new ViewModelProvider(this).get(ConversationsViewModel.class);

        mRoot = inflater.inflate(R.layout.fragment_conversations, container, false);
        mContext = mRoot.getContext();

        viewPager = mRoot.findViewById(R.id.viewpager);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.add(new NotificationsFragment(), "Notifications");
        viewPagerAdapter.add(new ChatFragment(), "Chats");
        viewPagerAdapter.add(new ProfileFragment(), "Chat Profile");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout = mRoot.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        TextView tContext = mRoot.findViewById(R.id.tContext);
        TextView tToolbar = mRoot.findViewById(R.id.tToolbar);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    tToolbar.setText("My Notifications");
                    tContext.setText("Azyma In-App\nNotifications");
                } else if (position == 1) {
                    tToolbar.setText("Private Chats");
                    tContext.setText(mContext.getResources().getString(R.string.private_chats));
                } else if (position == 2) {
                    tToolbar.setText("My Chat Profile");
                    tContext.setText("Edit Your Profile.\nLet's get Chatty");
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ImageView legend = mRoot.findViewById(R.id.gLegend);
        Glide.with(mRoot.getContext())
                .load(R.drawable.legend)
                .into(legend);

        ImageButton searchButton = mRoot.findViewById(R.id.main_search_btn);
        searchButton.setOnClickListener((v) -> {
            startActivity(new Intent(mContext, SearchUserActivity.class));
        });
        return mRoot;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ConversationsViewModel.class);
        // TODO: Use the ViewModel
    }

}
