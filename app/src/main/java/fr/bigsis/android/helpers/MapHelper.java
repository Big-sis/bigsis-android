package fr.bigsis.android.helpers;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.geojson.Point;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.turf.TurfConstants;
import com.mapbox.turf.TurfMeasurement;

import fr.bigsis.android.R;
import fr.bigsis.android.constant.Constant;
import fr.bigsis.android.fragment.AlertFragment;
import fr.bigsis.android.fragment.ItineraryFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.mapbox.api.isochrone.IsochroneCriteria.PROFILE_CYCLING;
import static com.mapbox.api.isochrone.IsochroneCriteria.PROFILE_DRIVING;
import static com.mapbox.api.isochrone.IsochroneCriteria.PROFILE_WALKING;

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

    public static void findRouteDriving(LocationEngineResult result, Point  destination, Context mContext, Activity activity) {
        Location userLocation = result.getLastLocation();
        if (userLocation == null) {
            Timber.d("calculateRoute: User location is null, therefore, origin can't be set.");
            return;
        }
        Point origin = Point.fromLngLat(userLocation.getLongitude(), userLocation.getLatitude());
        if (TurfMeasurement.distance(origin, destination, TurfConstants.UNIT_METERS) < 50) {
            return;
        }

        NavigationRoute.builder(mContext)
                .accessToken(Constant.MAPBOX_ACCESS_TOKEN)
                .profile(PROFILE_DRIVING)
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                .directionsRoute(response.body().routes().get(0))
                                .shouldSimulateRoute(true)
                                .build();
                        NavigationLauncher.startNavigation(activity, options);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                    }
                });
    }
    public static void findRouteWalking(LocationEngineResult result, Point  destination, Context mContext, Activity activity) {
        Location userLocation = result.getLastLocation();
        if (userLocation == null) {
            Timber.d("calculateRoute: User location is null, therefore, origin can't be set.");
            return;
        }
        Point origin = Point.fromLngLat(userLocation.getLongitude(), userLocation.getLatitude());
        if (TurfMeasurement.distance(origin, destination, TurfConstants.UNIT_METERS) < 50) {
            return;
        }

        NavigationRoute.builder(mContext)
                .accessToken(Constant.MAPBOX_ACCESS_TOKEN)
                .profile(PROFILE_WALKING)
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                .directionsRoute(response.body().routes().get(0))
                                .shouldSimulateRoute(true)
                                .build();
                        NavigationLauncher.startNavigation(activity, options);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                    }
                });
    }

    public static void startNavigationDriving(Point originPoint, Point destinationPoint, Activity context) {
        NavigationRoute.builder(context)
                .accessToken(Constant.MAPBOX_ACCESS_TOKEN)
                .profile(PROFILE_DRIVING)
                .origin(originPoint)
                .destination(destinationPoint)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                .directionsRoute(response.body().routes().get(0))
                                .shouldSimulateRoute(true)
                                .build();
                        NavigationLauncher.startNavigation(context, options);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                    }
                });
    }

    public static void startNavigationWalking(Point originPoint, Point destinationPoint, Activity context) {
        NavigationRoute.builder(context)
                .accessToken(Constant.MAPBOX_ACCESS_TOKEN)
                .profile(PROFILE_WALKING)
                .origin(originPoint)
                .destination(destinationPoint)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                .directionsRoute(response.body().routes().get(0))
                                .shouldSimulateRoute(true)
                                .build();
                        NavigationLauncher.startNavigation(context, options);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                    }
                });
    }

    public static void startNavigationCycling(Point originPoint, Point destinationPoint, Activity context) {
        NavigationRoute.builder(context)
                .accessToken(Constant.MAPBOX_ACCESS_TOKEN)
                .profile(PROFILE_CYCLING)
                .origin(originPoint)
                .destination(destinationPoint)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                .directionsRoute(response.body().routes().get(0))
                                .shouldSimulateRoute(true)
                                .build();
                        NavigationLauncher.startNavigation(context, options);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                    }
                });
    }
}
