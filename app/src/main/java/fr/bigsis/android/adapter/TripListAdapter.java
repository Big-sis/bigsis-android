package fr.bigsis.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import fr.bigsis.android.R;
import fr.bigsis.android.model.TripModel;

public class TripListAdapter extends FirestoreRecyclerAdapter<TripModel, TripListAdapter.TripListHolder> {

    public TripListAdapter(@NonNull FirestoreRecyclerOptions<TripModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TripListHolder tripListHolder, int i, @NonNull TripModel tripModel) {
        tripListHolder.tvFromLocation.setText(tripModel.getFromLocation());
        tripListHolder.tvToLocation.setText(tripModel.getToLocation());
    }

    @NonNull
    @Override
    public TripListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_list_item, parent, false);
        return new TripListHolder(v);
    }

    class TripListHolder extends RecyclerView.ViewHolder {
        TextView tvFromLocation;
        TextView tvToLocation;
        public TripListHolder(@NonNull View itemView) {
            super(itemView);
            tvFromLocation = itemView.findViewById(R.id.tvTripsFrom);
            tvToLocation = itemView.findViewById(R.id.tvTripsTo);
        }
    }
}
