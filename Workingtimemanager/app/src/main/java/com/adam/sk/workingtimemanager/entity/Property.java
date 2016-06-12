package com.adam.sk.workingtimemanager.entity;

import com.orm.SugarApp;
import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by root on 6.6.2016.
 */
public class Property extends SugarRecord implements Serializable {

    long workTimePeriod;

    public Property(){

    }

    public Property(long workTimePeriod) {
        this.workTimePeriod = workTimePeriod;
    }

    public long getWorkTimePeriod() {
        return workTimePeriod;
    }

    public void setWorkTimePeriod(long workTimePeriod) {
        this.workTimePeriod = workTimePeriod;
    }
}
