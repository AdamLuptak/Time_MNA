package com.adam.sk.workingtimemanager.dager;

import com.adam.sk.workingtimemanager.controller.TimeController;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by root on 20.5.2016.
 */
@Module
public class WorkTimeModule {

    @Provides
    @Singleton
    TimeController provideWorTimeController(){
        return new TimeController();
    }

}
