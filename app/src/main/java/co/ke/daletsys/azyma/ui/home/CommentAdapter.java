package co.ke.daletsys.azyma.ui.home;


import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import co.ke.daletsys.azyma.R;
import co.ke.daletsys.azyma.global.ApiConfig;

public class CommentAdapter extends RecyclerView.Adapter <CommentAdapter.Viewholder > {

        private ArrayList<CommentHolder> cList;
        Context cContext;
        public CommentAdapter(Context context, ArrayList <CommentHolder> List) {
            cList = List;
            cContext = context;
        }

        public class Viewholder extends RecyclerView.ViewHolder {
            AppCompatImageView cIcon,gIcon;
            TextView cDelete,category,id,serial,offer,comment,user,email,status,created;
            SharedPreferences pSettings;
            String gEmail, gName;
            boolean gCache;
            LinearLayout lComments;
            Context mContext;
            public Viewholder(@NonNull View itemView) {
                super(itemView);

                pSettings = itemView.getContext().getSharedPreferences("GLOBAL", 0);
                gEmail = pSettings.getString("gEmail", "");
                gName = pSettings.getString("gName", "");
                gCache = pSettings.getBoolean("gCache", false);
                mContext = itemView.getContext();
                user = itemView.findViewById(R.id.cName);
                email = itemView.findViewById(R.id.cEmail);
                serial = itemView.findViewById(R.id.oSerial);
                category = itemView.findViewById(R.id.oCategory);
                cDelete = itemView.findViewById(R.id.cDelete);
                comment = itemView.findViewById(R.id.cComment);
                offer = itemView.findViewById(R.id.oId);
                cIcon = itemView.findViewById(R.id.cIcon);
                gIcon = itemView.findViewById(R.id.gIcon);
                id = itemView.findViewById(R.id.rId);
                lComments = itemView.findViewById(R.id.lComments);
            }
        }

        @NonNull
        @Override
        public CommentAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.comments, parent, false);
            return new Viewholder(v);
        }


        @Override
        public void onBindViewHolder(@NonNull CommentAdapter.Viewholder holder, int position) {
            holder.itemView.setTag(cList.get(position));

            if(cList.get(position).getEmail().equals(holder.gEmail)){
                holder.cDelete.setVisibility(View.VISIBLE);
                holder.gIcon.setVisibility(View.VISIBLE);
                holder.cIcon.setVisibility(View.GONE);
            }else {
                holder.cDelete.setVisibility(View.GONE);
                holder.gIcon.setVisibility(View.GONE);
                holder.cIcon.setVisibility(View.VISIBLE);
            }

            holder.user.setText(cList.get(holder.getAdapterPosition()).getUser());
            holder.email.setText(cList.get(position).getEmail());
            holder.offer.setText(cList.get(position).getOffer());
            holder.id.setText(cList.get(position).getId());
            holder.serial.setText(cList.get(position).getSerial());
            holder.category.setText(cList.get(position).getCategory());
            holder.comment.setText(cList.get(holder.getAdapterPosition()).getComment());

            Glide.with(holder.itemView.getContext())
                    .load(cList.get(position).getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .error(R.drawable.ic_azyma_light)
                    .into(holder.cIcon);

            Glide.with(holder.itemView.getContext())
                    .load(cList.get(position).getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .error(R.drawable.ic_azyma_light)
                    .into(holder.gIcon);

            holder.cDelete.setOnClickListener(view -> {
                deleted(cList.get(position).getId(), holder.gEmail, holder.mContext, position);
            });
        }


    private void deleted(String comment, String uEmail, Context mContext, int position) {

        String url = ApiConfig.DELETE_COMMENT;
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(mContext, jsonObject.getString("message").toString(), Toast.LENGTH_LONG).show();
                    cList.remove(position);
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> {

        }) {
            @Override
            public String getBodyContentType() {

                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("comment", comment);
                params.put("uEmail", uEmail);
                return params;
            }
        };

        queue.add(request);

    }

        @Override
        public int getItemCount() {
            return cList.size();
        }
}
