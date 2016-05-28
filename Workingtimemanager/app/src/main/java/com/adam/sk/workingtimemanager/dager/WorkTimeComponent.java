package com.adam.sk.workingtimemanager.dager;

import com.adam.sk.workingtimemanager.EditWorkTimeFragment;
import com.adam.sk.workingtimemanager.HomeFragment;
import com.adam.sk.workingtimemanager.Setup;
import com.adam.sk.workingtimemanager.controller.TimeController;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = {WorkTimeModule.class})
public interface WorkTimeComponent {
    void inject(HomeFragment homeFragment);
    void inject(EditWorkTimeFragment editWorkTimeFragment);
    void inject(Setup setup);

    TimeController provideWorTimeController();
}
