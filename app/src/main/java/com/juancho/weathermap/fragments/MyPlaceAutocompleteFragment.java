package com.juancho.weathermap.fragments;


import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLngBounds;
import com.juancho.weathermap.R;

//Obtenido de
//https://sunny89blog.wordpress.com/2016/02/07/placeautocompletefragment-with-custom-ui/
public class MyPlaceAutocompleteFragment extends PlaceAutocompleteFragment implements Button.OnClickListener{

    private EditText autocompletePlace;
    private TextView appName;
    private ImageButton searchPlaceButton;
    private ImageButton closeSearch;

    private View zzaRh;
    @Nullable
    private LatLngBounds latLngBounds;
    @Nullable
    private AutocompleteFilter autocompleteFilter;
    @Nullable
    private PlaceSelectionListener placeSelListener;


    public MyPlaceAutocompleteFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_my_place_autocomplete, container, false);

        autocompletePlace = rootView.findViewById(R.id.autocompletePlace);
        appName = rootView.findViewById(R.id.appName);
        searchPlaceButton = rootView.findViewById(R.id.searchPlaceButton);
        closeSearch = rootView.findViewById(R.id.closeSearch);

        autocompletePlace.setOnClickListener(this);
        searchPlaceButton.setOnClickListener(this);
        closeSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                autocompletePlace.setVisibility(View.INVISIBLE);
                closeSearch.setVisibility(View.INVISIBLE);
                searchPlaceButton.setVisibility(View.VISIBLE);
                appName.setVisibility(View.VISIBLE);
                setText("");
            }
        });

        return rootView;
    }

    public void onDestroyView() {
        this.zzaRh = null;
        //this.autocompletePlace = null;
        super.onDestroyView();
    }

    public void setBoundsBias(@Nullable LatLngBounds bounds) {
        this.latLngBounds = bounds;
    }

    public void setFilter(@Nullable AutocompleteFilter filter) {
        this.autocompleteFilter = filter;
    }

    public void setOnPlaceSelectedListener(PlaceSelectionListener listener) {
        this.placeSelListener = listener;
    }

    public void setText(CharSequence text) {
        this.autocompletePlace.setText(text);
    }

    public void setHint(CharSequence hint) {
        this.autocompletePlace.setHint(hint);
        this.zzaRh.setContentDescription(hint);
    }

    @Override
    public void onClick(View view) {
        int resultCode = -1;

        try {
            int viewId = view.getId();
            PlaceAutocomplete.IntentBuilder intentBuilder =
                    (new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN))
                    .setBoundsBias(this.latLngBounds)
                    .setFilter(this.autocompleteFilter)
                    .zzdz(1);
            if (viewId == R.id.autocompletePlace) {
                intentBuilder.zzim(this.autocompletePlace.getText().toString());
            }
            Intent intent = intentBuilder.build(this.getActivity());

            this.startActivityForResult(intent, 1);
        } catch (GooglePlayServicesRepairableException e) {
            resultCode = e.getConnectionStatusCode();
            //Log.e("Places", "Could not open autocomplete activity", var3);
        } catch (GooglePlayServicesNotAvailableException e) {
            resultCode = e.errorCode;
            //Log.e("Places", "Could not open autocomplete activity", var4);
        }

        if (resultCode != -1) {
            GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
            apiAvailability.showErrorDialogFragment(this.getActivity(), resultCode, 2);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == -1) {
                Place place = PlaceAutocomplete.getPlace(this.getActivity(), data);
                if (this.placeSelListener != null) {
                    searchPlaceButton.setVisibility(View.INVISIBLE);
                    appName.setVisibility(View.INVISIBLE);
                    autocompletePlace.setVisibility(View.VISIBLE);
                    closeSearch.setVisibility(View.VISIBLE);
                    this.placeSelListener.onPlaceSelected(place);
                }

                this.setText(place.getName().toString());
            } else if (resultCode == 2) {
                Status status = PlaceAutocomplete.getStatus(this.getActivity(), data);
                if (this.placeSelListener != null) {
                    this.placeSelListener.onError(status);
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
