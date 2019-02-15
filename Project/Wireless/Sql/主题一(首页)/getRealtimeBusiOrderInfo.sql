SELECT *
  FROM (SELECT COUNT (DISTINCT order_code) year_order_count,
               CEIL (SUM (end_time - start_time) * 24) year_hour_count
          FROM (SELECT order_code,
                       CASE
                          WHEN status_date > start_time
                             THEN status_date
                          ELSE start_time
                       END start_time,
                       CASE
                          WHEN SYSDATE > end_time
                             THEN end_time
                          ELSE SYSDATE
                       END end_time
                  FROM wxj_busiorder_detail_history_t
                 WHERE status_date >
                                   TO_DATE (TO_CHAR (SYSDATE, 'yyyy'), 'yyyy')
                   AND order_code NOT LIKE '88888%'
                   AND SYSDATE >= start_time
                   AND start_time < = end_time
                   AND LENGTH (station_code) = 2))
       JOIN
       (SELECT COUNT (DISTINCT order_code) today_order_count,
               CEIL (SUM (end_time - start_time) * 24) today_hour_count
          FROM (SELECT order_code,
                       CASE
                          WHEN status_date > start_time
                             THEN status_date
                          ELSE start_time
                       END start_time,
                       CASE
                          WHEN SYSDATE > end_time
                             THEN end_time
                          ELSE SYSDATE
                       END end_time
                  FROM wxj_busiorder_detail_realtime
                 WHERE status_date >
                                   TO_DATE (TO_CHAR (SYSDATE, 'yyyy'), 'yyyy')
                   AND order_code NOT LIKE '88888%'
                   AND SYSDATE >= start_time
                   AND start_time < = end_time
                   AND LENGTH (station_code) = 2)) ON 1 = 1