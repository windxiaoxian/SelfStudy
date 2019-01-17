SELECT   MONTH, MIN (accident_date) accident_date,
         SUM (stop_seconds) stop_seconds
    FROM (SELECT accident_date, stop_seconds, param_name MONTH
            FROM (SELECT TO_CHAR (accident_date, 'yyyy-MM') accident_date,
                         stop_seconds
                    FROM wxj_accident_info_t
                   WHERE accident_date >
                            TO_DATE (TO_CHAR (ADD_MONTHS (SYSDATE, -11),
                                              'yyyy-MM'
                                             ),
                                     'yyyy-MM'
                                    )) a
                 LEFT JOIN
                 (SELECT param_value, param_name
                    FROM wxj_common_param_t
                   WHERE param_type = 'month') b
                 ON SUBSTR (a.accident_date, 6, 2) = b.param_value
                 )
GROUP BY MONTH
ORDER BY accident_date