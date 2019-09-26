package fr.bigsis.android.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.bigsis.android.R;
import fr.bigsis.android.entity.TripEntity;

public class TripListViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tvTripsFrom)
    TextView mTextFrom;

    @BindView(R.id.tvTripsTo)
    TextView mTextTo;

    @BindView(R.id.ivTripImage)
    ImageView mImvTripImage;

    @BindView(R.id.tvDateTrip)
    TextView mTextDate;

    public TripListViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(@NonNull TripEntity item) {
        mTextFrom.setText(item.getFrom());
        mTextTo.setText(item.getTo());
        SimpleDateFormat format = new SimpleDateFormat("E dd MMM, HH:mm", Locale.FRENCH);
        mTextDate.setText(format.format(item.getDate().getTime()));

        RequestOptions myOptions = new RequestOptions()
                .fitCenter()
                .override(250, 250);

        Glide.with(mImvTripImage.getContext())
                .asBitmap()
                .apply(myOptions)
                .load(item.getImage())
                .into(mImvTripImage);
    }
}

