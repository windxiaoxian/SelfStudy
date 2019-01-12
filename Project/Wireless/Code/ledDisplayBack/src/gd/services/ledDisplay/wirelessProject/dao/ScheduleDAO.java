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

    public static String delValidRunplan(Map reqMap) {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("  delete wxj_runplan_realtime_t where station_code = '")
                .append(reqMap.get("stationCode"))
                .append("' and run_id = '")
                .append(reqMap.get("runId"))
                .append("'  ");
        return strBuffer.toString();
    }

    public static String cleanValidRunplan(Map reqMap) {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("  delete wxj_runplan_realtime_t ")
                .append(" where (station_code,run_id,acq_date_time) ")
                .append(" not in ( ")
                .append(" select station_code,run_id,max(acq_date_time) ")
                .append(" from wxj_runplan_realtime_t ")
                .append(" group by station_code,run_id ")
                .append(" )");
        return strBuffer.toString();
    }

    public static String truncateValidRunplan(Map reqMap) {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("  truncate table wxj_runplan_realtime_t ");
        return strBuffer.toString();
    }

    public static String insValidRunplan(Map reqMap) {
        if (reqMap.get("endDate") == null || "".equals(reqMap.get("endDate"))) {
            reqMap.put("endDate", "2049-12-31 23:59:59");
        }
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("  insert into wxj_runplan_realtime_t ")
                .append(" ( ")
                .append("acq_Date_Time, ")
                .append("station_Code, ")
                .append("dept_Code, ")
                .append("operate, ")
                .append("trans_Code, ")
                .append("run_Id, ")
                .append("antenna_Code, ")
                .append("ant_Prog, ")
                .append("order_Type, ")
                .append("start_Date, ")
                .append("end_Date, ")
                .append("start_Time, ")
                .append("end_Time, ")
                .append("freq, ")
                .append("program_Code, ")
                .append("program_Name, ")
                .append("power, ")
                .append("serv_Area, ")
                .append("days, ")
                .append("channel, ")
                .append("run_Type, ")
                .append("source_Type ")
                .append(" ) ")
                .append("values ")
                .append(" ( ")
                .append("to_date('" + reqMap.get("acqDateTime") + "','yyyy-MM-dd HH24:Mi:ss'), ")
                .append("'" + reqMap.get("stationCode") + "', ")
                .append("'" + reqMap.get("deptCode") + "', ")
                .append("'" + reqMap.get("operate") + "', ")
                .append("'" + reqMap.get("transCode") + "', ")
                .append("'" + reqMap.get("runId") + "', ")
                .append("'" + reqMap.get("antennaCode") + "', ")
                .append("'" + reqMap.get("antProg") + "', ")
                .append("'" + reqMap.get("orderType") + "', ")
                .append("to_date('" + reqMap.get("startDate") + "','yyyy-MM-dd HH24:Mi:ss'), ")
                .append("to_date('" + reqMap.get("endDate") + "','yyyy-MM-dd HH24:Mi:ss'), ")
                .append("to_date(to_char(sysdate,'yyyy-MM-dd ')||'" + ((String) reqMap.get("startTime")).substring(11) + "','yyyy-MM-dd HH24:Mi:ss'), ")
                .append("to_date(to_char(sysdate,'yyyy-MM-dd ')||'" + (((String) reqMap.get("endTime")).replace("00:00:00", "23:59:59")).substring(11) + "','yyyy-MM-dd HH24:Mi:ss'), ")
                .append("'" + reqMap.get("freq") + "', ")
                .append("'" + reqMap.get("programCode") + "', ")
                .append("'" + reqMap.get("programName") + "', ")
                .append("'" + reqMap.get("power") + "', ")
                .append("'" + reqMap.get("servArea") + "', ")
                .append("'" + reqMap.get("days") + "', ")
                .append("'" + reqMap.get("channel") + "', ")
                .append("'" + reqMap.get("runType") + "', ")
                .append("'" + reqMap.get("sourceType") + "' ")
                .append(" )  ");
        return strBuffer.toString();
    }
}
