package com.adam.sk.workingtimemanager.dager.property;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.adam.sk.workingtimemanager.entity.Property;
import com.orm.query.Select;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;

/**
 * Created by root on 1.6.2016.
 */
public class Util {

    private static Property property;

    public static String getProperty() throws IOException {
        String propertyString = "30600000";
        Property property = Select.from(Property.class)
                .first();
        if (!(property == null)) {
            propertyString = String.valueOf(property.getWorkTimePeriod());
        }
        return propertyString;
    }

    public static void setProperty(Long workTimeLong) throws IOException {
        Property propertyNull = Select.from(Property.class)
                .first();
        if (propertyNull == null) {
            workTimeLong = (workTimeLong == null) ? 30600000l : workTimeLong;
            property = new Property(workTimeLong);
            property.save();
        } else {
            property = new Property(workTimeLong);
            property.update();
        }
    }
}