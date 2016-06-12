package com.adam.sk.workingtimemanager;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.adam.sk.workingtimemanager.controller.TimeController;
import com.adam.sk.workingtimemanager.dager.property.Util;
import com.adam.sk.workingtimemanager.service.UpdaterService;
import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;

import org.joda.time.DateTime;
import org.joda.time.chrono.StrictChronology;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import at.markushi.ui.CircleButton;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Setup extends Fragment {

    @BindView(R.id.buttonUpdateWorkPeriod)
    CircleButton button;

    @BindView(R.id.editTextArrivalTime)
    EditText editText;

    @BindView(R.id.button)
    Button start;

    @BindView(R.id.button3)
    Button stop;

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
            editText.setText(getOverTime(workTime));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Inflate the layout for this fragment

        thisContext = container.getContext();
        ButterKnife.bind(this, rootView);

        button.setOnClickListener(v -> {
            String timeText = editText.getText().toString();
            Long minutes = Long.valueOf(timeText.substring(timeText.indexOf(":")+1));
            Long hours = Long.valueOf(timeText.substring(0,timeText.indexOf(":")));

            minutes = minutes * 1000 * 60;
            hours = hours * 1000 * 60 * 60;

            hours = hours + minutes;
            try {
                Util.setProperty(hours);
                TimeController.WORK_PERIOD = Long.valueOf(Util.getProperty());
            } catch (IOException e) {
                e.printStackTrace();
            }

            Toast.makeText(thisContext.getApplicationContext(),"New workTime Period saved: " + timeText , Toast.LENGTH_SHORT).show();

        });

        start.setOnClickListener(v -> startUpdateService(v));
        stop.setOnClickListener(v -> stopUpdateService(v));

        return rootView;
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
        Intent actIntent = new Intent(thisContext, UpdaterService.class);
        PendingIntent pi = PendingIntent.getService(thisContext, 0, actIntent, 0);
        Log.e("sdfs", "start service");
        AlarmManager am = (AlarmManager) thisContext.getSystemService(thisContext.ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                5000, // 1000 * 60 * 15,
                pi);
    }

    public void stopUpdateService(View view) {
        Log.e("sdfs", "stop service");
        Intent intentstop = new Intent(thisContext, UpdaterService.class);
        AlarmManager alarmManager = (AlarmManager) thisContext.getSystemService(thisContext.ALARM_SERVICE);
        PendingIntent displayIntent = PendingIntent.getService(thisContext.getApplicationContext(), 0, intentstop, 0);
        alarmManager.cancel(displayIntent);
    }


}
