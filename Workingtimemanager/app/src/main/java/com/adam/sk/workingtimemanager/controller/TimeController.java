package com.adam.sk.workingtimemanager.controller;

import android.util.Log;

import com.adam.sk.workingtimemanager.controller.api.ITimeController;
import com.adam.sk.workingtimemanager.entity.WorkTimeRecord;
import com.annimon.stream.Stream;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeController implements ITimeController {

    public static long WORK_PERIOD = 30600000;

    public static final String TAG = "TimeController";

    private String goHomeTime;

    private String goHomeTimeOV;

    private String overTime;

    private Long overTimeMillis;

    private Long goHomeOvMillis;

    private Long goHomeMillis;

    @Override
    public void saveWorkTime(DateTime date) {
        WorkTimeRecord lastWorkTimeRecord = findWorkTimeForThisDay();

        if (lastWorkTimeRecord == null) {
            Log.d(TAG, "create new workTime");
            WorkTimeRecord workTimeRecord = new WorkTimeRecord(date.toDate());
            workTimeRecord.save();

            calculateTime(date);

        } else {
            Log.d(TAG, "update workTime");
            lastWorkTimeRecord.setLeaveDate(date.toDate());
            lastWorkTimeRecord.save();
        }
    }

    public void calculateTime(DateTime date) {
        this.overTimeMillis = getWeekOverTime(date);

        WorkTimeRecord lastComeToWork = getLastWorkTimeRecordNull(date.getMillis());

        if (lastComeToWork == null) {
            goHomeMillis = 0l;
            goHomeOvMillis = 0l;
        } else {
            goHomeMillis = lastComeToWork.getArrivalDate().getTime() + WORK_PERIOD;
            goHomeOvMillis = goHomeMillis - this.overTimeMillis;
        }
    }

    public WorkTimeRecord findWorkTimeForThisDay() {
        return Select.from(WorkTimeRecord.class).where(Condition.prop("leave_date").isNull()).groupBy("arrival_date").first();
    }

    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

    @Override
    public String getGoHomeTimeOv() {

        return formatter.format(new DateTime(goHomeOvMillis).toDate()).toString();
    }

    @Override
    public String getGoHomeTime() {
        return formatter.format(new DateTime(goHomeMillis).toDate()).toString();
    }

    @Override
    public String getOverTime() {
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toHours(overTimeMillis),
                TimeUnit.MILLISECONDS.toMinutes(Math.abs(overTimeMillis)) % TimeUnit.HOURS.toMinutes(1));

    }

    long workTimePart = 0;

    public WorkTimeRecord getLastWorkTimeRecordNull(long fridayCome) {
        DateTime fridayComeDate = new DateTime(fridayCome).withHourOfDay(0).withSecondOfMinute(0);
        return Select.from(WorkTimeRecord.class).where(Condition.prop("arrival_date").gt(fridayComeDate.toDate().getTime())).groupBy("arrival_date").first();
    }

    public Long getWeekOverTime(DateTime today) {
        DateTime startOfWeek = today.weekOfWeekyear().roundFloorCopy();
        DateTime endOfWeek = today.weekOfWeekyear().roundCeilingCopy();
        long workTime = 0;

        List<WorkTimeRecord> workTimeRecords = getWorkTimeRecordsRange(startOfWeek, endOfWeek);

        for (int dayOFWeek = 0; dayOFWeek < 7; dayOFWeek++) {
            workTimePart = 0;
            DateTime nextDayCal = startOfWeek.plusDays(dayOFWeek);
            Stream.of(workTimeRecords).filter(workTimeLambda -> new DateTime(workTimeLambda.getArrivalDate()).getDayOfMonth() == nextDayCal.getDayOfMonth()).forEach(w -> {
                workTimePart += w.getLeaveDate().getTime() - w.getArrivalDate().getTime();
            });
            workTime += (workTimePart != 0) ? workTimePart - WORK_PERIOD : 0;
        }
        return workTime;
    }

    private List<WorkTimeRecord> getWorkTimeRecordsRange(DateTime startOfWeek, DateTime endOfWeek) {
        return Select.from(WorkTimeRecord.class).where(Condition.prop("arrival_date").gt(startOfWeek.toDate().getTime()), Condition.prop("arrival_date").lt(endOfWeek.toDate().getTime()), Condition.prop("leave_date").isNotNull()).groupBy("arrival_date").list();
    }

    public Long getOverTimeMillis() {
        return overTimeMillis;
    }

    public void setOverTimeMillis(Long overTimeMillis) {
        this.overTimeMillis = overTimeMillis;
    }

    public Long getGoHomeOvMillis() {
        return goHomeOvMillis;
    }

    public void setGoHomeOvMillis(Long goHomeOvMillis) {
        this.goHomeOvMillis = goHomeOvMillis;
    }

    public Long getGoHomeMillis() {
        return goHomeMillis;
    }

    public void setGoHomeMillis(Long goHomeMillis) {
        this.goHomeMillis = goHomeMillis;
    }

    public List<WorkTimeRecord> getYesterdayFoCorection(DateTime today){
        DateTime yesterday = today.minusDays(1).withHourOfDay(23).withSecondOfMinute(59);;
        return Select.from(WorkTimeRecord.class).where(Condition.prop("arrival_date").lt(yesterday.toDate().getTime()),Condition.prop("leave_date").isNull()).list();
    }

}
