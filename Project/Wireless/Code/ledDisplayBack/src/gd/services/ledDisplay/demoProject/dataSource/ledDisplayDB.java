package gd.services.ledDisplay.demoProject.dataSource;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class ledDisplayDB {
    private static final Logger logger = LogManager.getLogger(ledDisplayDB.class.getName());

    private static DataSource ledDisplayDB;

    //初始化一次db参数
    static {
        Properties dbProperties = new Properties();
        try {
            Properties properties = new Properties();
            properties.load(ledDisplayDB.class.getClassLoader().getResourceAsStream("dataSource.properties"));
            for (Object key : properties.keySet()) {
                String temp = (String) key;
                if (temp.startsWith("jdbc.ledDisplay.")) {
                    String name = temp.substring(16);
                    dbProperties.put(name, properties.getProperty(temp));
                }
            }
        } catch (Exception e) {
            dbProperties.put("url", "jdbc:mysql://127.0.0.1:3306/fire");
            dbProperties.put("username", "root");
            dbProperties.put("password", "root");
            dbProperties.put("driverClassName", "com.mysql.jdbc.Driver");
            dbProperties.put("initialSize", 5);
            dbProperties.put("maxActive", 50);
            dbProperties.put("maxWait", 60000);
            dbProperties.put("timeBetweenEvictionRunsMillis", 120000);
        } finally {
            ledDisplayDB = getDruidDataSource(dbProperties);
        }
    }

    public static Map<String, Object> qryList(String sql, Object... params) {
        Map<String, Object> resp = new ConcurrentHashMap<String, Object>();
        resp.put("code", "0000");
        try {
            QueryRunner run = new QueryRunner(ledDisplayDB);
            resp.put("data", run.query(
                    sql, new MapListHandler(), params));
        } catch (Exception e) {            //记录日志
            logger.error("[ledDisplayDB.qryList]后台报错" + e.getMessage());
            resp.put("code", "9999");
        }
        return resp;
    }

    public static Map<String, Object> qryMap(String sql, Object... params) {
        Map<String, Object> resp = new ConcurrentHashMap<String, Object>();
        resp.put("code", "0000");
        try {
            QueryRunner run = new QueryRunner(ledDisplayDB);
            resp.put("data", run.query(
                    sql, new MapHandler(), params));
        } catch (Exception e) {            //记录日志
            logger.error("[ledDisplayDB.qryMap]后台报错" + e.getMessage());
            resp.put("code", "9999");
        }
        return resp;
    }

    public static DruidDataSource getDruidDataSource(Properties dbProperties) {
        DruidDataSource dds = new DruidDataSource();
        dds.setUsername(dbProperties.get("username").toString());
        dds.setUrl(dbProperties.get("url").toString());
        dds.setPassword(dbProperties.get("password").toString());
        dds.setDriverClassName(dbProperties.get("driverClassName").toString());
        dds.setInitialSize(Integer.parseInt(dbProperties.get("initialSize").toString()));
        dds.setMaxActive(Integer.parseInt(dbProperties.get("maxActive").toString()));
        dds.setMaxWait(Integer.parseInt(dbProperties.get("maxWait").toString()));
        dds.setTimeBetweenConnectErrorMillis(Integer.parseInt(dbProperties.get("timeBetweenEvictionRunsMillis").toString()));
        dds.setTestWhileIdle(false);
        dds.setTestOnReturn(false);
        dds.setTestOnBorrow(false);
        return dds;
    }
}
