package fr.bigsis.android.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import fr.bigsis.android.R;
import fr.bigsis.android.viewModel.SearchMenuViewModel;

public class SearchMenuFragment extends Fragment {
    private SearchMenuViewModel viewModel;

    private OnFragmentInteractionListener mListener;
    private EditText etSearchFrom;
   // private FloatingActionButton fbtClose;
    private Button searchTrip;
    private Boolean isOpen;

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
        FloatingActionButton fbtClose = view.findViewById(R.id.fbtSearchClose);
        searchTrip = view.findViewById(R.id.btSearchTrip);
        etSearchFrom = view.findViewById(R.id.etSearchFrom);
        //final String newValue = etSearchFrom.getText().toString();
        viewModel = ViewModelProviders.of(getActivity()).get(SearchMenuViewModel.class);


        etSearchFrom.addTextChangedListener(new TextWatcher() {
                                                @Override
                                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                                    viewModel.setText("");
                                                }

                                                @Override
                                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                                    if (charSequence.length() > 0) {
                                                       viewModel.setText(charSequence.toString());
                                                    }
                                                }

                                                @Override
                                                public void afterTextChanged(Editable editable) {
                                                    if (editable.length() > 0) {
                                                        viewModel.setText(editable.toString());
                                                    }
                                                }
                                            });

                fbtClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().onBackPressed();
                    }
                });
        return view;
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void sendDaats(String fromLocation) {
        if (mListener != null) {
            isOpen = true;
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
