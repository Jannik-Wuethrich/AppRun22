package ch.coop.treasuremap;

import static org.osmdroid.tileprovider.util.StorageUtils.getStorage;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.multidex.BuildConfig;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.config.IConfigurationProvider;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ch.coop.treasuremap.databinding.ActivityMainBinding;
import ch.coop.treasuremap.modals.JSONModal;
import ch.coop.treasuremap.modals.LocationModal;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private GeoPoint actualGeopoint;
    private LocationCallback locationCallback;
    private final String filename = "data.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);

        findViewById(R.id.send).setOnClickListener(view -> log());
        setSupportActionBar(binding.toolbar);

        IConfigurationProvider provider = Configuration.getInstance();
        provider.setUserAgentValue(BuildConfig.APPLICATION_ID);
        provider.setOsmdroidBasePath(getStorage());
        provider.setOsmdroidTileCache(getStorage());

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        MapView map = findViewById(R.id.map);
        findViewById(R.id.fab).setOnClickListener(view -> saveLocationToJson(map, ctx));
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMaxZoomLevel(20.0);
        map.setMultiTouchControls(true);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
        IMapController controller = map.getController();
        controller.setZoom(15.0);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        MyLocationNewOverlay myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        myLocationOverlay.enableMyLocation();
        map.getOverlays().add(myLocationOverlay);

        loadAllMarkersFromJSON(map);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Toast.makeText(getApplicationContext(), "Have some locations", Toast.LENGTH_LONG).show();
                    controller.setCenter(new GeoPoint(location.getLatitude(), location.getLongitude()));
                    actualGeopoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                }
            }
        };
    }

    private void saveLocationToJson(MapView mapView, Context ctx) {
        try (FileInputStream fin = openFileInput(filename)) {
            addMarker(mapView);
            Gson gson = new Gson();
            JSONModal jsonModalFromJSONFile = gson.fromJson(readFileInputStream(fin), JSONModal.class);
            if (jsonModalFromJSONFile == null) {
                jsonModalFromJSONFile = new JSONModal();
            }
            Toast.makeText(getBaseContext(), "file read", Toast.LENGTH_SHORT).show();
            List<LocationModal> oldLocations = jsonModalFromJSONFile.getLocations() != null ? jsonModalFromJSONFile.getLocations() : new ArrayList<>();
            LocationModal newLocation = new LocationModal();
            newLocation.setLatitude(actualGeopoint.getLatitudeE6());
            newLocation.setLongitude(actualGeopoint.getLongitudeE6());
            oldLocations.add(newLocation);

            JSONModal newLocations = new JSONModal();
            newLocations.setLocations(oldLocations);
            FileOutputStream fOut = openFileOutput(filename, Context.MODE_PRIVATE);
            fOut.write(gson.toJson(newLocations).getBytes());
            fOut.close();
            Toast.makeText(getBaseContext(), "file saved", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            File f = new File(ctx.getApplicationInfo().dataDir + "/files/data.json");
            try {
                f.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(createLocationRequest(), locationCallback, Looper.getMainLooper());
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher, as an instance variable.
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            // Permission is granted. Continue the action or workflow in your
            // app.
        } else {
            // Explain to the user that the feature is unavailable because the
            // features requires a permission that the user has denied. At the
            // same time, respect the user's decision. Don't link to system
            // settings in an effort to convince the user to change their
            // decision.
        }
    });


    private String readFileInputStream(FileInputStream fis) throws IOException {
        StringBuilder tempBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                tempBuilder.append(line);
                tempBuilder.append("\n");
            }
        }
        fis.close();
        return tempBuilder.toString();
    }

    private void log() {
        try (FileInputStream fin = openFileInput(filename)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("task", "Schatzkarte");
            JSONArray jsonArray = new JSONArray();
            Gson gson = new Gson();
            JSONModal jsonModalFromJSONFile = gson.fromJson(readFileInputStream(fin), JSONModal.class);
            if (jsonModalFromJSONFile == null) {
                jsonModalFromJSONFile = new JSONModal();
            }
            Toast.makeText(getBaseContext(), "file read", Toast.LENGTH_SHORT).show();
            List<LocationModal> locations = jsonModalFromJSONFile.getLocations() != null ? jsonModalFromJSONFile.getLocations() : new ArrayList<>();
            for (LocationModal location : locations) {
                JSONObject locationJObject = new JSONObject();
                locationJObject.put("lat", location.getLatitude());
                locationJObject.put("lon", location.getLongitude());
                jsonArray.put(locationJObject);
            }
            Intent intent = new Intent("ch.apprun.intent.LOG");
            jsonObject.put("points", jsonArray);
            intent.putExtra("ch.apprun.logmessage", jsonObject.toString());
            startActivity(intent);
            Toast.makeText(getBaseContext(), "Send successful", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void addMarker(MapView mapView) {
        Marker startMarker = new Marker(mapView);
        startMarker.setPosition(actualGeopoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setOnMarkerClickListener(this::confirmDelete);
        mapView.getOverlays().add(startMarker);
    }

    private boolean removeMarker(Marker marker, MapView mapView) {
        mapView.getOverlays().remove(marker);
        removePoint(marker);
        return true;
    }

    private void loadAllMarkersFromJSON(MapView mapView) {
        try (FileInputStream fin = openFileInput(filename)) {
            Gson gson = new Gson();
            JSONModal jsonModalFromJSONFile = gson.fromJson(readFileInputStream(fin), JSONModal.class);
            if (jsonModalFromJSONFile == null) {
                jsonModalFromJSONFile = new JSONModal();
            }
            Toast.makeText(getBaseContext(), "file read", Toast.LENGTH_SHORT).show();
            List<LocationModal> locations = jsonModalFromJSONFile.getLocations() != null ? jsonModalFromJSONFile.getLocations() : new ArrayList<>();
            for (LocationModal location : locations
            ) {
                Marker startMarker = new Marker(mapView);
                startMarker.setPosition(new GeoPoint(location.getLatitude(), location.getLongitude()));
                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                startMarker.setOnMarkerClickListener(this::confirmDelete);
                mapView.getOverlays().add(startMarker);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removePoint(Marker marker) {
        try (FileInputStream fin = openFileInput(filename)) {
            Gson gson = new Gson();
            JSONModal jsonModalFromJSONFile = gson.fromJson(readFileInputStream(fin), JSONModal.class);
            if (jsonModalFromJSONFile == null) {
                jsonModalFromJSONFile = new JSONModal();
            }
            Toast.makeText(getBaseContext(), "file read", Toast.LENGTH_SHORT).show();
            List<LocationModal> locations = jsonModalFromJSONFile.getLocations() != null ? jsonModalFromJSONFile.getLocations() : new ArrayList<>();
            Optional<LocationModal> locationToRemove = locations.stream()
                    .filter(locationModal -> locationModal.getLatitude() == marker.getPosition().getLatitudeE6()
                            && locationModal.getLongitude() == marker.getPosition().getLongitudeE6()).findFirst();

            locations.remove(locationToRemove.get());
            JSONModal newLocations = new JSONModal();
            newLocations.setLocations(locations);
            FileOutputStream fOut = openFileOutput(filename, Context.MODE_PRIVATE);
            fOut.write(gson.toJson(newLocations).getBytes());
            fOut.close();
            Toast.makeText(getBaseContext(), "file saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean confirmDelete(Marker marker, MapView mapView) {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Delete");
        alert.setMessage("Are you sure you want to delete?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                removeMarker(marker, mapView);
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        alert.show();
        return true;
    }
}
