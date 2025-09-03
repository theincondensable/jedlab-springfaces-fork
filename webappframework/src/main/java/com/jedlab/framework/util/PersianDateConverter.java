package com.jedlab.framework.util;

import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;

/**
 * @author abbas
 */
public class PersianDateConverter implements Converter {
private static final PersianDateConverter INSTANCE = new PersianDateConverter();

    private PersianDateConverter() {
    }

    public static PersianDateConverter getInstance() {
        return INSTANCE;
    }

    /**
     * Determines if the given year is a leap year. Returns true if the
     * given year is a leap year.
     *
     * @param year the given year.
     * @return true if the given year is a leap year; false otherwise.
     */
    public static boolean isLeapYear(int year) {
        int mod = (year + 11) % 33;
        return mod % 4 == 0 && mod != 32;
    }

    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.length() == 0) return null;

        return convertSolarToGregorian(value);
    }

    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) return null;
        return GregorianToSolar((Date) value, false);
    }

    public Object convertSolarToGregorian(String value) {
        ULocale persianLocale = new ULocale("@calendar=persian");
        Calendar persianCal = Calendar.getInstance(persianLocale);
        persianCal.setTimeZone(TimeZone.getTimeZone("Asia/Tehran"));

        String[] valueSplit = value.split("/");
        int year = Integer.parseInt(valueSplit[0]);
        int month = Integer.parseInt(valueSplit[1]) - 1;
        int day = Integer.parseInt(valueSplit[2]);

        persianCal.set(year, month, day);

        return persianCal.getTime();
    }

    /**
     * @param solarDateAsTimeStamp : can be yyyy/MM/dd or yyyy/MM/dd HH:mm
     * @return Gregorian Date in STRING format
     */
    public String SolarToGregorian(String solarDateAsTimeStamp) {
        if (solarDateAsTimeStamp == null || solarDateAsTimeStamp.isEmpty()) return "";

        Calendar persianCal = preparePersianCalendar();
        TimeHolder th = prepareTimeHolder(solarDateAsTimeStamp);

        SimpleDateFormat resultFormat;
        if (th.getHour() == -1) {
            persianCal.set(th.getYear(), th.getMonth(), th.getDay());
            resultFormat = new SimpleDateFormat("yyyy/MM/dd");
        } else {
            persianCal.set(th.getYear(), th.getMonth(), th.getDay(), th.getHour(), th.getMinute());
            resultFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        }

        return resultFormat.format(persianCal.getTime());
    }

    /**
     * @param solarDateAsTimeStamp : can be yyyy/MM/dd or yyyy/MM/dd HH:mm
     * @return Gregorian Date in DATE format
     */
    public Date SolarToGregorianAsDate(String solarDateAsTimeStamp) {
        if (solarDateAsTimeStamp == null || solarDateAsTimeStamp.isEmpty()) return null;

        Calendar persianCal = preparePersianCalendar();
        TimeHolder th = prepareTimeHolder(solarDateAsTimeStamp);

        if (th.getHour() == -1)
            persianCal.set(th.getYear(), th.getMonth(), th.getDay());
        else
            persianCal.set(th.getYear(), th.getMonth(), th.getDay(), th.getHour(), th.getMinute());

        return persianCal.getTime();
    }

    public String GregorianToSolar(String gregorianDateAsTimeStamp) {
        SimpleDateFormat gregorianDF;
        SimpleDateFormat persianDF;
        ULocale persianLocale = new ULocale("@calendar=persian");
        if (gregorianDateAsTimeStamp.contains(":")) {
            gregorianDF = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US);
            persianDF = new SimpleDateFormat("yyyy/MM/dd HH:mm", persianLocale);
        } else {
            gregorianDF = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
            persianDF = new SimpleDateFormat("yyyy/MM/dd", persianLocale);
        }

        gregorianDF.setCalendar(Calendar.getInstance(new ULocale("@calendar=gregorian")));
        gregorianDF.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date = null;
        try {
            date = gregorianDF.parse(gregorianDateAsTimeStamp);
        } catch (Exception e) {
            e.getMessage();
        }

        persianDF.setTimeZone(TimeZone.getTimeZone("UTC"));

        return persianDF.format(date);
    }

    public String GregorianToSolar(Date gregorianDateAsTimeStamp, Boolean showTime) {
        ULocale persianLocale = new ULocale("@calendar=persian");
        SimpleDateFormat persianDF;
        if (showTime)
            persianDF = new SimpleDateFormat("yyyy/MM/dd HH:mm", persianLocale);
        else
            persianDF = new SimpleDateFormat("yyyy/MM/dd", persianLocale);
        return persianDF.format(gregorianDateAsTimeStamp);
    }

    private Calendar preparePersianCalendar() {
        ULocale persianLocale = new ULocale("@calendar=persian");
        Calendar persianCal = Calendar.getInstance(persianLocale);
        persianCal.setTimeZone(TimeZone.getTimeZone("Asia/Tehran"));

        return persianCal;
    }

    private TimeHolder prepareTimeHolder(String input) {
        String[] valueSplit = input.split("/");
        TimeHolder timeHolder = new TimeHolder(Integer.parseInt(
                valueSplit[0]),
                Integer.parseInt(valueSplit[1]) - 1,
                Integer.parseInt(valueSplit[2].split(" ")[0])
        );

        // then it is in format of yyyy/MM/dd HH:mm
        if (input.contains(":")) {
            String[] timeSection = valueSplit[2].split(":");
            timeHolder.setHour(Integer.parseInt(timeSection[0].split(" ")[1]));
            timeHolder.setMinute(Integer.parseInt(timeSection[1]));
        }

        return timeHolder;
    }

    private static class TimeHolder {
        private int year;
        private int month;
        private int day;
        private int hour;
        private int minute;

        private TimeHolder() {

        }

        public TimeHolder(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.hour = -1;
            this.minute = 0;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public int getHour() {
            return hour;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public int getMinute() {
            return minute;
        }

        public void setMinute(int minute) {
            this.minute = minute;
        }
    }
    
}
