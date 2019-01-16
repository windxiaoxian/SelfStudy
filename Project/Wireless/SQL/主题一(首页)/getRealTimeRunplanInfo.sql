SELECT SUM (freq_doing) freq_count, SUM (trans_doing) trans_count,
       SUM (power_doing) power_count
  FROM (SELECT total.param_value order_type,
               NVL (doing.freq_doing, 0) freq_doing,
               NVL (doing.power_doing, 0) power_doing,
               NVL (doing.trans_doing, 0) trans_doing
          FROM (SELECT *
                  FROM wxj_common_param_t
                 WHERE param_type = 'order_type') total
               LEFT JOIN
               (SELECT   order_type, COUNT (DISTINCT freq) freq_doing,
                         COUNT (DISTINCT program_name) program_doing,
                         COUNT (DISTINCT trans_code) trans_doing,
                         SUM (POWER) power_doing, COUNT (*) task_doing
                    FROM (SELECT trans_code, start_time, end_time, freq,
                                 program_name, POWER,
                                 DECODE (order_type,
                                         'I', 'SY',
                                         'F', 'DW',
                                         'M', 'DW',
                                         'DN'
                                        ) order_type
                            FROM wxj_runplan_realtime_t
                           WHERE SYSDATE BETWEEN start_date AND end_date
                             AND order_type IN ('D', 'S', 'L', 'F', 'M', 'I')
                             AND operate IN ('A', 'U')
                             AND run_type = 1
                             AND days LIKE
                                       '%'
                                    || (SELECT TO_CHAR (SYSDATE - 1, 'd')
                                          FROM DUAL)
                                    || '%'
                             AND trans_code IN (SELECT trans_code
                                                  FROM wxj_transmitter_status_t
                                                 WHERE work_status = '10')
                             AND (   (    (start_time <= end_time)
                                      AND (SYSDATE BETWEEN start_time AND end_time
                                          )
                                     )
                                  OR (    (start_time > end_time)
                                      AND (   SYSDATE >= start_time
                                           OR SYSDATE <= end_time
                                          )
                                     )
                                 ))
                GROUP BY order_type) doing
               ON doing.order_type = total.param_value
               )