package fr.bigsis.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import fr.bigsis.android.R;
import fr.bigsis.android.viewModel.MenuFilterViewModel;

public class MenuFilterFragment extends Fragment {

    private MenuFilterViewModel viewModel;
    private OnFragmentInteractionListener mListener;
    private ImageButton imageButtonpartner;
    private ImageButton imageButtonevent;
    private ImageButton imageButtonTrip;

    public MenuFilterFragment() {
    }

    public static MenuFilterFragment newInstance() {
        MenuFilterFragment fragment = new MenuFilterFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(MenuFilterViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_filter, container, false);
        imageButtonpartner = view.findViewById(R.id.imageButtonpartner);
        imageButtonevent = view.findViewById(R.id.imageButtonevent);
        imageButtonTrip = view.findViewById(R.id.imageButtonTrip);

        imageButtonpartner.setBackgroundResource(R.drawable.ic_partner_selected);
        imageButtonTrip.setBackgroundResource(R.drawable.ic_trip_route);
        imageButtonevent.setBackgroundResource(R.drawable.ic_event);

        imageButtonevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButtonevent.setBackgroundResource(R.drawable.ic_event_selected);
                imageButtonTrip.setBackgroundResource(R.drawable.ic_trip_route);
                imageButtonpartner.setBackgroundResource(R.drawable.ic_partner);
                viewModel.setfilterName("event");
            }
        });

        imageButtonTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButtonTrip.setBackgroundResource(R.drawable.ic_trip_selected);
                imageButtonpartner.setBackgroundResource(R.drawable.ic_partner);
                imageButtonevent.setBackgroundResource(R.drawable.ic_event);
                viewModel.setfilterName("trip");
            }
        });

        imageButtonpartner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButtonTrip.setBackgroundResource(R.drawable.ic_trip_route);
                imageButtonpartner.setBackgroundResource(R.drawable.ic_partner_selected);
                imageButtonevent.setBackgroundResource(R.drawable.ic_event);
                viewModel.setfilterName("partner");
            }
        });
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
}
