package ama.eeia.a214443.mibandapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ReadyActivity  extends AppCompatActivity  implements LocationListener {

    private static final String TAG = "MyMiBandApp";

    Button btnGetBoundedDevice, btnStartVibrate, btnStopVibrate;
    private OkHttpClient client;
    ExecutorService executor = Executors.newFixedThreadPool(1);
    private LocationManager locationManager;
    private double longitude;
    private double latitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ready_activity);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 1, this);
    }

    void checkNow() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                checkNearbyPlaces();
            }
        });
    }

    void checkNearbyPlaces() {
        client = new OkHttpClient();
        String postResponse = null;
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("maps.googleapis.com")
                .addPathSegment("maps")
                .addPathSegment("api")
                .addPathSegment("place")
                .addPathSegment("nearbysearch")
                .addPathSegment("json")
                .addQueryParameter("location", latitude + "," + longitude)
                .addQueryParameter("radius", "1000")
                .addQueryParameter("type", "store")
                .addQueryParameter("key", "AIzaSyC4l3FOqCAUPYIXEfypto0ceXVZt-qR4rI")
                .build();
        System.out.println(url.toString());

        try {
            postResponse = doGetRequest(url.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(postResponse);
    }

    String doGetRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "LocationChanged");
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        checkNow();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d(TAG, "StatusChanged");
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d(TAG, "ProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d(TAG, "ProviderDisabled");
    }
}
