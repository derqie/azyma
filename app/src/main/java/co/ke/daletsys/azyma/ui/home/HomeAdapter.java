package co.ke.daletsys.azyma.ui.home;


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
import co.ke.daletsys.azyma.global.ApiConfig;
import co.ke.daletsys.azyma.ui.CategoryDetails;
import co.ke.daletsys.azyma.ui.Users;

public class HomeAdapter extends RecyclerView.Adapter <HomeAdapter.Viewholder > {

        private ArrayList<HomeHolder> hList;
        public HomeAdapter(Context context, ArrayList <HomeHolder> List) {
            hList = List;
        }

        public class Viewholder extends RecyclerView.ViewHolder {
            ImageView hIcon;
            TextView hName, hDescription;
            Context mContext;

            public Viewholder(@NonNull View itemView) {
                super(itemView);
                mContext = itemView.getContext();
                hName = itemView.findViewById(R.id.hName);
                hDescription = itemView.findViewById(R.id.hDescription);
                hIcon = itemView.findViewById(R.id.hIcon);
            }
        }

        @NonNull
        @Override
        public HomeAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.home, parent, false);
            return new Viewholder(v);
        }


        @Override
        public void onBindViewHolder(@NonNull HomeAdapter.Viewholder holder, int position) {
            holder.itemView.setTag(hList.get(position));

            holder.hName.setText(hList.get(position)
                    .getName());
            holder.hDescription.setText(hList.get(position)
                    .getDescription());

            Glide.with(holder.itemView.getContext())
                    .load(hList.get(position)
                            .getDrawable())
                    .into(holder.hIcon);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(holder.getAdapterPosition() == 0){

                    }else if(holder.getAdapterPosition() == 1){
                        Intent nIntent = new Intent(holder.mContext, Users.class);
                        Bundle extras = new Bundle();
                        extras.putString("Category", "Users");
                        extras.putString("CategoryDetails", "Featured User Profiles");
                        extras.putString("Url", ApiConfig.MISC);
                        nIntent.putExtras(extras);
                        holder.mContext.startActivity(nIntent);
                    }else if(holder.getAdapterPosition() == 2){

                    }else if(holder.getAdapterPosition() == 3){

                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return hList.size();
        }
}
