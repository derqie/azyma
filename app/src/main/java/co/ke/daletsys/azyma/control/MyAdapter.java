package co.ke.daletsys.azyma.control;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import co.ke.daletsys.azyma.R;

public class MyAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Uri> arrayList;

    public MyAdapter(Context context, ArrayList<Uri> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        if (mInflater != null) {
            convertView = mInflater.inflate(R.layout.list_items, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.imageView);
        TextView imagePath = convertView.findViewById(R.id.imagePath);
        ImageView cImage = convertView.findViewById(R.id.cImage);

        //imagePath.setText(FileUtils.getPath(context, arrayList.get(position)));
        imagePath.setText("..."+FileUtils.getPath(context, arrayList.get(position)).substring(FileUtils.getPath(context, arrayList.get(position)).length()-6));

        Glide.with(context)
                .load(arrayList.get(position))
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);

        cImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayList.remove(arrayList.get(position));
                MyAdapter.this.notifyDataSetChanged();
            }
        });

        return convertView;
    }


}
