package fr.bigsis.android.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import fr.bigsis.android.R;
import fr.bigsis.android.entity.TripEntity;

public class AddTripFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private EditText etAddFromDestination;
    private EditText etAddToDestination;
    private Button btCreate;
    private TextView tvDateTrip;
    private Date date;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    public AddTripFragment() {
    }

    public static AddTripFragment newInstance() {
        AddTripFragment fragment = new AddTripFragment();
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
        View view = inflater.inflate(R.layout.fragment_add_trip, container, false);
        etAddFromDestination = view.findViewById(R.id.etAddFromDestination);
        etAddToDestination = view.findViewById(R.id.etAddToDestination);
        tvDateTrip = view.findViewById(R.id.tvDateTripAdd);
        btCreate = view.findViewById(R.id.btCreateTrip);
        tvDateTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateAdd();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                Calendar calendar = new GregorianCalendar(year, month, dayOfMonth);
                date = calendar.getTime();
                tvDateTrip.setText(format.format(date));
            }
        };
        btCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTrip();
            }
        });
        return view;
    }

    private void dateAdd() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), R.style.MyDatePickerDialogStyle, mDateSetListener,
                year, month, day);
        dialog.show();
    }

    private void createTrip() {
        String addFrom = etAddFromDestination.getText().toString();
        String toFrom = etAddToDestination.getText().toString();
        if (addFrom.trim().isEmpty() || toFrom.trim().isEmpty()) {
            Toast.makeText(getActivity(), "Veuillez remplir tous les champs", Toast.LENGTH_LONG).show();
            return;
        }
        CollectionReference tripReference = FirebaseFirestore.getInstance()
                .collection("trips");
        tripReference.add(new TripEntity(addFrom, toFrom, date));
        getActivity().finish();
    }
    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteractionAdd();
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
        void onFragmentInteractionAdd();
    }
}
