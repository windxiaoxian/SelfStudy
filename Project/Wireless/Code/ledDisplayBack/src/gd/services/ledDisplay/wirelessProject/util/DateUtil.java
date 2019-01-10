package gd.services.ledDisplay.wirelessProject.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 徐文正 on 2019/1/4.
 */
public class DateUtil {
    public static String getDesignedDateStr(long microSeconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        Date afterDate = new Date(now.getTime() + microSeconds);
        return sdf.format(afterDate);
    }
}
