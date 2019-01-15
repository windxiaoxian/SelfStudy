package gd.services.ledDisplay.wirelessProject.dao;

import gd.services.ledDisplay.wirelessProject.util.DateUtil;

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
                .append("work_status, ")
                .append("dept_code, ")
                .append("freq_offset, ")
                .append("power, ")
                .append("program_code, ")
                .append("freq, ")
                .append("ant_code, ")
                .append("amrp ")
                .append(" ) ")
                .append("values ")
                .append(" ( ")
                .append("'" + reqMap.get("stationCode") + "', ")
                .append("'" + reqMap.get("transCode") + "', ")
                .append("to_date('" + ((String) reqMap.get("statusTime")).substring(((String) reqMap.get("statusTime")).length() - 19) + "','yyyy-MM-dd HH24:Mi:ss'), ")
                .append("'" + reqMap.get("workStatus") + "', ")
                .append("'" + reqMap.get("deptCode") + "', ")
                .append("'" + reqMap.get("freqOffset") + "', ")
                .append("'" + reqMap.get("power") + "', ")
                .append("'" + reqMap.get("programCode") + "', ")
                .append("'" + reqMap.get("freq") + "', ")
                .append("'" + reqMap.get("antCode") + "', ")
                .append("'" + reqMap.get("amrp") + "' ")
                .append(" )  ");
        return strBuffer.toString();
    }

    public static String truncateTransStatus(Map reqMap) {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("  truncate table wxj_transmitter_status_t ");
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

    public static String delBusiOrderTotal(Map reqMap) {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("  delete wxj_busiorder_total_realtime where order_code = '")
                .append(reqMap.get("orderCode"))
                .append("'  ");
        return strBuffer.toString();
    }

    public static String insBusiOrderTotal(Map reqMap) {
        String order_code = (String) reqMap.get("orderCode");
        if (order_code.indexOf("99999") == 0) {
            reqMap.put("orderCodeDic", "广无临调字" + reqMap.get("orderYear") + "[" + order_code.substring(5) + "]号");
        } else if (order_code.indexOf("88888") == 0) {
            reqMap.put("orderCodeDic", "台调字" + reqMap.get("orderYear") + "[" + order_code.substring(5) + "]号");
        } else {
            reqMap.put("orderCodeDic", "广无调单字" + reqMap.get("orderYear") + "[" + order_code + "]号");
        }

        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("  insert into wxj_busiorder_total_realtime ")
                .append(" ( ")
                .append("station_Code, ")
                .append("operate, ")
                .append("source_type, ")
                .append("order_code, ")
                .append("order_code_dic, ")
                .append("order_status, ")
                .append("send_dept, ")
                .append("order_year, ")
                .append("status_date, ")
                .append("sender, ")
                .append("send_date, ")
                .append("send_assessor, ")
                .append("receiver, ")
                .append("receive_date, ")
                .append("corrector, ")
                .append("correct_date ")
                .append(" ) ")
                .append("values ")
                .append(" ( ")
                .append("'" + reqMap.get("stationCode") + "', ")
                .append("'" + reqMap.get("operate") + "', ")
                .append("'" + reqMap.get("sourceType") + "', ")
                .append("'" + reqMap.get("orderCode") + "', ")
                .append("'" + reqMap.get("orderCodeDic") + "', ")
                .append("'" + reqMap.get("orderStatus") + "', ")
                .append("'" + reqMap.get("sendDept") + "', ")
                .append("'" + reqMap.get("orderYear") + "', ")
                .append("to_date('" + reqMap.get("statusDate") + "','yyyy-MM-dd HH24:Mi:ss'), ")
                .append("'" + reqMap.get("sender") + "', ")
                .append("to_date('" + reqMap.get("sendDate") + "','yyyy-MM-dd HH24:Mi:ss'), ")
                .append("'" + reqMap.get("sendAssessor") + "', ")
                .append("'" + reqMap.get("receiver") + "', ")
                .append("to_date('" + reqMap.get("receiverDate") + "','yyyy-MM-dd HH24:Mi:ss'), ")
                .append("'" + reqMap.get("corrector") + "', ")
                .append("to_date('" + reqMap.get("correctDate") + "','yyyy-MM-dd HH24:Mi:ss') ")
                .append(" )  ");
        return strBuffer.toString();
    }

    public static String delBusiOrderDetail(Map reqMap) {
        if (reqMap.get("startDate") == null || "".equals(reqMap.get("startDate"))) {
            reqMap.put("startDate", DateUtil.getNowStr("yyyy-MM-dd") + " 00:00:00");
        }
        if (reqMap.get("endDate") == null || "".equals(reqMap.get("endDate"))) {
            reqMap.put("endDate", "2049-12-31 23:59:59");
        }
        reqMap.put("startTime", ((String) reqMap.get("startDate")).substring(0, 11) + ((String) reqMap.get("startTime")).substring(11));
        reqMap.put("endTime", ((String) reqMap.get("endDate")).substring(0, 11) + ((String) reqMap.get("endTime")).replace("00:00:00", "23:59:59").substring(11));

        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("  delete wxj_busiorder_detail_realtime where order_code = '")
                .append(reqMap.get("orderCode"))
                .append("' and trans_code = '")
                .append(reqMap.get("transCode"))
                .append("' and ant_code = '")
                .append(reqMap.get("antCode"))
                .append("' and to_char(start_time, 'yyyy-MM-dd HH24:Mi:ss') = '")
                .append(reqMap.get("startTime"))
                .append("' and to_char(end_time, 'yyyy-MM-dd HH24:Mi:ss') = '")
                .append(reqMap.get("endTime"))
                .append("'  ");
        return strBuffer.toString();
    }

    public static String insBusiOrderDetail(Map reqMap) {
        if (reqMap.get("startDate") == null || "".equals(reqMap.get("startDate"))) {
            reqMap.put("startDate", DateUtil.getNowStr("yyyy-MM-dd") + " 00:00:00");
        }
        if (reqMap.get("endDate") == null || "".equals(reqMap.get("endDate"))) {
            reqMap.put("endDate", "2049-12-31 23:59:59");
        }
        reqMap.put("startTime", ((String) reqMap.get("startDate")).substring(0, 11) + ((String) reqMap.get("startTime")).substring(11));
        reqMap.put("endTime", ((String) reqMap.get("endDate")).substring(0, 11) + ((String) reqMap.get("endTime")).replace("00:00:00", "23:59:59").substring(11));

        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("  insert into wxj_busiorder_detail_realtime ")
                .append(" ( ")
                .append("station_code, ")
                .append("trans_code, ")
                .append("ant_code, ")
                .append("status, ")
                .append("status_date, ")
                .append("remark, ")
                .append("channel, ")
                .append("order_code, ")
                .append("days, ")
                .append("serv_area, ")
                .append("power, ")
                .append("azimuthm, ")
                .append("program_name, ")
                .append("program_code, ")
                .append("freq, ")
                .append("start_time, ")
                .append("end_time, ")
                .append("order_type, ")
                .append("operate ")
                .append(" ) ")
                .append("values ")
                .append(" ( ")
                .append("'" + reqMap.get("stationCode") + "', ")
                .append("'" + reqMap.get("transCode") + "', ")
                .append("'" + reqMap.get("antCode") + "', ")
                .append("'" + reqMap.get("status") + "', ")
                .append("to_date('" + reqMap.get("statusDate") + "','yyyy-MM-dd HH24:Mi:ss'), ")
                .append("'" + reqMap.get("rmks") + "', ")
                .append("'" + reqMap.get("channel") + "', ")
                .append("'" + reqMap.get("orderCode") + "', ")
                .append("'" + reqMap.get("days") + "', ")
                .append("'" + reqMap.get("servArea") + "', ")
                .append("'" + reqMap.get("power") + "', ")
                .append("'" + reqMap.get("azimuthM") + "', ")
                .append("'" + reqMap.get("programName") + "', ")
                .append("'" + reqMap.get("programCode") + "', ")
                .append("'" + reqMap.get("freq") + "', ")
                .append("to_date('" + reqMap.get("startTime") + "','yyyy-MM-dd HH24:Mi:ss'), ")
                .append("to_date('" + reqMap.get("endTime") + "','yyyy-MM-dd HH24:Mi:ss'), ")
                .append("'" + reqMap.get("orderType") + "', ")
                .append("'" + reqMap.get("operate") + "' ")
                .append(" )  ");
        return strBuffer.toString();
    }

    public static String truncateBusiOrderTotal(Map reqMap) {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("  truncate table wxj_busiorder_total_realtime ");
        return strBuffer.toString();
    }

    public static String truncateBusiOrderDetail(Map reqMap) {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("  truncate table wxj_busiorder_detail_realtime ");
        return strBuffer.toString();
    }
}
