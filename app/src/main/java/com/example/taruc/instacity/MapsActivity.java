package com.example.taruc.instacity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS=1;
    LocationManager locationManager;
    private EditText mSearchText;
    private ImageView searchImage;

    /*private void init(){
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId==EditorInfo.IME_ACTION_SEARCH
                        ||actionId==EditorInfo.IME_ACTION_DONE
                        ||event.getAction()==KeyEvent.ACTION_DOWN
                        ||event.getAction()==KeyEvent.KEYCODE_ENTER){
                    geoLocate();

                }
                return true;
            }
        });
    }*/
    private void geoLocate(){

        if(mMap!=null){
            mMap.clear();
        }
        String searchString = mSearchText.getText().toString();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        try{
            List<Address> addressList = geocoder.getFromLocationName(searchString,1);
            String str = addressList.get(0).getAddressLine(0);
            str+=addressList.get(0).getLocality()+",";
            str+= addressList.get(0).getCountryName();
            if(addressList.get(0).getLocality().equals("Cyberjaya")){
                double latt=addressList.get(0).getLatitude();
                double longtt = addressList.get(0).getLongitude();
                LatLng search = new LatLng(latt,longtt);
                mMap.addMarker(new MarkerOptions().position(search).title(str));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(search,14F));
            }else{
                Toast.makeText(MapsActivity.this,"Invalid area input :"+addressList.get(0).getLocality(),Toast.LENGTH_SHORT).show();

            }


        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mSearchText = (EditText) findViewById(R.id.search_input) ;
        searchImage = (ImageView) findViewById(R.id.ic_magnify);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.





        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geoLocate();
            }
        });

        //check the network provider is enabled
        /*if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    //get latitude
                    double latitude=location.getLatitude();
                    //get longtitude
                    double longitude = location.getLongitude();
                    LatLng latlng = new LatLng(latitude,longitude);
                    Toast.makeText(MapsActivity.this,latlng.toString(),Toast.LENGTH_SHORT);
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try{
                        List<Address> addressList = geocoder.getFromLocation(latitude,longitude,1);
                        String str = addressList.get(0).getAddressLine(0);
                                str+=addressList.get(0).getLocality()+",";
                        str+= addressList.get(0).getCountryName();
                        LatLng cyberjaya = new LatLng(2.911275, 101.643709);
                        mMap.addMarker(new MarkerOptions().position(latlng).title(str));
                        mMap.addMarker(new MarkerOptions().position(cyberjaya).title("Cyberjaya"));

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,13F));
                    }catch (IOException e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    //get latitude
                    double latitude=location.getLatitude();
                    //get longtitude
                    double longitude = location.getLongitude();
                    LatLng latlng = new LatLng(latitude,longitude);
                    Toast.makeText(MapsActivity.this,latlng.toString(),Toast.LENGTH_SHORT);
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try{
                        List<Address> addressList = geocoder.getFromLocation(latitude,longitude,1);
                       String str = addressList.get(0).getLocality()+",";
                         str+= addressList.get(0).getCountryName();

                       mMap.addMarker(new MarkerOptions().position(latlng).title(str));
                       mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,10.2F));
                    }catch (IOException e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            })
        };*/


    }

    private void requestMapsPermission() {


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
       // LatLng cyberjaya = new LatLng(2.911275, 101.643709);
       // mMap.addMarker(new MarkerOptions().position(cyberjaya).title("Marker in Cyberjaya"));
      // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cyberjaya,14F));
    }
}
