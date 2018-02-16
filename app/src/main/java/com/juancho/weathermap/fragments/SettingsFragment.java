package com.juancho.weathermap.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.juancho.weathermap.R;
import com.juancho.weathermap.activities.MainActivity;
import com.juancho.weathermap.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment implements RadioGroup.OnCheckedChangeListener{

    private View rootView;
    private RadioGroup unitsGroup;
    private RadioButton unitsMetric;
    private RadioButton unitsImperial;

    private SharedPreferences settings;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        unitsGroup = rootView.findViewById(R.id.unitsGroup);
        unitsGroup.setOnCheckedChangeListener(this);
        unitsMetric = rootView.findViewById(R.id.unitsMetric);
        unitsImperial = rootView.findViewById(R.id.unitsImperial);

        setSavedSettings();

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        SharedPreferences.Editor editor = settings.edit();
        if(unitsMetric.isChecked()) {
            editor.putString("units", "metric");
            Utils.setUnits(((MainActivity)getActivity()).getRealm(),"metric");
        }else{
            editor.putString("units", "imperial");
            Utils.setUnits(((MainActivity)getActivity()).getRealm(),"imperial");
        }
        editor.apply();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int id) {
    }

    private void setSavedSettings(){
        settings = ((MainActivity)getActivity()).getSettings();
        String units = settings.getString("units", "");
        if(units.equals("metric")) unitsMetric.setChecked(true);
        else if(units.equals("imperial")) unitsImperial.setChecked(true);
    }
}
