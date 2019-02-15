SELECT total.param_value order_type, NVL (done.task_done, 0) task_done,
       NVL (doing.task_doing, 0) task_doing,
       NVL (doing.freq_doing, 0) freq_doing,
       NVL (doing.program_doing, 0) program_doing,
       NVL (doing.trans_doing, 0) trans_doing,
       NVL (doing.power_doing, 0) power_doing,
       NVL (todo.task_todo, 0) task_todo
  FROM (SELECT *
          FROM wxj_common_param_t
         WHERE param_type = 'order_type') total
       LEFT JOIN
       (SELECT   order_type, COUNT (*) task_done
            FROM (SELECT trans_code, start_time, end_time, freq, program_name,
                         POWER,
                         DECODE (order_type,
                                 'I', 'SY',
                                 'F', 'DW',
                                 'DN'
                                ) order_type
                    FROM wxj_runplan_realtime_v
                   WHERE SYSDATE BETWEEN start_date AND end_date
                     AND order_type IN ('D', 'F', 'I')
                     AND run_type = 1
                     AND days LIKE
                              '%' || (SELECT TO_CHAR (SYSDATE - 1, 'd')
                                        FROM DUAL) || '%'
                     AND (   (    (start_time <= end_time)
                              AND (SYSDATE > end_time)
                             )
                          OR (    (start_time > end_time)
                              AND (SYSDATE > end_time)
                             )
                         ))
        GROUP BY order_type) done ON done.order_type = total.param_value
       LEFT JOIN
       (SELECT   order_type,
                 COUNT (DISTINCT trans_code || '-' || freq) freq_doing,
                 COUNT (DISTINCT program_name) program_doing,
                 COUNT (DISTINCT trans_code) trans_doing,
                 SUM (POWER) power_doing, COUNT (*) task_doing
            FROM (SELECT trans_code, start_time, end_time, freq, program_name,
                         POWER,
                         DECODE (order_type,
                                 'I', 'SY',
                                 'F', 'DW',
                                 'DN'
                                ) order_type
                    FROM wxj_runplan_realtime_v
                   WHERE SYSDATE BETWEEN start_date AND end_date
                     AND order_type IN ('D', 'F', 'I')
                     AND run_type = 1
                     AND days LIKE
                              '%' || (SELECT TO_CHAR (SYSDATE - 1, 'd')
                                        FROM DUAL) || '%'
                     AND (   (    (start_time <= end_time)
                              AND (SYSDATE BETWEEN start_time AND end_time)
                             )
                          OR (    (start_time > end_time)
                              AND (SYSDATE >= start_time
                                   OR SYSDATE <= end_time
                                  )
                             )
                         ))
        GROUP BY order_type) doing ON doing.order_type = total.param_value
       LEFT JOIN
       (SELECT   order_type, COUNT (*) task_todo
            FROM (SELECT trans_code, start_time, end_time, freq, program_name,
                         POWER,
                         DECODE (order_type,
                                 'I', 'SY',
                                 'F', 'DW',
                                 'DN'
                                ) order_type
                    FROM wxj_runplan_realtime_v
                   WHERE SYSDATE BETWEEN start_date AND end_date
                     AND order_type IN ('D', 'F', 'I')
                     AND run_type = 1
                     AND days LIKE
                              '%' || (SELECT TO_CHAR (SYSDATE - 1, 'd')
                                        FROM DUAL) || '%'
                     AND (   (    (start_time <= end_time)
                              AND (SYSDATE < start_time)
                             )
                          OR (    (start_time > end_time)
                              AND (SYSDATE < start_time AND SYSDATE > end_time
                                  )
                             )
                         ))
        GROUP BY order_type) todo ON todo.order_type = total.param_value