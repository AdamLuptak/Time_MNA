package com.adam.sk.workingtimemanager;

import com.adam.sk.workingtimemanager.dager.DaggerWorkTimeComponent;
import com.adam.sk.workingtimemanager.dager.WorkTimeComponent;
import com.adam.sk.workingtimemanager.dager.WorkTimeModule;
import com.orm.SugarApp;

/**
 * Created by root on 28.5.2016.
 */
public class Main extends SugarApp {

    WorkTimeComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        buildComponentAndInject();
    }

    public void buildComponentAndInject() {
        component = DaggerWorkTimeComponent.builder().workTimeModule(new WorkTimeModule(this)).build();
    }

    public WorkTimeComponent getComponent() {
        return component;
    }
}
