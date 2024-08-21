package co.ke.daletsys.azyma.easychat.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import co.ke.daletsys.azyma.R;
import co.ke.daletsys.azyma.easychat.adapter.SearchUserRecyclerAdapter;
import co.ke.daletsys.azyma.easychat.model.UserModel;
import co.ke.daletsys.azyma.easychat.utils.FirebaseUtil;
import co.ke.daletsys.azyma.easychat.utils.WrapContentLinearLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class SearchUserActivity extends AppCompatActivity {
    EditText searchInput;
    ImageButton searchButton;
    ImageButton backButton;
    RecyclerView recyclerView;

    SearchUserRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        searchInput = findViewById(R.id.search_username_input);
        searchButton = findViewById(R.id.search_user_btn);
        backButton = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.search_user_recycler_view);

        searchInput.requestFocus();

        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        searchButton.setOnClickListener(v -> {
            String searchTerm = searchInput.getText().toString();
            if (searchTerm.isEmpty() || searchTerm.length() < 3) {
                searchInput.setError("Invalid Username");
                return;
            }
            setupSearchRecyclerView(searchTerm);
        });
    }

    void setupSearchRecyclerView(String searchTerm) {

        Query query = FirebaseUtil.allUserCollectionReference()
                .whereGreaterThanOrEqualTo("username", searchTerm)
                .whereLessThanOrEqualTo("username", searchTerm + '\uf8ff');

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class).build();

        RecyclerView.LayoutManager sLayoutManager;
        sLayoutManager = new WrapContentLinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        adapter = new SearchUserRecyclerAdapter(options, getApplicationContext());
        recyclerView.setLayoutManager(sLayoutManager);
        recyclerView.getRecycledViewPool().clear();
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null)
            adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.stopListening();
        recyclerView.invalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.startListening();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null)
            adapter.stopListening();
        recyclerView.invalidate();
    }


}












