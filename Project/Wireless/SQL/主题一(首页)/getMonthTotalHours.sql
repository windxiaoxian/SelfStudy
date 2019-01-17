SELECT   order_type, MONTH, MIN (history_date) history_date,
         CEIL (SUM (play_time) * 24) total_hour
    FROM (SELECT CASE
                    WHEN start_time <= end_time
                       THEN end_time - start_time
                    WHEN start_time > end_time
                       THEN   end_time
                            - TO_DATE (history_date || ' 00:00:00',
                                       'yyyy-MM-dd HH24:Mi:ss'
                                      )
                            + TO_DATE (history_date || ' 23:59:59',
                                       'yyyy-MM-dd HH24:Mi:ss'
                                      )
                            - start_time
                 END play_time,
                 DECODE (order_type_dic,
                         '实验', 'SY',
                         '对外广播', 'DW',
                         '中转外', 'DW',
                         'DN'
                        ) order_type,
                 history_date, b.param_name MONTH
            FROM (SELECT *
                    FROM wxj_runplan_history_t
                   WHERE history_date >
                                 TO_CHAR (ADD_MONTHS (SYSDATE, -11),
                                          'yyyy-MM')
                     AND order_type_dic IN
                            ('实验', '对内广播', '对外广播', '外转中',
                             '中转外', '地方广播')) a
                 LEFT JOIN
                 (SELECT param_value, param_name
                    FROM wxj_common_param_t
                   WHERE param_type = 'month') b
                 ON SUBSTR (a.history_date, 6, 2) = b.param_value
                 )
GROUP BY order_type, MONTH
ORDER BY order_type, history_date