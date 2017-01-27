package fr.todolist.todolist;

import org.junit.Test;

import java.util.Calendar;

import fr.todolist.todolist.utils.DateTimeManager;

import static org.junit.Assert.assertEquals;

/**
 * Created by Stephane on 27/01/2017.
 */

public class DateTimeUnitTest {

    @Test
    public void Test_FormateDateTime() throws Exception {
        int year = 1970;
        int month = 0;
        int day = 1;
        int hour = 0;
        int minute = 0;

        String dateTime = DateTimeManager.formatDateTime(year, month, day, hour, minute);
        String expected = "1970-01-01 00:00";

        assertEquals(expected, dateTime);
    }

    @Test
    public void Test_CastDateTimeToUnixTime() throws Exception {
        long result = DateTimeManager.castDateTimeToUnixTime("1970-01-01 00:00");
        long expected = -3600000;
        assertEquals(expected, result);
    }

    @Test
    public void Test_ConvertUnitTimeToMs() throws Exception {
        String seconds = "Second(s)";
        String minutes = "Minute(s)";
        int value = 10;

        long result_s = DateTimeManager.getMs(value, seconds);
        long result_m = DateTimeManager.getMs(value, minutes);

        long expected_s = DateTimeManager.getValueFromMs(seconds, 1000 * 10);
        long expected_m = DateTimeManager.getValueFromMs(minutes, 1000 * 60 * 10);

        assertEquals(expected_s, result_s / 1000);
        assertEquals(expected_m, result_m / 1000 / 60);
    }

    @Test
    public void Test_DateTimeValid() throws Exception {
        long invalid_ms;
        long valid_ms ;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, cal.get(Calendar.SECOND) + 1);

        invalid_ms = cal.getTimeInMillis() - 1000;
        valid_ms = cal.getTimeInMillis();

        assertEquals(false, DateTimeManager.isDateTimeValid(invalid_ms));
        assertEquals(true, DateTimeManager.isDateTimeValid(valid_ms));
    }

    @Test
    public void Test_GetYear() throws Exception {
        assertEquals("2017", DateTimeManager.getYear(2017));
        assertEquals("1050", DateTimeManager.getYear(1050));
        assertEquals("2576", DateTimeManager.getYear(2576));
    }

    @Test
    public void Test_GetMonth() throws Exception {
        assertEquals("Jan", DateTimeManager.getMonth(0));
        assertEquals("Fev", DateTimeManager.getMonth(1));
        assertEquals("Mar", DateTimeManager.getMonth(2));
        assertEquals("Apr", DateTimeManager.getMonth(3));
        assertEquals("May", DateTimeManager.getMonth(4));
        assertEquals("Jun", DateTimeManager.getMonth(5));
        assertEquals("Jul", DateTimeManager.getMonth(6));
        assertEquals("Aug", DateTimeManager.getMonth(7));
        assertEquals("Sep", DateTimeManager.getMonth(8));
        assertEquals("Oct", DateTimeManager.getMonth(9));
        assertEquals("Nov", DateTimeManager.getMonth(10));
        assertEquals("Dec", DateTimeManager.getMonth(11));
    }

    @Test
    public void Test_GetDay() throws Exception {
        assertEquals("1st", DateTimeManager.getDay(1));
        assertEquals("2nd", DateTimeManager.getDay(2));
        assertEquals("3rd", DateTimeManager.getDay(3));
        assertEquals("4th", DateTimeManager.getDay(4));
    }

    @Test
    public void Test_IsToday() throws Exception {
        Calendar cal = Calendar.getInstance();
        assertEquals(true, DateTimeManager.isToday(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)));
    }

}
