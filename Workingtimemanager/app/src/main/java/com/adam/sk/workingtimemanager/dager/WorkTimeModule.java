package com.adam.sk.workingtimemanager.dager;

import android.app.Application;
import android.content.Context;

import com.adam.sk.workingtimemanager.controller.TimeController;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by root on 20.5.2016.
 */
@Module
public class WorkTimeModule {

    private final Application application;

    public WorkTimeModule(Application application) {
        this.application = application;
    }

    @Provides
    Context provideContext(){
        return application;
    }


    @Provides
    @Singleton
    TimeController provideWorTimeController(){
        return new TimeController();
    }

}
