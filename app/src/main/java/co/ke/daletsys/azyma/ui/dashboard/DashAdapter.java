package co.ke.daletsys.azyma.ui.dashboard;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import co.ke.daletsys.azyma.R;
import co.ke.daletsys.azyma.ui.CategoryDetails;
import co.ke.daletsys.azyma.ui.home.HomeHolder;

public class DashAdapter extends RecyclerView.Adapter<DashAdapter.Viewholder> {

    private ArrayList<HomeHolder> hList;
    Context hContext;
    public DashAdapter(Context context, ArrayList<HomeHolder> List) {
        hList = List;
        hContext = context;
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ImageView hIcon;
        TextView hName, hDescription;
        CardView hCardView;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            hName = itemView.findViewById(R.id.hName);
            hDescription = itemView.findViewById(R.id.hDescription);
            hIcon = itemView.findViewById(R.id.hIcon);
        }
    }

    @NonNull
    @Override
    public DashAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dash, parent, false);
        return new Viewholder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull DashAdapter.Viewholder holder, int position) {
        holder.itemView.setTag(hList.get(holder.getAdapterPosition()));

        holder.hName.setText(hList.get(position)
                .getName());
        holder.hDescription.setText(hList.get(position)
                .getDescription());

        Glide.with(holder.itemView.getContext())
                .load(hList.get(position)
                        .getUrl())
                .into(holder.hIcon);

        holder.itemView.setOnClickListener(view -> {
            Intent nIntent = new Intent(view.getContext(), CategoryDetails.class);
            Bundle extras = new Bundle();
            extras.putString("Category", holder.hName.getText().toString());
            extras.putString("CategoryDetails", holder.hDescription.getText().toString());
            extras.putString("Url", hList.get(position).getUrl());
            nIntent.putExtras(extras);
            holder.itemView.getContext().startActivity(nIntent);
        });

    }

    @Override
    public int getItemCount() {
        return hList.size();
    }
}
