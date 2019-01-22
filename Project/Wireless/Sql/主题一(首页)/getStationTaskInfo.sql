SELECT todo.task_todo, todo.play_time_todo, done.task_done,
       done.play_time_done, STOP.stop_time, alarm.alarm_count,
       busiorder.busiorder_count, doing.power_doing
  FROM (SELECT *
          FROM wxj_common_param_t
         WHERE param_type = 'today_station_tasks') total
       LEFT JOIN
       (SELECT 'today_station_tasks' sql_type, COUNT (*) task_todo,
               NVL (CEIL (SUM (play_time) * 24), 0) play_time_todo
          FROM (SELECT CASE
                          WHEN start_time <= end_time
                             THEN end_time - start_time
                          WHEN start_time > end_time
                             THEN   end_time
                                  - TO_DATE (   TO_CHAR (SYSDATE,
                                                         'yyyy-MM-dd')
                                             || ' 00:00:00',
                                             'yyyy-MM-dd HH24:Mi:ss'
                                            )
                                  + TO_DATE (   TO_CHAR (SYSDATE,
                                                         'yyyy-MM-dd')
                                             || ' 23:59:59',
                                             'yyyy-MM-dd HH24:Mi:ss'
                                            )
                                  - start_time
                       END play_time
                  FROM wxj_runplan_realtime_v
                 WHERE station_code IN (SELECT station_id
                                          FROM wxj_station_info_t
                                         WHERE station_code = '2022')
                   AND SYSDATE BETWEEN start_date AND end_date
                   AND order_type IN ('D', 'F', 'I')
                   AND run_type = 1
                   AND days LIKE
                              '%' || (SELECT TO_CHAR (SYSDATE - 1, 'd')
                                        FROM DUAL) || '%')) todo
       ON todo.sql_type = total.param_value
       LEFT JOIN
       (SELECT 'today_station_tasks' sql_type,
               NVL (SUM (task_done), 0) task_done,
               NVL (SUM (play_time), 0) play_time_done
          FROM (SELECT COUNT (*) task_done,
                       CEIL (SUM (end_time - start_time) * 24) play_time
                  FROM (SELECT start_time, end_time
                          FROM wxj_runplan_realtime_v
                         WHERE station_code IN (SELECT station_id
                                                  FROM wxj_station_info_t
                                                 WHERE station_code = '2022')
                           AND SYSDATE BETWEEN start_date AND end_date
                           AND order_type IN ('D', 'F', 'I')
                           AND run_type = 1
                           AND days LIKE
                                     '%'
                                  || (SELECT TO_CHAR (SYSDATE - 1, 'd')
                                        FROM DUAL)
                                  || '%')
                 WHERE start_time <= end_time
                   AND start_time < SYSDATE
                   AND end_time <= SYSDATE
                UNION ALL
                SELECT COUNT (*) task_done,
                       CEIL (SUM (SYSDATE - start_time) * 24) play_time
                  FROM (SELECT start_time, end_time
                          FROM wxj_runplan_realtime_v
                         WHERE station_code IN (SELECT station_id
                                                  FROM wxj_station_info_t
                                                 WHERE station_code = '2022')
                           AND SYSDATE BETWEEN start_date AND end_date
                           AND order_type IN ('D', 'F', 'I')
                           AND run_type = 1
                           AND days LIKE
                                     '%'
                                  || (SELECT TO_CHAR (SYSDATE - 1, 'd')
                                        FROM DUAL)
                                  || '%')
                 WHERE start_time <= end_time
                   AND start_time < SYSDATE
                   AND end_time > SYSDATE
                UNION ALL
                SELECT COUNT (*) task_done,
                       CEIL
                           (  SUM (SYSDATE - start_time) * 24
                            +   SUM (  start_time
                                     - TO_DATE (   TO_CHAR (SYSDATE,
                                                            'yyyy-MM-dd'
                                                           )
                                                || ' 00:00:00',
                                                'yyyy-MM-dd HH24:Mi:ss'
                                               )
                                    )
                              * 24
                           ) play_time
                  FROM (SELECT start_time, end_time
                          FROM wxj_runplan_realtime_v
                         WHERE station_code IN (SELECT station_id
                                                  FROM wxj_station_info_t
                                                 WHERE station_code = '2022')
                           AND SYSDATE BETWEEN start_date AND end_date
                           AND order_type IN ('D', 'F', 'I')
                           AND run_type = 1
                           AND days LIKE
                                     '%'
                                  || (SELECT TO_CHAR (SYSDATE - 1, 'd')
                                        FROM DUAL)
                                  || '%')
                 WHERE start_time > end_time AND start_time <= SYSDATE
                UNION ALL
                SELECT COUNT (*) task_done,
                       CEIL
                           (  SUM (  SYSDATE
                                   - TO_DATE (   TO_CHAR (SYSDATE,
                                                          'yyyy-MM-dd'
                                                         )
                                              || ' 00:00:00',
                                              'yyyy-MM-dd HH24:Mi:ss'
                                             )
                                  )
                            * 24
                           ) play_time
                  FROM (SELECT start_time, end_time
                          FROM wxj_runplan_realtime_v
                         WHERE station_code IN (SELECT station_id
                                                  FROM wxj_station_info_t
                                                 WHERE station_code = '2022')
                           AND SYSDATE BETWEEN start_date AND end_date
                           AND order_type IN ('D', 'F', 'I')
                           AND run_type = 1
                           AND days LIKE
                                     '%'
                                  || (SELECT TO_CHAR (SYSDATE - 1, 'd')
                                        FROM DUAL)
                                  || '%')
                 WHERE start_time > end_time
                   AND start_time > SYSDATE
                   AND end_time > SYSDATE
                UNION ALL
                SELECT COUNT (*) task_done,
                       CEIL
                           (  SUM (  end_time
                                   - TO_DATE (   TO_CHAR (SYSDATE,
                                                          'yyyy-MM-dd'
                                                         )
                                              || ' 00:00:00',
                                              'yyyy-MM-dd HH24:Mi:ss'
                                             )
                                  )
                            * 24
                           ) play_time
                  FROM (SELECT start_time, end_time
                          FROM wxj_runplan_realtime_v
                         WHERE station_code IN (SELECT station_id
                                                  FROM wxj_station_info_t
                                                 WHERE station_code = '2022')
                           AND SYSDATE BETWEEN start_date AND end_date
                           AND order_type IN ('D', 'F', 'I')
                           AND run_type = 1
                           AND days LIKE
                                     '%'
                                  || (SELECT TO_CHAR (SYSDATE - 1, 'd')
                                        FROM DUAL)
                                  || '%')
                 WHERE start_time > end_time
                   AND start_time > SYSDATE
                   AND end_time <= SYSDATE)) done
       ON done.sql_type = total.param_value
       LEFT JOIN
       (SELECT 'today_station_tasks' sql_type, 0 stop_time
          FROM DUAL) STOP ON STOP.sql_type = total.param_value
       LEFT JOIN
       (SELECT 'today_station_tasks' sql_type, COUNT (*) alarm_count
          FROM wxj_alarm_realtime_t
         WHERE station_code = '2022') alarm ON alarm.sql_type =
                                                             total.param_value
       LEFT JOIN
       (SELECT 'today_station_tasks' sql_type, COUNT (*) busiorder_count
          FROM wxj_busiorder_total_realtime
         WHERE station_code IN (SELECT station_id
                                  FROM wxj_station_info_t
                                 WHERE station_code = '2022')
           AND order_code_dic NOT LIKE '台%') busiorder
       ON busiorder.sql_type = total.param_value
       LEFT JOIN
       (SELECT 'today_station_tasks' sql_type,
               NVL (SUM (POWER), 0) power_doing
          FROM (SELECT POWER
                  FROM wxj_runplan_realtime_v
                 WHERE station_code IN (SELECT station_id
                                          FROM wxj_station_info_t
                                         WHERE station_code = '2022')
                   AND SYSDATE BETWEEN start_date AND end_date
                   AND order_type IN ('D', 'F', 'I')
                   AND run_type = 1
                   AND days LIKE
                              '%' || (SELECT TO_CHAR (SYSDATE - 1, 'd')
                                        FROM DUAL) || '%'
                   AND (   (    (start_time <= end_time)
                            AND (SYSDATE BETWEEN start_time AND end_time)
                           )
                        OR (    (start_time > end_time)
                            AND (SYSDATE >= start_time OR SYSDATE <= end_time
                                )
                           )
                       ))) doing ON doing.sql_type = total.param_value