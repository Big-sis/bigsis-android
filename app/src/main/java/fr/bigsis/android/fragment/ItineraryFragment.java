package fr.bigsis.android.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;

import java.util.Locale;

import fr.bigsis.android.R;
import fr.bigsis.android.activity.MapsActivity;
import fr.bigsis.android.constant.Constant;
import fr.bigsis.android.viewModel.AlertLocateViewModel;
import fr.bigsis.android.viewModel.ItineraryViewModel;

import static fr.bigsis.android.constant.Constant.REQUEST_CODE_FROM_AUTOCOMPLETE;
import static fr.bigsis.android.constant.Constant.REQUEST_CODE_ITINERARY_AUTOCOMPLETE;

public class ItineraryFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    TextView tvAdressItinerary, tvPosition;
    double latDestination;
    double lngDestination;
    ItineraryViewModel itineraryViewModel;

    public ItineraryFragment() {
        // Required empty public constructor
    }

    public static ItineraryFragment newInstance() {
        ItineraryFragment fragment = new ItineraryFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_itinerary, container, false);
        tvPosition = view.findViewById(R.id.tvPosition);
        tvAdressItinerary = view.findViewById(R.id.tvAdressItinerary);
        tvAdressItinerary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suggestPlaces(REQUEST_CODE_ITINERARY_AUTOCOMPLETE);

            }
        });
   if(latDestination != 0) {

   }
        return view;
    }

    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }

    public void suggestPlaces(int code){
        Intent intent = new PlaceAutocomplete.IntentBuilder()
                .accessToken(Constant.MAPBOX_ACCESS_TOKEN)
                .placeOptions(PlaceOptions.builder()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .toolbarColor(getResources().getColor(R.color.colorAccent))
                        .limit(10)
                        .country(Locale.FRANCE)
                        .geocodingTypes()
                        .build(PlaceOptions.MODE_CARDS))
                .build(getActivity());
        startActivityForResult(intent, code);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_ITINERARY_AUTOCOMPLETE) {
            CarmenFeature feature = PlaceAutocomplete.getPlace(data);
            tvAdressItinerary.setText(feature.text());
            itineraryViewModel = ViewModelProviders.of(getActivity()).get(ItineraryViewModel.class);
            latDestination = ((Point) feature.geometry()).latitude();
            lngDestination = ((Point) feature.geometry()).longitude();

            itineraryViewModel = ViewModelProviders.of(getActivity()).get(ItineraryViewModel.class);
            itineraryViewModel.setLongitudeItinerary(lngDestination);
            itineraryViewModel.setLatitudeItinerary(latDestination);
        }
    }
}
