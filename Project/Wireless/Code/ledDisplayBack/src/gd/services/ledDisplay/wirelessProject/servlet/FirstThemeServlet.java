package gd.services.ledDisplay.wirelessProject.servlet;

import gd.services.ledDisplay.wirelessProject.dao.FrontPageThemeDAO;
import gd.services.ledDisplay.wirelessProject.dataSource.wirelessDB;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.*;

@WebServlet(name = "FirstThemeServlet")
public class FirstThemeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static Logger logger = LogManager.getLogger(FirstThemeServlet.class.getName());

    public FirstThemeServlet() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String screenInfo = "";//屏幕位置信息
        String paramsInfo = request.getParameter("param");//前台传递参数
        String position = JSONObject.fromObject(paramsInfo).getString("position");// 屏幕中的具体模块位置
        JSONObject jsonparam = null; //参数转为JSON格式
        if ("left".equals(request.getParameter("key"))) {
            screenInfo = "左";
        } else if ("right".equals(request.getParameter("key"))) {
            screenInfo = "右";
        } else {
            screenInfo = "中";
        }
        if (null != paramsInfo && !"".equals(paramsInfo)) {
            jsonparam = (JSONObject) JSONSerializer.toJSON(paramsInfo);
        }
        String callbackInfo = "";
        try {
            if (null != screenInfo) {
                // 主题信息获取
                if ("左".equals(screenInfo)) { //左侧屏幕
                    if ("broadCastGrade".equals(position)) {//播出成绩查询
                        callbackInfo = getBroadCastInfo(jsonparam, screenInfo);
                    } else if ("broadCastTimeAndStopRate".equals(position)) {//播出时长查询和停播率
                        callbackInfo = getBroadCastTimebyMonth(jsonparam, screenInfo);
                    } else if ("taskCount".equals(position)) { // 任务统计:每种任务对应的节目、频率、发射机、任务完成情况数量统计
                        callbackInfo = getTaskCountByBroadType(jsonparam, screenInfo);
                    } else if ("taskDetail".equals(position)) {
                        callbackInfo = getTaskDetailCount(jsonparam, screenInfo);
                    }
                }
            } else {
                callbackInfo = "9999|未匹配到后台接口，请核查参数！";
            }
        } catch (Exception e) {
            logger.error("[DemoServlet]后台报错：key=" + request.getParameter("key") + e.getMessage());
            callbackInfo = "9999|后台接口报错，请核查日志！";
        }
        response.setContentType("text/plain;charset=UTF-8");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setDateHeader("Expires", 0);

        PrintWriter out = response.getWriter();
//        out.println("jsonpCallback(" + callbackInfo + ")");
        out.println(callbackInfo);
        out.flush();
        out.close();
    }

    /**
     * 查询当年播出时长、播出频次、播出功率
     *
     * @author Liuyr
     * @date 2019-01-11
     */
    private String getBroadCastInfo(JSONObject json, String screenPosition) {
        try {
            JSONObject resultJson = new JSONObject();
            /*入参*/
            Map reqMap = new HashMap();
//            String [] months = new String[]{"一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月"};
//            String  currMonth = months[Calendar.getInstance().get(Calendar.MONTH)];
//            int broadCastInterTime = 0;//当年对内播出时长
//            int broadCastExterTime = 0;//当年对外播出时长
//            int broadCastTestTime  = 0; //当年实验播出时长
//            List  resList  = (List)ledDisplayDB.qryList(FirstThemeDao.getBroadCastDetails(reqMap)).get("data");
//            for( int i=0;i<resList.size();i++){
//                if(!"一月".equals(resList.get(i).month)){
//                    continue;
//                }
//                if("DW".equals(resList.get(i).order_type)) {
//                    broadCastExterTime += resList.get(i).total_hour;
//                }else if("DN".equals(resList.get(i).order_type)){
//                    broadCastInterTime += resList.get(i).total_hour;
//                }else  if("SY".equals(resList.get(i).order_type)){
//                    broadCastTestTime += resList.get(i).total_hour;
//                }
//            }
//            int broadCastTime = broadCastExterTime+broadCastInterTime+broadCastTestTime; // 取实验、对内、对外之和为总播出时长;
//            resultJson.accumulate("broadCastTime", broadCastTime);       //当年播出时间  resMap.get("")
            resultJson.accumulate("broadCastFrequency", "1440");  //当年播出频次
            resultJson.accumulate("broadCastPower", "1000");      //当年播出功率
//            resultJson.accumulate("broadCastTestTime",broadCastTestTime);    //当年播出实验时长
//            resultJson.accumulate("broadCastInter",broadCastInterTime);       //当年对内累计播出时长
//            resultJson.accumulate("broadCastExter",broadCastExterTime);       //当年对外累计播出时长
//            resultJson.accumulate("location", screenPosition);
            return resultJson.toString();
        } catch (Exception e) {
            logger.error("[FirstThemeServlet.left]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

    /**
     * 左侧屏幕----播出时长柱状图与停播率折线图
     *
     * @author Liuyr
     * @Date 2019-01-11
     */
    private String getBroadCastTimebyMonth(JSONObject json, String screenPosition) {
        try {
            JSONObject resultJson = new JSONObject();
            String[] months = new String[]{"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"};
            String currMonth = months[Calendar.getInstance().get(Calendar.MONTH)];
            int broadCastInterTime = 0;//当年对内播出时长
            int broadCastExterTime = 0;//当年对外播出时长
            int broadCastTestTime = 0; //当年实验播出时长
            int todayBroadTime = 0;
            int todayDWTemp = 0;
            int todayDNTemp = 0;
            int todaySYTemp = 0;
            Map reqMap = new HashMap();
            ArrayList list = new ArrayList();
            //播出时长柱状图与停播率条形图查询
            List BroadTimeList = (List) wirelessDB.qryList(FrontPageThemeDAO.getMonthToalHours(reqMap)).get("data");
            List StopRateList = (List) wirelessDB.qryList(FrontPageThemeDAO.getMonthStopSeconds(reqMap)).get("data");
            List TodayBroadCastList = (List) wirelessDB.qryList(FrontPageThemeDAO.getTodayToalHours(reqMap)).get("data");
            int numOfJanuary = 0;
            int ifMatch = -1;
            List DWList = new ArrayList();
            List DNList = new ArrayList();
            List SYList = new ArrayList();
            //拆分月份沉淀数据
            for (Object BroadTime : BroadTimeList) {
                Map BroadTimeMap = (Map) BroadTime;
                if ("DW".equals(BroadTimeMap.get("order_type"))) {
                    DWList.add(BroadTimeMap);
                    if (!"一月".equals(BroadTimeMap.get("month"))) {
                        if (ifMatch == -1)
                            numOfJanuary++;
                    } else {//匹配到一月的位序
                        ifMatch = 0;
                    }
                } else if ("DN".equals(BroadTimeMap.get("order_type"))) {
                    DNList.add(BroadTimeMap);
                } else if ("SY".equals(BroadTimeMap.get("order_type"))) {
                    SYList.add(BroadTimeMap);
                }
            }
            //拆分当天实时数据
            for (int k = 0; k < TodayBroadCastList.size(); k++) {
                if ("DW".equals(((Map) TodayBroadCastList.get(k)).get("order_type"))) {
                    todayDWTemp = ((BigDecimal) ((Map) TodayBroadCastList.get(k)).get("total_hour")).intValue();
                } else if ("DN".equals(((Map) TodayBroadCastList.get(k)).get("order_type"))) {
                    todayDNTemp = ((BigDecimal) ((Map) TodayBroadCastList.get(k)).get("total_hour")).intValue();
                } else if ("SY".equals(((Map) TodayBroadCastList.get(k)).get("order_type"))) {
                    todaySYTemp = ((BigDecimal) ((Map) TodayBroadCastList.get(k)).get("total_hour")).intValue();
                }

            }
            //当年沉淀数据统计
            for (int i = numOfJanuary; i < 12; i++) {
                broadCastExterTime += ((BigDecimal) ((Map) DWList.get(i)).get("total_hour")).intValue();
                broadCastInterTime += ((BigDecimal) ((Map) DNList.get(i)).get("total_hour")).intValue();
                broadCastTestTime += ((BigDecimal) ((Map) SYList.get(i)).get("total_hour")).intValue();
            }

            for (int j = 0; j < 12; j++) {
                HashMap temMap = new HashMap();
                temMap.put("month", ((Map) DWList.get(j)).get("month"));
                int totalTimeTemp = ((BigDecimal) ((Map) DWList.get(j)).get("total_hour")).intValue()
                        + ((BigDecimal) ((Map) DNList.get(j)).get("total_hour")).intValue()
                        + ((BigDecimal) ((Map) SYList.get(j)).get("total_hour")).intValue()
                        + todayBroadTime;
                if (j == 11) {//当月数据需要加上当天的实时数据
                    totalTimeTemp = todayDWTemp + todayDNTemp + todaySYTemp;
                }
                temMap.put("BroadTime", totalTimeTemp);
                temMap.put("stopHour", Math.floor(((BigDecimal) ((Map) StopRateList.get(j)).get("stop_seconds")).intValue() / (totalTimeTemp / 100)));
                logger.info("本月的位置:" + numOfJanuary);
                logger.info("DWList:" + temMap.get("month"));
                logger.info("StopRateList:" + ((Map) StopRateList.get(j)).get("month"));
                logger.info("DNList:" + ((Map) DNList.get(j)).get("month"));
                logger.info("SYList:" + ((Map) SYList.get(j)).get("month"));
                list.add(temMap);
            }

//            //获取当前月份
//            int month = Calendar.getInstance().get(Calendar.MONTH);
//            int currMonthIndex = 0;
//            for (int i = 0; i < BroadTimeList.size(); i++) {
//                Map broadCastItem = (Map) BroadTimeList.get(i);
//                if ("一月".equals(broadCastItem.get("month"))) {
//                    currMonthIndex = i;
//                }
//                if ((i >= currMonthIndex && i < ((currMonthIndex < 12) ? 12 : (currMonthIndex < 24) ? 24 : 36))) {
//                    if ("DW".equals(broadCastItem.get("order_type"))) {
//                        broadCastExterTime += ((BigDecimal) broadCastItem.get("total_hour")).intValue();
//                    } else if ("DN".equals(broadCastItem.get("order_type"))) {
//                        broadCastInterTime += ((BigDecimal) broadCastItem.get("total_hour")).intValue();
//                    } else if ("SY".equals(broadCastItem.get("order_type"))) {
//                        broadCastTestTime += ((BigDecimal) broadCastItem.get("total_hour")).intValue();
//                    }
//                }
//
//                if (i < 12) {
//                    HashMap temMap = new HashMap();
//                    temMap.put("month", broadCastItem.get("month"));
//                    if (currMonth.equals(broadCastItem.get("month"))) {
//                        for (int j = 0; j < TodayBroadCastList.size(); j++) {
//                            todayBroadTime += ((BigDecimal) ((Map) TodayBroadCastList.get(j)).get("total_hour")).intValue();
//                        }
//                        temMap.put("BroadTime", ((BigDecimal) broadCastItem.get("total_hour")).intValue() + ((BigDecimal) ((Map) BroadTimeList.get(i + 12)).get("total_hour")).intValue() + ((BigDecimal) ((Map) BroadTimeList.get(i + 12)).get("total_hour")).intValue() + todayBroadTime);
//                    } else {
//                        temMap.put("BroadTime", ((BigDecimal) broadCastItem.get("total_hour")).intValue() + ((BigDecimal) ((Map) BroadTimeList.get(i + 12)).get("total_hour")).intValue() + ((BigDecimal) ((Map) BroadTimeList.get(i + 12)).get("total_hour")).intValue());
//                    }
//                    temMap.put("stopHour", ((Map) StopRateList.get(i)).get("stop_seconds"));
//                    list.add(temMap);
//                }
//            }
            int broadCastTime = broadCastExterTime + broadCastInterTime + broadCastTestTime
                    + todayDWTemp + todayDNTemp + todaySYTemp; // 取实验、对内、对外之和为总播出时长;
            resultJson.accumulate("broadCastTimeAndStopRate", list);
            resultJson.accumulate("broadCastTime", broadCastTime);       //当年播出时间  resMap.get("")
            resultJson.accumulate("broadCastTestTime", broadCastTestTime + todaySYTemp);    //当年播出实验时长
            resultJson.accumulate("broadCastInter", broadCastInterTime + todayDNTemp);       //当年对内累计播出时长
            resultJson.accumulate("broadCastExter", broadCastExterTime + todayDWTemp);       //当年对外累计播出时长
            resultJson.accumulate("location", screenPosition);
            return resultJson.toString();
        } catch (Exception e) {
            logger.error("FirstThemeServlet." + screenPosition + "后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

    public String getTaskCountByBroadType(JSONObject json, String screenPosition) {
        try {
            HashMap reqMap = new HashMap();
            JSONObject resultJson = new JSONObject();
            ArrayList resultList = new ArrayList();
            List taskCountList = (List) wirelessDB.qryList(FrontPageThemeDAO.getTodayTotalTasks(reqMap)).get("data"); //每种任务对应的在播频率数
            for (Object taskCount : taskCountList) {
                Map map = new HashMap();
                Map taskCountItem = (Map) taskCount;
                map.put("freqCount", taskCountItem.get("freq_doing"));//每种任务在播频率统计
                map.put("programCount", taskCountItem.get("program_doing"));//每种任务节目统计
                map.put("stationCount", taskCountItem.get("trans_doing"));//每种任务发射机统计
                map.put("taskDone", taskCountItem.get("task_done"));//每种任务已完成统计
                map.put("taskUndo", taskCountItem.get("task_todo"));//每种任务未完成统计
                map.put("taskDoing", taskCountItem.get("task_doing"));//每种任务正在执行统计
                map.put("taskCount", ((BigDecimal) taskCountItem.get("task_done")).intValue() + ((BigDecimal) taskCountItem.get("task_todo")).intValue() + ((BigDecimal) taskCountItem.get("task_doing")).intValue());//任务总数
                resultList.add(map);
            }
//           for(int i=0;i<3;i++){
//               HashMap  map = new HashMap();
//               map.put("freqCount",i+20);//每种任务在播频率统计
//               map.put("programCount",i+30);//每种任务节目统计
//               map.put("stationCount",i+40);//每种任务发射机统计
//               map.put("taskDone",12);//每种任务已完成统计
//               map.put("taskUndo",34);//每种任务未完成统计
//               map.put("taskDoing",54);//每种任务正在执行统计
//               map.put("taskCount",100);//任务总数
//               resultList.add(map);
//           }
            resultJson.accumulate("taskCount", resultList);
            return resultJson.toString();
        } catch (Exception e) {
            logger.error("FirstThemeServlet." + screenPosition + "后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }


    public String getTaskDetailCount(JSONObject json, String screenPosition) {
        try {
            JSONObject resultJson = new JSONObject();
            HashMap reqMap = new HashMap();
            HashMap map = new HashMap();
//           ArrayList  list = new ArrayList();

//           Map StationMap  = (HashMap)ledDisplayDB.qryMap(FirstThemeDao.getStations(reqMap)).get("data");//获取我台与敌台发射机数量
//           Map freqMap     = (HashMap)ledDisplayDB.qryMap(FirstThemeDao.getFrequences(reqMap)).get("data");//获取我台与敌台频次数量
//           Map PowerMap    = (HashMap)ledDisplayDB.qryMap(FirstThemeDao.getPowers(reqMap)).get("data");//对内任务年度在播功率
//           Map LanguageMap = (HashMap)ledDisplayDB.qryMap(FirstThemeDao.getLanguages(reqMap)).get("data");//获取播出语言数量
//           Map AccFreqMap  = (HashMap)ledDisplayDB.qryMap(FirstThemeDao.getAccFrequences(reqMap)).get("data");//获取对外年度累计频次
//           Map AudiBilityMap  = (HashMap)ledDisplayDB.qryMap(FirstThemeDao.getAudiBility(reqMap)).get("data");//对内年度可听率
//           Map PassPrecentMap = (HashMap)ledDisplayDB.qryMap(FirstThemeDao.getAudiBility(reqMap)).get("data");//年度实验合格率

            map.put("myStation", 123);
            map.put("enemyStation", 23);
            map.put("myFreqency", 560);
            map.put("enemyFreqency", 100);
            map.put("passPrecent", 90);
            map.put("broadCastPower", 666);
            map.put("AudiBility", 98);
            map.put("Language", 56);
            map.put("accFreqency", 678);

            resultJson.accumulate("taskDetail", map);
            return resultJson.toString();
        } catch (Exception e) {
            logger.error("FirstThemeServlet." + screenPosition + "后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
