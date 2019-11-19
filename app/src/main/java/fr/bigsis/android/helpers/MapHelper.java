package fr.bigsis.android.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapHelper {
    private LocationComponent locationComponent;



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

    public static void setTheLocationComponent(@NonNull Style loadedMapStyle, Context context, MapboxMap mapboxMap, Location originLocation) {

        // Create and customize the LocationComponent's options
        LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(context)
                .elevation(5)
                .accuracyAlpha(.6f)
                .accuracyColor(R.color.colorPrimary)
                .foregroundDrawable(R.drawable.ic_marker_position)
                .build();
        LocationComponent locationComponent = mapboxMap.getLocationComponent();
        LocationComponentActivationOptions locationComponentActivationOptions =
                LocationComponentActivationOptions.builder(context, loadedMapStyle)
                        .locationComponentOptions(customLocationComponentOptions)
                        .build();
        locationComponent.activateLocationComponent(locationComponentActivationOptions);
        locationComponent.setLocationComponentEnabled(true);
// Set the component's camera mode
        locationComponent.setCameraMode(CameraMode.TRACKING);
        originLocation = locationComponent.getLastKnownLocation();

    }



}
