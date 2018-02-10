package ama.eeia.a214443.mibandapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ReadyActivity  extends AppCompatActivity  implements LocationListener {

    private static final String TAG = "MyMiBandApp";
    public static String type = "store";
    private Spinner spinner;
    private Button button;
    private TextView textView;
    private EditText timeBetweenCheckLocationText;

    Button btnGetBoundedDevice, btnStartVibrate, btnStopVibrate;
    private OkHttpClient client;
    ExecutorService executor = Executors.newFixedThreadPool(1);
    private LocationManager locationManager;
    private double longitude = 19.47658899999999;
    private double latitude = 51.7671891;
    long timeBetweenCheckLocation = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ready_activity);

        setUpVariables();
        setUpListeners();
        setUpLocationChange();
    }

    void setUpVariables() {
        spinner = (Spinner) findViewById(R.id.spinner);
        button = (Button) findViewById(R.id.button2);
        textView = (TextView) findViewById(R.id.textView);
        timeBetweenCheckLocationText = findViewById(R.id.timeBetweenCheckText);

        List<String> list = new ArrayList<String>();
        list.add("store");
        list.add("stadium");
        list.add("gym");
        list.add("school");
        list.add("police");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }
    void setUpListeners() {
        spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                checkNow();
            }
        });

        timeBetweenCheckLocationText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals("")) {
                    return;
                }
                timeBetweenCheckLocation = Long.parseLong(editable.toString());
                setUpLocationChange();
            }
        });
    }

    void setUpLocationChange() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, timeBetweenCheckLocation, 1, this);
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
                .addQueryParameter("radius", "10000")
                .addQueryParameter("type", type)
                .addQueryParameter("key", "AIzaSyC4l3FOqCAUPYIXEfypto0ceXVZt-qR4rI")
                .build();
        System.out.println(url.toString());

        try {
            postResponse = doGetRequest(url.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Odp " + postResponse);
        String finalPostResponse = "";

        try {
            JSONObject obj = new JSONObject(postResponse);
            JSONArray arr = obj.getJSONArray("results");
            for (int i = 0; i < arr.length(); i++) {
                finalPostResponse+= arr.getJSONObject(i).getString("vicinity");
                finalPostResponse+="\n";
            }
            String finalPostResponse1 = finalPostResponse;
            runOnUiThread(() -> textView.setText(finalPostResponse1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
