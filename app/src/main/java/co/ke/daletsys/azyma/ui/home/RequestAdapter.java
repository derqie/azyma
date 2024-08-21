package co.ke.daletsys.azyma.ui.home;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import co.ke.daletsys.azyma.R;
import co.ke.daletsys.azyma.control.Application;
import co.ke.daletsys.azyma.ui.CategoryDetails;

public class RequestAdapter extends RecyclerView.Adapter <RequestAdapter.Viewholder > {

        private ArrayList<RequestHolder> rList;

        public RequestAdapter(Context context, ArrayList <RequestHolder> List) {
            rList = List;
        }

        public class Viewholder extends RecyclerView.ViewHolder {
            AppCompatImageView cIcon;
            TextView category,id,serial,offer,rCost,comment,user,email,status,created;
            public Viewholder(@NonNull View itemView) {
                super(itemView);
                user = itemView.findViewById(R.id.rName);
                email = itemView.findViewById(R.id.rEmail);
                rCost = itemView.findViewById(R.id.rCost);
                serial = itemView.findViewById(R.id.oSerial);
                category = itemView.findViewById(R.id.oCategory);
                offer = itemView.findViewById(R.id.oId);
                cIcon = itemView.findViewById(R.id.cIcon);
                id = itemView.findViewById(R.id.rId);
            }
        }

        @NonNull
        @Override
        public RequestAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.requests, parent, false);
            return new Viewholder(v);
        }


        @Override
        public void onBindViewHolder(@NonNull RequestAdapter.Viewholder holder, int position) {
            holder.itemView.setTag(rList.get(position));

            holder.user.setText(rList.get(holder.getAdapterPosition()).getUser());
            holder.email.setText(rList.get(position).getEmail());
            holder.offer.setText(rList.get(position).getOffer());
            holder.id.setText(rList.get(position).getId());
            holder.serial.setText(rList.get(position).getSerial());
            holder.category.setText(rList.get(position).getCategory());
            holder.rCost.setText(Application.getFormatedNumber(rList.get(holder.getAdapterPosition()).getAmount()));

            Glide.with(holder.itemView.getContext())
                    .load(rList.get(position).getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .error(R.drawable.ic_azyma_light)
                    .into(holder.cIcon);

            holder.itemView.setOnClickListener(view -> {
            });
        }

        @Override
        public int getItemCount() {
            return rList.size();
        }
}
