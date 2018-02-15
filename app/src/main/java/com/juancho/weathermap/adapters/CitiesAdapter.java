package com.juancho.weathermap.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.juancho.weathermap.R;
import com.juancho.weathermap.models.City;
import com.juancho.weathermap.models.MapMarker;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Juancho on 02/14/18.
 */

public class CitiesAdapter extends RecyclerView.Adapter<CitiesAdapter.ViewHolder> {

    private Context context;
    private int layout;
    private List<MapMarker> mapMarkers;
    private OnPinClickListener pinClickListener;

    public CitiesAdapter(int layout, List<MapMarker> mapMarkers, OnPinClickListener pinClickListener){
        this.layout = layout;
        this.mapMarkers = mapMarkers;
        this.pinClickListener = pinClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(mapMarkers.get(position));
    }

    @Override
    public int getItemCount() {
        return mapMarkers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView citiesPin;
        public TextView citiesName;

        public ViewHolder(View itemView){
            super(itemView);
            citiesPin = itemView.findViewById(R.id.citiesPin);
            citiesName = itemView.findViewById(R.id.citiesName);
        }

        public void bind(final MapMarker mapMarker){
            citiesName.setText(mapMarker.getCity().getName() + ", " + mapMarker.getCity().getCountry());
            citiesPin.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    pinClickListener.onPinClick(mapMarker);
                }
            });
        }
    }

    public interface OnPinClickListener{
        void onPinClick(MapMarker marker);
    }
}
