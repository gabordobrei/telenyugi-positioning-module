
package hu.bme.emt.telenyugi.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "commments.db";
    private static final String SPACE = " ";
    private static final String COMMENT = ",";
    private static final int DATABASE_VERSION = 1;

    public SqlLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LocationFootprints.onCreate(db);
        ServiceStats.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static final class LocationFootprints {
        public static final String TABLE_NAME = "footprints";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_TIMESTAMP = "created";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_ALTITUDE = "altitude";
        public static final String COLUMN_UPLOADED = "uploaded";

        public static final String TYPE_TIMESTAMP = "INTEGER";
        public static final String TYPE_LATITUDE = "FLOAT";
        public static final String TYPE_LONGITUDE = "FLOAT";
        public static final String TYPE_ALTITUDE = "FLOAT";
        public static final String TYPE_UPLOADED = "INTEGER";

        public static void onCreate(SQLiteDatabase db) {
            StringBuilder builder = new StringBuilder("CREATE TABLE ");
            builder.append(TABLE_NAME).append("( ");
            builder.append(COLUMN_ID).append(SPACE).append(" integer primary key autoincrement")
                    .append(COMMENT);
            builder.append(COLUMN_TIMESTAMP).append(SPACE).append(TYPE_TIMESTAMP).append(COMMENT);
            builder.append(COLUMN_LATITUDE).append(SPACE).append(TYPE_LATITUDE).append(COMMENT);
            builder.append(COLUMN_LONGITUDE).append(SPACE).append(TYPE_LONGITUDE).append(COMMENT);
            builder.append(COLUMN_ALTITUDE).append(SPACE).append(TYPE_ALTITUDE).append(COMMENT);
            builder.append(COLUMN_UPLOADED).append(SPACE).append(TYPE_UPLOADED);
            builder.append(")");
            db.execSQL(builder.toString());
        }
    }

    public static final class ServiceStats {
        public static final String TABLE_NAME = "servicestats";
        public static final String COLUMN_RUNNING = "running";
        public static final String COLUMN_ID = "_id";
        public static final String TYPE_RUNNING = "INTEGER";

        public static void onCreate(SQLiteDatabase db) {
            StringBuilder builder = new StringBuilder("CREATE TABLE ");
            builder.append(TABLE_NAME).append("( ");
            builder.append(COLUMN_ID).append(SPACE).append(" integer primary key").append(COMMENT);
            builder.append(COLUMN_RUNNING).append(SPACE);
            builder.append(")");
            db.execSQL(builder.toString());
        }
    }

}
