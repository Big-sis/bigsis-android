package fr.bigsis.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.SimpleDateFormat;

import fr.bigsis.android.R;
import fr.bigsis.android.entity.TripEntity;

public class TripListAdapter extends FirestoreRecyclerAdapter<TripEntity, TripListAdapter.TripListHolder> {

    public TripListAdapter(@NonNull FirestoreRecyclerOptions<TripEntity> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TripListHolder tripListHolder, int i, @NonNull TripEntity tripEntity) {
        tripListHolder.tvFromLocation.setText(tripEntity.getFrom());
        tripListHolder.tvToLocation.setText(tripEntity.getTo());
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        tripListHolder.tvDateTrip.setText(format.format(tripEntity.getDate().getTime()));
    }

    @NonNull
    @Override
    public TripListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_list_item, parent, false);
        return new TripListHolder(v);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class TripListHolder extends RecyclerView.ViewHolder {
        TextView tvFromLocation;
        TextView tvToLocation;
        TextView tvDateTrip;
        private TripListHolder(@NonNull View itemView) {
            super(itemView);
            tvFromLocation = itemView.findViewById(R.id.tvTripsFrom);
            tvToLocation = itemView.findViewById(R.id.tvTripsTo);
            tvDateTrip = itemView.findViewById(R.id.tvDateTrip);
        }
    }
}
