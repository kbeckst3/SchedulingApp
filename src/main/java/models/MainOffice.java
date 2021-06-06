package models;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.util.TimeZone;

/**
 * Abstract class mostly used for validation purposes and so the company has a singular point to come to to adjust any sort office
 * hours or relocating of the main office.
 */
public abstract class MainOffice {
    //This class is used to check against appointment times also used to convert times
    private static final TimeZone officeTimeZone = TimeZone.getTimeZone("US/Eastern");
    private static final LocalTime officeOpenTime = LocalTime.of(8, 0, 0, 0);
    private static final LocalTime officeCloseTime = LocalTime.of(22, 0, 0, 0);

    public static TimeZone getOfficeTimeZone() {
        return officeTimeZone;
    }

    public static LocalTime getOfficeOpenTime() {
        return officeOpenTime;
    }

    public static LocalTime getOfficeCloseTime() {
        return officeCloseTime;
    }

    public static ZoneId getOfficeZoneId() {
        return getOfficeTimeZone().toZoneId();
    }

    public static boolean isAppointmentInOfficeHours(ZonedDateTime startZoneDateTime, ZonedDateTime endZoneDateTime) {

        //Convert ZonedTimes to the office time zone of EST
        ZonedDateTime estStart = startZoneDateTime.withZoneSameInstant(getOfficeZoneId());
        ZonedDateTime estEnd = endZoneDateTime.withZoneSameInstant(getOfficeZoneId());
        //Set office dates to sames dates as start and end to make the check robust enough for any timezone
        ZonedDateTime officeOpenDateTime = ZonedDateTime.of(estStart.toLocalDate(), getOfficeOpenTime(), getOfficeZoneId());
        ZonedDateTime officeCloseDateTime = ZonedDateTime.of(estEnd.toLocalDate(), getOfficeCloseTime(), getOfficeZoneId());

        //Check to see if time is in office hours.
        return ((estStart.isAfter(ChronoZonedDateTime.from(officeOpenDateTime)) || estStart.isEqual(ChronoZonedDateTime.from(officeOpenDateTime))) &&
                (estEnd.isBefore(ChronoZonedDateTime.from(officeCloseDateTime)) || estEnd.isEqual(ChronoZonedDateTime.from(officeCloseDateTime))));

    }

}
