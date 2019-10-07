package fr.bigsis.android.fragment;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import fr.bigsis.android.R;
import fr.bigsis.android.activity.TripListActivity;
import fr.bigsis.android.helpers.KeyboardHelper;
import fr.bigsis.android.viewModel.SearchContactViewModel;


public class SearchContactFragment extends Fragment {

    private SearchContactViewModel viewModel;
    private OnFragmentInteractionContact mListener;

    private EditText etSearchContact;


    public SearchContactFragment() {
        // Required empty public constructor
    }
    public static SearchContactFragment newInstance() {
        SearchContactFragment fragment = new SearchContactFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(SearchContactViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_search_contact, container, false); ;
        etSearchContact = view.findViewById(R.id.etSearchContact);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        etSearchContact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setContact(s.toString());
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionContact) {
            mListener = (OnFragmentInteractionContact) context;
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

    public interface OnFragmentInteractionContact {
        void onFragmentInteractionContact();
    }

}
