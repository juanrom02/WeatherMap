package com.juancho.weathermap.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.VectorDrawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.juancho.weathermap.R;
import com.juancho.weathermap.models.MapMarker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Juancho on 02/08/18.
 */

public class ColorGridAdapter extends ArrayAdapter<Float> {

    private Context context;
    private int layout;
    private List<Float> colorList;
    private ImageView colorButton;
    private OnItemClickListener onItemClickListener;

    public ColorGridAdapter(Context context, int layout, List<Float> colorList,
                            OnItemClickListener onItemClickListener) {
        super(context, layout, colorList);
        this.context = context;
        this.layout = layout;
        this.colorList = colorList;
        this.onItemClickListener = onItemClickListener;

    }

    @Override
    public int getCount() {
        return this.colorList.size();
    }

    @Override
    public Float getItem(int position) {
        return this.colorList.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        if(convertView == null){
            LayoutInflater layoutInflater = LayoutInflater.from(this.context);
            convertView = layoutInflater.inflate(R.layout.color_grid_item, null);

            colorButton = convertView.findViewById(R.id.colorButton);
            colorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onColorClick(position);
                    //setChecked(position);
                }
            });
        }

        float currentColor = colorList.get(position);

        GradientDrawable circleShape = (GradientDrawable) colorButton.getDrawable();
        circleShape.setColor(Color.HSVToColor(new float[]{currentColor, 1F, 1F}));
        circleShape.setStroke(30, Color.HSVToColor(new float[]{currentColor, 1F, 0.5F}));
        colorButton.setImageDrawable(circleShape);

        return convertView;
    }

    public interface OnItemClickListener{
        void onColorClick(int position);
    }

/*    private void setChecked(int position){
        if (this.isChecked.contains(true)){
            for(int i=0; i < this.isChecked.size(); i++){
                if(this.isChecked.get(i)){
                    this.isChecked.set(i, false);
                    break;
                }
            }
        }
        this.isChecked.set(position, true);
        colorCheckMark.setImageAlpha(255);
    }*/

}
