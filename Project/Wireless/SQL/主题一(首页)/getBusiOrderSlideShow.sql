SELECT *
  FROM (SELECT   a.order_code_dic order_name, a.send_dept, a.sender,
                 b.station_name receive_station, a.receiver, b.station_name,
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
        ORDER BY status_date DESC)
 WHERE station_name IS NOT NULL AND LENGTH (transmitter) = 3 AND ROWNUM < 11