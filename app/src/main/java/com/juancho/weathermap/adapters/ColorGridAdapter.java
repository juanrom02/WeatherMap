package com.juancho.weathermap.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;

import com.juancho.weathermap.R;

import java.util.List;

/**
 * Created by Juancho on 02/08/18.
 */

public class ColorGridAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<Float> colorList;

    public ColorGridAdapter(Context context, int layout, List<Float> colorList) {
        this.context = context;
        this.layout = layout;
        this.colorList = colorList;
    }

    @Override
    public int getCount() {
        return this.colorList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.colorList.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder holder;

        if(convertView == null){

            LayoutInflater layoutInflater = LayoutInflater.from(this.context);
            convertView = layoutInflater.inflate(this.layout, null);

            holder = new ViewHolder();
            holder.color = (ImageButton) convertView.findViewById(R.id.colorButton);

            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        float currentColor = colorList.get(position);
        GradientDrawable circleShape = (GradientDrawable) holder.color.getDrawable();
        circleShape.setColor(Color.HSVToColor(new float[]{currentColor, 1F, 1F}));
        circleShape.setStroke(4, Color.HSVToColor(new float[]{currentColor, 1F, 0.5F}));

        return convertView;
    }

    static class ViewHolder{
        private ImageButton color;
    }
}
