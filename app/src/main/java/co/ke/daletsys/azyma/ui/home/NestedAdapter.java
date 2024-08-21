package co.ke.daletsys.azyma.ui.home;


import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import co.ke.daletsys.azyma.R;
import co.ke.daletsys.azyma.global.ApiConfig;

public class NestedAdapter extends RecyclerView.Adapter <NestedAdapter.Viewholder > {

        private ArrayList<ImageHolder> hList;

        public NestedAdapter(Context context, ArrayList <ImageHolder> List) {
            hList = List;
        }

        public class Viewholder extends RecyclerView.ViewHolder {
            ImageView hIcon;
            TextView hName, hDescription,hUrl;
            CardView hCardView;
            Context mContext;
            SharedPreferences pSettings;
            boolean gCache;

            public Viewholder(@NonNull View itemView) {
                super(itemView);
                mContext = itemView.getContext();
                pSettings = mContext.getSharedPreferences("GLOBAL", 0);
                gCache = pSettings.getBoolean("gCache", false);

                hName = itemView.findViewById(R.id.hName);
                hDescription = itemView.findViewById(R.id.hDescription);
                hUrl = itemView.findViewById(R.id.hUrl);
                hIcon = itemView.findViewById(R.id.hIcon);
            }
        }

        @NonNull
        @Override
        public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.nested, parent, false);
            return new Viewholder(v);
        }


        @Override
        public void onBindViewHolder(@NonNull Viewholder holder, int position) {
            holder.itemView.setTag(hList.get(position));

            holder.hName.setText(hList.get(position)
                    .getSerial());
            holder.hDescription.setText(hList.get(position)
                    .getIdentify());
            holder.hUrl.setText(hList.get(position)
                    .getUrl());

            String iPath = ApiConfig.UPLOADS+hList.get(position)
                    .getSerial()+"/"+hList.get(position).getUrl();

            if(holder.gCache){
                Glide.with(holder.itemView.getContext())
                        .load(iPath)
                        .into(holder.hIcon);
            }else {
                RequestOptions reqOptions = new RequestOptions()
                        .fitCenter()
                        .override(100, 100);
                Glide.with(holder.itemView.getContext())
                        .asBitmap()
                        .apply(reqOptions)
                        .load(iPath)
                        .into(holder.hIcon);
            }
        }

        @Override
        public int getItemCount() {
            return hList.size();
        }
}
