package gd.services.ledDisplay.wirelessProject.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 徐文正 on 2019/1/4.
 */
public class DateUtil {
    private static final Logger logger = LogManager.getLogger(DateUtil.class.getName());

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

    public static Date strToDate(String dateStr, String formatInfo) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatInfo);
        Date date;
        try {
            date = sdf.parse(dateStr);
        } catch (Exception e) {
            logger.error("[DateUtil.strToDate]后台报错：" + e.getMessage());
            date = new Date();
        }
        return date;
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
