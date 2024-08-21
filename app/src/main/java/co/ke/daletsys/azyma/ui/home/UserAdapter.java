package co.ke.daletsys.azyma.ui.home;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import co.ke.daletsys.azyma.R;
import co.ke.daletsys.azyma.control.Application;
import co.ke.daletsys.azyma.global.ExpandCollapseExtention;
import co.ke.daletsys.azyma.ui.CategoryDetails;
import co.ke.daletsys.azyma.ui.UserActivity;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.Viewholder> {

    private ArrayList<UserHolder> cList;

    public UserAdapter(Context context, ArrayList<UserHolder> List) {
        cList = List;
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        AppCompatImageView cIcon, oShare, oSettings, oLike, iViews;
        LinearLayout lPersonal;
        TextView cSignal, cName, cEmail, cCount, tHide, tShow,ratings,cVerified;
        Context mContext;
        RatingBar rating;
        LinearLayout lSignal;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            mContext = itemView.getContext();
            cName = itemView.findViewById(R.id.cName);
            cEmail = itemView.findViewById(R.id.cEmail);
            cCount = itemView.findViewById(R.id.cCount);
            cIcon = itemView.findViewById(R.id.cIcon);
            lPersonal = itemView.findViewById(R.id.lPersonal);
            tHide = itemView.findViewById(R.id.tHide);
            tShow = itemView.findViewById(R.id.tShow);
            oLike = itemView.findViewById(R.id.oLike);
            oShare = itemView.findViewById(R.id.oShare);
            iViews = itemView.findViewById(R.id.iViews);
            rating = itemView.findViewById(R.id.rating);
            ratings = itemView.findViewById(R.id.ratings);
            cSignal = itemView.findViewById(R.id.cSignal);
            cVerified = itemView.findViewById(R.id.cVerified);
            lSignal = itemView.findViewById(R.id.lSignal);
        }
    }

    @NonNull
    @Override
    public UserAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.users, parent, false);
        return new Viewholder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull UserAdapter.Viewholder holder, int position) {
        holder.mContext = holder.itemView.getContext();
        holder.itemView.setTag(cList.get(position));

        holder.cName.setText(cList.get(position).getName());
        holder.cEmail.setText(cList.get(position).getEmail());
        holder.cCount.setText(cList.get(position).getPhone());
        holder.ratings.setText(cList.get(position).getRating());

        String verified = cList.get(position).getVerified();
        if(verified.equals("true")){
            holder.cVerified.setText("Verified");
            holder.cVerified.setTextColor(holder.mContext.getResources().getColor(R.color.lime));
        }else {
            holder.cVerified.setText("Not Verified");
            holder.cVerified.setTextColor(holder.mContext.getResources().getColor(R.color.amber));
        }

        Glide.with(holder.itemView.getContext())
                .load(cList.get(position)
                        .getUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.cIcon);

        String cMiles = cList.get(position).getSignal();
        if (cMiles.equals("")) {

        } else {
            double yKilo = Double.parseDouble(cMiles);
            double wKilo = (yKilo) / 1000;
            double hKilo = Application.round(wKilo, 2);
            holder.cSignal.setText(hKilo + " KM(s)");

        }

        holder.cIcon.setOnClickListener(view -> {
            Intent nIntent = new Intent(holder.mContext, UserActivity.class);
            Bundle extras = new Bundle();
            extras.putString("Name", cList.get(position).getName());
            extras.putString("Email", cList.get(position).getEmail());
            extras.putString("Url", cList.get(position).getUrl());
            extras.putString("Phone", cList.get(position).getPhone());
            extras.putString("Signal", holder.cSignal.getText().toString());
            extras.putString("Verified", cList.get(position).getVerified());
            extras.putString("Ratings", cList.get(position).getRating());
            extras.putString("Latitude", cList.get(position).getLatitude());
            extras.putString("Longitude", cList.get(position).getLongitude());
            nIntent.putExtras(extras);
            holder.mContext.startActivity(nIntent);
        });

        holder.tShow.setOnClickListener(view -> {
            ExpandCollapseExtention.expand(holder.lPersonal);
            ExpandCollapseExtention.collapse(holder.tShow);
            ExpandCollapseExtention.collapse(holder.lSignal);

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    ExpandCollapseExtention.collapse(holder.lPersonal);
                    ExpandCollapseExtention.expand(holder.tShow);
                    ExpandCollapseExtention.expand(holder.lSignal);
                }
            }, 10000);
        });

        Glide.with(holder.mContext).load(R.drawable.eye).into(holder.iViews);
        Glide.with(holder.mContext).load(R.drawable.share).into(holder.oShare);
        Glide.with(holder.mContext).load(R.drawable.hearted).into(holder.oLike);

        holder.oLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(holder.mContext, "Liked", Toast.LENGTH_SHORT).show();
            }
        });
        holder.oShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(holder.mContext, "Shared", Toast.LENGTH_SHORT).show();
            }
        });
        holder.iViews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(holder.mContext, "Peep'd", Toast.LENGTH_SHORT).show();
            }
        });

        holder.rating.setRating(1);
    }



    @Override
    public int getItemCount() {
        return cList.size();
    }
}
