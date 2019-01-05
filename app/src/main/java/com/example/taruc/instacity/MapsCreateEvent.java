package com.example.taruc.instacity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsCreateEvent extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    LocationManager locationManager;
    private EditText mSearchText;
    private ImageView searchImage;
    private Button conifrmButton;
    String str;
    double latt, longtt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_create_event);
        conifrmButton = (Button) findViewById(R.id.buttonConfirm);
        mSearchText = findViewById(R.id.search_input);
        searchImage = findViewById(R.id.ic_magnify);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conifrmButton.setVisibility(2);
            }
        });


    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng search = new LatLng(latt, longtt);
        mMap.addMarker(new MarkerOptions().position(search).title(str));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(search, 14F));

        // Add a marker in Sydney and move the camera
        // LatLng cyberjaya = new LatLng(2.911275, 101.643709);
        // mMap.addMarker(new MarkerOptions().position(cyberjaya).title("Marker in Cyberjaya"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cyberjaya,14F));
    }

    private void geoLocate() {

        if (mMap != null) {
            mMap.clear();
        }
        String searchString = mSearchText.getText().toString();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocationName(searchString, 1);
            str = addressList.get(0).getAddressLine(0);
            str += addressList.get(0).getLocality() + ",";
            str += addressList.get(0).getCountryName();
            //if(addressList.get(0).getLocality().equals("Cyberjaya")){
            latt = addressList.get(0).getLatitude();
            longtt = addressList.get(0).getLongitude();

            //}else{
            //    Toast.makeText(MapsActivity.this,"Invalid area input :"+addressList.get(0).getLocality(),Toast.LENGTH_SHORT).show();

            //}


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
