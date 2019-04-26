package com.example.hayoung.googlemap;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.Manifest;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    String[] permission_list = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    Location myLocation;
    LocationManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        checkPermission();
    }


    public void checkPermission() {
        boolean isGrant = false;
        for (String str : permission_list) {
            if (ContextCompat.checkSelfPermission(this, str) == PackageManager.PERMISSION_GRANTED) {
            } else {
                isGrant = false;
                break;
            }
        }
        if (isGrant == false) {
            ActivityCompat.requestPermissions(this, permission_list, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isGrant = true;
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                isGrant = false;
                break;
            }
        }
        // 모든 권한을 허용했다면 사용자 위치를 측정한다.
        if (isGrant == true) {
            getMyLocation();
        }
    }

    // 현재 위치를 가져온다.
    public void getMyLocation() {
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // 권한이 모두 허용되어 있을 때만 동작하도록 한다.
        int chk1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int chk2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (chk1 == PackageManager.PERMISSION_GRANTED && chk2 == PackageManager.PERMISSION_GRANTED) {
            myLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            showMyLocation();
        }
        // 새롭게 위치를 측정한다.
        GpsListener listener = new GpsListener();
        if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, listener);
        }
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, listener);
        }
    }

    // GPS Listener
    class GpsListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            // 현재 위치 값을 저장한다.
            myLocation = location;
            // 위치 측정을 중단한다.
            manager.removeUpdates(this);
            // 지도를 현재 위치로 이동시킨다.
            showMyLocation();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public void showMyLocation() {

        if (myLocation == null) {
            return;
        }

        double lat = myLocation.getLatitude();
        double lng = myLocation.getAccuracy();

        LatLng position = new LatLng(lat, lng);

        CameraUpdate update1 = CameraUpdateFactory.newLatLng(position);
        mMap.moveCamera(update1);

        CameraUpdate update2 = CameraUpdateFactory.zoomTo(15);
        mMap.animateCamera(update2);

        mMap.setMyLocationEnabled(true);

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

     //@Override
    public boolean onCreateOptionMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

     //@Override
    public boolean onOptionsItemselected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.item1:
                getMyLocation();
                break;
            case R.id.item2:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
