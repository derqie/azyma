package co.ke.daletsys.azyma.ui.home;


import android.content.Context;
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

public class SplashAdapter extends RecyclerView.Adapter <SplashAdapter.Viewholder > {

        private ArrayList<HomeHolder> hList;

        public SplashAdapter(Context context, ArrayList <HomeHolder> List) {
            hList = List;
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
        public SplashAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.splash, parent, false);

            //int width = v.getWidth();
            //ViewGroup.LayoutParams params = v.getLayoutParams();
            //params.width = (int)(width * 0.8);
            //v.setLayoutParams(params);

            return new Viewholder(v);
        }


        @Override
        public void onBindViewHolder(@NonNull SplashAdapter.Viewholder holder, int position) {
            holder.itemView.setTag(hList.get(position));

            holder.hName.setText(hList.get(position)
                    .getName());
            holder.hDescription.setText(hList.get(position)
                    .getDescription());

            Glide.with(holder.itemView.getContext())
                    .load(hList.get(position)
                            .getDrawable())
                    .into(holder.hIcon);
        }

        @Override
        public int getItemCount() {
            return hList.size();
        }
}
