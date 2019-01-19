SELECT SUM (access_count) access_count, SUM (virus_count) virus_count,
       SUM (mole_count) mole_count, SUM (bug_count) bug_count
  FROM wxj_network_check_t