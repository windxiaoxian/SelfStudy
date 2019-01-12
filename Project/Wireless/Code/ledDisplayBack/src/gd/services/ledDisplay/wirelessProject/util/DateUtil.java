package gd.services.ledDisplay.wirelessProject.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 徐文正 on 2019/1/4.
 */
public class DateUtil {
    public static String getDesignedDateStr(long microSeconds, String formatInfo) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatInfo);
        Date now = new Date();
        Date afterDate = new Date(now.getTime() + microSeconds);
        return sdf.format(afterDate);
    }

    public static Date getDesignedDate(long microSeconds) {
        Date now = new Date();
        Date afterDate = new Date(now.getTime() + microSeconds);
        return afterDate;
    }

    public static String getNowStr(String formatInfo) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatInfo);
        Date now = new Date();
        return sdf.format(now);
    }

    public static String getDateToStr(Date date, String formatInfo) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatInfo);
        return sdf.format(date);
    }
}
