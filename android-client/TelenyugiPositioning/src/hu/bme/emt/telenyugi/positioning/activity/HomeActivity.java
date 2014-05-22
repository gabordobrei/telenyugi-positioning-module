
package hu.bme.emt.telenyugi.positioning.activity;

import hu.bme.emt.telenyugi.UIUtils;
import hu.bme.emt.telenyugi.db.SqlDatabase;
import hu.bme.emt.telenyugi.location.LocationService;
import hu.bme.emt.telenyugi.model.LocationFootprint;
import hu.bme.emt.telenyugi.positioning.R;
import hu.bme.emt.telenyugi.positioning.utils.DateUtils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class HomeActivity extends SherlockActivity {

    protected static final String TAG = HomeActivity.class.getSimpleName();
    private TextView kmTitle;
    private TextView kmValview;
    private TextView weeklyTitle;
    private TextView weeklyTotalTitle;
    private TextView weeklyTotalValue;
    private TextView weeklyDailyTitle;
    private TextView weeklyDailyValue;
    private TextView summaryTitle;
    private TextView summaryStatusTitle;
    private TextView summaryStatusValue;
    private SqlDatabase db;
    private PollerHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        kmTitle = (TextView) findViewById(R.id.km_view_title);
        kmValview = (TextView) findViewById(R.id.km_view_valview);
        weeklyTitle = (TextView) findViewById(R.id.weekly_title);
        weeklyTotalTitle = (TextView) findViewById(R.id.weekly_total_title);
        weeklyTotalValue = (TextView) findViewById(R.id.weekly_total_value);
        weeklyDailyTitle = (TextView) findViewById(R.id.weekly_daily_title);
        weeklyDailyValue = (TextView) findViewById(R.id.weekly_daily_value);
        summaryTitle = (TextView) findViewById(R.id.summary_title);
        summaryStatusTitle = (TextView) findViewById(R.id.summary_total_title);
        summaryStatusValue = (TextView) findViewById(R.id.summary_total_value);
        db = new SqlDatabase(this);
        db.open();
        mHandler = new PollerHandler();
        initViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(PollerHandler.WHAT_POLL_SERVICE);
        db.close();
    }

    private void initViews() {
        String serviceStatus = db.isServiceRunning() ? "Running" : "Stopped";
        summaryStatusValue.setText(serviceStatus);
        weeklyTitle.setTypeface(UIUtils.getRobotoRegular(getApplicationContext()));
        summaryTitle.setTypeface(UIUtils.getRobotoRegular(getApplicationContext()));
        weeklyTotalTitle.setTypeface(UIUtils.getRobotoRegular(getApplicationContext()));
        weeklyTotalValue.setTypeface(UIUtils.getRobotoBold(getApplicationContext()));
        weeklyDailyTitle.setTypeface(UIUtils.getRobotoRegular(getApplicationContext()));
        weeklyDailyValue.setTypeface(UIUtils.getRobotoBold(getApplicationContext()));
        summaryStatusTitle.setTypeface(UIUtils.getRobotoRegular(getApplicationContext()));
        summaryStatusValue.setTypeface(UIUtils.getRobotoBold(getApplicationContext()));

        double distanceCurrentWeek = getDistanceCurrentWeek() / 1000;
        double distanceToday = getDistanceToday() / 1000;
        NumberFormat kmFormat = NumberFormat.getInstance();
        kmFormat.setMaximumFractionDigits(1);
        kmFormat.setMinimumFractionDigits(0);
        String distanceTodayString = kmFormat.format(distanceToday);
        kmTitle.setText(UIUtils.setKmTitle("You walked " + distanceTodayString
                + " km today!", "You walked ".length(),
                distanceTodayString.length(), getApplicationContext()));
        double neededKm = Math.max(0, 2.0 - distanceToday);
        kmValview.setText(UIUtils.setKmValue(kmFormat.format(neededKm), getApplicationContext()));
        weeklyTotalValue.setText(kmFormat.format(distanceCurrentWeek) + " km");

    }

    public void onServiceStart(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title, positiveButton;
        final boolean isRunning = db.isServiceRunning();
        title = getString(isRunning ? R.string.want_to_stop_service
                : R.string.want_to_start_service);
        positiveButton = getString(isRunning ? R.string.stop_service
                : R.string.start_service);
        builder.setTitle(title);

        final int what = PollerHandler.WHAT_POLL_SERVICE;
        final Intent service = new Intent(HomeActivity.this, LocationService.class);
        builder.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mHandler.removeMessages(what);
                if (!isRunning) {
                    startService(service);
                }
                else {
                    boolean status = stopService(service);
                    Log.e(TAG, "Service stop result = " + status);
                }
                mHandler.sendMessage(getPollingMessage(isRunning));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private Message getPollingMessage(boolean previousStatus) {
        int status = previousStatus ? 1 : 0;
        return mHandler.obtainMessage(PollerHandler.WHAT_POLL_SERVICE, status, 1,
                Integer.valueOf(50));
    }

    private class PollerHandler extends Handler {

        public static final int WHAT_POLL_SERVICE = 1;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (db.isClosed()) {
                return;
            }
            final boolean isRunning = db.isServiceRunning();
            final String serviceStatus = isRunning ? "Running" : "Stopped";
            String oldStatus = summaryStatusValue.getText().toString();
            Log.v(TAG, oldStatus + " -> " + serviceStatus);
            if (!oldStatus.equalsIgnoreCase(serviceStatus)) {
                final Animation anim1 = new TranslateAnimation(0, -500, 0, 0);
                anim1.setDuration(300);
                anim1.setRepeatCount(1);
                anim1.setRepeatMode(Animation.REVERSE);
                anim1.setAnimationListener(new AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        summaryStatusValue.setText(serviceStatus);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }

                });
                summaryStatusValue.startAnimation(anim1);

            }
            Integer newDelay = Integer.valueOf(5000);
            Message newMessage = obtainMessage(WHAT_POLL_SERVICE, msg.arg1, msg.arg2 + 1, newDelay);
            this.sendMessageDelayed(newMessage, newDelay);
            Log.v(TAG, "Rescheduling msg " + msg.toString());
        }
    }

    public double getDistanceToday() {
        return getDistanceCovered(db.getFootprints(DateUtils.getMidnight()
                .getTime()));
    }

    public double getDistanceCurrentWeek() {
        return getDistanceCovered(db.getFootprints(DateUtils.getStartOfWeek()
                .getTime()));
    }

    private double getDistanceCovered(ArrayList<LocationFootprint> footprints) {
        if (footprints.size() < 2) {
            return 0;
        }
        Collections.sort(footprints);
        double result = 0;
        for (int i = 0; i < footprints.size() - 1; i++) {
            LocationFootprint from = footprints.get(i);
            LocationFootprint to = footprints.get(i + 1);
            result += haversine(from, to);
        }
        return result;
    }

    public double haversine(LocationFootprint from, LocationFootprint to) {

        double R = 6371 * 1000; // km

        double dLongitude = Math.toRadians(from.longitude
                - to.longitude);
        double dLatitude = Math
                .toRadians(from.latitude - to.latitude);

        double sindLat = Math.sin(dLatitude / 2.0);
        double sindLong = Math.sin(dLongitude / 2.0);
        double a = sindLat * sindLat
                + Math.cos(Math.toRadians(from.latitude))
                * Math.cos(Math.toRadians(to.latitude)) * sindLong
                * sindLong;
        double b = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = R * b;

        return distance;

    }
}
