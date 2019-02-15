SELECT   order_type, SUM (total_hour) total_hour
    FROM (SELECT   order_type,
                   CEIL (SUM (end_time - start_time) * 24) total_hour
              FROM (SELECT trans_code, start_time, end_time, freq,
                           program_name, POWER,
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
                                        FROM DUAL) || '%')
             WHERE start_time <= end_time
               AND start_time < SYSDATE
               AND end_time <= SYSDATE
          GROUP BY order_type
          UNION ALL
          SELECT   order_type,
                   CEIL (SUM (SYSDATE - start_time) * 24) total_hour
              FROM (SELECT trans_code, start_time, end_time, freq,
                           program_name, POWER,
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
                                        FROM DUAL) || '%')
             WHERE start_time <= end_time
               AND start_time < SYSDATE
               AND end_time > SYSDATE
          GROUP BY order_type
          UNION ALL
          SELECT   order_type,
                   CEIL (  SUM (SYSDATE - start_time) * 24
                         +   SUM (  start_time
                                  - TO_DATE (   TO_CHAR (SYSDATE,
                                                         'yyyy-MM-dd')
                                             || ' 00:00:00',
                                             'yyyy-MM-dd HH24:Mi:ss'
                                            )
                                 )
                           * 24
                        ) total_hour
              FROM (SELECT trans_code, start_time, end_time, freq,
                           program_name, POWER,
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
                                        FROM DUAL) || '%')
             WHERE start_time > end_time AND start_time <= SYSDATE
          GROUP BY order_type
          UNION ALL
          SELECT   order_type,
                   CEIL (  SUM (  SYSDATE
                                - TO_DATE (   TO_CHAR (SYSDATE, 'yyyy-MM-dd')
                                           || ' 00:00:00',
                                           'yyyy-MM-dd HH24:Mi:ss'
                                          )
                               )
                         * 24
                        ) total_hour
              FROM (SELECT trans_code, start_time, end_time, freq,
                           program_name, POWER,
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
                                        FROM DUAL) || '%')
             WHERE start_time > end_time
               AND start_time > SYSDATE
               AND end_time > SYSDATE
          GROUP BY order_type
          UNION ALL
          SELECT   order_type,
                   CEIL (  SUM (  end_time
                                - TO_DATE (   TO_CHAR (SYSDATE, 'yyyy-MM-dd')
                                           || ' 00:00:00',
                                           'yyyy-MM-dd HH24:Mi:ss'
                                          )
                               )
                         * 24
                        ) total_hour
              FROM (SELECT trans_code, start_time, end_time, freq,
                           program_name, POWER,
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
                                        FROM DUAL) || '%')
             WHERE start_time > end_time
               AND start_time > SYSDATE
               AND end_time <= SYSDATE
          GROUP BY order_type)
GROUP BY order_type