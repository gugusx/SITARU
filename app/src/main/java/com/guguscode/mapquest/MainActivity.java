package com.guguscode.mapquest;

import android.graphics.Color;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import com.google.gson.JsonElement;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.tooltip.Tooltip;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity;

public class MainActivity extends AppCompatActivity implements PermissionsListener,
        MapboxMap.OnMapClickListener  {

    MapView mapView;
    PermissionsManager permissionsManager;
    MapboxMap mapboxMap1 ;
    Marker featureMarker;
    Tooltip tooltip1;
    private LocationComponent locationComponent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.access_token));

        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Sistem Informasi Tata Ruang");

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                mapboxMap1 = mapboxMap;
                mapboxMap1.setStyle(Style.LIGHT, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        mapboxMap1.addOnMapClickListener(MainActivity.this);
                        enableLocationComponent(style);
                        FloatingActionButton FAB = findViewById(R.id.myLocationButton);
                    tooltip1 = new Tooltip.Builder(FAB)
                                .setText("Jika error, buka kembali aplikasi")
                                .setBackgroundColor(getResources().getColor(R.color.colorAccent))
                                .setTextColor(Color.WHITE)
                               .setGravity(Gravity.START)
                                .show();


                        FAB.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                tooltip1.dismiss();
                                if(mapboxMap1.getLocationComponent().getLastKnownLocation() != null) { // Check to ensure coordinates aren't null, probably a better way of doing this...
                                    mapboxMap1.animateCamera(com.mapbox.mapboxsdk.camera.CameraUpdateFactory.newLatLngZoom
                                            (new LatLng(mapboxMap1.getLocationComponent().getLastKnownLocation().getLatitude(),
                                                    mapboxMap1.getLocationComponent().getLastKnownLocation().getLongitude()), 18));
                                }
                            }
                        });

                        try {
                          GeoJsonSource  rdtr = new GeoJsonSource("rdtr-areas",
                                    new URI("http://peta.jogjakota.go.id:8080/geoserver/sitaru/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=sitaru:pola_ruang_rdtr&minFeatures=0&outputFormat=application/json"));
                            style.addSource(rdtr);

                            FillLayer rdtrArea = new FillLayer("rdtr-feature", "rdtr-areas");
                            rdtrArea.setProperties(
                                fillColor(Color.GREEN),
                                    fillOpacity(0.4f)
                            );
                            style.addLayerAbove(rdtrArea,"building");

                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }


                    }



                });
            }
        });
    }


    private void enableLocationComponent(Style style) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(this)
                    .elevation(5)
                    .accuracyAlpha(.2f)
                    .accuracyColor(Color.YELLOW)
                    .foregroundDrawable(R.drawable.navigation)
                    .build();

            // Get an instance of the component
            locationComponent = mapboxMap1.getLocationComponent();

            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, style)
                            .locationComponentOptions(customLocationComponentOptions)
                            .build();

            // Activate with options
            locationComponent.activateLocationComponent(locationComponentActivationOptions);

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);



        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap1.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                }
            });
        } else {
            finish();
        }

    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        if (featureMarker != null) {
            mapboxMap1.removeMarker(featureMarker);
        }

        final PointF pixel = mapboxMap1.getProjection().toScreenLocation(point);
        List<Feature> features = mapboxMap1.queryRenderedFeatures(pixel, "rdtr-feature");

        if (!features.isEmpty()) {
            Feature feature = features.get(0);


            StringBuilder stringBuilder = new StringBuilder();
            if (feature.properties() != null) {
                for (Map.Entry<String, JsonElement> entry : feature.properties().entrySet()) {
                    stringBuilder.append(String.format("%s : %s", entry.getKey(), entry.getValue()));
                    stringBuilder.append(System.getProperty("line.separator"));
                }

                featureMarker = mapboxMap1.addMarker(new MarkerOptions()
                        .position(point)
                        .title("Keterangan")
                        .snippet("Status Kawasan : "+ feature.getStringProperty("pola_ruang")
                                +"\nZona : "+ feature.getStringProperty("zona")
                                +"\nSub Zona : "+ feature.getStringProperty("sub_zona"))
                );
            }

        } else {
            featureMarker = mapboxMap1.addMarker(new MarkerOptions()
                    .position(point)
                    .snippet("Tidak ada keterangan"));
        }
        mapboxMap1.selectMarker(featureMarker);
        return true;
    }




}
