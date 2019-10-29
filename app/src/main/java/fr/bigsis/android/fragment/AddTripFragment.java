package fr.bigsis.android.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import fr.bigsis.android.R;
import fr.bigsis.android.entity.TripEntity;

public class AddTripFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private EditText etAddFromDestination;
    private EditText etAddToDestination;
    private Button btCreate;
    private TextView tvDateTrip;
    private Date date;
    private Calendar dateCal;

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
        tvDateTrip.setText(getDateTimeFromTimeStamp(System.currentTimeMillis(),"E dd MMM, HH:mm"));
        tvDateTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimePicker();
            }
        });
        btCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTrip();
            }
        });
        return view;
    }

    private void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
        dateCal = Calendar.getInstance();
        new DatePickerDialog(getActivity(),R.style.MyDatePickerDialogStyle, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                dateCal.set(year, month, dayOfMonth);
                new TimePickerDialog(getActivity(),R.style.MyDatePickerDialogStyle, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        dateCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        dateCal.set(Calendar.MINUTE, minute);
                        SimpleDateFormat format = new SimpleDateFormat("E dd MMM, HH:mm", Locale.FRENCH);
                        date = dateCal.getTime();
                        tvDateTrip.setText(format.format(date));
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    private static String getDateTimeFromTimeStamp(Long time, String mDateFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(mDateFormat, Locale.FRANCE);
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        Date dateTime = new Date(time);
        return dateFormat.format(dateTime);
    }

    private void createTrip() {
        String addFrom = etAddFromDestination.getText().toString();
        String toFrom = etAddToDestination.getText().toString();
        String KEY = "eCinHruQlvOrt7tG4MbkaVIvuiyeYzir";
        String url = "https://open.mapquestapi.com/staticmap/v5/map?start=" + addFrom + "|via-33AB62&end=" + toFrom + "&routeWidth=3&routeColor=33AB62&type=light&size=170,170&&defaultMarker=marker-sm-33AB62&key="+KEY;

        if (addFrom.trim().isEmpty() || toFrom.trim().isEmpty()) {
            Toast.makeText(getActivity(), "Veuillez remplir tous les champs", Toast.LENGTH_LONG).show();
            return;
        }
        if (date == null) {
            Toast.makeText(getActivity(), "Veuillez indiquer la date et l'heure du trajet", Toast.LENGTH_LONG).show();
            return;
        }

        CollectionReference tripReference = FirebaseFirestore.getInstance()
                .collection("trips");
        tripReference.add(new TripEntity(addFrom, toFrom, date, url));
        getActivity().onBackPressed();
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
