package fr.bigsis.android.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.util.List;

import fr.bigsis.android.R;
import fr.bigsis.android.constant.Constant;
import fr.bigsis.android.fragment.AlertFragment;
import fr.bigsis.android.fragment.ItineraryFragment;
import fr.bigsis.android.fragment.MenuFilterFragment;
import fr.bigsis.android.helpers.MapHelper;
import fr.bigsis.android.view.CurvedBottomNavigationView;
import fr.bigsis.android.viewModel.AlertLocateViewModel;
import fr.bigsis.android.viewModel.ItineraryViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.api.isochrone.IsochroneCriteria.PROFILE_CYCLING;
import static com.mapbox.api.isochrone.IsochroneCriteria.PROFILE_DRIVING;
import static com.mapbox.api.isochrone.IsochroneCriteria.PROFILE_WALKING;
import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ROTATION_ALIGNMENT_VIEWPORT;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static fr.bigsis.android.helpers.MapHelper.openItineraryFragment;

public class MapsActivity extends BigsisActivity implements MenuFilterFragment.OnFragmentInteractionListener,
        OnMapReadyCallback, PermissionsListener, MapboxMap.OnInfoWindowClickListener, MapboxMap.OnMapClickListener,
        AlertFragment.OnFragmentInteractionListener, ItineraryFragment.OnFragmentInteractionListener {

    private static final String TAG = "DirectionsActivity";
    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String ICON_ID = "ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";
    LocationComponent locationComponent;
    FloatingActionButton imgBtItinarary;
    TextView tvOpenItinerary;
    ItineraryViewModel itineraryViewModel;
    String driving;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;
    private FloatingActionButton imgBtRecenter;
    private FirebaseFirestore firebaseFirestore;
    private FloatingActionButton fbAlertGreen;
    private FloatingActionButton fbAlertRed;
    private AlertLocateViewModel alertLocateViewModel;
    private String profileCriteria = DirectionsCriteria.PROFILE_DRIVING, unitsCriteria = DirectionsCriteria.METRIC;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, Constant.MAPBOX_ACCESS_TOKEN);
        setContentView(R.layout.activity_maps);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        imgBtRecenter = findViewById(R.id.imgBtRecenter);
        fbAlertGreen = findViewById(R.id.fbAlert);
        fbAlertRed = findViewById(R.id.fbAlertRed);
        AlertFragment alertFragment = AlertFragment.newInstance();
        ItineraryFragment itineraryFragment = ItineraryFragment.newInstance();
        fbAlertRed.hide();
        firebaseFirestore = FirebaseFirestore.getInstance();
        imgBtItinarary = findViewById(R.id.imgBtItinarary);
        tvOpenItinerary = findViewById(R.id.tvOpenItinerary);
        openItineraryFragment(tvOpenItinerary, MapsActivity.this);
        MapHelper.setOnCLickButton(fbAlertGreen, fbAlertRed, MapsActivity.this, this, tvOpenItinerary);
        if (alertFragment.isAdded()) {
            fbAlertRed.show();
            fbAlertGreen.hide();
        }

        final CurvedBottomNavigationView curvedBottomNavigationView = findViewById(R.id.customBottomBar);
        curvedBottomNavigationView.inflateMenu(R.menu.bottom_menu);
        curvedBottomNavigationView.setItemIconTintList(null);
        curvedBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return selectItem(item, curvedBottomNavigationView);
            }
        });

    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        Query query = firebaseFirestore.collection("places").orderBy("name", Query.Direction.ASCENDING);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        IconFactory iconFactory = IconFactory.getInstance(MapsActivity.this);
                        com.mapbox.mapboxsdk.annotations.Icon icon = iconFactory.fromResource(R.drawable.mapbox_logo_icon);

                        double lat = doc.getDocument().getDouble("latitude");
                        double lng = doc.getDocument().getDouble("longitude");
                        String titre = doc.getDocument().getString("name");
                        mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lng))
                                .snippet("hiii")
                                .icon(icon));
                    }
                }
            }
        });


        mapboxMap.setStyle(new Style.Builder().withImage("test-icon", getResources().getDrawable(R.drawable.ic_message))

                .withLayer(new SymbolLayer(LAYER_ID, SOURCE_ID)
                        .withProperties(PropertyFactory.iconImage("test-icon"),
                                iconAllowOverlap(true),
                                iconIgnorePlacement(true),
                                iconOffset(new Float[]{0f, -9f}))
                ), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                // Map is set up and the style has loaded. Now you can add additional data or make other map adjustments.


            }
        });
        mapboxMap.setStyle(getString(R.string.navigation_guidance_day), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                //addDestinationIconSymbolLayer(style);
                // create symbol manager object
                // style.addImage("SMALL_MONSTER_ICON", MapsActivity.this, R.drawable.ic_message));
                enableLocationComponent(style);


                style.addImageAsync("SMALL_MONSTER_ICON", BitmapUtils.getBitmapFromDrawable(
                        getResources().getDrawable(R.drawable.ic_message)));
                SymbolManager symbolManager = new SymbolManager(mapView, mapboxMap, style);

// add click listeners if desired
                Symbol symbol = symbolManager.create(new SymbolOptions()
                        .withLatLng(new LatLng(43.604604, 1.445432))

                        .withIconImage("SMALL_MONSTER_ICON")

                        .withIconSize(2.0f));


// set non-data-driven properties, such as:
                symbolManager.setIconAllowOverlap(true);
                symbolManager.setIconTranslate(new Float[]{-4f, 5f});
                symbolManager.setIconRotationAlignment(ICON_ROTATION_ALIGNMENT_VIEWPORT);
            }
        });

        itineraryViewModel = ViewModelProviders.of(MapsActivity.this).get(ItineraryViewModel.class);
        itineraryViewModel.getLatitudeItinerary().observe(MapsActivity.this, new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                Point destinationPoint = Point.fromLngLat(itineraryViewModel.getLongitudeItinerary().getValue(),
                        itineraryViewModel.getLatitudeItinerary().getValue());
                Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                        locationComponent.getLastKnownLocation().getLatitude());
                itineraryViewModel.getModeItinerary().observe(MapsActivity.this, new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        if (s.equals("DRIVING")) {
                            getRouteDriving(originPoint, destinationPoint);
                        }
                        if (s.equals("WALKING")) {
                            getRouteWalking(originPoint, destinationPoint);
                        }

                        if (s.equals("CYCLING")) {
                            getRouteCycling(originPoint, destinationPoint);
                        }
                    }
                });
                imgBtItinarary.show();
                imgBtItinarary.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       /* NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                .directionsRoute(currentRoute)
                                .build();
                        NavigationLauncher.startNavigation(MapsActivity.this, options);*/
                        enableLocationComponent(mapboxMap.getStyle());
                        itineraryViewModel.getModeItinerary().observe(MapsActivity.this, new Observer<String>() {
                            @Override
                            public void onChanged(String s) {
                                if (s.equals("DRIVING")) {
                                    MapHelper.startNavigationDriving(originPoint, destinationPoint, MapsActivity.this);
                                }

                                if (s.equals("WALKING")) {
                                    MapHelper.startNavigationWalking(originPoint, destinationPoint, MapsActivity.this);
                                }

                                if (s.equals("CYCLING")) {
                                    MapHelper.startNavigationCycling(originPoint, destinationPoint, MapsActivity.this);
                                }
                            }
                        });
                    }
                });
            }
        });

        imgBtRecenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableLocationComponent(mapboxMap.getStyle());
            }
        });
        mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                onMapClick(marker.getPosition());
                boolean simulateRoute = true;

                marker.showInfoWindow(mapboxMap, mapView);
                onInfoWindowClick(marker);
                mapboxMap.addOnCameraMoveListener(() -> mapboxMap.getMarkers().forEach(Marker::hideInfoWindow));
                mapboxMap.addOnCameraMoveListener(() -> imgBtItinarary.hide());
                imgBtItinarary.show();
                imgBtItinarary.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                .directionsRoute(currentRoute)
                                .build();
                        NavigationLauncher.startNavigation(MapsActivity.this, options);
                        enableLocationComponent(mapboxMap.getStyle());
                    }
                });
                return true;
            }
        });
    }


    @Override
    public boolean onInfoWindowClick(@NonNull Marker marker) {




       /* String snippest = marker.getSnippet();
        mapboxMap.addMarker(new MarkerOptions()
                .icon(icon)
                .position(marker.getPosition())
                .title(marker.getTitle()));
        Log.d(TAG, "onMarkerClick: " + snippest);*/
        return true;
    }

    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id",
                BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }

    @SuppressWarnings({"MissingPermission"})
    public boolean onMapClick(@NonNull LatLng point) {
        Point destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
        Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                locationComponent.getLastKnownLocation().getLatitude());
        getRouteWalking(originPoint, destinationPoint);

        return true;
    }

    private void getRouteDriving(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .profile(PROFILE_DRIVING)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }
                        currentRoute = response.body().routes().get(0);
                        //   mapboxMap.addOnCameraMoveListener(() -> navigationMapRoute.removeRoute());
                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                    }
                });
    }

    private void getRouteWalking(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .profile(PROFILE_WALKING)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }
                        currentRoute = response.body().routes().get(0);

                        //    mapboxMap.addOnCameraMoveListener(() -> navigationMapRoute.removeRoute());
                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                    }
                });
    }

    private void getRouteCycling(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .profile(PROFILE_CYCLING)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }
                        currentRoute = response.body().routes().get(0);

                        //    mapboxMap.addOnCameraMoveListener(() -> navigationMapRoute.removeRoute());
                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                    }
                });
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (locationEnabled()) {
            if (PermissionsManager.areLocationPermissionsGranted(this)) {
                Bitmap icon = BitmapFactory.decodeResource(MapsActivity.this.getResources(), R.drawable.ic_marker_position);
                loadedMapStyle.addImage("position marker", icon);
                // Activate the MapboxMap LocationComponent to show user location
                // Adding in LocationComponentOptions is also an optional parameter
                locationComponent = mapboxMap.getLocationComponent();
                locationComponent.activateLocationComponent(this, loadedMapStyle);
                locationComponent.setLocationComponentEnabled(true);
                // Set the component's camera mode
                locationComponent.setCameraMode(CameraMode.TRACKING);
                alertLocateViewModel = ViewModelProviders.of(MapsActivity.this).get(AlertLocateViewModel.class);

                double latitudeAlert = locationComponent.getLastKnownLocation().getLatitude();
                double longitudeAlert = locationComponent.getLastKnownLocation().getLongitude();
                alertLocateViewModel.setLatitudeAlert(latitudeAlert);
                alertLocateViewModel.setLongitudeAlert(longitudeAlert);


            } else {
                permissionsManager = new PermissionsManager(this);
                permissionsManager.requestLocationPermissions(this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "user_location_permission_explanation", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(mapboxMap.getStyle());
        } else {
            Toast.makeText(this, "user_location_permission_not_granted", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onFragmentInteraction() {
        onBackPressed();
    }

    private boolean locationEnabled() {

        LocationManager lm = (LocationManager) MapsActivity.this.getSystemService(Context.LOCATION_SERVICE);
        try {
            return lm != null && lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        tvOpenItinerary.setVisibility(View.VISIBLE);
        fbAlertGreen.show();
        fbAlertRed.hide();
        tvOpenItinerary.setVisibility(View.VISIBLE);
    }
}