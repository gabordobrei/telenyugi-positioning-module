
package hu.bme.emt.telenyugi.db;

import hu.bme.emt.telenyugi.db.SqlLiteHelper.LocationFootprints;
import hu.bme.emt.telenyugi.db.SqlLiteHelper.ServiceStats;
import hu.bme.emt.telenyugi.model.LocationFootprint;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

public class SqlDatabase {

    public static final String TAG = SqlDatabase.class.getSimpleName();

    private final SqlLiteHelper mDatabaseOpenHelper;
    private SQLiteDatabase database;

    public SqlDatabase(final Context context) {
        mDatabaseOpenHelper = new SqlLiteHelper(context);
    }

    public void close() {
        mDatabaseOpenHelper.close();
    }
    
    public boolean isClosed() {
        return !database.isOpen();
    }

    public void open() throws SQLException {
        database = mDatabaseOpenHelper.getWritableDatabase();
    }

    public ArrayList<LocationFootprint> getAllFootprints() {
        ArrayList<LocationFootprint> result = new ArrayList<LocationFootprint>();
        Cursor cursor = database.query(LocationFootprints.TABLE_NAME, null, null, null, null,
                null, null);
        while (cursor.moveToNext()) {
            result.add(cursorToLocationFootprint(cursor));
        }
        return result;
    }

    public ArrayList<LocationFootprint> getFootprints(long startTime) {
        ArrayList<LocationFootprint> result = new ArrayList<LocationFootprint>();
        final String where = LocationFootprints.COLUMN_TIMESTAMP + " >= ?";
        Cursor cursor = database.query(LocationFootprints.TABLE_NAME, null, where, new String[] {
                Long.toString(startTime)
        }, null, null, null);
        while (cursor.moveToNext()) {
            result.add(cursorToLocationFootprint(cursor));
        }
        return result;

    }

    public ArrayList<LocationFootprint> getLocalFootprints() {
        ArrayList<LocationFootprint> result = new ArrayList<LocationFootprint>();
        final String where = LocationFootprints.COLUMN_UPLOADED + " = 0";
        Cursor cursor = database.query(LocationFootprints.TABLE_NAME, null, where, null, null,
                null, null);
        while (cursor.moveToNext()) {
            result.add(cursorToLocationFootprint(cursor));
        }
        return result;
    }

    public long addLocation(Location loc) {
        ContentValues values = new ContentValues();
        values.put(LocationFootprints.COLUMN_ALTITUDE, loc.getAltitude());
        values.put(LocationFootprints.COLUMN_LATITUDE, loc.getLatitude());
        values.put(LocationFootprints.COLUMN_LONGITUDE, loc.getLongitude());
        values.put(LocationFootprints.COLUMN_TIMESTAMP, loc.getTime());
        values.put(LocationFootprints.COLUMN_UPLOADED, false);
        long insertId = database.insert(LocationFootprints.TABLE_NAME, null,
                values);
        return insertId;
    }

    private LocationFootprint cursorToLocationFootprint(Cursor cursor) {
        final long id = cursor.getLong(cursor.getColumnIndex(LocationFootprints.COLUMN_ID));
        final double latitude = cursor.getDouble(cursor
                .getColumnIndex(LocationFootprints.COLUMN_LATITUDE));
        final double longitude = cursor.getDouble(cursor
                .getColumnIndex(LocationFootprints.COLUMN_LONGITUDE));
        final double altitude = cursor.getDouble(cursor
                .getColumnIndex(LocationFootprints.COLUMN_ALTITUDE));
        final long timestamp = cursor.getLong(cursor
                .getColumnIndex(LocationFootprints.COLUMN_TIMESTAMP));
        int booleanValue = cursor
                .getInt(cursor.getColumnIndex(LocationFootprints.COLUMN_UPLOADED));
        final boolean uploaded = booleanValue > 0;
        LocationFootprint location = new LocationFootprint(id, latitude, longitude, altitude,
                timestamp);
        location.setUploaded(uploaded);
        return location;
    }

    public void updateLocation(LocationFootprint location) {
        ContentValues values = new ContentValues();
        values.put(LocationFootprints.COLUMN_ALTITUDE, location.altitude);
        values.put(LocationFootprints.COLUMN_LATITUDE, location.latitude);
        values.put(LocationFootprints.COLUMN_LONGITUDE, location.longitude);
        values.put(LocationFootprints.COLUMN_TIMESTAMP, location.timestamp);
        values.put(LocationFootprints.COLUMN_UPLOADED, location.isUploaded());
        final String where = new StringBuilder(LocationFootprints.COLUMN_ID).append("= ?")
                .toString();
        database.update(LocationFootprints.TABLE_NAME, values, where, new String[] {
                Long.toString(location.id)
        });
    }

    public void setServiceStat(boolean running) {
        database.delete(ServiceStats.TABLE_NAME, null, null);
        ContentValues values = new ContentValues();
        values.put(ServiceStats.COLUMN_ID, 1);
        values.put(ServiceStats.COLUMN_RUNNING, running ? 1 : 0);
        long insertId = database.insert(ServiceStats.TABLE_NAME, null, values);
        Log.i(TAG, "InsertID for ServiceStat=" + insertId);
    }

    public boolean isServiceRunning() {
        Cursor cursor = database.query(ServiceStats.TABLE_NAME, null, null, null, null,
                null, null);
        if (cursor.moveToLast()) {
            return cursor.getInt(1) > 0;
        } else {
            return false;
        }
    }

}
