package com.adam.sk.workingtimemanager;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.adam.sk.workingtimemanager.controller.TimeController;
import com.adam.sk.workingtimemanager.dager.property.Util;
import com.adam.sk.workingtimemanager.service.UpdaterService;
import com.android.datetimepicker.date.DatePickerDialog;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.markushi.ui.CircleButton;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Setup extends Fragment {

    public static final String SHARED_PREFERENCES_NAME = "com.osmand.settings";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String LONG_LAT_PATTERN = "\\d{0,4}\\.\\d{0,15}";
    public static final String LAT_LON_PATTERN = "#.##########";

    private AlarmManager alarmManager;

    @BindView(R.id.buttonUpdateWorkPeriod)
    CircleButton button;

    @BindView(R.id.workTimePeriod)
    EditText workTimePeriod;

    @BindView(R.id.button)
    Button start;

    @BindView(R.id.button3)
    Button stop;

    @BindView(R.id.buttonStartGps)
    Button buttonStartGps;

    @BindView(R.id.buttonStoptGps)
    Button buttonStoptGps;

    @BindView(R.id.longitude)
    EditText longitude;

    @BindView(R.id.latitude)
    EditText latitude;

    @BindView(R.id.workTime_period_layout)
    TextInputLayout workTime_period_layout;

    @BindView(R.id.latitude_layout)
    TextInputLayout latitude_layout;

    @BindView(R.id.longitude_layout)
    TextInputLayout longitude_layout;

    @BindView(R.id.notificationCheck)
    CheckBox notificationCheck;

    @BindView(R.id.gpsTracking)
    CheckBox gpsTracking;

    private TextView lblDate;
    private TextView lblTime;
    private Calendar calendar;
    private DateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private Context thisContext;
    private FragmentActivity myContext;
    private String workTimePeriodString;

    public Setup() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public String getOverTime(Long time) {
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toHours(time),
                TimeUnit.MILLISECONDS.toMinutes(time) % TimeUnit.HOURS.toMinutes(1));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setup, container, false);
        ButterKnife.bind(this, rootView);

        try {
            workTimePeriodString = Util.getProperty();
            long workTime = Long.valueOf(workTimePeriodString);
            workTimePeriod.setText(getOverTime(workTime));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Inflate the layout for this fragment

        thisContext = container.getContext();
        ButterKnife.bind(this, rootView);

        button.setOnClickListener(v -> {
            submitMainForm();
        });

        start.setOnClickListener(v -> startUpdateService(v));
        stop.setOnClickListener(v -> stopUpdateService(v));

        notificationCheck.setOnClickListener(v -> {
            if (notificationCheck.isChecked()) {
                start.performClick();
            }else{
                stop.performClick();
            }
        });

        if(checkIfServiceIsSchedulled()){
            notificationCheck.setChecked(true);
        }

        DecimalFormat df = new DecimalFormat(LAT_LON_PATTERN);
        SharedPreferences prefs = thisContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_WORLD_READABLE);

        double lat = (double) prefs.getFloat(LATITUDE, 8);
        double lon = (double) prefs.getFloat(LONGITUDE, 8);

        latitude.setText(String.valueOf(df.format(lat)));
        longitude.setText(String.valueOf(df.format(lon)));

        workTimePeriod.addTextChangedListener(new
                MyTextWatcher(workTimePeriod)
        );

        longitude.addTextChangedListener(new
                MyTextWatcher(longitude)
        );

        latitude.addTextChangedListener(new
                MyTextWatcher(latitude)
        );

        alarmManager = (AlarmManager) thisContext.getSystemService(thisContext.ALARM_SERVICE);

        return rootView;
    }

    private void submitMainForm() {
        submitForm();

        String timeText = workTimePeriod.getText().toString();
        Long minutes = Long.valueOf(timeText.substring(timeText.indexOf(":") + 1));
        Long hours = Long.valueOf(timeText.substring(0, timeText.indexOf(":")));

        minutes = minutes * 1000 * 60;
        hours = hours * 1000 * 60 * 60;

        hours = hours + minutes;
        try {
            Util.setProperty(hours);
            TimeController.WORK_PERIOD = Long.valueOf(Util.getProperty());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkIfServiceIsSchedulled() {
        //checking if alarm is working with pendingIntent #3
        Intent intent = new Intent(thisContext, UpdaterService.class);//the same as up
        intent.setAction(UpdaterService.ACTION_ALARM_RECEIVER);//the same as up
        boolean isWorking = (PendingIntent.getService(thisContext, 1001, intent, PendingIntent.FLAG_NO_CREATE) != null);//just changed the flag
        Log.e("TAG: TEST APP:  ", "alarm is " + (isWorking ? "" : "not") + " working...");
        return isWorking;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {

        }

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            //Do whatever you want
        }
    };

    @Override
    public void onAttach(Context context) {
        myContext = (FragmentActivity) context;
        super.onAttach(context);
    }


    @Override
    
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void startUpdateService(View view) {
        Toast.makeText(thisContext.getApplicationContext(), "Service start", Toast.LENGTH_SHORT).show();
        long aroundInterval = 5000;
        Intent intent = new Intent(thisContext, UpdaterService.class);
        intent.setAction(UpdaterService.ACTION_ALARM_RECEIVER);//my custom string action name
        PendingIntent pendingIntent = PendingIntent.getService(thisContext, 1001, intent, PendingIntent.FLAG_CANCEL_CURRENT);//used unique ID as 1001
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), aroundInterval, pendingIntent);//first start will start asap
    }

    public void stopUpdateService(View view) {
        Toast.makeText(thisContext.getApplicationContext(), "Service Stop", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(thisContext, UpdaterService.class);//the same as up
        intent.setAction(UpdaterService.ACTION_ALARM_RECEIVER);//the same as up
        PendingIntent pendingIntent = PendingIntent.getService(thisContext, 1001, intent, PendingIntent.FLAG_CANCEL_CURRENT);//the same as up
        alarmManager.cancel(pendingIntent);//important
        pendingIntent.cancel();//important
    }

    /**
     * Validating form
     */
    private void submitForm() {
        if (!validateWorkTimePeriod()) {
            Toast.makeText(thisContext.getApplicationContext(), "Invalid workTimePeriod valid (8:30)", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validateLongitude()) {
            Toast.makeText(thisContext.getApplicationContext(), "Invalid Longitude valid (8.32450)", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validateLatitude()) {
            Toast.makeText(thisContext.getApplicationContext(), "Invalid Latitude valid (8.32450)", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(thisContext.getApplicationContext(), "Everything is valid and saved", Toast.LENGTH_SHORT).show();
    }

    private boolean validateWorkTimePeriod() {
        String pattern = "\\d{0,2}:\\d\\d";
        String workTimePeriodText = workTimePeriod.getText().toString().trim();
        if (workTimePeriodText.isEmpty() || !(regex(pattern, workTimePeriodText))) {
            workTime_period_layout.setError(getString(R.string.err_msg_workTime));
            requestFocus(workTimePeriod);
            return false;
        } else {
            workTime_period_layout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean regex(String matcher, String workTimePeriodText) {
        return Pattern.compile(matcher).matcher(workTimePeriodText).matches();
    }

    private boolean validateLongitude() {
        String longituteEditText = longitude.getText().toString().trim();

        if (longituteEditText.isEmpty() || !(regex(LONG_LAT_PATTERN, longituteEditText))) {
            longitude_layout.setError(getString(R.string.err_msg_longitude));
            requestFocus(longitude);
            return false;
        } else {
            longitude_layout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateLatitude() {
        String latitudeEditText = latitude.getText().toString().trim();

        if (latitudeEditText.isEmpty() || !(regex(LONG_LAT_PATTERN, latitudeEditText))) {
            latitude_layout.setError(getString(R.string.err_msg_latitude));
            requestFocus(latitude);
            return false;
        } else {
            latitude_layout.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.workTimePeriod:
                    validateWorkTimePeriod();
                    break;
                case R.id.longitude:
                    validateLongitude();
                    break;
                case R.id.latitude:
                    validateLatitude();
                    break;
            }

        }
    }


}
