package fr.bigsis.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import fr.bigsis.android.R;
import fr.bigsis.android.activity.TripListActivity;
import fr.bigsis.android.viewModel.SearchMenuViewModel;

public class SearchMenuFragment extends Fragment {
    private SearchMenuViewModel viewModel;
    private OnFragmentInteractionListener mListener;
    private EditText etSearchFrom;
    private Button bt;

    public SearchMenuFragment() {
        // Required empty public constructor
    }

    public static SearchMenuFragment newInstance() {
        SearchMenuFragment fragment = new SearchMenuFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(SearchMenuViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_menu, container, false);
        bt = view.findViewById(R.id.btAdd);
        etSearchFrom = view.findViewById(R.id.etSearchFrom);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

       // etSearchFrom.setText(viewModel.getDepartureName());
       /* etSearchFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setDepartureName(s.toString());
            }
        });*/

        SearchMenuViewModel viewModel = ViewModelProviders.of(this).get(SearchMenuViewModel.class);
        viewModel.setDepartureName(etSearchFrom.getText().toString());
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TripListActivity.class);
                startActivity(intent);
            }
        });

    }
    public void sendDatas() {
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
