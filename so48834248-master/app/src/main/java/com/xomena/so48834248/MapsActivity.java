package com.xomena.so48834248;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int MY_SOCKET_TIMEOUT_MS = 15000;
    private GoogleMap mMap;
    private DebugHelper hlp;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private Context context;
    private RequestQueue requestQueue; // This is our requests queue to process our HTTP requests.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        context = this;
        requestQueue = Volley.newRequestQueue(this); // This setups up a new request queue which we will need to make HTTP requests.
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

        LatLng center = new LatLng(41.385064, 2.173403);

        mMap.getUiSettings().setZoomControlsEnabled(true);

        hlp = new DebugHelper();
        mMap.setOnCameraIdleListener(this);
        mMap.setMinZoomPreference(13);
        mMap.setMaxZoomPreference(20);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(center));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location arg0) {
                // TODO Auto-generated method stub

                mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
            }
        });
        //final LatLngBounds DUBAI_BOUNDS = new LatLngBounds(new LatLng(24.5908366068, 54.84375), new LatLng(25.3303729706, 55.6835174561));


        //mMap.setLatLngBoundsForCameraTarget(DUBAI_BOUNDS);
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status)
            return true;
        else {
            if (googleApiAvailability.isUserResolvableError(status))
                Toast.makeText(this, "Please Install google play services to use this application", Toast.LENGTH_LONG).show();
        }
        return false;
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (isGooglePlayServicesAvailable()) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            startCurrentLocationUpdates();
        }
        context = this;
    }

    private void startCurrentLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                return;
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult.getLastLocation() == null)
                return;
            if (currentLocation != locationResult.getLastLocation()) {
                currentLocation = locationResult.getLastLocation();
                Geocoder geoCoder = new Geocoder(context, Locale.getDefault()); //it is Geocoder
                StringBuilder builder = new StringBuilder();
                try {
                    List<Address> address = geoCoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                    Address address1 = address.get(0);
                    int maxLines = address1.getMaxAddressLineIndex() + 1;
                    for (int i = 0; i < maxLines; i++) {
                        String addressStr = address1.getAddressLine(i);
                        builder.append(addressStr);
                        builder.append(" ");
                    }

                    String finalAddress = builder.toString(); //This is the complete address.
                    String url = "http://192.168.42.107/api/Level0/" + address1.getCountryName();
                    if (address1.getAdminArea() != null) {
                        url += "/Level1/" + address1.getAdminArea();
                    }
                    if (address1.getSubAdminArea() != null) {
                        url += "/Level2/" + address1.getSubAdminArea();
                    }
                    if (address1.getLocality() != null) {
                        url += "/Level3/" + address1.getLocality();
                    }
                    if (address1.getSubLocality() != null) {
                        url += "/Level4/" + address1.getSubLocality();
                    }
                    Log.e(" HELLO : ", url);
                    JsonObjectRequest arrReq = new JsonObjectRequest(url,null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // Check the length of our response (to see if the user has any repos)
                                    if (response.length() > 0) {
                                        // The user does have repos, so let's loop through them all.
                                        for (int i = 0; i < response.length(); i++) {
                                            //try {
                                                // For each repo, add a new line to our repo list.
                                                //JSONObject jsonObj = response.getJSONObject(i);
                                                //Log.e(" Volley ", jsonObj.toString());
                                                //String repoName = jsonObj.get("name").toString();
                                                //String lastUpdated = jsonObj.get("updated_at").toString();
                                                //addToRepoList(repoName, lastUpdated);
                                            //} catch (JSONException e) {
                                                // If there is an error then output this to the logs.
                                                Log.e(" Volley ", "Invalid JSON Object.");
                                            //}

                                        }
                                    } else {
                                        // The user didn't have any repos.
                                        //setRepoListText("No repos found.");
                                    }

                                }
                            },

                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // If there a HTTP error then add a note to our repo list.
                                    //setRepoListText("Error while calling REST API");
                                    Log.e("Volley", error.toString());
                                }
                            }
                    );
                    arrReq.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    // Add the request we just defined to our request queue.
                    // The request queue will automatically handle the request as soon as it can.
                    requestQueue.add(arrReq);
                } catch (IOException e) {
                } catch (NullPointerException e) {
                }
            }
        }
    };

    @Override
    public void onCameraIdle() {
        Projection projection = mMap.getProjection();
        double l1 = projection.getVisibleRegion().farLeft.longitude;
        double l2 = projection.getVisibleRegion().farRight.longitude;

        double grdSize =  0.0005 * mMap.getCameraPosition().zoom;

        hlp.drawDebugGrid(mMap, grdSize);
    }
}
