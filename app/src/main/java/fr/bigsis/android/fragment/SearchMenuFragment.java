package fr.bigsis.android.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import fr.bigsis.android.R;
import fr.bigsis.android.viewModel.SearchMenuViewModel;

public class SearchMenuFragment extends Fragment {
    private SearchMenuViewModel viewModel;
    private OnFragmentInteractionListener mListener;
    private EditText etSearchFrom;

    public SearchMenuFragment() {
        // Required empty public constructor
    }

    public static SearchMenuFragment newInstance() {
        SearchMenuFragment fragment = new SearchMenuFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_search_menu, container, false);
        etSearchFrom = view.findViewById(R.id.etSearchFrom);
        viewModel = ViewModelProviders.of(getActivity()).get(SearchMenuViewModel.class);
        if(!etSearchFrom.getText().toString().isEmpty()) {
            viewModel.setText(etSearchFrom.getText().toString());
            sendDaats(etSearchFrom.toString());
        }
        return view;
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void sendDaats(String fromLocation) {
        if (mListener != null) {
            mListener.onFragmentInteraction(fromLocation);
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
        // TODO: Update argument type and name
        void onFragmentInteraction(String fromLocation);
    }
}
