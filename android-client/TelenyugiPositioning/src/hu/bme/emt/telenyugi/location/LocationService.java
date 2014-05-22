
package hu.bme.emt.telenyugi.location;

import hu.bme.emt.telenyugi.Preference;
import hu.bme.emt.telenyugi.db.SqlDatabase;
import hu.bme.emt.telenyugi.model.LocationFootprint;
import hu.bme.emt.telenyugi.msg.StatusMessage;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;

public class LocationService extends IntentService {
    public static final String TAG = LocationService.class.getSimpleName();
    private static final int THIRTY_SECONDS = 1000 * 60 * 2;
    private static final int TEN_SECONDS = 1000 * 60 * 5;
    private static final int REQUEST_CODE_GPS_FIX = 2;
    private AlarmManager alarmManager;
    private SqlDatabase db;
    private Preference prefs;
    private Gson gson;
    private LocationManager manager;

    public LocationService() {
        super(LocationService.class.getSimpleName());
        gson = new Gson();
    }

    @Override
    public void onCreate() {
        // android.os.Debug.waitForDebugger();
        super.onCreate();
        Log.i(TAG, "onCreate");
        if (prefs == null) {
            prefs = new Preference(getBaseContext().getApplicationContext());
        }
        if (db == null) {
            db = new SqlDatabase(getApplication().getBaseContext());
        }
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        db.open();
        db.setServiceStat(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        removePendingIntents(getPendingIntent());
        db.setServiceStat(false);
        Log.i(TAG, "Setting db to finished service");
        db.close();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        List<String> allProviders = manager.getAllProviders();
        Location location = null;
        for (String provider : allProviders) {
            Location newLocation = manager.getLastKnownLocation(provider);
            if (location == null) {
                location = newLocation;
                continue;
            }
            if (newLocation != null && isBetterLocation(newLocation, location)) {
                location = newLocation;
            }
        }
        if (location != null) {
            db.addLocation(location);
        }
        PendingIntent pendingIntent = getPendingIntent();
        removePendingIntents(pendingIntent);
        if (location == null || olderThanTwoMinutes(location)) {
            manager.requestSingleUpdate(LocationManager.GPS_PROVIDER, pendingIntent);
            Log.i(TAG, "Requesting simple location update");
        } else {
            long triggerTime = System.currentTimeMillis() + TEN_SECONDS;
            Log.i(TAG, "Setting alarm to wake up 5min later");
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent);
        }

        if (isUploadScheduled()) {
            Log.i(TAG, "Uploading");
            boolean succesful = uploadFootprints();
            if (succesful) {
                prefs.setLastUpload(System.currentTimeMillis());
            }
        }
    }

    private void removePendingIntents(PendingIntent pendingIntent) {
        manager.removeUpdates(pendingIntent);
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, LocationService.class);
        return PendingIntent.getService(getApplicationContext(),
                REQUEST_CODE_GPS_FIX, intent, 0);
    }

    private boolean olderThanTwoMinutes(Location location) {
        return System.currentTimeMillis() - THIRTY_SECONDS > location.getTime();
    }

    private boolean uploadFootprints() {
        ArrayList<LocationFootprint> localFootprints = db.getLocalFootprints();
        boolean allUploaded = true;
        for (LocationFootprint location : localFootprints) {
            boolean succesful = uploadFootprint(location);
            allUploaded = allUploaded & succesful;
            if (succesful) {
                location.setUploaded(true);
                db.updateLocation(location);
            }
        }
        return allUploaded;
    }

    private boolean uploadFootprint(LocationFootprint location) {
        Log.i(TAG, MessageFormat.format("Uploading location={0}", location.toString()));
        HttpURLConnection connection = null;
        try {
            String url = createURLForFootprint(location);
            Log.i(TAG, url);
            connection = (HttpURLConnection) new URL(
                    url).openConnection();
            connection.setDoOutput(true);
            connection.setConnectTimeout(4000);
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return false;
            }
            StatusMessage message = gson.fromJson(
                    new InputStreamReader(connection.getInputStream()), StatusMessage.class);
            return message.isOk();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String createURLForFootprint(LocationFootprint location) {
        Uri.Builder uriBuilder = Uri.parse("http://192.168.1.101:8082/Add").buildUpon();
        uriBuilder.appendQueryParameter("userId", "7677");
        uriBuilder.appendQueryParameter("lat", Double.toString(location.latitude));
        uriBuilder.appendQueryParameter("long", Double.toString(location.longitude));
        uriBuilder.appendQueryParameter("alt", Double.toString(location.altitude));
        uriBuilder.appendQueryParameter("time", Long.toString(location.timestamp));
        return uriBuilder.build().toString();
    }

    private boolean isUploadScheduled() {
        long threshold = prefs.getThreshold() * 1000 * 60;
        return System.currentTimeMillis() - prefs.getLastupload() >= threshold;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Determines whether one Location reading is better than the current
     * Location fix
     * 
     * @param location The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to
     *            compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > THIRTY_SECONDS;
        boolean isSignificantlyOlder = timeDelta < -THIRTY_SECONDS;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use
        // the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be
            // worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and
        // accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
