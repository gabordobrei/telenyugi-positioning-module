
package hu.bme.emt.telenyugi;

import android.content.Context;
import android.content.SharedPreferences;

public class Preference {

    private static final String SHARED_PREFERENCES_NAME = "telenyugi-preferences";
    private SharedPreferences preferences;
    private static final String PREF_LAST_UPLOAD = "last-upload";

    public Preference(Context context) {
        preferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public long getLastupload() {
        return preferences.getLong(PREF_LAST_UPLOAD, 0);
    }

    public void setLastUpload(long time) {
        preferences.edit().putLong(PREF_LAST_UPLOAD, time).commit();
    }

    /**
     * @return The threshold in minutes
     */
    public int getThreshold() {
        return 10;
    }

}
