package com.adam.sk.workingtimemanager.controller.api;

import org.joda.time.DateTime;

/**
 * Created by root on 20.5.2016.
 */
public interface ITimeController {

    void saveWorkTime(DateTime date);

    String getGoHomeTimeOv();

    String getGoHomeTime();

    String getOverTime();
}
