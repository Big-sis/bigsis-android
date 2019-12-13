package fr.bigsis.android.activity;

import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.io.IOException;
import java.util.List;

import fr.bigsis.android.R;
import fr.bigsis.android.constant.Constant;
import fr.bigsis.android.fragment.AlertFragment;
import fr.bigsis.android.fragment.MenuFilterFragment;
import fr.bigsis.android.helpers.MapHelper;
import fr.bigsis.android.view.CurvedBottomNavigationView;
import fr.bigsis.android.viewModel.MenuFilterViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class MapsActivity extends BigsisActivity implements MenuFilterFragment.OnFragmentInteractionListener,
        OnMapReadyCallback, PermissionsListener, MapboxMap.OnInfoWindowClickListener,
        AlertFragment.OnFragmentInteractionListener {

    private static final String TAG = "DirectionsActivity";
    LocationComponent locationComponent;
    MenuFilterFragment menuFilterFragment = MenuFilterFragment.newInstance();
    MenuFilterViewModel viewModel;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;
    private FloatingActionButton imgBtRecenter;
    private FirebaseFirestore firebaseFirestore;
    private FloatingActionButton fbAlertGreen;
    private FloatingActionButton fbAlertRed;

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
        viewModel = ViewModelProviders.of(this).get(MenuFilterViewModel.class);
        fbAlertRed.hide();
        firebaseFirestore = FirebaseFirestore.getInstance();
       // setMenuFilterFragment();
        MapHelper.setOnCLickButton(fbAlertGreen, fbAlertRed, MapsActivity.this, this);
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

    private void setMenuFilterFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.add(R.id.fragment_container_menu, menuFilterFragment, "MENU_FILTER_FRAGMENT")
                .commit();
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
                        double lat = doc.getDocument().getDouble("latitude");
                        double lng = doc.getDocument().getDouble("longitude");
                        String titre = doc.getDocument().getString("name");
                        mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lng))
                                .title(titre));
                    }
                }
            }
        });

        viewModel.getfilterName().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (viewModel.getfilterName().getValue().equals("event")) {
                    mapboxMap.clear();

                    Query query = firebaseFirestore.collection("events").orderBy("titleEvent", Query.Direction.ASCENDING);
                    query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                                    String addressEvent = doc.getDocument().getString("adressEvent");
                                    String titleEvent = doc.getDocument().getString("titleEvent");
                                    List<Address> addresses;
                                    try {
                                        addresses = geocoder.getFromLocationName(addressEvent, 1);
                                        if (addresses.size() > 0) {
                                            double latitude = addresses.get(0).getLatitude();
                                            double longitude = addresses.get(0).getLongitude();
                                            mapboxMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(latitude, longitude))
                                                    .title(titleEvent));
                                        }
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                    });
                }
               /* if (viewModel.getfilterName().getValue().equals("trip")) {
                    mapboxMap.clear();

                    Query query = firebaseFirestore.collection("trips").orderBy("titleEvent", Query.Direction.ASCENDING);
                    query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                                    String adress = doc.getDocument().getString("from");
                                    String titleEvent = doc.getDocument().getString("titleEvent");
                                    String idEvent = doc.getDocument().getId();
                                    List<Address> addresses;
                                    try {
                                        addresses = geocoder.getFromLocationName(adress, 1);
                                        if (addresses.size() > 0) {
                                            double latitude = addresses.get(0).getLatitude();
                                            double longitude = addresses.get(0).getLongitude();
                                            mapboxMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(latitude, longitude)));
                                            // .title(titleEvent));
                                        }
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                    });
                }
                if (viewModel.getfilterName().getValue().equals("partner")) {
                    mapboxMap.clear();

                    Query query = firebaseFirestore.collection("places").orderBy("name", Query.Direction.ASCENDING);
                    query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                    double lat = doc.getDocument().getDouble("latitude");
                                    double lng = doc.getDocument().getDouble("longitude");
                                    String titre = doc.getDocument().getString("name");
                                    String idPartner = doc.getDocument().getId();
                                    mapboxMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(lat, lng))
                                            .title(titre));
                                }
                            }
                        }
                    });
                }*/
            }
        });
        mapboxMap.setStyle(getString(R.string.navigation_guidance_day), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent(style);
                //addDestinationIconSymbolLayer(style);
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
                marker.showInfoWindow(mapboxMap, mapView);
                onInfoWindowClick(marker);
                mapboxMap.addOnCameraMoveListener(() -> mapboxMap.getMarkers().forEach(Marker::hideInfoWindow));
                ImageButton imgBtItinarary = findViewById(R.id.imgBtItinarary);
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
        String snippest = marker.getSnippet();
        mapboxMap.addMarker(new MarkerOptions()
                .position(marker.getPosition())
                .title(marker.getTitle()));
        Log.d(TAG, "onMarkerClick: " + snippest);
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
        getRoute(originPoint, destinationPoint);
        return true;
    }

    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
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
                        mapboxMap.addOnCameraMoveListener(() -> navigationMapRoute.removeRoute());

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
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Activate the MapboxMap LocationComponent to show user location
            // Adding in LocationComponentOptions is also an optional parameter
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
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
}