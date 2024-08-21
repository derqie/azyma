package co.ke.daletsys.azyma.ui.home;


import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static co.ke.daletsys.azyma.control.InternetConnection.checkConnection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import co.ke.daletsys.azyma.R;
import co.ke.daletsys.azyma.control.Application;
import co.ke.daletsys.azyma.global.ApiConfig;
import co.ke.daletsys.azyma.global.ExpandCollapseExtention;
import co.ke.daletsys.azyma.global.HttpsTrustManager;
import co.ke.daletsys.azyma.global.LinePagerIndicatorDecoration;
import co.ke.daletsys.azyma.ui.Images;
import co.ke.daletsys.azyma.ui.UserActivity;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.Viewholder> {

    private ArrayList<OfferHolder> cList;
    private Context mContext;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    public OfferAdapter(Context context, ArrayList<OfferHolder> List) {
        cList = List;
        context = context;
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        AppCompatImageView cIcon, oShare, oSettings, oLike, iViews;
        TextView oShared, oLiked, oViewed;
        TextView cCategory, oCurrency, oSerial, cName, cDescription, oCount, oReviews, created,
                uName, uPhone, uEmail;
        AppCompatImageView cMain;
        RelativeLayout rMain;
        RecyclerView mRecyclerView;
        ArrayList<ImageHolder> mItems;
        RecyclerView.Adapter mAdapter;
        SharedPreferences pSettings;
        String gEmail, gName;
        boolean gCache;
        LinearLayout rSettings, rShare;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            pSettings = itemView.getContext().getSharedPreferences("GLOBAL", 0);
            gEmail = pSettings.getString("gEmail", "");
            gName = pSettings.getString("gName", "");
            gCache = pSettings.getBoolean("gCache", false);

            mRecyclerView = (RecyclerView) itemView.findViewById(R.id.mList);
            mItems = new ArrayList<>();
            RecyclerView.LayoutManager mManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            PagerSnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(mRecyclerView);
            mRecyclerView.addItemDecoration(new LinePagerIndicatorDecoration());
            mRecyclerView.setLayoutManager(mManager);
            mManager.setItemPrefetchEnabled(true);
            mAdapter = new NestedAdapter(mContext, mItems);
            mRecyclerView.setAdapter(mAdapter);

            oSerial = itemView.findViewById(R.id.oSerial);
            cName = itemView.findViewById(R.id.oName);
            cDescription = itemView.findViewById(R.id.oDescription);
            cCategory = itemView.findViewById(R.id.cCategory);
            oCount = itemView.findViewById(R.id.oCount);
            oReviews = itemView.findViewById(R.id.oReviews);
            oCurrency = itemView.findViewById(R.id.oCurrency);
            created = itemView.findViewById(R.id.created);

            oLike = itemView.findViewById(R.id.oLike);
            oShare = itemView.findViewById(R.id.oShare);
            oSettings = itemView.findViewById(R.id.oSettings);
            iViews = itemView.findViewById(R.id.iViews);

            oLiked = itemView.findViewById(R.id.oLiked);
            oShared = itemView.findViewById(R.id.oShared);
            oViewed = itemView.findViewById(R.id.oViewed);

            uName = itemView.findViewById(R.id.uName);
            uPhone = itemView.findViewById(R.id.uPhone);
            uEmail = itemView.findViewById(R.id.uEmail);
            cIcon = itemView.findViewById(R.id.cIcon);
            cMain = itemView.findViewById(R.id.cMain);
            rMain = itemView.findViewById(R.id.rMain);
            rShare = itemView.findViewById(R.id.rShare);
            rSettings = itemView.findViewById(R.id.rSettings);

        }
    }

    @NonNull
    @Override
    public OfferAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.offer, parent, false);
        return new Viewholder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull OfferAdapter.Viewholder holder, @SuppressLint("RecyclerView") int position) {
        mContext = holder.itemView.getContext();
        holder.setIsRecyclable(false);

        OfferHolder offerHolder = cList.get(holder.getAdapterPosition());
        holder.itemView.setTag(cList.get(position));

        holder.oSerial.setText(cList.get(position).getSerial());
        holder.cName.setText(cList.get(position).getName());
        holder.cDescription.setText(cList.get(position).getDescription());
        holder.cCategory.setText(cList.get(position).getCategory());


        String cCost = cList.get(position).getCount();
        if (cCost.equals("0")) {
            holder.oCount.setText("Freebie!");
        } else {
            holder.oCount.setText(Application.getFormatedNumber(cList.get(position).getCount()));
        }

        String gCurrrency = cList.get(position).getCurrency();
        if (gCurrrency.length() < 1) {
            gCurrrency = "KSH";
            holder.oCurrency.setText(gCurrrency);
        } else {
            holder.oCurrency.setText(gCurrrency);
        }

        holder.oReviews.setText(cList.get(position).getReviews());
        holder.uName.setText(cList.get(position).getUser());
        holder.uPhone.setText(cList.get(position).getPhone());
        holder.uEmail.setText(cList.get(position).getEmail());
        holder.created.setText(gDateDiff(cList.get(position).getCreated()));

        Glide.with(holder.itemView.getContext())
                .load(R.drawable.ic_azyma_light)
                .error(R.drawable.ic_azyma_light)
                .into(holder.cMain);

        if (holder.gCache) {
            Glide.with(holder.itemView.getContext())
                    .load(cList.get(position).getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .error(R.drawable.ic_azyma_light)
                    .into(holder.cIcon);
        } else {
            RequestOptions reqOptions = new RequestOptions()
                    .fitCenter()
                    .override(100, 100);
            Glide.with(holder.itemView.getContext())
                    .asBitmap()
                    .load(cList.get(position).getUrl())
                    .apply(reqOptions)
                    .apply(RequestOptions.circleCropTransform())
                    .error(R.drawable.ic_azyma_light)
                    .into(holder.cIcon);
        }

        String cMiles = cList.get(position).getDistance();
        double hKilo = 0.00;
        if (cMiles.equals("")) {

        } else {
            double yKilo = Double.parseDouble(cMiles);
            double wKilo = (yKilo) / 1000;
            hKilo = Application.round(wKilo, 2);

        }
        String nKilo = hKilo+"";

        holder.cIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nIntent = new Intent(mContext, UserActivity.class);
                Bundle extras = new Bundle();
                extras.putString("Name", cList.get(position).getUser());
                extras.putString("Email", cList.get(position).getEmail());
                extras.putString("Url", cList.get(position).getUrl());
                extras.putString("Phone", cList.get(position).getPhone());
                extras.putString("Signal", nKilo+" KM(s)");
                extras.putString("Verified", "");
                extras.putString("Ratings", "");
                nIntent.putExtras(extras);
                mContext.startActivity(nIntent);
            }
        });

        if (cList.get(holder.getAdapterPosition()).getLiked().equals("0") ||
                cList.get(holder.getAdapterPosition()).getLiked() == null ||
                cList.get(holder.getAdapterPosition()).getLiked().equals("")) {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.hearted)
                    .into(holder.oLike);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.heart)
                    .into(holder.oLike);
        }
        holder.oLiked.setText(cList.get(holder.getAdapterPosition()).getLikes() + " Like(s)");
        holder.oLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                liked(cList.get(holder.getAdapterPosition()).getSerial(),
                        holder.gEmail, holder.gName, cList.get(holder.getAdapterPosition()).getEmail(), mContext,
                        holder.oLike);
            }
        });

        if (cList.get(holder.getAdapterPosition()).getPeeped().equals("0") ||
                cList.get(holder.getAdapterPosition()).getPeeped() == null ||
                cList.get(holder.getAdapterPosition()).getPeeped().equals("")) {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.eye)
                    .into(holder.iViews);

        } else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.eyed)
                    .into(holder.iViews);
        }
        holder.oViewed.setText(cList.get(holder.getAdapterPosition()).getPeeps() + " Peeps(s)");
        holder.iViews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewed(cList.get(holder.getAdapterPosition()).getSerial(),
                        holder.gEmail, holder.gName, cList.get(holder.getAdapterPosition()).getEmail(), mContext,
                        holder.iViews);
            }
        });

        if (holder.gEmail.equals(cList.get(holder.getAdapterPosition()).getEmail())) {
            holder.rShare.setVisibility(View.GONE);
            holder.rSettings.setVisibility(View.VISIBLE);

            Glide.with(mContext).load(R.drawable.settings).into(holder.oSettings);
            holder.oSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showSettingsDialog(cList.get(position).getSerial(), holder.gEmail, mContext,
                            holder.getAdapterPosition(), holder.mRecyclerView);
                }
            });
        } else {
            holder.rShare.setVisibility(View.VISIBLE);
            holder.rSettings.setVisibility(View.GONE);

            Glide.with(mContext).load(R.drawable.share).into(holder.oShare);
            holder.oShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog(cList.get(position).getName(), mContext);
                }
            });
        }

        /* HoldImages */
        String json = cList.get(position).getUploads();
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(json);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                list.add(jsonArray.getString(i));
                JSONObject data = jsonArray.getJSONObject(i);
                ImageHolder md = new ImageHolder();
                md.setSerial(data.getString("iSerial"));
                md.setIdentify(data.getString("iId"));
                md.setUrl(data.getString("iName"));
                holder.mItems.add(md);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        holder.mAdapter.notifyDataSetChanged();
        int mCount = holder.mAdapter.getItemCount();
        if (mCount < 1) {
            holder.cMain.setVisibility(View.VISIBLE);
            holder.mRecyclerView.setVisibility(View.GONE);
        } else {
            holder.cMain.setVisibility(View.GONE);
            holder.mRecyclerView.setVisibility(View.VISIBLE);
        }
        /* HoldImages [Yah Is The Greatest] */


        holder.rMain.setOnClickListener(view -> {
            holder.itemView.performClick();

        });

        holder.rMain.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent nIntent = new Intent(mContext, Images.class);
                Bundle extras = new Bundle();
                extras.putString("Serial", cList.get(holder.getAdapterPosition()).getSerial());
                nIntent.putExtras(extras);
                mContext.startActivity(nIntent);
                return false;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDetailsDialog(holder.mAdapter, holder.mItems, holder.getAdapterPosition(),
                        mContext, cList.get(holder.getLayoutPosition()).getSerial(),
                        cList.get(holder.getAdapterPosition()).getName(),
                        cList.get(holder.getAdapterPosition()).getDescription(),
                        cList.get(holder.getAdapterPosition()).getCategory(),
                        cList.get(holder.getAdapterPosition()).getCurrency(),
                        cList.get(holder.getAdapterPosition()).getCount(),
                        cList.get(holder.getAdapterPosition()).getReviews(),
                        cList.get(holder.getAdapterPosition()).getCreated(),
                        cList.get(holder.getAdapterPosition()).getUser(),
                        cList.get(holder.getAdapterPosition()).getEmail(),
                        cList.get(holder.getAdapterPosition()).getPhone(),
                        cList.get(holder.getAdapterPosition()).getUrl(),
                        cList.get(holder.getAdapterPosition()).getLikes(),
                        cList.get(holder.getAdapterPosition()).getLiked(),
                        cList.get(holder.getAdapterPosition()).getPeeps(),
                        cList.get(holder.getAdapterPosition()).getPeeped(),
                        cList.get(holder.getAdapterPosition()).getRequests(),
                        cList.get(holder.getAdapterPosition()).getRequested(),
                        cList.get(holder.getAdapterPosition()).getUploads());
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });


    }


    private void liked(String oSerial, String uEmail, String uName, String oEmail, Context mContext,
                       AppCompatImageView iLiked) {

        String url = ApiConfig.SYNC_LIKE;
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(mContext, jsonObject.getString("message").toString(), Toast.LENGTH_LONG).show();
                    if (jsonObject.getString("message").equals("Posted!")) {

                        Glide.with(mContext)
                                .load(R.drawable.heart)
                                .into(iLiked);

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public String getBodyContentType() {

                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("oSerial", oSerial);
                params.put("uEmail", uEmail);
                params.put("uName", uName);
                params.put("oEmail", oEmail);
                return params;
            }
        };

        queue.add(request);
    }


    private void viewed(String oSerial, String uEmail, String uName, String oEmail, Context mContext,
                        AppCompatImageView iViewed) {

        String url = ApiConfig.SYNC_PEEPS;
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(mContext, jsonObject.getString("message").toString(), Toast.LENGTH_LONG).show();
                    if (jsonObject.getString("message").equals("Posted!")) {

                        Glide.with(mContext)
                                .load(R.drawable.eyed)
                                .into(iViewed);

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public String getBodyContentType() {

                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("oSerial", oSerial);
                params.put("uEmail", uEmail);
                params.put("uName", uName);
                params.put("oEmail", oEmail);
                return params;
            }
        };

        queue.add(request);
    }

    private void requested(View bsView, String oSerial, String category, String uEmail, String uName,
                           String oEmail, String amount, Context mContext, AppCompatImageView cInfo,
                           AppCompatImageView cInboxed, ProgressBar cProgress) {

        cInfo.setVisibility(View.GONE);
        cProgress.setVisibility(View.VISIBLE);
        cInboxed.setVisibility(View.GONE);

        String url = ApiConfig.SYNC_REQUEST;
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(mContext, jsonObject.getString("message").toString(), Toast.LENGTH_LONG).show();
                    if (jsonObject.getString("message").equals("Posted!")) {

                        cInboxed.setVisibility(View.VISIBLE);
                        cProgress.setVisibility(View.GONE);
                        cInfo.setVisibility(View.GONE);

                        Glide.with(mContext)
                                .load(R.drawable.inbox)
                                .into(cInboxed);

                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ExpandCollapseExtention.expand(bsView.findViewById(R.id.sRequest));
                                ExpandCollapseExtention.collapse(bsView.findViewById(R.id.lRequest));
                            }
                        }, 2500);

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public String getBodyContentType() {

                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("oSerial", oSerial);
                params.put("oCategory", category);
                params.put("uEmail", uEmail);
                params.put("uName", uName);
                params.put("oEmail", oEmail);
                params.put("rAmount", amount);
                return params;
            }
        };

        queue.add(request);
    }

    private void commented(View bsView, String oSerial, String category, String uEmail, String uName,
                           String oEmail, String comment, Context mContext, MaterialButton bPost,
                           TextInputEditText oComment, String offer, String email) {

        bPost.setEnabled(false);
        String url = ApiConfig.SYNC_COMMENTS;
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(mContext, jsonObject.getString("message").toString(), Toast.LENGTH_LONG).show();
                    if (jsonObject.getString("message").equals("Posted!")) {

                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                oComment.clearFocus();
                                oComment.setText("");
                                bPost.setEnabled(true);
                                fComments(bsView, offer, email);
                            }
                        }, 1500);

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public String getBodyContentType() {

                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("oSerial", oSerial);
                params.put("oCategory", category);
                params.put("uEmail", uEmail);
                params.put("uName", uName);
                params.put("oEmail", oEmail);
                params.put("cComment", comment);
                return params;
            }
        };

        queue.add(request);
    }

    private void deleted(String oSerial, String uEmail, Context mContext, int position, RecyclerView mRecycler) {

        String url = ApiConfig.DELETE_OFFER;
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(mContext, jsonObject.getString("message").toString(), Toast.LENGTH_LONG).show();
                    cList.remove(position);
                    Objects.requireNonNull(mRecycler.getAdapter()).notifyItemRemoved(position);
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public String getBodyContentType() {

                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("oSerial", oSerial);
                params.put("uEmail", uEmail);
                return params;
            }
        };

        queue.add(request);

    }

    private void revoke(String oSerial, String uEmail, Context mContext, int position, RecyclerView mRecycler) {

        String url = ApiConfig.DELETE_REQUEST;
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(mContext, jsonObject.getString("message").toString(), Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public String getBodyContentType() {

                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("oSerial", oSerial);
                params.put("uEmail", uEmail);
                return params;
            }
        };

        queue.add(request);

    }

    void showDetailsDialog(RecyclerView.Adapter nAdapter, ArrayList<ImageHolder> nItems,
                           int position, Context mContext, String serial, String name, String description,
                           String category, String currency, String count, String reviews, String created,
                           String user, String email, String phone, String url, String likes, String liked,
                           String peeps, String peeped, String requests, String requested, String uploads) {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        View bsView = LayoutInflater.from(mContext).inflate(R.layout.details, null);
        bottomSheetDialog.setTitle("More | " + name);
        bottomSheetDialog.setContentView(bsView);
        bottomSheetDialog.show();

        SharedPreferences pSettings = mContext.getSharedPreferences("GLOBAL", 0);
        String gEmail = pSettings.getString("gEmail", "");
        String gName = pSettings.getString("gName", "");
        String gUrl = pSettings.getString("gUrlImage", "");
        boolean gCache = pSettings.getBoolean("gCache", false);

        RecyclerView mRecyclerView = bsView.findViewById(R.id.mList);
        nItems = new ArrayList<>();
        RecyclerView.LayoutManager mManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.addItemDecoration(new LinePagerIndicatorDecoration());
        mRecyclerView.setLayoutManager(mManager);
        mManager.setItemPrefetchEnabled(true);
        nAdapter = new NestedAdapter(mContext, nItems);
        mRecyclerView.setAdapter(nAdapter);

        AppCompatImageView gImage = bsView.findViewById(R.id.gImage);
        TextView oSerial = bsView.findViewById(R.id.oSerial);
        TextView cName = bsView.findViewById(R.id.oName);
        TextView cDescription = bsView.findViewById(R.id.oDescription);
        TextView cCategory = bsView.findViewById(R.id.cCategory);
        TextView oCount = bsView.findViewById(R.id.oCount);
        TextView oReviews = bsView.findViewById(R.id.oReviews);
        TextView oCurrency = bsView.findViewById(R.id.oCurrency);
        TextView oCreated = bsView.findViewById(R.id.created);
        TextView oLiked = bsView.findViewById(R.id.oLiked);
        TextView oShared = bsView.findViewById(R.id.oShared);
        TextView oViewed = bsView.findViewById(R.id.oViewed);
        TextView uName = bsView.findViewById(R.id.uName);
        TextView uPhone = bsView.findViewById(R.id.uPhone);
        TextView uEmail = bsView.findViewById(R.id.uEmail);
        AppCompatImageView oLike = bsView.findViewById(R.id.oLike);
        AppCompatImageView oShare = bsView.findViewById(R.id.oShare);
        AppCompatImageView iViews = bsView.findViewById(R.id.iViews);
        AppCompatImageView cIcon = bsView.findViewById(R.id.cIcon);
        AppCompatImageView cMain = bsView.findViewById(R.id.cMain);
        AppCompatImageView cInfo = bsView.findViewById(R.id.cInfo);
        AppCompatImageView cInboxed = bsView.findViewById(R.id.cInboxed);
        AppCompatImageView cRequested = bsView.findViewById(R.id.cRequested);
        AppCompatImageView cOptions = bsView.findViewById(R.id.cOptions);
        ProgressBar cProgress = bsView.findViewById(R.id.cProgress);
        AppCompatButton caRequest = bsView.findViewById(R.id.caRequest);
        TextInputEditText rAmount = bsView.findViewById(R.id.rAmount);
        RatingBar rating = bsView.findViewById(R.id.rating);

        oSerial.setText(serial);
        cName.setText(name);
        cDescription.setText(description);
        cCategory.setText(category);
        //oReviews.setText(reviews + " Reviews(s)");
        oReviews.setText("4.5");
        oCreated.setText(gDateDiff(created));
        oLiked.setText(likes + " Like(s)");
        oViewed.setText(peeps + " Peep(s)");
        uName.setText(user);
        uPhone.setText(phone);
        uEmail.setText(email);

        mRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nIntent = new Intent(mContext, Images.class);
                Bundle extras = new Bundle();
                extras.putString("Serial", serial);
                nIntent.putExtras(extras);
                mContext.startActivity(nIntent);
            }
        });

        LayoutInflater layoutInflater = (LayoutInflater) bsView.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.person, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, 200, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setElevation(3);
        TextView tvProfile = popupView.findViewById(R.id.tvProfile);
        TextView tvChat = popupView.findViewById(R.id.tvChat);
        TextView tvClose = popupView.findViewById(R.id.tvClose);
        TextView tvReview = popupView.findViewById(R.id.tvReview);
        cOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupWindow.isShowing())
                    popupWindow.dismiss();
                else
                    popupWindow.showAsDropDown(cOptions, 50, 0);
                tvChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }); tvClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        popupWindow.dismiss();
                    }
                });

            }
        });


        Glide.with(mContext)
                .load(gUrl)
                .apply(RequestOptions.circleCropTransform())
                .error(R.drawable.ic_azyma_light)
                .into(gImage);

        Glide.with(mContext)
                .load(R.drawable.ic_azyma_light)
                .error(R.drawable.ic_azyma_light)
                .into(cMain);

        Glide.with(mContext)
                .load(R.drawable.inbox)
                .error(R.drawable.inbox)
                .into(cRequested);

        if (gCache) {
            Glide.with(mContext)
                    .load(url)
                    .apply(RequestOptions.circleCropTransform())
                    .error(R.drawable.ic_azyma_light)
                    .into(cIcon);
        } else {
            RequestOptions reqOptions = new RequestOptions()
                    .fitCenter()
                    .override(100, 100);
            Glide.with(mContext)
                    .asBitmap()
                    .load(cList.get(position).getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .apply(reqOptions)
                    .error(R.drawable.ic_azyma_light)
                    .into(cIcon);
        }

        if (liked.equals("0") || liked.equals("")) {
            Glide.with(mContext)
                    .load(R.drawable.hearted)
                    .into(oLike);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.heart)
                    .into(oLike);
        }
        if (peeped.equals("0") || peeped.equals("")) {
            Glide.with(mContext)
                    .load(R.drawable.eye)
                    .into(iViews);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.eyed)
                    .into(iViews);
        }//Peeps+LikedUI Stuff

        if (requested.equals("0") || requested.equals("")) {
            bsView.findViewById(R.id.llRequest).setVisibility(View.VISIBLE);
            bsView.findViewById(R.id.llRequested).setVisibility(View.GONE);
        } else {
            bsView.findViewById(R.id.llRequest).setVisibility(View.GONE);
            bsView.findViewById(R.id.llRequested).setVisibility(View.VISIBLE);
        }//RequestUI Stuff

        Glide.with(mContext)
                .load(R.drawable.share)
                .into(oShare);

        oShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/*");
                intent.putExtra(Intent.EXTRA_TEXT, "Sharing " + name);
                mContext.startActivity(intent);
            }
        });

        oLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                liked(serial, gEmail, gName, email, mContext, oLike);
            }
        });
        iViews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewed(serial, gEmail, gName, email, mContext, iViews);
            }
        });


        if (count.equals("0")) {
            oCount.setText("Freebie!");
        } else {
            oCount.setText(Application.getFormatedNumber(cList.get(position).getCount()));
        }

        if (currency.length() < 1) {
            currency = "KSH";
            oCurrency.setText(currency);
        } else {
            oCurrency.setText(currency);
        }

        //...Ratings + Review
        rating.setRating(1);

        /* HoldImages */
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(uploads);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                list.add(jsonArray.getString(i));
                JSONObject data = jsonArray.getJSONObject(i);
                ImageHolder md = new ImageHolder();
                md.setSerial(data.getString("iSerial"));
                md.setIdentify(data.getString("iId"));
                md.setUrl(data.getString("iName"));
                nItems.add(md);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        nAdapter.notifyDataSetChanged();
        int mCount = nAdapter.getItemCount();
        if (mCount < 1) {
            cMain.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            cMain.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
        /* HoldImages [Yah Is The Greatest] */

        AppCompatButton bDismiss = bsView.findViewById(R.id.bDismiss);
        MaterialButton bPost = bsView.findViewById(R.id.bPost);
        AppCompatButton sRequest = bsView.findViewById(R.id.sRequest);
        AppCompatButton clRequest = bsView.findViewById(R.id.clRequest);
        AppCompatButton cRequest = bsView.findViewById(R.id.cRequest);
        TextInputEditText oComment = bsView.findViewById(R.id.oComment);
        bsView.findViewById(R.id.sText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bsView.findViewById(R.id.sOffer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/*");
                intent.putExtra(Intent.EXTRA_TEXT, "Sharing " + name);
                mContext.startActivity(intent);
            }
        });

        bPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                commented(bsView, serial, category, gEmail, gName, email, oComment.getText().toString().trim(),
                        mContext, bPost, oComment, serial, email);

            }
        });

        boolean isCollapsed = false;
        boolean isConfirmed = false;
        sRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExpandCollapseExtention.expand(bsView.findViewById(R.id.lRequest));
                ExpandCollapseExtention.collapse(bsView.findViewById(R.id.sRequest));
            }
        });
        clRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExpandCollapseExtention.expand(bsView.findViewById(R.id.sRequest));
                ExpandCollapseExtention.collapse(bsView.findViewById(R.id.lRequest));
            }
        });

        cRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rAmount.getText().toString().equals("")) {
                    rAmount.setError("Field Required!");
                } else {
                    requested(bsView, serial, category, gEmail, gName, email, rAmount.getText().toString(), mContext, cInfo,
                            cInboxed, cProgress);
                }
            }
        });

        bDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        caRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                revoke(serial, gEmail, mContext, position, mRecyclerView);
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });

        fRequests(bsView, serial, email);
        fComments(bsView, serial, email);
    }


    void showSettingsDialog(String serial, String gEmail, Context mContext, int position, RecyclerView mRecycler) {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        View bsView = LayoutInflater.from(mContext).inflate(R.layout.edit, null);
        bottomSheetDialog.setTitle("More | " + serial);
        bottomSheetDialog.setContentView(bsView);
        bottomSheetDialog.show();
        bsView.findViewById(R.id.dOffer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        bsView.findViewById(R.id.dOffer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleted(serial, gEmail, mContext, position, mRecycler);
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });
    }

    void showDialog(String content, Context mContext) {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        View bsView = LayoutInflater.from(mContext).inflate(R.layout.share, null);
        bottomSheetDialog.setTitle("More | " + content);
        bottomSheetDialog.setContentView(bsView);
        bottomSheetDialog.show();
        bsView.findViewById(R.id.sText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bsView.findViewById(R.id.sOffer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/*");
                intent.putExtra(Intent.EXTRA_TEXT, "Sharing " + content);
                mContext.startActivity(intent);
            }
        });
        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
            }
        });

    }


    public void fRequests(View bsView, String offer, String email) {
        RequestQueue requestQueue;
        int requestCount = 1;
        RecyclerView cRecyclerView;
        RecyclerView.Adapter cAdapter;
        RecyclerView.LayoutManager nLayoutManager;
        ArrayList<RequestHolder> clHolder;
        cRecyclerView = bsView.findViewById(R.id.rList);
        nLayoutManager = new LinearLayoutManager(bsView.getContext());
        cRecyclerView.setLayoutManager(nLayoutManager);
        cRecyclerView.setNestedScrollingEnabled(false);
        clHolder = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(bsView.getContext());
        cAdapter = new RequestAdapter(bsView.getContext(), clHolder);
        cRecyclerView.setAdapter(cAdapter);

        if (!checkConnection(bsView.getContext())) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, 2500);
        } else {
            HttpsTrustManager.allowAllSSL();
            getRequests(cAdapter, clHolder, requestQueue, requestCount, bsView, offer, email);

        }

    }

    private JsonArrayRequest gRequestsFromServer(RecyclerView.Adapter cAdapter, ArrayList<RequestHolder> clHolder,
                                                 int requestCount, View bsView, String offer, String email) {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(ApiConfig.REQUESTS_URL + String.valueOf(requestCount) + "&offer=" + offer + "&user=" + email,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        if (response.toString().equals("[]")) {
                            bsView.findViewById(R.id.rError).setVisibility(View.VISIBLE);
                        } else {
                            parseData(cAdapter, clHolder, response);
                            bsView.findViewById(R.id.rError).setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.toString().equals("com.android.volley.TimeoutError")) {
                            bsView.findViewById(R.id.rError).setVisibility(View.VISIBLE);
                            Log.e(ApiConfig.AZYMA_APP, error.toString());
                        } else if (error.toString().contains("com.android.volley.ParseError: org.json.JSONException: Value over of type java.lang.String cannot be converted to JSONArray")) {
                            bsView.findViewById(R.id.rError).setVisibility(View.VISIBLE);
                            Log.e(ApiConfig.AZYMA_APP, error.toString());
                        } else if (error.toString().contains("com.android.volley.ParseError: org.json.JSONException: End of input at character 0 of")) {
                            bsView.findViewById(R.id.rError).setVisibility(View.VISIBLE);
                            Log.e(ApiConfig.AZYMA_APP, error.toString());
                        } else {
                            bsView.findViewById(R.id.rError).setVisibility(View.VISIBLE);
                            Log.e(ApiConfig.AZYMA_APP, error.toString());
                        }

                    }
                });

        return jsonArrayRequest;
    }

    private void getRequests(RecyclerView.Adapter cAdapter, ArrayList<RequestHolder> clHolder,
                             RequestQueue requestQueue, int requestCount, View bsView,
                             String offer, String email) {
        requestQueue.add(gRequestsFromServer(cAdapter, clHolder, requestCount, bsView, offer, email));
        requestCount++;
    }

    private void parseData(RecyclerView.Adapter rAdapter, ArrayList<RequestHolder> clHolder, JSONArray array) {
        for (int i = 0; i < array.length(); i++) {

            RequestHolder rHolder = new RequestHolder();
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);

                rHolder.setId(json.getString(ApiConfig.REQ_ID));
                rHolder.setSerial(json.getString(ApiConfig.REQ_SERIAL));
                rHolder.setOffer(json.getString(ApiConfig.REQ_OID));
                rHolder.setAmount(json.getString(ApiConfig.REQ_AMOUNT));
                rHolder.setComment(json.getString(ApiConfig.REQ_COMMENT));
                rHolder.setCategory(json.getString(ApiConfig.REQ_CATE));
                rHolder.setUser(json.getString(ApiConfig.OFF_USER));
                rHolder.setEmail(json.getString(ApiConfig.OFF_EMAIL));
                rHolder.setUrl(json.getString(ApiConfig.REQ_URL));
                rHolder.setStatus(json.getString(ApiConfig.REQ_STATUS));
                rHolder.setCreated(json.getString(ApiConfig.CREATED));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            clHolder.add(rHolder);
        }

        rAdapter.notifyDataSetChanged();
    }


    public void fComments(View bsView, String offer, String email) {
        RequestQueue commentQueue;
        int commentCount = 1;
        RecyclerView cRecyclerView;
        RecyclerView.Adapter cAdapter;
        RecyclerView.LayoutManager nLayoutManager;
        ArrayList<CommentHolder> clHolder;
        cRecyclerView = bsView.findViewById(R.id.cList);
        cRecyclerView.invalidate();
        nLayoutManager = new LinearLayoutManager(bsView.getContext());
        cRecyclerView.setLayoutManager(nLayoutManager);
        cRecyclerView.setNestedScrollingEnabled(false);
        clHolder = new ArrayList<>();
        commentQueue = Volley.newRequestQueue(bsView.getContext());
        cAdapter = new CommentAdapter(bsView.getContext(), clHolder);
        cRecyclerView.setAdapter(cAdapter);

        if (!checkConnection(bsView.getContext())) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, 2500);
        } else {
            HttpsTrustManager.allowAllSSL();
            getComments(cAdapter, clHolder, commentQueue, commentCount, bsView, offer, email);

        }

    }

    private JsonArrayRequest gCommentsFromServer(RecyclerView.Adapter cAdapter, ArrayList<CommentHolder> clHolder,
                                                 int commentsCount, View bsView, String offer, String email) {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(ApiConfig.COMMENTS_URL + String.valueOf(commentsCount) + "&offer=" + offer + "&user=" + email,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        if (response.toString().equals("[]")) {
                            bsView.findViewById(R.id.cError).setVisibility(View.VISIBLE);
                        } else {
                            parseComments(cAdapter, clHolder, response);
                            bsView.findViewById(R.id.cError).setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.toString().equals("com.android.volley.TimeoutError")) {
                            bsView.findViewById(R.id.cError).setVisibility(View.VISIBLE);
                            Log.e(ApiConfig.AZYMA_APP, error.toString());
                        } else if (error.toString().contains("com.android.volley.ParseError: org.json.JSONException: Value over of type java.lang.String cannot be converted to JSONArray")) {
                            bsView.findViewById(R.id.cError).setVisibility(View.VISIBLE);
                            Log.e(ApiConfig.AZYMA_APP, error.toString());
                        } else if (error.toString().contains("com.android.volley.ParseError: org.json.JSONException: End of input at character 0 of")) {
                            bsView.findViewById(R.id.cError).setVisibility(View.VISIBLE);
                            Log.e(ApiConfig.AZYMA_APP, error.toString());
                        } else {
                            bsView.findViewById(R.id.cError).setVisibility(View.VISIBLE);
                            Log.e(ApiConfig.AZYMA_APP, error.toString());
                        }

                    }
                });

        return jsonArrayRequest;
    }

    private void getComments(RecyclerView.Adapter cAdapter, ArrayList<CommentHolder> clHolder,
                             RequestQueue commentsQueue, int commentsCount, View bsView,
                             String offer, String email) {
        commentsQueue.add(gCommentsFromServer(cAdapter, clHolder, commentsCount, bsView, offer, email));
        commentsCount++;
    }

    private void parseComments(RecyclerView.Adapter cAdapter, ArrayList<CommentHolder> clHolder, JSONArray array) {
        for (int i = 0; i < array.length(); i++) {

            CommentHolder cHolder = new CommentHolder();
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);

                cHolder.setId(json.getString(ApiConfig.COM_ID));
                cHolder.setSerial(json.getString(ApiConfig.REQ_SERIAL));
                cHolder.setOffer(json.getString(ApiConfig.REQ_OID));
                cHolder.setAmount(json.getString(ApiConfig.COM_AMOUNT));
                cHolder.setComment(json.getString(ApiConfig.COM_COMMENT));
                cHolder.setCategory(json.getString(ApiConfig.REQ_CATE));
                cHolder.setUser(json.getString(ApiConfig.OFF_USER));
                cHolder.setEmail(json.getString(ApiConfig.OFF_EMAIL));
                cHolder.setUrl(json.getString(ApiConfig.REQ_URL));
                cHolder.setStatus(json.getString(ApiConfig.REQ_STATUS));
                cHolder.setCreated(json.getString(ApiConfig.CREATED));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            clHolder.add(cHolder);
        }

        cAdapter.notifyDataSetChanged();
    }

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }

    public String gDateDiff(String oDate) {
        int day = 0;
        int hh = 0;
        int mm = 0;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
            Date olDate = sdf.parse(oDate);
            Date cDate = new Date();
            Long tDiff = cDate.getTime() - olDate.getTime();
            day = (int) TimeUnit.MILLISECONDS.toDays(tDiff);
            hh = (int) (TimeUnit.MILLISECONDS.toHours(tDiff) - TimeUnit.DAYS.toHours(day));
            mm = (int) (TimeUnit.MILLISECONDS.toMinutes(tDiff) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(tDiff)));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (day == 0) {
            return hh + "H";
        } else if (hh == 0) {
            return mm + "M";
        } else {
            return day + "D";
        }
    }

    public String gDateDiffDetails(String oDate) {
        int day = 0;
        int hh = 0;
        int mm = 0;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
            Date olDate = sdf.parse(oDate);
            Date cDate = new Date();
            Long tDiff = cDate.getTime() - olDate.getTime();
            day = (int) TimeUnit.MILLISECONDS.toDays(tDiff);
            hh = (int) (TimeUnit.MILLISECONDS.toHours(tDiff) - TimeUnit.DAYS.toHours(day));
            mm = (int) (TimeUnit.MILLISECONDS.toMinutes(tDiff) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(tDiff)));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (day == 0) {
            return hh + "H " + mm + "M";
        } else if (hh == 0) {
            return mm + "M";
        } else {
            return day + "D " + hh + "H " + mm + "M";
        }
    }

    @Override
    public int getItemCount() {
        return cList.size();
    }
}
