package co.ke.daletsys.azyma.control;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import co.ke.daletsys.azyma.R;

public class CustomMarkerView extends FrameLayout {
    private ImageView mImage;
    private TextView mTitle;

    public CustomMarkerView(ViewGroup root, String text, boolean isSelected) {
        super(root.getContext());
        init(root.getContext(), text, isSelected);
    }

    private void init(Context context, String text, boolean isSelected) {
        View.inflate(context, R.layout.marker, this);
        //mImage = findViewById(R.id.mImage);
        mTitle = findViewById(R.id.mTitle);
        measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mTitle.setText(text);
        if (isSelected) {
            //mImage.setImageResource(R.drawable.radio_flat_selected);
        } else {
            //mImage.setImageResource(R.drawable.radio_flat_regular);
        }
    }

    public static BitmapDescriptor getMarkerIcon(ViewGroup root, String text, boolean isSelected) {
        CustomMarkerView markerView = new CustomMarkerView(root, text, isSelected);
        markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        markerView.layout(0, 0, markerView.getMeasuredWidth(), markerView.getMeasuredHeight());

        markerView.setDrawingCacheEnabled(true);
        markerView.invalidate();
        markerView.buildDrawingCache();

        Bitmap bitmap = Bitmap.createBitmap(markerView.getDrawingCache());
        markerView.setDrawingCacheEnabled(false);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
