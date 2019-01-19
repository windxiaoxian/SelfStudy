SELECT   a.station_code, NVL (b.work_status, 'work') work_status
    FROM wxj_station_info_t a
         LEFT JOIN
         (SELECT station_code,
                 CASE
                    WHEN work_status = '2'
                       THEN 'free'
		    WHEN work_status = '3'
                       THEN 'bad'
                    WHEN work_status = '4'
                       THEN 'fix'
                    ELSE 'work'
                 END work_status
            FROM (SELECT   station_code, SUM (work_status) work_status
                      FROM (SELECT station_code,
                                   DECODE (SUBSTR (work_status, 1, 1),
                                           '1', '7',
                                           '2', '2',
					   '3', '3',
                                           '4', '4',
                                           'error'
                                          ) work_status
                              FROM wxj_transmitter_status_t
                             WHERE LENGTH (station_code) = 2)
                     WHERE work_status <> 'error'
                  GROUP BY station_code)) b ON a.station_id = b.station_code
ORDER BY station_code