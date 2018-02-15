package com.juancho.weathermap.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.juancho.weathermap.R;
import com.juancho.weathermap.activities.MainActivity;
import com.juancho.weathermap.adapters.CitiesAdapter;
import com.juancho.weathermap.models.MapMarker;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class CitiesFragment extends Fragment implements RealmChangeListener<RealmResults<MapMarker>> {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private CitiesAdapter citiesAdapter;
    private MainActivity mainActivity;

    private RealmResults<MapMarker> mapMarkers;

    public CitiesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cities, container, false);
        mainActivity = (MainActivity) getActivity();

        mapMarkers = mainActivity.getMapMarkers();
        mapMarkers.addChangeListener(this);

        citiesAdapter = new CitiesAdapter(R.layout.cities_item, mapMarkers, mainActivity.getOnPinClickListener());
        recyclerView = rootView.findViewById(R.id.citiesRecycler);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(citiesAdapter);
        recyclerView.setLayoutManager(layoutManager);

        return rootView;
    }

    @Override
    public void onChange(RealmResults<MapMarker> mapMarkers) {
        citiesAdapter.notifyDataSetChanged();
    }
}
