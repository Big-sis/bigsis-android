package fr.bigsis.android.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
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
import fr.bigsis.android.fragment.DatePickerFragment;

public class AddTripActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    TextView textView;
    Date dateRepresentation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        dateAdd();
        addOneTrip();
    }

    private void dateAdd() {
        textView = findViewById(R.id.tvAddDate);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = new GregorianCalendar(year, month, dayOfMonth);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        dateRepresentation = calendar.getTime();
        textView.setText(format.format(dateRepresentation));
    }

    private void addOneTrip() {
        final EditText etAddFrom = findViewById(R.id.etAddFrom);
        final EditText etAddTo = findViewById(R.id.etAddTo);
        textView = findViewById(R.id.tvAddDate);
        Button saveAddTrip = findViewById(R.id.btAddTrip);
        saveAddTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String addFrom = etAddFrom.getText().toString();
                String toFrom = etAddTo.getText().toString();
                if (addFrom.trim().isEmpty() || toFrom.trim().isEmpty()) {
                    Toast.makeText(AddTripActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_LONG).show();
                    return;
                }
                CollectionReference tripReference = FirebaseFirestore.getInstance()
                        .collection("trips");
                tripReference.add(new TripEntity(addFrom, toFrom, dateRepresentation));
                finish();
            }
        });
    }
}
