package com.adam.sk.workingtimemanager.controller.api;

import org.joda.time.DateTime;

/**
 * Created by root on 20.5.2016.
 */
public interface ITimeController {
    // TODO if has  record than update , if not than create
    void saveWorkTime(DateTime date);

    String workTimeOv();

    String workTime();
}
