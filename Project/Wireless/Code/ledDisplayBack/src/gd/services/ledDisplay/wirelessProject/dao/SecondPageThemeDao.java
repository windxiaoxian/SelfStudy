package gd.services.ledDisplay.wirelessProject.dao;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liubaoyun on 2019/3/313:48
 */
public class SecondPageThemeDao {

    /*
    获取实时资源比对
     */
    public static String getResourceCompareInfo(HashMap reqMap) {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(" SELECT station_count,    ") /* 当前实验发射机个数*/
                .append("  play_time_done,         ") /* 当前累计播音时间 */
                .append("  freq_time,              ") /* 当日累计频次*/
                .append("  freq_count,             ") /* 当日累计频率个数*/
                .append("  target_station_count,   ") /* 敌台在播个数*/
                .append("  target_play_time_done,  ") /* 敌台当前累计播音时间 */
                .append("  target_freq_time,       ") /* 敌台当日累计频次*/
                .append("  target_freq_count       ") /* 当日累计频率个数*/
                .append("   FROM (SELECT * ")
                .append("           FROM wxj_common_param_t ")
                .append("          WHERE param_type = 'today_station_tasks') total ")
                .append("        LEFT JOIN                                      ")  /* 当前实验发射机个数*/
                .append("        (SELECT 'today_station_tasks' sql_type, ")
                .append("                COUNT (DISTINCT station_code) station_count ")
                .append("           FROM wxj_runplan_realtime_v ")
                .append("          WHERE SYSDATE BETWEEN start_date AND end_date ")
                .append("            AND order_type IN ('I') ")
                .append("            AND run_type = 1 ")
                .append("            AND days LIKE '%' || (SELECT TO_CHAR (SYSDATE - 1, 'd') ")
                .append("                                    FROM DUAL) || '%' ")
                .append("            AND SYSDATE BETWEEN start_time AND end_time ")
                .append("            AND OPERATE != 'D') china1 ")
                .append("        ON china1.sql_type = total.param_value ")
                .append("        LEFT JOIN                                        ") /* 当前累计播音时间 */
                .append("        (SELECT 'today_station_tasks' sql_type, ")
                .append("                NVL (SUM (play_time), 0) play_time_done ")
                .append("           FROM (SELECT CEIL (SUM (end_time - start_time) * 24) play_time ")
                .append("                   FROM (SELECT start_time, end_time ")
                .append("                           FROM wxj_runplan_realtime_v ")
                .append("                          WHERE SYSDATE BETWEEN start_date AND end_date ")
                .append("                            AND order_type IN ('I') ")
                .append("                            AND run_type = 1 ")
                .append("                            AND days LIKE ")
                .append("                                      '%' ")
                .append("                                   || (SELECT TO_CHAR (SYSDATE - 1, 'd') ")
                .append("                                         FROM DUAL) ")
                .append("                                   || '%' ")
                .append("                           AND OPERATE != 'D') ")
                .append("                  WHERE start_time <= end_time ")
                .append("                    AND start_time < SYSDATE ")
                .append("                    AND end_time <= SYSDATE  ")
                .append("                 UNION ALL ")
                .append("                 SELECT CEIL (SUM (SYSDATE - start_time) * 24) play_time ")
                .append("                   FROM (SELECT start_time, end_time ")
                .append("                           FROM wxj_runplan_realtime_v ")
                .append("                          WHERE SYSDATE BETWEEN start_date AND end_date ")
                .append("                            AND order_type IN ('I') ")
                .append("                            AND run_type = 1 ")
                .append("                            AND days LIKE ")
                .append("                                      '%' ")
                .append("                                   || (SELECT TO_CHAR (SYSDATE - 1, 'd') ")
                .append("                                         FROM DUAL) ")
                .append("                                   || '%' ")
                .append("                             AND OPERATE != 'D') ")
                .append("                  WHERE start_time <= end_time ")
                .append("                    AND start_time < SYSDATE ")
                .append("                    AND end_time > SYSDATE ")
                .append("                    )) playtime ")
                .append("        ON playtime.sql_type = total.param_value ")
                .append("        LEFT JOIN                          ")  /* 当日累计频次和当日累计频率个数*/
                .append("        (SELECT 'today_station_tasks' sql_type, ")
                .append("                COUNT (station_code || '-' || freq) freq_time, ")
                .append("                COUNT (DISTINCT station_code || '-' || freq) freq_count ")
                .append("           FROM wxj_runplan_realtime_v ")
                .append("          WHERE SYSDATE BETWEEN start_date AND end_date ")
                .append("            AND order_type IN ('I') ")
                .append("            AND run_type = 1 ")
                .append("            AND days LIKE '%' || (SELECT TO_CHAR (SYSDATE - 1, 'd') ")
                .append("                                    FROM DUAL) || '%' ")
                .append("            AND OPERATE != 'D') china2 ")
                .append("        ON china2.sql_type = total.param_value ")
                .append("        LEFT JOIN                                           ")  /* 敌台在播个数*/
                .append("        (SELECT 'today_station_tasks' sql_type, ")
                .append("                COUNT (DISTINCT station_name) target_station_count ")
                .append("           FROM (SELECT station_name, freq, ")
                .append("                        TO_DATE (   TO_CHAR (SYSDATE, 'yyyy-MM-dd') ")
                .append("                                 || TO_CHAR (start_time, ' HH24:Mi:ss'), ")
                .append("                                 'yyyy-MM-dd HH24:Mi:ss' ")
                .append("                                ) start_time, ")
                .append("                        TO_DATE (   TO_CHAR (SYSDATE, 'yyyy-MM-dd') ")
                .append("                                 || TO_CHAR (end_time, ' HH24:Mi:ss'), ")
                .append("                                 'yyyy-MM-dd HH24:Mi:ss' ")
                .append("                                ) end_time ")
                .append("                   FROM wxj_target_runplan_info_t ")
                .append("                  WHERE SYSDATE BETWEEN start_date AND end_date ")
                .append("                    AND days LIKE ")
                .append("                               '%' || (SELECT TO_CHAR (SYSDATE - 1, 'd') ")
                .append("                                         FROM DUAL) || '%') ")
                .append("          WHERE start_time <= end_time ")
                .append("            AND SYSDATE BETWEEN start_time AND end_time) target1 ")
                .append("        ON target1.sql_type = total.param_value ")
                .append("        LEFT JOIN                     ")  /* 敌台当日累计频次和当日累计频率个数*/
                .append("        (SELECT 'today_station_tasks' sql_type, ")
                .append("                COUNT (station_name || '-' || freq) target_freq_time, ")
                .append("                COUNT (DISTINCT station_name || '-' || freq) target_freq_count ")
                .append("           FROM (SELECT station_name, freq, ")
                .append("                        TO_DATE (   TO_CHAR (SYSDATE, 'yyyy-MM-dd') ")
                .append("                                 || TO_CHAR (start_time, ' HH24:Mi:ss'), ")
                .append("                                 'yyyy-MM-dd HH24:Mi:ss' ")
                .append("                                ) start_time, ")
                .append("                        TO_DATE (   TO_CHAR (SYSDATE, 'yyyy-MM-dd') ")
                .append("                                 || TO_CHAR (end_time, ' HH24:Mi:ss'), ")
                .append("                                 'yyyy-MM-dd HH24:Mi:ss' ")
                .append("                                ) end_time ")
                .append("                   FROM wxj_target_runplan_info_t ")
                .append("                    WHERE SYSDATE BETWEEN start_date AND end_date ")
                .append("                    AND days LIKE ")
                .append("                               '%' || (SELECT TO_CHAR (SYSDATE - 1, 'd') ")
                .append("                                         FROM DUAL) || '%') ")
                .append("                 where SYSDATE > start_time) target2 ")
                .append("        ON target2.sql_type = total.param_value       ")
                .append("         LEFT JOIN                                ")        /* 敌台当前累计播音时间 */
                .append("        (SELECT 'today_station_tasks' sql_type, ")
                .append("                NVL (SUM (play_time), 0) target_play_time_done ")
                .append("           FROM (SELECT CEIL (SUM (end_time - start_time) * 24) play_time ")
                .append("                   FROM (SELECT  TO_DATE (   TO_CHAR (SYSDATE, 'yyyy-MM-dd') ")
                .append("                                 || TO_CHAR (start_time, ' HH24:Mi:ss'), ")
                .append("                                 'yyyy-MM-dd HH24:Mi:ss' ")
                .append("                                ) start_time, ")
                .append("                        TO_DATE (   TO_CHAR (SYSDATE, 'yyyy-MM-dd') ")
                .append("                                 || TO_CHAR (end_time, ' HH24:Mi:ss'), ")
                .append("                                 'yyyy-MM-dd HH24:Mi:ss' ")
                .append("                                ) end_time ")
                .append("                   FROM wxj_target_runplan_info_t ")
                .append("                    WHERE SYSDATE BETWEEN start_date AND end_date ")
                .append("                    AND days LIKE ")
                .append("                               '%' || (SELECT TO_CHAR (SYSDATE - 1, 'd') ")
                .append("                                         FROM DUAL) || '%') ")
                .append("                  WHERE start_time <= end_time ")
                .append("                    AND start_time < SYSDATE ")
                .append("                    AND end_time <= SYSDATE ")
                .append("                 UNION ALL ")
                .append("                 SELECT CEIL (SUM (SYSDATE - start_time) * 24) play_time ")
                .append("                   FROM (SELECT  TO_DATE (   TO_CHAR (SYSDATE, 'yyyy-MM-dd') ")
                .append("                                 || TO_CHAR (start_time, ' HH24:Mi:ss'), ")
                .append("                                 'yyyy-MM-dd HH24:Mi:ss' ")
                .append("                                ) start_time, ")
                .append("                        TO_DATE (   TO_CHAR (SYSDATE, 'yyyy-MM-dd') ")
                .append("                                 || TO_CHAR (end_time, ' HH24:Mi:ss'), ")
                .append("                                 'yyyy-MM-dd HH24:Mi:ss' ")
                .append("                                ) end_time ")
                .append("                   FROM wxj_target_runplan_info_t ")
                .append("                    WHERE SYSDATE BETWEEN start_date AND end_date ")
                .append("                    AND days LIKE ")
                .append("                               '%' || (SELECT TO_CHAR (SYSDATE - 1, 'd') ")
                .append("                                         FROM DUAL) || '%') ")
                .append("                  WHERE start_time <= end_time ")
                .append("                    AND start_time < SYSDATE ")
                .append("                    AND end_time > SYSDATE)) target_playtime ")
                .append("        ON target_playtime.sql_type = total.param_value ");
        return strBuffer.toString();
    }

    /*
        突发频率实时分析
     */
    public static String getBurstFreqAnalysis(Integer freq) {
        StringBuffer strBuffer = new StringBuffer();

        strBuffer.append(" SELECT ")
                .append("  NVL (w.num, 0) + NVL (m.num, 0) + NVL (n.num, 0) times   ") /* 推送次数*/
                .append("   FROM (SELECT   TO_CHAR (h.times, 'hh24') AS dayhour, '0' num ")
                .append("             FROM (SELECT TO_DATE (TO_CHAR (SYSDATE, 'yyyy-MM-dd'), ")
                .append("                                         'yyyy-MM-dd' ")
                .append("                                        ) ")
                .append("                              + (ROWNUM - 1) / 24 times ")
                .append("                         FROM DUAL ")
                .append("                   CONNECT BY LEVEL <= 24) h ")
                .append("         ORDER BY TO_CHAR (h.times, 'hh24')) w ")
                .append("        LEFT JOIN ")
                .append("        (SELECT   TO_CHAR (t.status_date, 'hh24') AS dayhour, ")
                .append("                  COUNT (TO_CHAR (t.status_date, 'hh24')) AS num ")
                .append("             FROM wxj_busiorder_detail_history_t t ")
                .append("            WHERE t.status_date >= ")
                .append("                     TO_DATE ((SELECT    TO_CHAR (SYSDATE - 30, 'yyyy-mm-dd') ")
                .append("                                      || ' 00:00:00' ")
                .append("                                 FROM DUAL), ")
                .append("                              'yyyy-MM-dd HH24:Mi:ss' ")
                .append("                             ) ")
                .append("     and t.order_type = 'I' ");

                if(freq != null)   // 判断是否逐个获取指定频率的24小时突发频率
                    strBuffer .append("     and t.freq = '" +  freq + "'" ) ;

                strBuffer.append("         GROUP BY TO_CHAR (t.status_date, 'hh24') ")
                .append("         ORDER BY TO_CHAR (t.status_date, 'hh24')) m ON w.dayhour = m.dayhour ")
                .append("        LEFT JOIN ")
                .append("        (SELECT   TO_CHAR (t.status_date, 'hh24') AS dayhour, ")
                .append("                  COUNT (TO_CHAR (t.status_date, 'hh24')) AS num ")
                .append("             FROM wxj_busiorder_detail_realtime t ")
                .append("     where t.order_type = 'I' ");

                if(freq != null)   // 判断是否逐个获取指定频率的24小时突发频率
                    strBuffer .append("     and t.freq = '" +  freq + "'" ) ;

                strBuffer .append("         GROUP BY TO_CHAR (t.status_date, 'hh24') ")
                .append("         ORDER BY TO_CHAR (t.status_date, 'hh24')) n ON n.dayhour = m.dayhour ");
        return strBuffer.toString();
    }

    /*
        突发频率排名
     */
    public static String getFreqencyRanking(Map reqMap) {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(" SELECT m.freq,              ")                                     /* 频率名称*/
                .append("               NVL (n.times, 0) + NVL (m.times, 0) times ")         /* 次数*/
                .append("   FROM (SELECT   t.freq, COUNT (t.freq) times ")
                .append("             FROM wxj_busiorder_detail_history_t t ")
                .append("            WHERE t.status_date >= ")
                .append("                     TO_DATE ((SELECT    TO_CHAR (SYSDATE - 30, 'yyyy-mm-dd') ")
                .append("                                      || ' 00:00:00' ")
                .append("                                 FROM DUAL), ")
                .append("                              'yyyy-MM-dd HH24:Mi:ss' ")
                .append("                             ) ")
                .append("              AND t.order_type = 'I' ")
                .append("         GROUP BY t.freq) m ")
                .append("        LEFT JOIN ")
                .append("        (SELECT   t.freq, COUNT (t.freq) times ")
                .append("             FROM wxj_busiorder_detail_realtime t ")
                .append("            WHERE t.order_type = 'I' ")
                .append("         GROUP BY t.freq ")
                .append("         ) n ON n.freq = m.freq ")
                .append("     ORDER BY times DESC ");
        return strBuffer.toString();
    }

    public static String getAnnualSituationInfo(Map reqMap) {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(" SELECT order_count,  ")/* 全局下发调度令*/
                .append(" order_count_done,    ")/* 全局下发已完成调度令,用来计算执行率*/
                .append(" agent_freq_count,    ")/* 全局年度至今总代播频次*/
                .append(" agent_order_duration ")/* 全局年度至今总代播时长*/
                .append("   FROM (SELECT * ")
                .append("           FROM wxj_common_param_t ")
                .append("          WHERE param_type = 'today_station_tasks') total ")
                .append("        LEFT JOIN                                            ") /* 年调度令总数*/
                .append("        (SELECT 'today_station_tasks' sql_type, SUM (order_count) order_count ")
                .append("           FROM (SELECT COUNT (*) order_count ")
                .append("                   FROM wxj_busiorder_detail_history_t ")
                .append("                  WHERE status_date ")
                .append("                           BETWEEN (SELECT TO_DATE ")
                .append("                                                  (   TO_CHAR (TRUNC (SYSDATE, ")
                .append("                                                                      'yyyy' ")
                .append("                                                                     ), ")
                .append("                                                               'yyyy-MM-dd' ")
                .append("                                                              ) ")
                .append("                                                   || ' ' ")
                .append("                                                   || '00:00:00', ")
                .append("                                                   'yyyy-MM-dd HH24:Mi:ss' ")
                .append("                                                  ) ")
                .append("                                      FROM DUAL) ")
                .append("                               AND SYSDATE ")
                .append("                 UNION ALL ")
                .append("                 SELECT COUNT (*) order_count ")
                .append("                   FROM wxj_busiorder_detail_realtime)) ordercount ")
                .append("        ON ordercount.sql_type = total.param_value ")
                .append("        LEFT JOIN                                      ") /* 年执行的调度令总数*/
                .append("        (SELECT 'today_station_tasks' sql_type, ")
                .append("                SUM (order_count_done) order_count_done ")
                .append("           FROM (SELECT COUNT (order_code) order_count_done ")
                .append("                   FROM wxj_busiorder_detail_history_t ")
                .append("                  WHERE status_date ")
                .append("                           BETWEEN (SELECT TO_DATE ")
                .append("                                                  (   TO_CHAR (TRUNC (SYSDATE, ")
                .append("                                                                      'yyyy' ")
                .append("                                                                     ), ")
                .append("                                                               'yyyy-MM-dd' ")
                .append("                                                              ) ")
                .append("                                                   || ' ' ")
                .append("                                                   || '00:00:00', ")
                .append("                                                   'yyyy-MM-dd HH24:Mi:ss' ")
                .append("                                                  ) ")
                .append("                                      FROM DUAL) ")
                .append("                               AND SYSDATE ")
                .append("                    AND status = '70' ")
                .append("                 UNION ALL ")
                .append("                 SELECT COUNT (order_code) order_count ")
                .append("                   FROM wxj_busiorder_detail_realtime ")
                .append("                  WHERE status = '70')) donecount ")
                .append("        ON donecount.sql_type = total.param_value ")
                .append("        LEFT JOIN                                          ") /* 年临时代播频次*/
                .append("        (SELECT 'today_station_tasks' sql_type, ")
                .append("                SUM (agent_freq_count) agent_freq_count ")
                .append("           FROM (SELECT COUNT (b.freq) agent_freq_count ")
                .append("                   FROM wxj_busiorder_total_history_t a, ")
                .append("                        wxj_busiorder_detail_history_t b ")
                .append("                  WHERE a.send_date ")
                .append("                           BETWEEN (SELECT TO_DATE ")
                .append("                                                  (   TO_CHAR (TRUNC (SYSDATE, ")
                .append("                                                                      'yyyy' ")
                .append("                                                                     ), ")
                .append("                                                               'yyyy-MM-dd' ")
                .append("                                                              ) ")
                .append("                                                   || ' ' ")
                .append("                                                   || '00:00:00', ")
                .append("                                                   'yyyy-MM-dd HH24:Mi:ss' ")
                .append("                                                  ) ")
                .append("                                      FROM DUAL) ")
                .append("                               AND SYSDATE ")
                .append("                    AND a.source_type ='INTR' ")
                .append("                    AND a.order_code = b.order_code ")
                .append(" 		   AND a.OPERATE != 'D' ")
                .append("                 UNION ALL ")
                .append("                 SELECT COUNT (d.freq) agent_freq_count ")
                .append("                   FROM wxj_busiorder_total_realtime c, ")
                .append("                        wxj_busiorder_detail_realtime d ")
                .append("                  WHERE c.source_type ='INTR' ")
                .append("                    AND c.order_code = d.order_code ")
                .append(" 		   AND c.OPERATE != 'D')) agentfreqcount ")
                .append("        ON agentfreqcount.sql_type = total.param_value ")
                .append("        LEFT JOIN                                           ")/* 年临时代播时长*/
                .append("        (SELECT 'today_station_tasks' sql_type, ")
                .append("                NVL (SUM (play_time), 0) agent_order_duration ")
                .append("           FROM (SELECT CEIL (SUM (end_time - start_time) * 24) play_time ")
                .append("                   FROM (SELECT b.start_time, b.end_time ")
                .append("                           FROM wxj_busiorder_total_history_t a, ")
                .append("                                wxj_busiorder_detail_history_t b ")
                .append("                          WHERE a.send_date ")
                .append("                                   BETWEEN (SELECT TO_DATE ")
                .append("                                                      (   TO_CHAR ")
                .append("                                                              (TRUNC (SYSDATE, ")
                .append("                                                                      'yyyy' ")
                .append("                                                                     ), ")
                .append("                                                               'yyyy-MM-dd' ")
                .append("                                                              ) ")
                .append("                                                       || ' ' ")
                .append("                                                       || '00:00:00', ")
                .append("                                                       'yyyy-MM-dd HH24:Mi:ss' ")
                .append("                                                      ) ")
                .append("                                              FROM DUAL) ")
                .append("                                       AND SYSDATE ")
                .append("                            AND a.source_type ='INTR' ")
                .append("                            AND a.order_code = b.order_code ")
                .append(" 			   AND a.OPERATE != 'D') ")
                .append("                 UNION ALL ")
                .append("                 SELECT CEIL (SUM (end_time - start_time) * 24) play_time ")
                .append("                   FROM (SELECT b.start_time, b.end_time ")
                .append("                           FROM wxj_busiorder_total_realtime a, ")
                .append("                                wxj_busiorder_detail_realtime b ")
                .append("                          WHERE a.source_type ='INTR' ")
                .append("                            AND a.order_code = b.order_code ")
                .append(" 			  AND a.OPERATE != 'D') ")
                .append("                  WHERE start_time <= end_time ")
                .append("                    AND start_time < SYSDATE ")
                .append("                    AND end_time <= SYSDATE ")
                .append("                 UNION ALL ")
                .append("                 SELECT CEIL (SUM (SYSDATE - start_time) * 24) play_time ")
                .append("                   FROM (SELECT b.start_time, b.end_time ")
                .append("                           FROM wxj_busiorder_total_realtime a, ")
                .append("                                wxj_busiorder_detail_realtime b ")
                .append("                          WHERE a.source_type = 'INTR' ")
                .append("                            AND a.order_code = b.order_code ")
                .append(" 			  AND a.OPERATE != 'D') ")
                .append("                  WHERE start_time <= end_time ")
                .append("                    AND start_time < SYSDATE ")
                .append("                    AND end_time > SYSDATE)) playtime ")
                .append("        ON playtime.sql_type = total.param_value ");
        return strBuffer.toString();
    }

    public static String getOrderTimeList(Map reqMap) {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(" SELECT   x.station_code, ")/* 台站代码*/
                .append(" x.station_code_dic,      ")/* 台站名称*/
                .append(" x.order_total,           ")/* 调度次数*/
                .append(" y.done_count             ")/* 调度完成次数,用于计算执行率*/
                .append("     FROM (   ") /* 年度至今调度指令总数*/
                .append("           SELECT m.station_code, m.station_code_dic, ")
                .append("                  (NVL(m.num, 0) + NVL(n.num, 0)) AS order_total ")
                .append("             FROM (SELECT  b.station_code, b.station_code_dic, ")
                .append("                            COUNT (b.station_code) num ")
                .append("                       FROM wxj_busiorder_detail_history_t b ")
                .append("                      WHERE b.status_date ")
                .append("                               BETWEEN (SELECT TO_DATE ")
                .append("                                                  (   TO_CHAR (TRUNC (SYSDATE, ")
                .append("                                                                      'yyyy' ")
                .append("                                                                     ), ")
                .append("                                                               'yyyy-MM-dd' ")
                .append("                                                              ) ")
                .append("                                                   || ' ' ")
                .append("                                                   || '00:00:00', ")
                .append("                                                   'yyyy-MM-dd HH24:Mi:ss' ")
                .append("                                                  ) ")
                .append("                                          FROM DUAL) ")
                .append("                                   AND SYSDATE ")
                .append("                   GROUP BY b.station_code, b.station_code_dic) m ")
                .append("                   LEFT JOIN ")
                .append("                  (SELECT   b.station_code, COUNT (b.station_code) num ")
                .append("                       FROM wxj_busiorder_detail_realtime b ")
                .append("                   GROUP BY b.station_code) n ")
                .append("                   ON m.station_code = n.station_code) x,                  ")
                .append("          (SELECT m.station_code, m.station_code_dic,   ")/* 年度至今调度指令完成总数*/
                .append("                  (NVL(m.num, 0) + NVL(n.num, 0)) AS done_count ")
                .append("             FROM (SELECT   b.station_code, b.station_code_dic, ")
                .append("                            COUNT (b.station_code) num ")
                .append("                       FROM wxj_busiorder_detail_history_t b ")
                .append("                      WHERE b.status_date ")
                .append("                               BETWEEN (SELECT TO_DATE ")
                .append("                                                  (   TO_CHAR (TRUNC (SYSDATE, ")
                .append("                                                                      'yyyy' ")
                .append("                                                                     ), ")
                .append("                                                               'yyyy-MM-dd' ")
                .append("                                                              ) ")
                .append("                                                   || ' ' ")
                .append("                                                   || '00:00:00', ")
                .append("                                                   'yyyy-MM-dd HH24:Mi:ss' ")
                .append("                                                  ) ")
                .append("                                          FROM DUAL) ")
                .append("                                   AND SYSDATE ")
                .append("                        AND b.status = '70' ") /* b.status 不明确，影响查询结果 */
                .append("                   GROUP BY b.station_code, b.station_code_dic) m ")
                .append("                   LEFT JOIN ")
                .append("                  (SELECT   b.station_code, COUNT (b.station_code) num ")
                .append("                       FROM wxj_busiorder_detail_realtime b ")
                .append("                      WHERE b.status = '70'") /* b.status 不明确，影响查询结果 */
                .append("                   GROUP BY b.station_code) n ")
                .append("                   ON m.station_code = n.station_code) y ")
                .append("    WHERE x.station_code = y.station_code ")
                .append(" ORDER BY x.order_total DESC ");
        return strBuffer.toString();
    }

    public static String getGenerationList(Map reqMap) {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(" SELECT   x.station_code,  ")/*台站代码*/
                .append(" x.station_code_dic,       ")/* 台站名称*/
                .append(" x.play_time,              ")/* 代播他台时长*/
                .append(" y.agent_order_count,      ")/* 代播他台次数,用于计算代播执行率*/
                .append("  z.agent_done_count       ")/* 代播完成次数,用于计算代播执行率*/
                .append("     FROM                                                  ")/*各台代播他台时长*/
                .append("          (SELECT l.station_code, l.station_code_dic, ")
                .append("                  (  NVL (l.play_time, 0) ")
                .append("                   + NVL (m.play_time, 0) ")
                .append("                   + NVL (n.play_time, 0) ")
                .append("                  ) AS play_time ")
                .append("             FROM (SELECT   station_code, station_code_dic, ")
                .append("                            SUM (CEIL ((end_time - start_time) * 24)) ")
                .append("                                                                     play_time ")
                .append("                       FROM (SELECT b.station_code, b.station_code_dic, ")
                .append("                                    b.start_time, b.end_time ")
                .append("                               FROM wxj_busiorder_total_history_t a, ")
                .append("                                    wxj_busiorder_detail_history_t b ")
                .append("                              WHERE b.status_date ")
                .append("                                       BETWEEN (SELECT TO_DATE ")
                .append("                                                          (   TO_CHAR ")
                .append("                                                                 (TRUNC ")
                .append("                                                                      (SYSDATE, ")
                .append("                                                                       'yyyy' ")
                .append("                                                                      ), ")
                .append("                                                                  'yyyy-MM-dd' ")
                .append("                                                                 ) ")
                .append("                                                           || ' ' ")
                .append("                                                           || '00:00:00', ")
                .append("                                                           'yyyy-MM-dd HH24:Mi:ss' ")
                .append("                                                          ) ")
                .append("                                                  FROM DUAL) ")
                .append("                                           AND SYSDATE ")
                .append("                                AND a.source_type = 'INTR' ")
                .append("                                AND a.order_code = b.order_code) ")
                .append("                   GROUP BY station_code, station_code_dic) l ")
                .append("                  LEFT JOIN ")
                .append("                  (SELECT   station_code, ")
                .append("                            SUM (CEIL ((end_time - start_time) * 24)) ")
                .append("                                                                     play_time ")
                .append("                       FROM (SELECT b.station_code, b.start_time, b.end_time ")
                .append("                               FROM wxj_busiorder_total_realtime a, ")
                .append("                                    wxj_busiorder_detail_realtime b ")
                .append("                              WHERE a.source_type = 'INTR' ")
                .append("                                AND a.order_code = b.order_code) ")
                .append("                      WHERE start_time <= end_time ")
                .append("                        AND start_time < SYSDATE ")
                .append("                        AND end_time <= SYSDATE ")
                .append("                   GROUP BY station_code) m ON l.station_code = m.station_code ")
                .append("                  LEFT JOIN ")
                .append("                  (SELECT   station_code, ")
                .append("                            SUM (CEIL ((SYSDATE - start_time) * 24)) play_time ")
                .append("                       FROM (SELECT b.station_code, b.start_time, b.end_time ")
                .append("                               FROM wxj_busiorder_total_realtime a, ")
                .append("                                    wxj_busiorder_detail_realtime b ")
                .append("                              WHERE a.source_type = 'INTR' ")
                .append("                                AND a.order_code = b.order_code) ")
                .append("                      WHERE start_time <= end_time ")
                .append("                        AND start_time < SYSDATE ")
                .append("                        AND end_time > SYSDATE ")
                .append("                   GROUP BY station_code) n ON m.station_code = n.station_code ")
                .append("                  ) x, ")
                .append("          ( SELECT l.station_code, l.station_code_dic,   ")/*各台代播他台总次数*/
                .append("                  (NVL (l.agent_count, 0) + NVL (m.agent_count, 0) ")
                .append("                  ) AS agent_order_count ")
                .append("             FROM (SELECT   b.station_code, b.station_code_dic, ")
                .append("                            COUNT (b.station_code) agent_count ")
                .append("                       FROM wxj_busiorder_total_history_t a, ")
                .append("                            wxj_busiorder_detail_history_t b ")
                .append("                      WHERE b.status_date ")
                .append("                               BETWEEN (SELECT TO_DATE ")
                .append("                                                  (   TO_CHAR (TRUNC (SYSDATE, ")
                .append("                                                                      'yyyy' ")
                .append("                                                                     ), ")
                .append("                                                               'yyyy-MM-dd' ")
                .append("                                                              ) ")
                .append("                                                   || ' ' ")
                .append("                                                   || '00:00:00', ")
                .append("                                                   'yyyy-MM-dd HH24:Mi:ss' ")
                .append("                                                  ) ")
                .append("                                          FROM DUAL) ")
                .append("                                   AND SYSDATE ")
                .append("                        AND a.source_type ='INTR' ")
                .append("                        AND a.order_code = b.order_code ")
                .append("                   GROUP BY b.station_code, b.station_code_dic) l ")
                .append("                  LEFT JOIN ")
                .append("                  (SELECT   b.station_code, COUNT (b.station_code) agent_count ")
                .append("                       FROM wxj_busiorder_total_realtime a, ")
                .append("                            wxj_busiorder_detail_realtime b ")
                .append("                      WHERE a.source_type = 'INTR' ")
                .append("                        AND a.order_code = b.order_code ")
                .append("                        AND b.start_time < SYSDATE ")
                .append("                        AND b.end_time <= SYSDATE ")
                .append("                   GROUP BY b.station_code) m ON l.station_code = ")
                .append("                                                                 m.station_code ")
                .append("                  ) y, ")
                .append("          ( SELECT l.station_code, l.station_code_dic,  ")/*各台代播他台播放完成次数*/
                .append("                  (NVL (l.agent_count, 0) + NVL (m.agent_count, 0) ")
                .append("                  ) AS agent_done_count ")
                .append("             FROM (SELECT   b.station_code, b.station_code_dic, ")
                .append("                            COUNT (b.station_code) agent_count ")
                .append("                       FROM wxj_busiorder_total_history_t a, ")
                .append("                            wxj_busiorder_detail_history_t b ")
                .append("                      WHERE b.status_date ")
                .append("                               BETWEEN (SELECT TO_DATE ")
                .append("                                                  (   TO_CHAR (TRUNC (SYSDATE, ")
                .append("                                                                      'yyyy' ")
                .append("                                                                     ), ")
                .append("                                                               'yyyy-MM-dd' ")
                .append("                                                              ) ")
                .append("                                                   || ' ' ")
                .append("                                                   || '00:00:00', ")
                .append("                                                   'yyyy-MM-dd HH24:Mi:ss' ")
                .append("                                                  ) ")
                .append("                                          FROM DUAL) ")
                .append("                                   AND SYSDATE ")
                .append("                        AND a.source_type = 'INTR' ")
                .append("                        AND a.order_code = b.order_code ")
                .append("                        AND b.status = '70'   ")/* b.status  不明确，影响查询结果 */
                .append("                   GROUP BY b.station_code, b.station_code_dic) l ")
                .append("                  LEFT JOIN ")
                .append("                  (SELECT   b.station_code, COUNT (b.station_code) agent_count ")
                .append("                       FROM wxj_busiorder_total_realtime a, ")
                .append("                            wxj_busiorder_detail_realtime b ")
                .append("                      WHERE a.source_type ='INTR' ")
                .append("                        AND a.order_code = b.order_code ")
                .append("                        AND b.start_time < SYSDATE ")
                .append("                        AND b.end_time <= SYSDATE ")
                .append("                        AND b.status = '70' ")
                .append("                   GROUP BY b.station_code) m ON l.station_code = ")
                .append("                                                                 m.station_code ")
                .append("                  ) z ")
                .append("    WHERE x.station_code = y.station_code AND y.station_code = z.station_code ")
                .append(" ORDER BY x.play_time DESC ");
        return strBuffer.toString();
    }


    public static String getBroadCastResourceUpInfo(Map reqMap) {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(" SELECT NVL (doing.station_doing, 0) station_doing,               ")/* 在播台站*/
                .append("        NVL (doing.freq_doing, 0) freq_doing,                    ")/* 在播频率*/
                .append("        NVL (doing.power_doing, 0) power_doing,                  ")/* 在播功率*/
                .append("        NVL (doing.trans_doing, 0) trans_doing,               ")/* 在播发射机*/
                .append("        NVL (playtime.playtime_doing, 0) playtime_doing,      ")/* 当日累计播音时间*/
                .append("        NVL (doing.program_doing, 0) program_doing,           ")/* 在播节目数量*/
                .append("        NVL (testdoing.task_doing, 0) task_doing              ")/* 实验压制敌台次数*/
                .append("   FROM (SELECT * ")
                .append("           FROM wxj_common_param_t ")
                .append("          WHERE param_type = 'today_station_tasks') total ")
                .append("        LEFT JOIN ")
                .append("        (SELECT 'today_station_tasks' sql_type, ")
                .append("                COUNT (DISTINCT station_code) station_doing, ")
                .append("                COUNT (DISTINCT trans_code || '-' || freq) freq_doing, ")
                .append("                COUNT (DISTINCT program_name) program_doing, ")
                .append("                COUNT (DISTINCT trans_code) trans_doing, ")
                .append("                SUM (POWER) power_doing, COUNT (*) task_doing ")
                .append("           FROM (SELECT station_code, trans_code, start_time, end_time, freq, ")
                .append("                        program_name, POWER ")
                .append("                   FROM wxj_runplan_realtime_v t ")
                .append("                  WHERE SYSDATE BETWEEN start_date AND end_date ")
                .append("                    AND order_type IN ('D', 'F', 'I') ")
                .append("                    AND run_type = 1 ")
                .append("                    AND days LIKE ")
                .append("                               '%' || (SELECT TO_CHAR (SYSDATE - 1, 'd') ")
                .append("                                         FROM DUAL) || '%' ")
                .append("                    AND (   (    (start_time <= end_time) ")
                .append("                             AND (SYSDATE BETWEEN start_time AND end_time) ")
                .append("                            ) ")
                .append("                         OR (    (start_time > end_time) ")
                .append("                             AND (SYSDATE >= start_time OR SYSDATE <= end_time ")
                .append("                                 ) ")
                .append("                            ) ")
                .append("                        ))) doing ON doing.sql_type = total.param_value ")
                .append("        LEFT JOIN                                        ")/* 当前累计播音时间 */
                .append("        (SELECT 'today_station_tasks' sql_type, ")
                .append("                NVL (SUM (play_time), 0) playtime_doing ")
                .append("           FROM (SELECT CEIL (SUM (end_time - start_time) * 24) play_time ")
                .append("                   FROM (SELECT start_time, end_time ")
                .append("                           FROM wxj_runplan_realtime_v ")
                .append("                          WHERE SYSDATE BETWEEN start_date AND end_date ")
                .append("                            AND order_type IN ('D', 'F', 'I') ")
                .append("                            AND run_type = 1 ")
                .append("                            AND days LIKE ")
                .append("                                      '%' ")
                .append("                                   || (SELECT TO_CHAR (SYSDATE - 1, 'd') ")
                .append("                                         FROM DUAL) ")
                .append("                                   || '%') ")
                .append("                  WHERE start_time <= end_time ")
                .append("                    AND start_time < SYSDATE ")
                .append("                    AND end_time <= SYSDATE ")
                .append("                 UNION ALL ")
                .append("                 SELECT CEIL (SUM (SYSDATE - start_time) * 24) play_time ")
                .append("                   FROM (SELECT start_time, end_time ")
                .append("                           FROM wxj_runplan_realtime_v ")
                .append("                          WHERE SYSDATE BETWEEN start_date AND end_date ")
                .append("                            AND order_type IN ('I') ")
                .append("                            AND run_type = 1 ")
                .append("                            AND days LIKE ")
                .append("                                      '%' ")
                .append("                                   || (SELECT TO_CHAR (SYSDATE - 1, 'd') ")
                .append("                                         FROM DUAL) ")
                .append("                                   || '%') ")
                .append("                  WHERE start_time <= end_time ")
                .append("                    AND start_time < SYSDATE ")
                .append("                    AND end_time > SYSDATE)) playtime ")
                .append("        ON playtime.sql_type = total.param_value ")
                .append("        LEFT JOIN ")
                .append("        (SELECT 'today_station_tasks' sql_type, COUNT (*) task_doing ")
                .append("           FROM (SELECT * ")
                .append("                   FROM wxj_runplan_realtime_v t ")
                .append("                  WHERE SYSDATE BETWEEN start_date AND end_date ")
                .append("                    AND order_type IN ('I') ")
                .append("                    AND run_type = 1 ")
                .append("                    AND days LIKE ")
                .append("                               '%' || (SELECT TO_CHAR (SYSDATE - 1, 'd') ")
                .append("                                         FROM DUAL) || '%' ")
                .append("                    AND (   (    (start_time <= end_time) ")
                .append("                             AND (SYSDATE BETWEEN start_time AND end_time) ")
                .append("                            ) ")
                .append("                         OR (    (start_time > end_time) ")
                .append("                             AND (SYSDATE >= start_time OR SYSDATE <= end_time ")
                .append("                                 ) ")
                .append("                            ) ")
                .append("                        ))) testdoing ON testdoing.sql_type = total.param_value ");
        return strBuffer.toString();
    }

    public static String getBroadCastResourceDownInfo(Map reqMap) {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(" SELECT order_count,                                       ") /* 当日调度令个数*/
                .append("                    play_time,                     ") /* 当日调度令个数播放时长*/
                .append("                              newordercount ")
                .append("   FROM (SELECT * ")
                .append("           FROM wxj_common_param_t ")
                .append("          WHERE param_type = 'today_station_tasks') total ")
                .append("        LEFT JOIN                                          ") /* 当日调度令个数*/
                .append("        (SELECT 'today_station_tasks' sql_type, COUNT (*) order_count ")
                .append("           FROM wxj_busiorder_total_realtime c, ")
                .append("                wxj_busiorder_detail_realtime d ")
                .append("          WHERE c.order_code = d.order_code AND c.operate != 'D') ordercount ")
                .append("        ON ordercount.sql_type = total.param_value ")
                .append("        LEFT JOIN                                  ") /* 当日调度令个数播放时长*/
                .append("        (SELECT 'today_station_tasks' sql_type, ")
                .append("                NVL (SUM (play_time), 0) play_time ")
                .append("           FROM (SELECT CEIL (SUM (end_time - start_time) * 24) play_time ")
                .append("                   FROM (SELECT b.start_time, b.end_time ")
                .append("                           FROM wxj_busiorder_total_realtime a, ")
                .append("                                wxj_busiorder_detail_realtime b ")
                .append("                          WHERE a.order_code = b.order_code ")
                .append("                                AND a.operate != 'D') ")
                .append("                  WHERE start_time <= end_time ")
                .append("                    AND start_time < SYSDATE ")
                .append("                    AND end_time <= SYSDATE ")
                .append("                 UNION ALL ")
                .append("                 SELECT CEIL (SUM (SYSDATE - start_time) * 24) play_time ")
                .append("                   FROM (SELECT b.start_time, b.end_time ")
                .append("                           FROM wxj_busiorder_total_realtime a, ")
                .append("                                wxj_busiorder_detail_realtime b ")
                .append("                          WHERE a.order_code = b.order_code ")
                .append("                                AND a.operate != 'D') ")
                .append("                  WHERE start_time <= end_time ")
                .append("                    AND start_time < SYSDATE ")
                .append("                    AND end_time > SYSDATE)) playtime ")
                .append("        ON playtime.sql_type = total.param_value ")
                .append("        LEFT JOIN                                         ")  /* 当日调度令个数*/
                .append("        (SELECT 'today_station_tasks' sql_type, COUNT (*) newordercount ")
                .append("           FROM wxj_busiorder_total_realtime c, ")
                .append("                wxj_busiorder_detail_realtime d ")
                .append("          WHERE c.order_code = d.order_code AND c.operate != 'A') newordercount ")
                .append("        ON newordercount.sql_type = total.param_value ");
        return strBuffer.toString();
    }

    public static String getBroadCastResourceList(Map reqMap) {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer .append(" SELECT n.order_code_dic, m.freq, t.station_name, b.txname, ")
                .append("        a.antusedcode, decode(m.STATUS,    ")
                .append("        '40', '已检验' , '41' , '不可执行', '60', '已下发',  ")
                .append("        '70', '已完成', '80', '已下发'，'81', '可执行', '未执行') status ")
                .append("   FROM wxj_busiorder_detail_realtime m, ")
                .append("        wxj_busiorder_total_realtime n, ")
                .append("        wxj_station_info_t t, ")
                .append("        wxj_anterna_info_t a, ")
                .append("        wxj_transmitter_info_t b ")
                .append("  WHERE m.order_code = n.order_code ")
                .append("    AND t.station_id = m.station_code ")
                .append("    AND b.transcode = m.trans_code ")
                .append("    AND a.antcode = m.ant_code ")
                .append("    AND m.status_date > (SYSDATE - TO_DSINTERVAL ('0 1:00:00')) ");
        return strBuffer.toString();
    }

    public static String getStationResourceOfTransmitter(Map reqMap) {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer .append(" SELECT   CASE ")
                .append("             WHEN t.work_status BETWEEN 10 AND 19 ")
                .append("                THEN 'inuse' ")
                .append("             WHEN t.work_status BETWEEN 20 AND 29 ")
                .append("                THEN 'free' ")
                .append("             WHEN t.work_status BETWEEN 30 AND 39 ")
                .append("                THEN 'bad' ")
                .append("             WHEN t.work_status BETWEEN 40 AND 49 ")
                .append("                THEN 'overhaul' ")
                .append("             ELSE 'canuse' ")
                .append("          END work_status, ")
                .append("          SUM (num) ")
                .append("     FROM (SELECT   t.work_status, COUNT (t.work_status) num ")
                .append("               FROM (SELECT trans_code, work_status ")
                .append("                       FROM (SELECT ROW_NUMBER () OVER  ")
                .append("                         (PARTITION BY trans_code ORDER BY status_time DESC)   rn, ")
                .append("                                    wxj_transmitter_status_t.* ")
                .append("                               FROM wxj_transmitter_status_t) ")
                .append("                      WHERE rn = 1) t ")
                .append("              WHERE LENGTH (t.work_status) = 2 ")
                .append("                AND TO_NUMBER (t.work_status) BETWEEN 10 AND 49 ")
                .append("           GROUP BY t.work_status) t ")
                .append(" GROUP BY CASE ")
                .append("             WHEN t.work_status BETWEEN 10 AND 19 ")
                .append("                THEN 'inuse' ")
                .append("             WHEN t.work_status BETWEEN 20 AND 29 ")
                .append("                THEN 'free' ")
                .append("             WHEN t.work_status BETWEEN 30 AND 39 ")
                .append("                THEN 'bad' ")
                .append("             WHEN t.work_status BETWEEN 40 AND 49 ")
                .append("                THEN 'overhaul' ")
                .append("             ELSE 'canuse' ")
                .append("          END ");
        return strBuffer.toString();
    }

    public static String getDoingProgramList(Map reqMap) {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer .append(" SELECT t.projdeptname, SUBSTR(t.dt_projplanstartdate,1,10) dt_projplanstartdate,  ")
                .append(" NVL(substr(t.dt_projplanenddate,1,10),'2019-10-01') dt_projplanenddate,t.vc_projname ")
                .append(" FROM wxj_project_info_t t ")
                .append("  WHERE t.int_projstates = '3' ")
                .append(" ORDER BY t.dt_projplanstartdate ");
        return strBuffer.toString();
    }

    public static String getTodoProgramList(Map reqMap) {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer .append(" SELECT   t.projdeptname, substr(t.dt_projplanstartdate, 1, 10) dt_projplanstartdate,  ")
                .append("  NVL(substr(t.dt_projplanenddate , 1, 10), '2019-10-01') dt_projplanenddate, t.vc_projname ")
                .append(" FROM wxj_project_info_t t ")
                .append("  WHERE TO_CHAR (SYSDATE, 'yyyy-MM-dd hh24:mi:ss') < t.dt_projplanstartdate ")
                 .append("  AND t.int_projstates = '1' ")
                .append(" ORDER BY t.dt_projplanstartdate ");
        return strBuffer.toString();
    }

    public static String getCompleteConditionInfo(Map reqMap) {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer .append(" SELECT projdeptname, donecount ")
                .append("   FROM (SELECT * ")
                .append("           FROM wxj_common_param_t ")
                .append("          WHERE param_type = 'today_station_tasks') total ")
                .append("        LEFT JOIN ")
                .append("        (SELECT 'today_station_tasks' sql_type, ")
                .append("                COUNT (DISTINCT projdeptname) projdeptname ")
                .append("           FROM wxj_project_info_t ")
                .append("          WHERE int_projstates = '1') stanum        ")/* !!!状态为已完成的项目 */
                .append("        ON stanum.sql_type = total.param_value ")
                .append("        LEFT JOIN ")
                .append("        (SELECT 'today_station_tasks' sql_type, COUNT (*) donecount ")
                .append("           FROM wxj_project_info_t ")
                .append("          WHERE int_projstates = '1') donecount    ") /* !!!状态为已完成的项目 */
                .append("        ON donecount.sql_type = total.param_value ");
        return strBuffer.toString();
    }

    public static String getCompleteConditionList(Map reqMap) {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(" SELECT   projdeptname, COUNT (projdeptname) projdeptname, ")
                .append("          SUM (CEIL (  (  (SELECT ADD_MONTHS (SYSDATE, 36) ")
                .append("                             FROM DUAL) ")
                .append("     - TO_DATE (   SUBSTR (dt_projplanstartdate, 0, 10)")/* !!!项目的开始时间和结束时间不准确 */
                .append("                                   || ' 00:00:00', ")
                .append("                                   'yyyy-MM-dd hh24:mi:ss' ")
                .append("                                  ) ")
                .append("                       ) ")
                .append("                     * 24 ")
                .append("                    ) ")
                .append("              )  time ")
                .append("     FROM wxj_project_info_t t ")
                .append("    WHERE int_projstates = '1'                      ")/* !!!状态为已完成的项目 */
                .append(" GROUP BY projdeptname ");
        return strBuffer.toString();
    }

    public static String getProgramPlaybackTimeList(Map reqMap) {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(" select t.PROGRAM_NAME, NVL(p1.play_time, 0) + NVL(p2.play_time, 0) + NVL(p3.play_time, 0) + NVL(p4.play_time, 0) play_time ")
                .append(" from wxj_program_info_t t ")
                .append(" left join ")
                .append(" (SELECT  PROGRAM_NAME,  CEIL (SUM (end_time - start_time) * 24) play_time ")
                .append("                           FROM wxj_runplan_history_t  ")
                .append("                          WHERE  start_time >= to_date('2019-01-01 00:00:00', 'yyyy-MM-dd hh24:mi:ss') ")
                .append("                                AND end_time <= to_date('2019-01-07 17:00:00', 'yyyy-MM-dd hh24:mi:ss') ")
                .append("                                AND OPERATE != 'D'            ")
                .append("                         group by PROGRAM_NAME ")
                .append("                         ) p1 ")/* 开始时间和结束时间都在重保期时间范围内*/
                .append(" on p1.PROGRAM_NAME = t.PROGRAM_NAME ")
                .append(" left join ")
                .append(" (SELECT PROGRAM_NAME,  CEIL (SUM (to_date('2019-01-07 17:00:00', 'yyyy-MM-dd hh24:mi:ss')  - start_time) * 24) play_time ")
                .append("                           FROM wxj_runplan_history_t  ")
                .append("                          WHERE  start_time >= to_date('2019-01-01 00:00:00', 'yyyy-MM-dd hh24:mi:ss') ")
                .append("                                AND start_time < to_date('2019-01-07 17:00:00', 'yyyy-MM-dd hh24:mi:ss') ")
                .append("                                AND end_time > to_date('2019-01-07 17:00:00', 'yyyy-MM-dd hh24:mi:ss')  ")
                .append("                                AND OPERATE != 'D'         ")
                .append("                         group by PROGRAM_NAME ")
                .append("                         ) p2 ")/* 开始时间在重保期时间范围内,结束时间重保期时间范围超过*/
                .append(" on p2.PROGRAM_NAME = t.PROGRAM_NAME ")
                .append(" left join ")
                .append(" (SELECT PROGRAM_NAME,  CEIL (SUM (end_time - start_time) * 24) play_time ")
                .append("                           FROM wxj_runplan_realtime_t  ")
                .append("                          WHERE  start_time >= to_date('2019-01-01 00:00:00', 'yyyy-MM-dd hh24:mi:ss') ")
                .append("                                AND end_time <= to_date('2019-01-07 17:00:00', 'yyyy-MM-dd hh24:mi:ss') ")
                .append("                                AND OPERATE != 'D'            ")
                .append("                         group by PROGRAM_NAME ")
                .append("                         ) p3 ")/* 开始时间和结束时间都在重保期时间范围内*/
                .append(" on p3.PROGRAM_NAME = t.PROGRAM_NAME ")
                .append(" left join ")
                .append(" (SELECT PROGRAM_NAME,  CEIL (SUM (to_date('2019-01-07 17:00:00', 'yyyy-MM-dd hh24:mi:ss')  - start_time) * 24) play_time ")
                .append("                           FROM wxj_runplan_realtime_t  ")
                .append("                          WHERE  start_time >= to_date('2019-01-01 00:00:00', 'yyyy-MM-dd hh24:mi:ss') ")
                .append("                                AND start_time < to_date('2019-01-07 17:00:00', 'yyyy-MM-dd hh24:mi:ss') ")
                .append("                                AND end_time > to_date('2019-01-07 17:00:00', 'yyyy-MM-dd hh24:mi:ss')  ")
                .append("                                AND OPERATE != 'D'         ")
                .append("                         group by PROGRAM_NAME ")
                .append("                         ) p4 ")/* 开始时间在重保期时间范围内,结束时间重保期时间范围超过*/
                .append(" on p4.PROGRAM_NAME = t.PROGRAM_NAME ")
                .append(" order by play_time desc ");
        return strBuffer.toString();
    }

    public static String getProgramPlayStatusList(Map reqMap) {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(" SELECT info.program_name, info.usedcode, info.antcode_bfd_dic, info.freq, ")
                .append("        excep.excep, info.serv_area ")
                .append("   FROM (SELECT   t.program_code, t.program_name, m.usedcode, n.antcode_bfd_dic, ")
                .append("                  w.freq, w.serv_area ")
                .append("             FROM wxj_program_info_t t, ")
                .append("                  wxj_runplan_realtime_t w, ")
                .append("                  wxj_transmitter_info_t m, ")
                .append("                  wxj_anterna_info_t n ")
                .append("            WHERE SYSDATE BETWEEN w.start_time AND w.end_time ")
                .append("              AND t.program_code = w.program_code ")
                .append("              AND w.antenna_code = n.antcode ")
                .append("              AND w.trans_code = m.transcode ")
                .append("              AND w.operate != 'D' ")
                .append("              AND w.order_type IN ('D', 'F', 'I') ")
                .append("              AND t.pg_id = '4' ")
                .append("         ORDER BY t.program_name) info ")
                .append("        LEFT JOIN ")
                .append("        (SELECT program_code, 'err' excep ")
                .append("           FROM wxj_runplan_realtime_t ")
                .append("          WHERE run_type != 1) excep ON info.program_code = excep.program_code ");
        return strBuffer.toString();
    }
}
