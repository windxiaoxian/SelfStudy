package gd.services.ledDisplay.demoProject.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by 徐文正 on 2018/12/20.
 */
@Component
public class DemoSchedule {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(DemoSchedule.class.getName());

    /**
     * 定时任务，每分钟执行一次
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void demoScheduleTasks() {
        try {
//            Map reqMap = new HashMap();
//            reqMap.put("dbType", "MySql");
//            Map resMap = (HashMap) ledDisplayDB.qryMap(DemoDAO.getToday(reqMap)).get("data");
//            logger.info("定时任务测试【ledDisplayDB】：" + resMap.get("today"));
//            resMap = (HashMap) demoDB.qryMap(DemoDAO.getToday(reqMap)).get("data");
//            logger.info("定时任务测试【demoDB】：" + resMap.get("today"));

            //MongoDB
//            String dbName = "GD_RT_statistics";//数据库名
//            String collName = "BusiOrder";//表名
//            MongoCollection<Document> coll = MongoUtil.instance.getCollection(dbName, collName);
//
//            int count = MongoUtil.instance.getCount(coll);
//            logger.info("表中记录数：" + count);

            //按ID倒叙排列取acqDateTime字段不为空的最近3条记录
//            JSONArray callbackInfo = new JSONArray();
//            Bson queryFilter = new BasicDBObject("acqDateTime", new BasicDBObject("$ne", null));//查询条件
//            Bson sortFilter = new BasicDBObject("_id", -1);//排序(-1:倒叙 1：正序)
//            int limitFilter = 3;//查询条数
//            int skipFilter = 0;//跳过的条数
//            MongoCursor<Document> cursor = MongoUtil.instance.find(coll, queryFilter, sortFilter, limitFilter, skipFilter);
//            while (cursor.hasNext()) {
//                Document doc = cursor.next();
//                callbackInfo.add(doc.toJson());
//            }
//            logger.info("定时任务测试【MongoDB】：" + CommonUtil.JsonFormat(callbackInfo.toString()));
        } catch (Exception e) {
            logger.error("[DemoSchedule.demoScheduleTasks]后台报错" + e.getMessage());
        }
    }
}
