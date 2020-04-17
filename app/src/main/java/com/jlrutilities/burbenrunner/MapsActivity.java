package com.jlrutilities.burbenrunner;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

  private GoogleMap mMap;
  private UiSettings mUiSettings;
  private FusedLocationProviderClient fusedLocationClient;
  private LocationCallback locationCallback;

  private List<Polyline> polylines= new ArrayList<>();
  private List<Marker> markers = new ArrayList<>();

  TextView tvInfo;
  private int index = 0;
  private boolean isMetric;
  private boolean routeMode;
  private double distance;

  //intent info
  private int listPosition;
  private int mapId;
  private String mapName;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maps);

    //Intent Map Information
    Intent intent = getIntent();
    listPosition = intent.getIntExtra("list_position", -1);
    mapId = intent.getIntExtra("map_id", -1);
    mapName =intent.getStringExtra("map_name");


    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    // default setting
    isMetric = false;
    routeMode = false;

    // TextView
    tvInfo = findViewById(R.id.info_text_view);
    if (isMetric) {
      tvInfo.setText(mapName +"\n Distance: \n 0.00 km");
    } else {
      tvInfo.setText(mapName +"\n Distance: \n 0.00 mi");
    }

    // FAB
    FloatingActionButton fabMyLocation = findViewById(R.id.fab_my_location);
    FloatingActionButton fabAdd = findViewById(R.id.fab_add);
    FloatingActionButton fabClear = findViewById(R.id.fab_clear_all);

    fabMyLocation.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        fusedLocationClient.getLastLocation()
            .addOnSuccessListener(MapsActivity.this, new OnSuccessListener<Location>() {
              @Override
              public void onSuccess(Location currentLocation) {
                // last location known
                if (currentLocation != null){
                  LatLng latLng = new LatLng(
                      currentLocation.getLatitude(),
                      currentLocation.getLongitude()
                  );
                  CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                  mMap.animateCamera(update);
                } else {
                  Toast.makeText(MapsActivity.this, "Could not Connect!", Toast.LENGTH_SHORT).show();
                }
              }
            });
      }
    });

    fabAdd.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        //middle button
        if (routeMode){
        } else {
        }
      }
    });

    fabClear.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        removeEverything();
      }
    });

  }


  /**
   * Manipulates the map once available.
   * This callback is triggered when the map is ready to be used.
   * This is where we can add markers or lines, add listeners or move the camera. In this case,
   * we just add a marker near Sydney, Australia.
   * If Google Play services is not installed on the device, the user will be prompted to install
   * it inside the SupportMapFragment. This method will only be triggered once the user has
   * installed Google Play services and returned to the app.
   */
  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;

    mUiSettings = mMap.getUiSettings();

    // Keep the UI settings state in sync with the checkboxes
    /*mUiSettings.setCompassEnabled(true);
    mUiSettings.setMyLocationButtonEnabled(true);
    mUiSettings.setMapToolbarEnabled(true);
    mMap.setMyLocationEnabled(true);*/

    // Add a marker in Sydney and move the camera
    LatLng sydney = new LatLng(-34, 151);
    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
      @Override
      public View getInfoWindow(Marker marker) {
        return null;
      }

      @Override
      public View getInfoContents(Marker marker) {
        View view = getLayoutInflater().inflate(R.layout.info_window, null);
        TextView tvLocality = view.findViewById(R.id.tvLocality);
        TextView tvLat = view.findViewById(R.id.tvLat);
        TextView tvLng = view.findViewById(R.id.tvLng);
        TextView tvSnippet = view.findViewById(R.id.tvSnippet);

        LatLng latLng = marker.getPosition();
        tvLocality.setText(marker.getTitle());
        tvLat.setText("Latitude: " + latLng.latitude);
        tvLng.setText("Longitude: " + latLng.longitude);
        tvSnippet.setText(marker.getSnippet());

        return view;
      }
    });

    //Long click listener
    mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
      @Override
      public void onMapLongClick(LatLng latLng) {
        MapsActivity.this.addMarker(latLng.latitude, latLng.longitude);
      }
    });

    // marker click listener
    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
      @Override
      public boolean onMarkerClick(Marker marker) {
        String msg = marker.getTitle() + " (" +
            marker.getPosition().latitude + ", "+
            marker.getPosition().longitude+ ")";
        Toast.makeText(MapsActivity.this, msg, Toast.LENGTH_SHORT).show();
        return false;
      }
    });

    //on drag listener
    mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
      @Override
      public void onMarkerDragStart(Marker marker) {
        // set index on beginning of drag for replacing marker on drag end
        index = markers.indexOf(marker);
      }

      @Override
      public void onMarkerDrag(Marker marker) {
      }

      @Override
      public void onMarkerDragEnd(Marker marker) {
        // remove marker with initial values and replace with new marker with new values
        markers.remove(index);
        markers.add(index, marker);


        if(markers.size() > 1){
          calculateDistance();
          updateMultipleLines();
        }
      }
    });
  }

  //private void addMarker(Address address, double lat, double lng){
  private void addMarker(double lat, double lng){

    LatLng latLng = new LatLng(lat, lng);

    //Mark does not persist on screen rotation!!!! need to do sharedpreferences or DB to store in between
    MarkerOptions options = new MarkerOptions()
        .position(latLng)
        .draggable(true)
        .icon(BitmapDescriptorFactory.defaultMarker());
    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker));

    markers.add(mMap.addMarker(options));
    if (markers.size() > 1){
      drawMultipleLines();
      calculateDistance();
    }
  }

  private void calculateDistance() {
    float[] result = new float[1];
    double finalResult = 0.0;

    for( int i = 1; i < markers.size(); i++){
      LatLng one = markers.get(i-1).getPosition();
      LatLng two = markers.get(i).getPosition();
      Location.distanceBetween(one.latitude, one.longitude, two.latitude, two.longitude, result);
      finalResult += result[0];
    }

    if (isMetric) {
      double metersToKm = finalResult * 0.001;
      tvInfo.setText(String.format(" Distance: \n %.2f mi", metersToKm));
    } else {
      double metersToMiles = finalResult * 0.00062137;
      tvInfo.setText(String.format(" Distance: \n %.2f mi", metersToMiles));
    }
  }

  private void removeEverything() {

    // polygon or multiple lines
    for(Marker marker : markers){
      marker.remove();
    }
    markers.clear();

    for(Polyline polyline : polylines){
      polyline.remove();
    }
    polylines.clear();

    tvInfo.setText(" Distance: \n 0.00 ");
  }

  private void removePolyLines(){
    for(Polyline polyline : polylines){
      polyline.remove();
    }
    polylines.clear();
  }

  private void drawMultipleLines(){

    int size = markers.size();
    Marker markerStart = markers.get(size-2);
    Marker markerEnd = markers.get(size-1);

    PolylineOptions options = new PolylineOptions()
        .add(markerStart.getPosition())
        .add(markerEnd.getPosition())
        .color(Color.RED)
        .width(20);

    polylines.add(mMap.addPolyline(options));
  }

  private void updateMultipleLines(){
    removePolyLines();

    for (int i = 1; i < markers.size(); i++){
      PolylineOptions options = new PolylineOptions()
          .add(markers.get(i-1).getPosition())
          .add(markers.get(i).getPosition())
          .color(Color.BLUE)
          .width(20);

      polylines.add(mMap.addPolyline(options));
    }
  }
}
