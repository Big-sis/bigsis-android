package fr.bigsis.android.helpers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import fr.bigsis.android.R;
import fr.bigsis.android.fragment.AlertFragment;
import fr.bigsis.android.fragment.ItineraryFragment;
import fr.bigsis.android.fragment.MenuFilterFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapHelper {

    public static void setOnCLickButton (FloatingActionButton fbAlertGreen, FloatingActionButton fbAlertRed,
                                         Activity activity, Context context) {
        AlertFragment alertFragment = AlertFragment.newInstance();

        fbAlertGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbAlertGreen.hide();
                fbAlertRed.show();
                FragmentManager fragmentManager = ((FragmentActivity)activity).getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.addToBackStack(null);
                transaction.add(R.id.fragment_container_alert, alertFragment, "MENU_ALERT_FRAGMENT")
                        .commit();
            }
        });
    }

public static void openItineraryFragment(TextView tv, Activity activity) {
    ItineraryFragment itineraryFragment = ItineraryFragment.newInstance();
    tv.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            tv.setVisibility(View.INVISIBLE);
            FragmentManager fragmentManager = ((FragmentActivity)activity).getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.addToBackStack(null);
            transaction.add(R.id.fragment_container_alert, itineraryFragment, "MENU_ITINERARY_FRAGMENT")
                    .commit();
        }
    });

}

    public static void addCustomLayers(@NonNull Style mMapboxMapStyle, Context context) {
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_marker_position);
        mMapboxMapStyle.addImage("position marker", icon);
       /* SymbolLayer symbolLayer = new SymbolLayer("position marker", );
        symbolLayer.setProperties(
                iconImage(MOSQUITO_COLLECTION_ICON),
                iconSize(dynamicIconSize));
      //  symbolLayer.setFilter(eq(get(TYPE), StructureType.MOSQUITO_COLLECTION_POINT));
        mMapboxMapStyle.addLayer(symbolLayer);*/
    }
}
