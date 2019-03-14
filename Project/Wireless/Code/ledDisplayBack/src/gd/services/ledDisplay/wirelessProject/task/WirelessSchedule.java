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
import java.util.Date;


/**
 * Created by 徐文正 on 2019/01/10.
 */
@Component
public class WirelessSchedule {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(WirelessSchedule.class.getName());

    /**
     * 有效运行图信息沉淀|全量数据
     * 定时任务，每天凌晨2点执行一次
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void validRunplanRefreshAllTask() {
        try {
            //MongoDB
            String dbName = "GD_RT_statistics";//数据库名
            String collName = "BaseSchedule";//表名
            MongoCollection<Document> coll = MongoUtil.instance.getCollection(dbName, collName);

            int count = MongoUtil.instance.getCount(coll);
            logger.info("有效运行图全量信息沉淀【MongoDB】开始：" + count);

            //查询条件|$ne 、$gt 、$gte 、 $lt 、 $lte （分别对应 <> 、 > 、  >=  、 <  、 <= ）
            String jsonInfo = "{\"$and\":[{\"startDate\":{\"$lte\":\""
                    + DateUtil.getNowStr("yyyy-MM-dd HH:mm:ss")
                    + "\"},\"endDate\":{\"$gte\":\""
                    + DateUtil.getNowStr("yyyy-MM-dd HH:mm:ss")
                    + "\"}}]}";
            Bson queryFilter = BasicDBObject.parse(jsonInfo);
            //排序条件|(-1:倒叙 1：正序)
            Bson sortFilter = new BasicDBObject("stationCode", 1)
                    .append("runId", 1)
                    .append("acqDateTime", -1);
            //查询条数
            int limitFilter = 0;
            //跳过的条数
            int skipFilter = 0;

            MongoCursor<Document> cursor = MongoUtil.instance.find(coll, queryFilter, sortFilter, limitFilter, skipFilter);
            List validRunplanList = new ArrayList();
            String stationCodeOld = "-1";
            String runIdOld = "-1";
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Map validRunplanMap = new HashMap();
                String stationCodeTemp = (String) doc.get("stationCode");
                String runIdTemp = doc.get("runId").toString();
                if (stationCodeTemp.equals(stationCodeOld)) {
                    if (runIdTemp.equals(runIdOld)) {
                        continue;
                    } else {
                        validRunplanMap.put("acqDateTime", DateUtil.getDateToStr((Date) doc.get("acqDateTime"), "yyyy-MM-dd HH:mm:ss"));
                        validRunplanMap.put("stationCode", stationCodeTemp);
                        validRunplanMap.put("deptCode", doc.get("deptCode"));
                        validRunplanMap.put("operate", doc.get("operate"));
                        validRunplanMap.put("transCode", doc.get("transCode"));
                        validRunplanMap.put("runId", runIdTemp);
                        validRunplanMap.put("antennaCode", doc.get("antennaCode"));
                        validRunplanMap.put("antProg", doc.get("antProg"));
                        validRunplanMap.put("orderType", doc.get("orderType"));
                        validRunplanMap.put("startDate", doc.get("startDate"));
                        validRunplanMap.put("endDate", doc.get("endDate"));
                        validRunplanMap.put("startTime", doc.get("startTime"));
                        validRunplanMap.put("endTime", doc.get("endTime"));
                        validRunplanMap.put("freq", doc.get("freq"));
                        validRunplanMap.put("programCode", doc.get("programCode"));
                        validRunplanMap.put("programName", doc.get("programName"));
                        validRunplanMap.put("power", doc.get("power"));
                        validRunplanMap.put("servArea", doc.get("servArea"));
                        validRunplanMap.put("days", doc.get("days"));
                        validRunplanMap.put("channel", doc.get("channel"));
                        validRunplanMap.put("runType", doc.get("runType"));
                        validRunplanMap.put("sourceType", doc.get("sourceType"));
                        stationCodeOld = stationCodeTemp;
                        runIdOld = runIdTemp;
                    }
                } else {
                    validRunplanMap.put("acqDateTime", DateUtil.getDateToStr((Date) doc.get("acqDateTime"), "yyyy-MM-dd HH:mm:ss"));
                    validRunplanMap.put("stationCode", stationCodeTemp);
                    validRunplanMap.put("deptCode", doc.get("deptCode"));
                    validRunplanMap.put("operate", doc.get("operate"));
                    validRunplanMap.put("transCode", doc.get("transCode"));
                    validRunplanMap.put("runId", runIdTemp);
                    validRunplanMap.put("antennaCode", doc.get("antennaCode"));
                    validRunplanMap.put("antProg", doc.get("antProg"));
                    validRunplanMap.put("orderType", doc.get("orderType"));
                    validRunplanMap.put("startDate", doc.get("startDate"));
                    validRunplanMap.put("endDate", doc.get("endDate"));
                    validRunplanMap.put("startTime", doc.get("startTime"));
                    validRunplanMap.put("endTime", doc.get("endTime"));
                    validRunplanMap.put("freq", doc.get("freq"));
                    validRunplanMap.put("programCode", doc.get("programCode"));
                    validRunplanMap.put("programName", doc.get("programName"));
                    validRunplanMap.put("power", doc.get("power"));
                    validRunplanMap.put("servArea", doc.get("servArea"));
                    validRunplanMap.put("days", doc.get("days"));
                    validRunplanMap.put("channel", doc.get("channel"));
                    validRunplanMap.put("runType", doc.get("runType"));
                    validRunplanMap.put("sourceType", doc.get("sourceType"));
                    stationCodeOld = stationCodeTemp;
                    runIdOld = runIdTemp;
                }
                validRunplanList.add(validRunplanMap);
            }

            wirelessDB.executeSql(ScheduleDAO.truncateValidRunplan(null));

            for (Object validRunplan : validRunplanList) {
                try {
                    wirelessDB.executeSql(ScheduleDAO.insValidRunplan((Map) validRunplan));
                } catch (Exception e) {
                    logger.error("有效运行图全量信息沉淀【MongoDB】报错：" + e.getMessage());
                    continue;
                }
            }

            logger.info("有效运行图全量信息沉淀【MongoDB】完成：" + validRunplanList.size());
        } catch (Exception e) {
            logger.error("[WirelessSchedule.validRunplanRefreshAllTask]后台报错：" + e.getMessage());
        }
    }

    /**
     * 有效运行图信息沉淀|近5分钟增量数据
     * 定时任务，每隔一分钟执行一次
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void validRunplanRefreshAddTask() {
        try {
            //MongoDB
            String dbName = "GD_RT_statistics";//数据库名
            String collName = "BaseSchedule";//表名
            MongoCollection<Document> coll = MongoUtil.instance.getCollection(dbName, collName);

            int count = MongoUtil.instance.getCount(coll);
            logger.info("有效运行图增量信息沉淀【MongoDB】开始：" + count);

            //查询条件|$ne 、$gt 、$gte 、 $lt 、 $lte （分别对应 <> 、 > 、  >=  、 <  、 <= ）
            Bson queryFilter = new BasicDBObject("acqDateTime", new BasicDBObject("$gte", DateUtil.getDesignedDate(-5 * 60 * 1000)));
            //排序条件|(-1:倒叙 1：正序)
            Bson sortFilter = new BasicDBObject("stationCode", 1)
                    .append("runId", 1)
                    .append("acqDateTime", -1);
            //查询条数
            int limitFilter = 0;
            //跳过的条数
            int skipFilter = 0;

            MongoCursor<Document> cursor = MongoUtil.instance.find(coll, queryFilter, sortFilter, limitFilter, skipFilter);
            List validRunplanList = new ArrayList();
            String stationCodeOld = "-1";
            String runIdOld = "-1";
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Map validRunplanMap = new HashMap();
                String stationCodeTemp = (String) doc.get("stationCode");
                String runIdTemp = doc.get("runId").toString();
                if (stationCodeTemp.equals(stationCodeOld)) {
                    if (runIdTemp.equals(runIdOld)) {
                        continue;
                    } else {
                        validRunplanMap.put("acqDateTime", DateUtil.getDateToStr((Date) doc.get("acqDateTime"), "yyyy-MM-dd HH:mm:ss"));
                        validRunplanMap.put("stationCode", stationCodeTemp);
                        validRunplanMap.put("deptCode", doc.get("deptCode"));
                        validRunplanMap.put("operate", doc.get("operate"));
                        validRunplanMap.put("transCode", doc.get("transCode"));
                        validRunplanMap.put("runId", runIdTemp);
                        validRunplanMap.put("antennaCode", doc.get("antennaCode"));
                        validRunplanMap.put("antProg", doc.get("antProg"));
                        validRunplanMap.put("orderType", doc.get("orderType"));
                        validRunplanMap.put("startDate", doc.get("startDate"));
                        validRunplanMap.put("endDate", doc.get("endDate"));
                        validRunplanMap.put("startTime", doc.get("startTime"));
                        validRunplanMap.put("endTime", doc.get("endTime"));
                        validRunplanMap.put("freq", doc.get("freq"));
                        validRunplanMap.put("programCode", doc.get("programCode"));
                        validRunplanMap.put("programName", doc.get("programName"));
                        validRunplanMap.put("power", doc.get("power"));
                        validRunplanMap.put("servArea", doc.get("servArea"));
                        validRunplanMap.put("days", doc.get("days"));
                        validRunplanMap.put("channel", doc.get("channel"));
                        validRunplanMap.put("runType", doc.get("runType"));
                        validRunplanMap.put("sourceType", doc.get("sourceType"));
                        stationCodeOld = stationCodeTemp;
                        runIdOld = runIdTemp;
                    }
                } else {
                    validRunplanMap.put("acqDateTime", DateUtil.getDateToStr((Date) doc.get("acqDateTime"), "yyyy-MM-dd HH:mm:ss"));
                    validRunplanMap.put("stationCode", stationCodeTemp);
                    validRunplanMap.put("deptCode", doc.get("deptCode"));
                    validRunplanMap.put("operate", doc.get("operate"));
                    validRunplanMap.put("transCode", doc.get("transCode"));
                    validRunplanMap.put("runId", runIdTemp);
                    validRunplanMap.put("antennaCode", doc.get("antennaCode"));
                    validRunplanMap.put("antProg", doc.get("antProg"));
                    validRunplanMap.put("orderType", doc.get("orderType"));
                    validRunplanMap.put("startDate", doc.get("startDate"));
                    validRunplanMap.put("endDate", doc.get("endDate"));
                    validRunplanMap.put("startTime", doc.get("startTime"));
                    validRunplanMap.put("endTime", doc.get("endTime"));
                    validRunplanMap.put("freq", doc.get("freq"));
                    validRunplanMap.put("programCode", doc.get("programCode"));
                    validRunplanMap.put("programName", doc.get("programName"));
                    validRunplanMap.put("power", doc.get("power"));
                    validRunplanMap.put("servArea", doc.get("servArea"));
                    validRunplanMap.put("days", doc.get("days"));
                    validRunplanMap.put("channel", doc.get("channel"));
                    validRunplanMap.put("runType", doc.get("runType"));
                    validRunplanMap.put("sourceType", doc.get("sourceType"));
                    stationCodeOld = stationCodeTemp;
                    runIdOld = runIdTemp;
                }
                validRunplanList.add(validRunplanMap);
            }

            for (Object validRunplan : validRunplanList) {
                try {
                    wirelessDB.executeSql(ScheduleDAO.delValidRunplan((Map) validRunplan));
                    wirelessDB.executeSql(ScheduleDAO.insValidRunplan((Map) validRunplan));
                } catch (Exception e) {
                    logger.error("有效运行图增量信息沉淀【MongoDB】报错：" + e.getMessage());
                    continue;
                }
            }

            wirelessDB.executeSql(ScheduleDAO.cleanValidRunplan(null));
            wirelessDB.executeSql(ScheduleDAO.cleanValidRunplan2(null));

            logger.info("有效运行图增量信息沉淀【MongoDB】完成：" + validRunplanList.size());
        } catch (Exception e) {
            logger.error("[WirelessSchedule.validRunplanRefreshAddTask]后台报错：" + e.getMessage());
        }
    }

    /**
     * 发射机信息沉淀|全量数据
     * 定时任务，每天凌晨1点执行一次
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void transStatusRefreshAllTask() {
        try {
            //MongoDB
            String dbName = "GD_RT_statistics";//数据库名
            String collName = "transmitter_quality_second";//表名
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
                    .append("detectTime", -1);
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
                        transStatusMap.put("statusTime", doc.get("detectTime"));
                        transStatusMap.put("workStatus", doc.get("workStatus"));
                        transStatusMap.put("deptCode", doc.get("deptCode"));
                        transStatusMap.put("freqOffset", doc.get("freqOffset"));
                        transStatusMap.put("power", doc.get("power"));
                        transStatusMap.put("programCode", doc.get("programCode"));
                        transStatusMap.put("freq", doc.get("freq"));
                        transStatusMap.put("antCode", doc.get("antCode"));
                        transStatusMap.put("amrp", doc.get("amrp"));
                        stationCodeOld = stationCodeTemp;
                        transCodeOld = transCodeTemp;
                    }
                } else {
                    transStatusMap.put("stationCode", stationCodeTemp);
                    transStatusMap.put("transCode", transCodeTemp);
                    transStatusMap.put("statusTime", doc.get("detectTime"));
                    transStatusMap.put("workStatus", doc.get("workStatus"));
                    transStatusMap.put("deptCode", doc.get("deptCode"));
                    transStatusMap.put("freqOffset", doc.get("freqOffset"));
                    transStatusMap.put("power", doc.get("power"));
                    transStatusMap.put("programCode", doc.get("programCode"));
                    transStatusMap.put("freq", doc.get("freq"));
                    transStatusMap.put("antCode", doc.get("antCode"));
                    transStatusMap.put("amrp", doc.get("amrp"));
                    stationCodeOld = stationCodeTemp;
                    transCodeOld = transCodeTemp;
                }
                transStatusList.add(transStatusMap);
            }

            wirelessDB.executeSql(ScheduleDAO.truncateTransStatus(null));
            for (Object transStatus : transStatusList) {
                try {
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
            String collName = "transmitter_quality_second";//表名
            MongoCollection<Document> coll = MongoUtil.instance.getCollection(dbName, collName);

            int count = MongoUtil.instance.getCount(coll);
            logger.info("发射机实时状态增量信息沉淀【MongoDB】开始：" + count);

            //查询条件|$ne 、$gt 、$gte 、 $lt 、 $lte （分别对应 <> 、 > 、  >=  、 <  、 <= ）
            Bson queryFilter = new BasicDBObject("stationCode", new BasicDBObject("$ne", null))
                    .append("stationCode", new BasicDBObject("$ne", ""))
                    .append("transCode", new BasicDBObject("$ne", null))
                    .append("transCode", new BasicDBObject("$ne", ""))
                    .append("detectTime", new BasicDBObject("$gte", DateUtil.getDesignedDateStr(-5 * 60 * 1000, "yyyy-MM-dd HH:mm:ss")));
            //排序条件|(-1:倒叙 1：正序)
            Bson sortFilter = new BasicDBObject("stationCode", 1)
                    .append("transCode", 1)
                    .append("detectTime", -1);
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
                        transStatusMap.put("statusTime", doc.get("detectTime"));
                        transStatusMap.put("workStatus", doc.get("workStatus"));
                        transStatusMap.put("deptCode", doc.get("deptCode"));
                        transStatusMap.put("freqOffset", doc.get("freqOffset"));
                        transStatusMap.put("power", doc.get("power"));
                        transStatusMap.put("programCode", doc.get("programCode"));
                        transStatusMap.put("freq", doc.get("freq"));
                        transStatusMap.put("antCode", doc.get("antCode"));
                        transStatusMap.put("amrp", doc.get("amrp"));
                        stationCodeOld = stationCodeTemp;
                        transCodeOld = transCodeTemp;
                    }
                } else {
                    transStatusMap.put("stationCode", stationCodeTemp);
                    transStatusMap.put("transCode", transCodeTemp);
                    transStatusMap.put("statusTime", doc.get("detectTime"));
                    transStatusMap.put("workStatus", doc.get("workStatus"));
                    transStatusMap.put("deptCode", doc.get("deptCode"));
                    transStatusMap.put("freqOffset", doc.get("freqOffset"));
                    transStatusMap.put("power", doc.get("power"));
                    transStatusMap.put("programCode", doc.get("programCode"));
                    transStatusMap.put("freq", doc.get("freq"));
                    transStatusMap.put("antCode", doc.get("antCode"));
                    transStatusMap.put("amrp", doc.get("amrp"));
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

    /**
     * 调度令当天信息沉淀|全量数据
     * 定时任务，每天凌晨1点30执行一次
     */
    @Scheduled(cron = "0 30 1 * * ?")
    public void busiOrderRefreshAllTask() {
        try {
            //MongoDB
            String dbName = "GD_RT_statistics";//数据库名
            String collName = "BusiOrder";//表名
            MongoCollection<Document> coll = MongoUtil.instance.getCollection(dbName, collName);

            logger.info("调度令当天全量信息沉淀【MongoDB】开始：");

            //查询条件
            Bson queryFilter = new BasicDBObject("acqDateTime", new BasicDBObject("$gte", DateUtil.strToDate(DateUtil.getNowStr("yyyy-MM-dd") + " 00:00:00", "yyyy-MM-dd HH:mm:ss")))
                    .append("acqDateTime", new BasicDBObject("$lt", DateUtil.getDesignedDate(1000)))
                    .append("sendDate", new BasicDBObject("$gte", DateUtil.getNowStr("yyyy-MM-dd")));
            //排序(-1:倒叙 1：正序)
            Bson sortFilter = new BasicDBObject("acqDateTime", 1);
            //查询条数
            int limitFilter = 0;
            //跳过的条数
            int skipFilter = 0;

            MongoCursor<Document> cursor = MongoUtil.instance.find(coll, queryFilter, sortFilter, limitFilter, skipFilter);
            List busiOrderList = new ArrayList();
            List busiOrderDetailList = new ArrayList();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Map busiOrderMap = new HashMap();
                busiOrderMap.put("stationCode", doc.get("stationCode"));// 台站编码
                busiOrderMap.put("operate", doc.get("operate")); //操作标志
                busiOrderMap.put("sourceType", doc.get("sourceType"));//调度令性质
                busiOrderMap.put("orderCode", doc.get("orderCode"));// 文号
                busiOrderMap.put("orderStatus", doc.get("orderStatus"));// 状态
                busiOrderMap.put("sendDept", doc.get("sendDept"));// 发送单位
                busiOrderMap.put("orderYear", doc.get("orderYear"));// 年份
                busiOrderMap.put("statusDate", doc.get("statusDate"));// 状态日期
                busiOrderMap.put("sender", doc.get("sender"));//发送人
                busiOrderMap.put("sendDate", doc.get("sendDate"));// 发送时间
                busiOrderMap.put("sendAssessor", doc.get("sendAssessor"));// 发送审核人
                busiOrderMap.put("receiver", doc.get("receiver"));// 台站接收人
                busiOrderMap.put("receiverDate", doc.get("receiverDate"));//接收时间
                busiOrderMap.put("corrector", doc.get("corrector"));// 接受校对人
                busiOrderMap.put("correctDate", doc.get("correctDate"));// 校对时间
                if (doc.get("orderOps") != null) {
                    List busiOrderDetailTempList = (ArrayList) doc.get("orderOps");
                    for (Object orderOps : busiOrderDetailTempList) {
                        Map busiOrderDetailMap = new HashMap();
                        busiOrderDetailMap.put("stationCode", doc.get("stationCode"));// 台站编码
                        busiOrderDetailMap.put("transCode", ((Map) orderOps).get("transCode"));//发射机编码
                        busiOrderDetailMap.put("antCode", ((Map) orderOps).get("antCode"));//天线编码
                        busiOrderDetailMap.put("status", ((Map) orderOps).get("status"));//状态
                        busiOrderDetailMap.put("statusDate", ((Map) orderOps).get("statusDate"));//状态日期
                        busiOrderDetailMap.put("rmks", ((Map) orderOps).get("rmks"));//备注
                        busiOrderDetailMap.put("channel", ((Map) orderOps).get("channel"));//节目通道
                        busiOrderDetailMap.put("orderCode", doc.get("orderCode"));//文号
                        busiOrderDetailMap.put("days", ((Map) orderOps).get("days"));//周期
                        busiOrderDetailMap.put("servArea", ((Map) orderOps).get("servArea"));//服务区
                        busiOrderDetailMap.put("power", ((Map) orderOps).get("power"));//功率
                        busiOrderDetailMap.put("azimuthM", ((Map) orderOps).get("azimuthM"));//方向
                        busiOrderDetailMap.put("programName", ((Map) orderOps).get("programName"));//节目名称
                        busiOrderDetailMap.put("programCode", ((Map) orderOps).get("programCode"));//节目编码
                        busiOrderDetailMap.put("freq", ((Map) orderOps).get("freq"));//频率
                        busiOrderDetailMap.put("startDate", ((Map) orderOps).get("startDate"));//开始执行日期
                        busiOrderDetailMap.put("endDate", ((Map) orderOps).get("endDate"));//结束执行日期
                        busiOrderDetailMap.put("startTime", ((Map) orderOps).get("startTime"));//开始时间
                        busiOrderDetailMap.put("endTime", ((Map) orderOps).get("endTime"));//结束时间
                        busiOrderDetailMap.put("orderType", ((Map) orderOps).get("orderType"));//任务类型
                        busiOrderDetailMap.put("operate", ((Map) orderOps).get("operate"));//操作标志
                        busiOrderDetailList.add(busiOrderDetailMap);
                    }
                }
                busiOrderList.add(busiOrderMap);
            }

            wirelessDB.executeSql(ScheduleDAO.truncateBusiOrderTotal(null));
            for (Object total : busiOrderList) {
                try {
                    wirelessDB.executeSql(ScheduleDAO.insBusiOrderTotal((Map) total));
                } catch (Exception e) {
                    logger.error("调度令当天全量信息|busiOrderList沉淀【MongoDB】报错：" + e.getMessage());
                    continue;
                }
            }

            wirelessDB.executeSql(ScheduleDAO.truncateBusiOrderDetail(null));
            for (Object detail : busiOrderDetailList) {
                try {
                    wirelessDB.executeSql(ScheduleDAO.insBusiOrderDetail((Map) detail));
                } catch (Exception e) {
                    logger.error("调度令当天全量信息|busiOrderDetailList沉淀【MongoDB】报错：" + e.getMessage());
                    continue;
                }
            }

            logger.info("调度令当天全量信息沉淀【MongoDB】完成：" + busiOrderList.size());
        } catch (Exception e) {
            logger.error("[WirelessSchedule.busiOrderRefreshAllTask]后台报错：" + e.getMessage());
        }
    }

    /**
     * 调度令当天信息沉淀|近5分钟增量数据
     * 定时任务，每隔一分钟执行一次
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void busiOrderRefreshAddTask() {
        try {
            //MongoDB
            String dbName = "GD_RT_statistics";//数据库名
            String collName = "BusiOrder";//表名
            MongoCollection<Document> coll = MongoUtil.instance.getCollection(dbName, collName);

            logger.info("调度令当天增量信息沉淀【MongoDB】开始：");

            //查询条件|$ne 、$gt 、$gte 、 $lt 、 $lte （分别对应 <> 、 > 、  >=  、 <  、 <= ）
            Bson queryFilter = new BasicDBObject("acqDateTime", new BasicDBObject("$gte", DateUtil.getDesignedDate(-5 * 60 * 1000)))
                    .append("acqDateTime", new BasicDBObject("$lt", DateUtil.getDesignedDate(1000)))
                    .append("sendDate", new BasicDBObject("$gte", DateUtil.getNowStr("yyyy-MM-dd")));
            //排序条件|(-1:倒叙 1：正序)
            Bson sortFilter = new BasicDBObject("acqDateTime", 1);
            //查询条数
            int limitFilter = 0;
            //跳过的条数
            int skipFilter = 0;

            MongoCursor<Document> cursor = MongoUtil.instance.find(coll, queryFilter, sortFilter, limitFilter, skipFilter);
            List busiOrderList = new ArrayList();
            List busiOrderDetailList = new ArrayList();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Map busiOrderMap = new HashMap();
                busiOrderMap.put("stationCode", doc.get("stationCode"));
                busiOrderMap.put("operate", doc.get("operate"));
                busiOrderMap.put("sourceType", doc.get("sourceType"));
                busiOrderMap.put("orderCode", doc.get("orderCode"));
                busiOrderMap.put("orderStatus", doc.get("orderStatus"));
                busiOrderMap.put("sendDept", doc.get("sendDept"));
                busiOrderMap.put("orderYear", doc.get("orderYear"));
                busiOrderMap.put("statusDate", doc.get("statusDate"));
                busiOrderMap.put("sender", doc.get("sender"));
                busiOrderMap.put("sendDate", doc.get("sendDate"));
                busiOrderMap.put("sendAssessor", doc.get("sendAssessor"));
                busiOrderMap.put("receiver", doc.get("receiver"));
                busiOrderMap.put("receiverDate", doc.get("receiverDate"));
                busiOrderMap.put("corrector", doc.get("corrector"));
                busiOrderMap.put("correctDate", doc.get("correctDate"));
                if (doc.get("orderOps") != null) {
                    List busiOrderDetailTempList = (ArrayList) doc.get("orderOps");
                    for (Object orderOps : busiOrderDetailTempList) {
                        Map busiOrderDetailMap = new HashMap();
                        busiOrderDetailMap.put("stationCode", doc.get("stationCode"));
                        busiOrderDetailMap.put("transCode", ((Map) orderOps).get("transCode"));
                        busiOrderDetailMap.put("antCode", ((Map) orderOps).get("antCode"));
                        busiOrderDetailMap.put("status", ((Map) orderOps).get("status"));
                        busiOrderDetailMap.put("statusDate", ((Map) orderOps).get("statusDate"));
                        busiOrderDetailMap.put("rmks", ((Map) orderOps).get("rmks"));
                        busiOrderDetailMap.put("channel", ((Map) orderOps).get("channel"));
                        busiOrderDetailMap.put("orderCode", doc.get("orderCode"));
                        busiOrderDetailMap.put("days", ((Map) orderOps).get("days"));
                        busiOrderDetailMap.put("servArea", ((Map) orderOps).get("servArea"));
                        busiOrderDetailMap.put("power", ((Map) orderOps).get("power"));
                        busiOrderDetailMap.put("azimuthM", ((Map) orderOps).get("azimuthM"));
                        busiOrderDetailMap.put("programName", ((Map) orderOps).get("programName"));
                        busiOrderDetailMap.put("programCode", ((Map) orderOps).get("programCode"));
                        busiOrderDetailMap.put("freq", ((Map) orderOps).get("freq"));
                        busiOrderDetailMap.put("startDate", ((Map) orderOps).get("startDate"));
                        busiOrderDetailMap.put("endDate", ((Map) orderOps).get("endDate"));
                        busiOrderDetailMap.put("startTime", ((Map) orderOps).get("startTime"));
                        busiOrderDetailMap.put("endTime", ((Map) orderOps).get("endTime"));
                        busiOrderDetailMap.put("orderType", ((Map) orderOps).get("orderType"));
                        busiOrderDetailMap.put("operate", ((Map) orderOps).get("operate"));
                        busiOrderDetailList.add(busiOrderDetailMap);
                    }
                }
                busiOrderList.add(busiOrderMap);
            }

            for (Object total : busiOrderList) {
                try {
                    wirelessDB.executeSql(ScheduleDAO.delBusiOrderTotal((Map) total));
                    wirelessDB.executeSql(ScheduleDAO.insBusiOrderTotal((Map) total));
                } catch (Exception e) {
                    logger.error("调度令当天增量信息|busiOrderList沉淀【MongoDB】报错：" + e.getMessage());
                    continue;
                }
            }

            for (Object detail : busiOrderDetailList) {
                try {
                    wirelessDB.executeSql(ScheduleDAO.delBusiOrderDetail((Map) detail));
                    wirelessDB.executeSql(ScheduleDAO.insBusiOrderDetail((Map) detail));
                } catch (Exception e) {
                    logger.error("调度令当天增量信息|busiOrderDetailList沉淀【MongoDB】报错：" + e.getMessage());
                    continue;
                }
            }

            logger.info("调度令当天增量信息沉淀【MongoDB】完成：" + busiOrderList.size());
        } catch (Exception e) {
            logger.error("[WirelessSchedule.busiOrderRefreshAddTask]后台报错：" + e.getMessage());
        }
    }
}
