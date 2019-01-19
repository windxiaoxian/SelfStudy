SELECT *
  FROM (SELECT   busiorder.*, transmitter_status.POWER,
                 transmitter_status.amrp
            FROM (SELECT a.order_code_dic order_name, a.send_dept, a.sender,
                         b.station_name receive_station, a.receiver,
                         b.station_name,
                            DECODE (SUBSTR (c.trans_code, 4, 1),
                                    '0', 'A',
                                    '1', 'B',
                                    '2', 'C',
                                    '3', 'D',
                                    '4', 'E',
                                    '5', 'F',
                                    '6', 'G',
                                    '7', 'H',
                                    'ERROR'
                                   )
                         || SUBSTR (c.trans_code, 5) transmitter,
                         a.status_date
                    FROM (SELECT *
                            FROM wxj_busiorder_total_realtime
                           WHERE LENGTH (station_code) = 2) a
                         LEFT JOIN
                         wxj_station_info_t b ON a.station_code = b.station_id
                         LEFT JOIN wxj_busiorder_detail_realtime c
                         ON a.order_code = c.order_code
                         ) busiorder
                 LEFT JOIN
                 (SELECT NVL (id2stationname (station_code),
                              '-1'
                             ) station_name,
                            DECODE (SUBSTR (trans_code, 4, 1),
                                    '0', 'A',
                                    '1', 'B',
                                    '2', 'C',
                                    '3', 'D',
                                    '4', 'E',
                                    '5', 'F',
                                    '6', 'G',
                                    '7', 'H',
                                    'ERROR'
                                   )
                         || SUBSTR (trans_code, 5) transmitter,
                         CASE
                            WHEN CEIL (POWER) < 0
                               THEN '0'
                            WHEN CEIL (POWER) > 100
                               THEN '100'
                            ELSE TO_CHAR (CEIL (POWER))
                         END POWER,
                         CASE
                            WHEN CEIL (amrp) < 0
                               THEN '0'
                            WHEN CEIL (amrp) > 100
                               THEN '100'
                            ELSE TO_CHAR (CEIL (amrp))
                         END amrp
                    FROM wxj_transmitter_status_t
                   WHERE LENGTH (station_code) = 2 AND LENGTH (trans_code) = 6) transmitter_status
                 ON transmitter_status.station_name = busiorder.station_name
               AND transmitter_status.transmitter = busiorder.transmitter
           WHERE busiorder.station_name IS NOT NULL
             AND LENGTH (busiorder.transmitter) = 3
        ORDER BY status_date DESC)
 WHERE ROWNUM < 11