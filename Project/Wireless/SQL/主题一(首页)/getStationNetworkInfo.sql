SELECT a.station_code, a.ping_status, b.bandwidth_used, b.bandwidth_unused
  FROM wxj_station_network_info_t a
       JOIN
       (SELECT CEIL (SUM (bandwidth_used)) bandwidth_used,
               CEIL (SUM (bandwidth_unused)) bandwidth_unused
          FROM wxj_station_network_info_t) b ON 1 = 1