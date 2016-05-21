package com.adam.sk.workingtimemanager.controller;

import android.util.Log;

import com.adam.sk.workingtimemanager.controller.api.ITimeController;
import com.adam.sk.workingtimemanager.dager.WorkTimeComponent;
import com.adam.sk.workingtimemanager.entity.WorkTimeRecord;
import com.annimon.stream.Stream;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;

import dagger.Component;

public class TimeController implements ITimeController {

    public static final long WORK_PERIOD = 30600000l;

    public static final String TAG = "TimeController";

    @Override
    public void saveWorkTime(DateTime date) {
        WorkTimeRecord lastWorkTimeRecord = findWorkTimeForThisDay();

        if (lastWorkTimeRecord == null) {
            Log.d(TAG, "create new workTime");
            WorkTimeRecord workTimeRecord = new WorkTimeRecord(date.toDate());
            workTimeRecord.save();
        } else {
            Log.d(TAG, "update workTime");
            lastWorkTimeRecord.setLeaveDate(date.toDate());
            lastWorkTimeRecord.save();
        }
    }

    private WorkTimeRecord findWorkTimeForThisDay() {
        return Select.from(WorkTimeRecord.class).where(Condition.prop("leave_date").isNull()).groupBy("arrival_date").first();
    }

    @Override
    public String workTimeOv() {
        return null;
    }

    @Override
    public String workTime() {
        return null;
    }

    private long workTimePart =0;
    private long getOverTime(DateTime today) {
        DateTime startOfWeek = today.weekOfWeekyear().roundFloorCopy();
        DateTime endOfWeek = today.weekOfWeekyear().roundCeilingCopy();
        long workTime = 0;
        List<WorkTimeRecord> workTimeRecords = getWorkTimeRecordsRange(startOfWeek, endOfWeek);
        for (int dayOFWeek = 0; dayOFWeek < 7; dayOFWeek++) {
            workTimePart = 0;
            DateTime nextDayCal = startOfWeek.plusDays(dayOFWeek);
            Stream.of(workTimeRecords).filter(workTimeLambda -> new DateTime(workTimeLambda.getArrivalDate()).getDayOfMonth() == nextDayCal.getDayOfMonth()).forEach(w ->{
                workTimePart += w.getLeaveDate().getTime() - w.getArrivalDate().getTime();
            });
            workTime += (workTimePart != 0) ? workTimePart - WORK_PERIOD : 0;
        }
        return workTime;
    }

    private List<WorkTimeRecord> getWorkTimeRecordsRange(DateTime startOfWeek, DateTime endOfWeek) {
        return Select.from(WorkTimeRecord.class).where(Condition.prop("arrival_date").gt(startOfWeek.toDate().getTime()), Condition.prop("arrival_date").lt(endOfWeek.toDate().getTime())).groupBy("arrival_date").list();
    }

}
