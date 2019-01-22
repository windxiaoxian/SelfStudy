SELECT station_count, freq_count, target_station_count, target_freq_count
  FROM (SELECT *
          FROM wxj_common_param_t
         WHERE param_type = 'today_station_tasks') total
       LEFT JOIN
       (SELECT 'today_station_tasks' sql_type,
               COUNT (DISTINCT station_name) target_station_count,
               COUNT (DISTINCT station_name || '-' || freq) target_freq_count
          FROM (SELECT station_name, freq,
                       TO_DATE (   TO_CHAR (SYSDATE, 'yyyy-MM-dd')
                                || TO_CHAR (start_time, ' HH24:Mi:ss'),
                                'yyyy-MM-dd HH24:Mi:ss'
                               ) start_time,
                       TO_DATE (   TO_CHAR (SYSDATE, 'yyyy-MM-dd')
                                || TO_CHAR (end_time, ' HH24:Mi:ss'),
                                'yyyy-MM-dd HH24:Mi:ss'
                               ) end_time
                  FROM wxj_target_runplan_info_t
                 WHERE SYSDATE BETWEEN start_date AND end_date
                   AND days LIKE
                              '%' || (SELECT TO_CHAR (SYSDATE - 1, 'd')
                                        FROM DUAL) || '%')
         WHERE (   (    (start_time <= end_time)
                    AND (SYSDATE BETWEEN start_time AND end_time)
                   )
                OR (    (start_time > end_time)
                    AND (SYSDATE >= start_time OR SYSDATE <= end_time)
                   )
               )) target ON target.sql_type = total.param_value
       LEFT JOIN
       (SELECT 'today_station_tasks' sql_type,
               COUNT (DISTINCT station_code) station_count,
               COUNT (DISTINCT station_code || '-' || freq) freq_count
          FROM wxj_runplan_realtime_v
         WHERE SYSDATE BETWEEN start_date AND end_date
           AND order_type IN ('I')
           AND run_type = 1
           AND days LIKE '%' || (SELECT TO_CHAR (SYSDATE - 1, 'd')
                                   FROM DUAL) || '%'
           AND (   (    (start_time <= end_time)
                    AND (SYSDATE BETWEEN start_time AND end_time)
                   )
                OR (    (start_time > end_time)
                    AND (SYSDATE >= start_time OR SYSDATE <= end_time)
                   )
               )) china ON china.sql_type = total.param_value