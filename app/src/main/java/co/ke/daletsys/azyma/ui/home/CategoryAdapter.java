package co.ke.daletsys.azyma.ui.home;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import co.ke.daletsys.azyma.R;
import co.ke.daletsys.azyma.ui.CategoryDetails;

public class CategoryAdapter extends RecyclerView.Adapter <CategoryAdapter.Viewholder > {

        private ArrayList<CategoryHolder> cList;
        public CategoryAdapter(Context context, ArrayList <CategoryHolder> List) {
            cList = List;
        }
        public class Viewholder extends RecyclerView.ViewHolder {
            AppCompatImageView cIcon;
            TextView cName, cDescription, cCount;
            CardView hCardView;
            Context mContext;
            public Viewholder(@NonNull View itemView) {
                super(itemView);
                mContext = itemView.getContext();
                cName = itemView.findViewById(R.id.cName);
                cDescription = itemView.findViewById(R.id.cDescription);
                cCount = itemView.findViewById(R.id.cCount);
                cIcon = itemView.findViewById(R.id.cIcon);
            }
        }

        @NonNull
        @Override
        public CategoryAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.category, parent, false);
            return new Viewholder(v);
        }


        @Override
        public void onBindViewHolder(@NonNull CategoryAdapter.Viewholder holder, int position) {
            holder.itemView.setTag(cList.get(position));

            holder.cName.setText(cList.get(position)
                    .getName());
            holder.cDescription.setText(cList.get(position)
                    .getDescription());
            holder.cCount.setText(cList.get(position)
                    .getCount());

            Glide.with(holder.itemView.getContext())
                    .load(cList.get(position)
                            .getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.cIcon);

            holder.cIcon.setOnClickListener(view -> {
                Intent nIntent = new Intent(holder.mContext, CategoryDetails.class);
                Bundle extras = new Bundle();
                extras.putString("Category", holder.cName.getText().toString());
                extras.putString("CategoryDetails", holder.cDescription.getText().toString());
                extras.putString("Url", cList.get(position).getUrl());
                nIntent.putExtras(extras);
                holder.mContext.startActivity(nIntent);
            });
        }

        @Override
        public int getItemCount() {
            return cList.size();
        }
}
