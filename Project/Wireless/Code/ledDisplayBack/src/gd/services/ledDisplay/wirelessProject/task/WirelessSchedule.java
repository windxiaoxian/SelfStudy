package gd.services.ledDisplay.wirelessProject.task;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import gd.services.ledDisplay.wirelessProject.dao.ScheduleDAO;
import gd.services.ledDisplay.wirelessProject.dataSource.wirelessDB;
import gd.services.ledDisplay.wirelessProject.util.DateUtil;
import gd.services.ledDisplay.wirelessProject.util.MongoUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by 徐文正 on 2019/01/10.
 */
@Component
public class WirelessSchedule {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(WirelessSchedule.class.getName());

    /**
     * 发射机信息沉淀|全量数据
     * 定时任务，每天凌晨1点执行一次
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void transStatusRefreshAllTask() {
        try {
            //MongoDB
            String dbName = "GD_RT_statistics";//数据库名
            String collName = "transmitter_status";//表名
            MongoCollection<Document> coll = MongoUtil.instance.getCollection(dbName, collName);

            int count = MongoUtil.instance.getCount(coll);
            logger.info("发射机实时状态全量信息沉淀【MongoDB】开始：" + count);

            //查询条件
            Bson queryFilter = new BasicDBObject("stationCode", new BasicDBObject("$ne", null))
                    .append("stationCode", new BasicDBObject("$ne", ""))
                    .append("transCode", new BasicDBObject("$ne", null))
                    .append("transCode", new BasicDBObject("$ne", ""));
            //排序(-1:倒叙 1：正序)
            Bson sortFilter = new BasicDBObject("stationCode", 1)
                    .append("transCode", 1)
                    .append("statusTime", -1);
            //查询条数
            int limitFilter = 0;
            //跳过的条数
            int skipFilter = 0;

            MongoCursor<Document> cursor = MongoUtil.instance.find(coll, queryFilter, sortFilter, limitFilter, skipFilter);
            List transStatusList = new ArrayList();
            String stationCodeOld = "-1";
            String transCodeOld = "-1";
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Map transStatusMap = new HashMap();
                String stationCodeTemp = (String) doc.get("stationCode");
                String transCodeTemp = (String) doc.get("transCode");
                if (stationCodeTemp.equals(stationCodeOld)) {
                    if (transCodeTemp.equals(transCodeOld)) {
                        continue;
                    } else {
                        transStatusMap.put("stationCode", stationCodeTemp);
                        transStatusMap.put("transCode", transCodeTemp);
                        transStatusMap.put("statusTime", (String) doc.get("statusTime"));
                        transStatusMap.put("workStatus", (String) doc.get("workStatus"));
                        stationCodeOld = stationCodeTemp;
                        transCodeOld = transCodeTemp;
                    }
                } else {
                    transStatusMap.put("stationCode", stationCodeTemp);
                    transStatusMap.put("transCode", transCodeTemp);
                    transStatusMap.put("statusTime", (String) doc.get("statusTime"));
                    transStatusMap.put("workStatus", (String) doc.get("workStatus"));
                    stationCodeOld = stationCodeTemp;
                    transCodeOld = transCodeTemp;
                }
                transStatusList.add(transStatusMap);
            }

            for (Object transStatus : transStatusList) {
                try {
                    wirelessDB.executeSql(ScheduleDAO.delTransStatus((Map) transStatus));
                    wirelessDB.executeSql(ScheduleDAO.insTransStatus((Map) transStatus));
                } catch (Exception e) {
                    logger.error("发射机实时状态全量信息沉淀【MongoDB】报错：" + e.getMessage());
                    continue;
                }
            }

            logger.info("发射机实时状态全量信息沉淀【MongoDB】完成：" + transStatusList.size());
        } catch (Exception e) {
            logger.error("[WirelessSchedule.transStatusRefreshAllTask]后台报错：" + e.getMessage());
        }
    }

    /**
     * 发射机信息沉淀|近5分钟增量数据
     * 定时任务，每隔一分钟执行一次
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void transStatusRefreshAddTask() {
        try {
            //MongoDB
            String dbName = "GD_RT_statistics";//数据库名
            String collName = "transmitter_status";//表名
            MongoCollection<Document> coll = MongoUtil.instance.getCollection(dbName, collName);

            int count = MongoUtil.instance.getCount(coll);
            logger.info("发射机实时状态增量信息沉淀【MongoDB】开始：" + count);

            //查询条件|$ne 、$gt 、$gte 、 $lt 、 $lte （分别对应 <> 、 > 、  >=  、 <  、 <= ）
            Bson queryFilter = new BasicDBObject("stationCode", new BasicDBObject("$ne", null))
                    .append("stationCode", new BasicDBObject("$ne", ""))
                    .append("transCode", new BasicDBObject("$ne", null))
                    .append("transCode", new BasicDBObject("$ne", ""))
                    .append("statusTime", new BasicDBObject("$gte", DateUtil.getDesignedDateStr(-5 * 60 * 1000)));
            //排序条件|(-1:倒叙 1：正序)
            Bson sortFilter = new BasicDBObject("stationCode", 1)
                    .append("transCode", 1)
                    .append("statusTime", -1);
            //查询条数
            int limitFilter = 0;
            //跳过的条数
            int skipFilter = 0;

            MongoCursor<Document> cursor = MongoUtil.instance.find(coll, queryFilter, sortFilter, limitFilter, skipFilter);
            List transStatusList = new ArrayList();
            String stationCodeOld = "-1";
            String transCodeOld = "-1";
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Map transStatusMap = new HashMap();
                String stationCodeTemp = (String) doc.get("stationCode");
                String transCodeTemp = (String) doc.get("transCode");
                if (stationCodeTemp.equals(stationCodeOld)) {
                    if (transCodeTemp.equals(transCodeOld)) {
                        continue;
                    } else {
                        transStatusMap.put("stationCode", stationCodeTemp);
                        transStatusMap.put("transCode", transCodeTemp);
                        transStatusMap.put("statusTime", (String) doc.get("statusTime"));
                        transStatusMap.put("workStatus", (String) doc.get("workStatus"));
                        stationCodeOld = stationCodeTemp;
                        transCodeOld = transCodeTemp;
                    }
                } else {
                    transStatusMap.put("stationCode", stationCodeTemp);
                    transStatusMap.put("transCode", transCodeTemp);
                    transStatusMap.put("statusTime", (String) doc.get("statusTime"));
                    transStatusMap.put("workStatus", (String) doc.get("workStatus"));
                    stationCodeOld = stationCodeTemp;
                    transCodeOld = transCodeTemp;
                }
                transStatusList.add(transStatusMap);
            }

            for (Object transStatus : transStatusList) {
                try {
                    wirelessDB.executeSql(ScheduleDAO.delTransStatus((Map) transStatus));
                    wirelessDB.executeSql(ScheduleDAO.insTransStatus((Map) transStatus));
                } catch (Exception e) {
                    logger.error("发射机实时状态增量信息沉淀【MongoDB】报错：" + e.getMessage());
                    continue;
                }
            }

            logger.info("发射机实时状态增量信息沉淀【MongoDB】完成：" + transStatusList.size());
        } catch (Exception e) {
            logger.error("[WirelessSchedule.transStatusRefreshAddTask]后台报错：" + e.getMessage());
        }
    }
}
