package gd.services.ledDisplay.wirelessProject.dao;

import java.util.Map;

public class ScheduleDAO {

    public static String delTransStatus(Map reqMap) {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("  delete wxj_transmitter_status_t where station_code = '")
                .append(reqMap.get("stationCode"))
                .append("' and trans_code = '")
                .append(reqMap.get("transCode"))
                .append("'  ");
        return strBuffer.toString();
    }

    public static String insTransStatus(Map reqMap) {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("  insert into wxj_transmitter_status_t ")
                .append(" ( ")
                .append("station_code, ")
                .append("trans_code, ")
                .append("status_time, ")
                .append("work_status ")
                .append(" ) ")
                .append("values ")
                .append(" ( ")
                .append("'" + reqMap.get("stationCode") + "', ")
                .append("'" + reqMap.get("transCode") + "', ")
                .append("to_date('" + reqMap.get("statusTime") + "','yyyy-MM-dd HH24:Mi:ss'), ")
                .append("'" + reqMap.get("workStatus") + "' ")
                .append(" )  ");
        return strBuffer.toString();
    }
}
