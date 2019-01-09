package gd.services.ledDisplay.demoProject.dao;

import java.util.Map;

public class DemoDAO {

    public static String getToday(Map reqMap) {
        StringBuffer strBuffer = new StringBuffer();
        if ("MySql".equals(reqMap.get("dbType"))) {
            strBuffer.append("     SELECT date_format(now(),'%Y-%m-%d %H:%i:%s') today	FROM DUAL	");
        } else {
            strBuffer.append("     SELECT to_char(sysdate,'yyyy-MM-dd HH24:Mi:ss') today	FROM DUAL	");
        }
        return strBuffer.toString();
    }
}
