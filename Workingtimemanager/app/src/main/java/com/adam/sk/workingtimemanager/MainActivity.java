package com.adam.sk.workingtimemanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.adam.sk.workingtimemanager.controller.TimeController;
import com.adam.sk.workingtimemanager.dager.DaggerWorkTimeComponent;
import com.adam.sk.workingtimemanager.dager.WorkTimeComponent;
import com.adam.sk.workingtimemanager.dager.WorkTimeModule;

import org.joda.time.DateTime;

import java.util.Date;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    private TimeController timeController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WorkTimeComponent component = DaggerWorkTimeComponent.builder().workTimeModule(new WorkTimeModule()).build();
        timeController = component.provideWorTimeController();
        timeController.saveWorkTime(new DateTime(new Date()));

    }
}
