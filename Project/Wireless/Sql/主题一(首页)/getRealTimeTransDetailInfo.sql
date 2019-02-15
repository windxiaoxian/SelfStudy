SELECT   room, transmitter, work_status
    FROM (SELECT DISTINCT SUBSTR (trans_code, 4, 1) room_order,
                             '机房'
                          || DECODE (SUBSTR (trans_code, 4, 1),
                                     '0', '甲',
                                     '1', '乙',
                                     '2', '丙',
                                     '3', '丁',
                                     '4', '戊',
                                     '5', '己',
                                     '6', '庚',
                                     '7', '辛',
                                     'ERROR'
                                    ) room,
                          SUBSTR (trans_code, 5) transmitter,
                          DECODE (SUBSTR (work_status, 1, 1),
                                  '1', '正常',
                                  '2', '空闲',
                                  '3', '故障',
                                  '4', '检修',
                                  '未知'
                                 ) work_status
                     FROM wxj_transmitter_status_t
                    WHERE station_code IN (SELECT station_id
                                             FROM wxj_station_info_t
                                            WHERE station_code = '2022')
                      AND LENGTH (trans_code) = 6)
   WHERE room <> '机房ERROR'
ORDER BY room_order, transmitter