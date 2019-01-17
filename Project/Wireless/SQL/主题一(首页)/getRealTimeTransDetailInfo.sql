SELECT DISTINCT *
           FROM (SELECT    '机房'
                        || DECODE (SUBSTR (trans_code, 4, 1),
                                   '0', 'A',
                                   '1', 'B',
                                   '2', 'C',
                                   '3', 'D',
                                   '4', 'E',
                                   '5', 'F',
                                   '6', 'G',
                                   '7', 'H',
                                   'ERROR'
                                  ) room,
                        SUBSTR (trans_code, 5) transmitter,
                        DECODE (SUBSTR (work_status, 1, 1),
                                '1', '正常',
                                '2', '空闲',
                                '3', '故障',
                                '4', '检修',
                                '未知'
                               ) work_status,
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
                  WHERE station_code IN (SELECT station_id
                                           FROM wxj_station_info_t
                                          WHERE station_code = '2022')
                    AND LENGTH (trans_code) = 6)
          WHERE room <> '机房ERROR'
       ORDER BY room, transmitter