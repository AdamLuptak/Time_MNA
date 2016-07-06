package com.adam.sk.workingtimemanager.dager;

import android.app.Application;
import android.content.Context;

import com.adam.sk.workingtimemanager.controller.LocationController;
import com.adam.sk.workingtimemanager.controller.TimeController;
import com.adam.sk.workingtimemanager.dager.property.Util;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class WorkTimeModule {

    private final Application application;

    public WorkTimeModule(Application application) {
        this.application = application;
    }

    @Provides
    Context provideContext() {
        return application;
    }


    @Provides
    @Singleton
    TimeController provideWorTimeController() {
        TimeController timeController = new TimeController();
        try {
            String workTimeInterval = Util.getProperty();
            if (workTimeInterval == null) {
                Util.setProperty(30600000l);
            }
            timeController.WORK_PERIOD = Long.valueOf(Util.getProperty());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return timeController;
    }

    @Provides
    @Singleton
    LocationController providerLocationController() {
        return new LocationController(application.getApplicationContext());
    }


}
