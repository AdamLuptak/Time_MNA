package com.adam.sk.workingtimemanager.dager;

import com.adam.sk.workingtimemanager.MainActivity;
import com.adam.sk.workingtimemanager.controller.TimeController;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = {WorkTimeModule.class})
public interface WorkTimeComponent {
    void inject(MainActivity mainActivity);

    TimeController provideWorTimeController();

}
