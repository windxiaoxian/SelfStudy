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
        String callbackInfo = "";
        String paramsInfo = request.getParameter("param");//前台传递参数
        String position = JSONObject.fromObject(paramsInfo).getString("position");// 屏幕中的具体模块位置
        String station = JSONObject.fromObject(paramsInfo).getString("position");//轮播图中的正在展示台站
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
        try {
            if (null != screenInfo) {
                // 主题信息获取
                if ("左".equals(screenInfo)) { //主题一@左侧屏幕
                    if ("broadCastGrade".equals(position)) {//播出成绩查询
                        callbackInfo = getBroadCastInfo(jsonparam, screenInfo);//当年播出频次、播出功率
                    } else if ("broadCastTimeAndStopRate".equals(position)) {//播出时长查询和停播率
                        callbackInfo = getBroadCastTimebyMonth(jsonparam, screenInfo);
                    } else if ("taskCount".equals(position)) { // 任务统计:每种任务对应的节目、频率、发射机、任务完成情况数量统计
                        callbackInfo = getTaskCountByBroadType(jsonparam, screenInfo);
                    } else if ("taskDetail".equals(position)) {//每种任务的在播功率、播出语种、年度可听率、年度实验合格率
                        callbackInfo = getTaskDetailCount(jsonparam, screenInfo);
                    }
                } else if ("右".equals(screenInfo)) {//主题一@右侧屏幕
                    if ("programTrans".equals(position)) {//节传相关
                        callbackInfo = getProgramTransInfo(jsonparam, screenInfo);
                    } else if ("netSecurity".equals(position)) {//网络安全
                        callbackInfo = getNetSecurityInfo(jsonparam, screenInfo);
                    } else if ("earthStation".equals(position)) {//地球站资源
                        callbackInfo = getEarthStationInfo(jsonparam, screenInfo);
                    }
                } else {//主题一@中间屏幕
                    if ("subStation".equals(position)) {//
                        callbackInfo = getSubStationInfo(jsonparam, screenInfo);
                    } else if ("realTimeInfo".equals(position)) {
                        callbackInfo = getRealTimeInfo(jsonparam, screenInfo);//获取实时在播频次、功率、发射机
                    } else if ("dispatchOrder".equals(position)) {
                        callbackInfo = getDispatchOrder(jsonparam, screenInfo);
                    } else if ("stationStatus".equals(position)) {//站点状态
                        callbackInfo = getStationStatus(jsonparam, screenInfo);
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
     * 台站的状态信息
     *
     * @param json
     * @param screenPosition
     * @return
     */
    protected String getStationStatus(JSONObject json, String screenPosition) {
        try {
            JSONObject resultJson = new JSONObject();
            Map reqMap = new HashMap();
            List stationStatusList = (List) wirelessDB.qryList(FrontPageThemeDAO.getTodayTotalTasks(reqMap)).get("data");
            resultJson.accumulate("stationStatus", stationStatusList);
            return resultJson.toString();
        } catch (Exception e) {
            logger.error("[FirstThemeServlet.left]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

    /**
     * 查询当年播出频次、播出功率
     *
     * @author Liuyr
     * @date 2019-01-11
     */
    private String getBroadCastInfo(JSONObject json, String screenPosition) {
        try {
            JSONObject resultJson = new JSONObject();
            /*入参*/
            Map reqMap = new HashMap();
            resultJson.accumulate("broadCastFrequency", "1440");  //当年播出频次
            resultJson.accumulate("broadCastPower", "1000");      //当年播出功率
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
//            String[] months = new String[]{"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"};
//            String currMonth = months[Calendar.getInstance().get(Calendar.MONTH)];
            int broadCastInterTime = 0;//当年对内播出时长
            int broadCastExterTime = 0;//当年对外播出时长
            int broadCastTestTime = 0; //当年实验播出时长
            int broadCastInterToday = 0;//当天的对内播出时长
            int broadCastExterToday = 0;//当天的对内播出时长
            int broadCastTestToday = 0;//当天的对内播出时长
//            int todayBroadTime = 0; //当天播出时长总数
            int todayDWTemp = 0;
            int todayDNTemp = 0;
            int todaySYTemp = 0;
            Map reqMap = new HashMap();
            ArrayList list = new ArrayList();
            //播出时长柱状图与停播率条形图查询
            List BroadTimeList = (List) wirelessDB.qryList(FrontPageThemeDAO.getMonthTotalHours(reqMap)).get("data");
            List StopRateList = (List) wirelessDB.qryList(FrontPageThemeDAO.getMonthStopSeconds(reqMap)).get("data");
            List TodayBroadCastList = (List) wirelessDB.qryList(FrontPageThemeDAO.getMonthTotalHours(reqMap)).get("data");
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
                        + ((BigDecimal) ((Map) SYList.get(j)).get("total_hour")).intValue();
                if (j == 11) {//当月数据需要加上当天的实时数据
                    totalTimeTemp += todayDWTemp + todayDNTemp + todaySYTemp;
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
//            int currMonthIndex = -1;
//            for (int i = 0; i < 12; i++) {
//                Map broadCastItem = (Map) BroadTimeList.get(i);
//                if ("一月".equals(broadCastItem.get("month"))) {
//                    currMonthIndex = i;
//                }
//                    for (int j = 0; j < TodayBroadCastList.size(); j++) {
//                        if ("DW".equals(((Map)TodayBroadCastList.get(j)).get("order_type"))) {
            //当天对内播出时长
//                            broadCastInterToday =((BigDecimal) ((Map) TodayBroadCastList.get(j)).get("total_hour")).intValue();
//                        }else if("DW".equals(((Map)TodayBroadCastList.get(j)).get("order_type"))){
            //当天对外播出时长
//                            broadCastExterToday =((BigDecimal) ((Map) TodayBroadCastList.get(j)).get("total_hour")).intValue();
//                        }else{
            //当天实验播出时长
//                            broadCastTestToday =((BigDecimal) ((Map) TodayBroadCastList.get(j)).get("total_hour")).intValue();
//                        }
//                            todayBroadTime = broadCastInterToday+broadCastExterToday+broadCastTestToday;//当天的总播出时长
//                    }
            //各种任务当年累计播出时长
//                if (i>=currMonthIndex) {
//                    if ("DW".equals(broadCastItem.get("order_type"))) {
//                        broadCastExterTime += ((BigDecimal) broadCastItem.get("total_hour")).intValue();
//                            if(month.equals(broadCastItem.get("month"))){//如果是当前月将今天的时长相加
//                                broadCastExterTime +=broadCastExterToday;
//                            }
//                    } else if ("DN".equals(broadCastItem.get("order_type"))) {
//                        broadCastInterTime += ((BigDecimal) broadCastItem.get("total_hour")).intValue();
//                            if(month.equals(broadCastItem.get("month"))){//如果是当前月将今天的时长相加
//                                broadCastInterTime +=broadCastInterToday;
//                            }
//                    } else if ("SY".equals(broadCastItem.get("order_type"))) {
//                        broadCastTestTime += ((BigDecimal) broadCastItem.get("total_hour")).intValue();
//                            if(month.equals(broadCastItem.get("month"))){//如果是当前月将今天的时长相加
//                                broadCastExterTime +=broadCastTestToday;
//                            }
//                    }
//                }
            //  每个月的播出时长总数
//                    HashMap temMap = new HashMap();
//                    temMap.put("month", broadCastItem.get("month"));
//                    if (currMonth.equals(broadCastItem.get("month"))) {
//                        temMap.put("BroadTime", ((BigDecimal) broadCastItem.get("total_hour")).intValue() + ((BigDecimal) ((Map) BroadTimeList.get(i + 12)).get("total_hour")).intValue() + ((BigDecimal) ((Map) BroadTimeList.get(i + 12)).get("total_hour")).intValue() + todayBroadTime);
//                    } else {
//                        temMap.put("BroadTime", ((BigDecimal) broadCastItem.get("total_hour")).intValue() + ((BigDecimal) ((Map) BroadTimeList.get(i + 12)).get("total_hour")).intValue() + ((BigDecimal) ((Map) BroadTimeList.get(i + 12)).get("total_hour")).intValue());
//                    }
//                    temMap.put("stopHour", ((Map) StopRateList.get(i)).get("stop_seconds")/100/todayBroadTime);
//                    list.add(temMap);
//                }
            int broadCastTime = broadCastExterTime + broadCastInterTime + broadCastTestTime
                    + todayDWTemp + todayDNTemp + todaySYTemp; // 取实验、对内、对外之和为总播出时长;
            resultJson.accumulate("broadCastTimeAndStopRate", list);                         //统计图数据源
            resultJson.accumulate("broadCastTime", broadCastTime);                           //当年播出时间
            resultJson.accumulate("broadCastTestTime", broadCastTestTime + todaySYTemp);     //当年播出实验时长
            resultJson.accumulate("broadCastInter", broadCastInterTime + todayDNTemp);       //当年对内累计播出时长
            resultJson.accumulate("broadCastExter", broadCastExterTime + todayDWTemp);       //当年对外累计播出时长
            resultJson.accumulate("location", screenPosition);
            return resultJson.toString();
        } catch (Exception e) {
            logger.error("FirstThemeServlet." + screenPosition + "后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

    /**
     * 各种任务统计情况
     *
     * @author Liuyr
     * @Date 2019-01-11
     */
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
                map.put("taskBroadPower", taskCountItem.get("power_doing"));//每种任务对应的在播功率
                resultList.add(map);
            }
            resultJson.accumulate("taskCount", resultList);
            return resultJson.toString();
        } catch (Exception e) {
            logger.error("FirstThemeServlet." + screenPosition + "后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }


    /**
     * 各种任务详情数据:
     *
     * @author Liuyr
     * @Date 2019-01-11
     */
    public String getTaskDetailCount(JSONObject json, String screenPosition) {
        try {
            JSONObject resultJson = new JSONObject();
            HashMap reqMap = new HashMap();
            HashMap map = new HashMap();
//           ArrayList  list = new ArrayList();

//           Map StationMap  = (HashMap)ledDisplayDB.qryMap(FirstThemeDao.getStations(reqMap)).get("data");//获取我台与敌台发射机数量
//           Map freqMap     = (HashMap)ledDisplayDB.qryMap(FirstThemeDao.getFrequences(reqMap)).get("data");//获取我台与敌台频次数量
//           Map PowerMap    = (HashMap)ledDisplayDB.qryMap(FirstThemeDao.getPowers(reqMap)).get("data");//对内任务年度在播功率
//           Map AccFreqMap  = (HashMap)ledDisplayDB.qryMap(FirstThemeDao.getAccFrequences(reqMap)).get("data");//获取对外年度累计频次
//           Map AudiBilityMap  = (HashMap)ledDisplayDB.qryMap(FirstThemeDao.getAudiBility(reqMap)).get("data");//对内年度可听率
//           Map PassPrecentMap = (HashMap)ledDisplayDB.qryMap(FirstThemeDao.getAudiBility(reqMap)).get("data");//年度实验合格率

            map.put("station", 38);//台站
            map.put("testStation", 7);//实验台站
            map.put("interPower", 560);//对内在播功率
            map.put("exterPower", 660);//对外在播功率
            map.put("enemyFreqency", 123);//敌台频次
            map.put("freqency", 100);
            map.put("passPrecent", 98.63);
            map.put("AudiBilityInter", 90.12);
            map.put("AudiBilityExter", 69.42);
            map.put("Language", 49);//播出语言:总共49种

            resultJson.accumulate("taskDetail", map);
            return resultJson.toString();
        } catch (Exception e) {
            logger.error("FirstThemeServlet." + screenPosition + "后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }


    /**
     * 节传第一个接口
     *
     * @ author Liuyr
     * @ Date 2019-01-14
     */
    protected String getProgramTransInfo(JSONObject json, String screenPosition) {
        try {
            JSONObject resultJson = new JSONObject();
            resultJson.accumulate("centerRadio", 12);//中央广播
            resultJson.accumulate("localRadio", 13);//地方广播
            resultJson.accumulate("center", 14);//中央
            resultJson.accumulate("local", 15);//地方
            resultJson.accumulate("highDefinition", 16);//高清
            resultJson.accumulate("standerDefinition", 56);//标清
            resultJson.accumulate("dataServices", 27);//数据业务
            resultJson.accumulate("earthStation", 381944);//截止上月地球站累计数量
            resultJson.accumulate("programTransTime", 253471);//截止上月节传累计时长
            return resultJson.toString();
        } catch (Exception e) {
            logger.error("FirstThemeServlet." + screenPosition + "后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }


    /**
     * 地球站信息
     *
     * @Author Liuyr
     * @Date 2019-01-14
     */
    protected String getEarthStationInfo(JSONObject json, String screenPosition) {
        try {
            JSONObject resultJson = new JSONObject();
            Map reqMap = new HashMap();
            reqMap.put("station", json.get("station"));//入参@正在轮播的具体台站名称

            resultJson.accumulate("antenna_16", 17);//地球站资源——16米天线
            resultJson.accumulate("antenna_13", 18);//地球站资源——13米天线
            resultJson.accumulate("antenna_move", 19);//地球站资源——转动天线
            resultJson.accumulate("C_band", 20);//C波段
            resultJson.accumulate("KU_band", 21);//Ku波段
            resultJson.accumulate("DHS_band", 22);//DHS波段
            resultJson.accumulate("repeater", 23);//转发器
            resultJson.accumulate("satelliteSys", 24);//上星卫星系统
            resultJson.accumulate("programPlatform", 25);//节目集成平台
            resultJson.accumulate("programTrans_again", 26);//再传节目

            return resultJson.toString();
        } catch (Exception e) {
            logger.error("FirstThemeServlet." + screenPosition + "后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

    /**
     * 网络安全
     *
     * @author Liuyr
     * @Date 2019-01-14
     */
    protected String getNetSecurityInfo(JSONObject json, String screenPosition) {
        try {
            JSONObject resultJson = new JSONObject();
            Map reqMap = new HashMap();//入参
            Map resMap = (HashMap) wirelessDB.qryMap(FrontPageThemeDAO.getNetWorkCheckInfo(reqMap)).get("data");
            List resList = (List) wirelessDB.qryList(FrontPageThemeDAO.getStationNetworkInfo(reqMap)).get("data");
            resultJson.accumulate("stationsList", resList);//台站的连接状态
            resultJson.accumulate("bandInUse", ((Map) resList.get(0)).get("bandwidth_used"));//已使用带宽数
            resultJson.accumulate("bandNoUse", ((Map) resList.get(0)).get("bandwidth_unused"));//未使用带宽数
            resultJson.accumulate("netAccessAbonormal", ((BigDecimal) resMap.get("virus_count")).intValue() + ((BigDecimal) resMap.get("bug_count")).intValue() + ((BigDecimal) resMap.get("mole_count")).intValue());//网络访问行为异常数 ((BigDecimal)resMap.get("virus_count")).intValue()+((BigDecimal)resMap.get("bug_count")).intValue()+((BigDecimal)resMap.get("mole_count")).intValue()
            resultJson.accumulate("netAccessTotal", resMap.get("access_count"));//网络访问行为总数
            resultJson.accumulate("virus", resMap.get("virus_count"));//病毒 //
            resultJson.accumulate("loophole", resMap.get("bug_count"));//漏洞
            resultJson.accumulate("spy", resMap.get("mole_count"));//间谍
            return resultJson.toString();
        } catch (Exception e) {
            logger.error("FirstThemeServlet." + screenPosition + "后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }


    /**
     * 轮播台站详情
     *
     * @Author Liuyr
     * @Date 2019-01-14
     */
    protected String getSubStationInfo(JSONObject json, String screenPosition) {
        try {
            JSONObject resultJson = new JSONObject();
            Map reqMap = new HashMap();
            reqMap.put("stationCode", json.get("stationCode"));
            logger.debug("getSubStationInfo：" + json.get("stationCode"));
            int transmitterInUseCount = 0;//正常播音发射机数量
            int transmitterNoUseCount = 0;//空闲播音发射机数量
            int transmitterRepairCount = 0;//检修播音发射机数量
            int transmitterAbonormalCount = 0;//故障播音发射机数量
            int transmitterUnknownCount = 0;//未知播音发射机数量

            int realTimePower = -1; //当前发射机状态功率
            int realTimeAM = -1;  //当前发射机状态调制度
            String transmitterName = "";//当前展示的发射机
            Map stationBasicMap = (HashMap) wirelessDB.qryMap(FrontPageThemeDAO.getStationBasicInfo(reqMap)).get("data");
            Map resMap = (HashMap) wirelessDB.qryMap(FrontPageThemeDAO.getStationTaskInfo(reqMap)).get("data");
            List machineList = (List) wirelessDB.qryList(FrontPageThemeDAO.getRealTimeTransDetailInfo(reqMap)).get("data");
            List machineFinalList = new ArrayList();
            String room = "";
            if (machineList != null && machineList.size() > 0)
                room = ((HashMap) machineList.get(0)).get("room").toString();//当前遍历机房号
            String workStatus = "";//当前该发射机的状态
            List transList = new ArrayList();
            for (Object machineItem : machineList) {
                Map machineMap = (Map) machineItem;
                //计算所有机房的发射机的各种状态数据汇总
                if ("正常".equals(machineMap.get("work_status"))) {
                    if (realTimePower == -1 || realTimeAM == -1 || "".equals(transmitterName)) {//  发射机当前状态 : 取其中一条正常的数据展示
                        transmitterName = machineMap.get("room").toString().substring(2) + machineMap.get("transmitter");
                        realTimePower = (int) Math.ceil(Integer.parseInt((String) machineMap.get("power")) / 10);
                        realTimeAM = (int) Math.ceil(Integer.parseInt((String) machineMap.get("amrp")) / 10);
                    }
                    workStatus = "Normal";
                    transmitterInUseCount++;
                } else if ("空闲".equals(machineMap.get("work_status").toString())) {
                    workStatus = "Free";
                    transmitterNoUseCount++;
                } else if ("检修".equals(machineMap.get("work_status").toString())) {
                    workStatus = "Repair";
                    transmitterRepairCount++;
                } else if ("未知".equals(machineMap.get("work_status").toString())) {
                    workStatus = "Unknown";
                    transmitterUnknownCount++;
                } else {
                    workStatus = "Broken";
                    transmitterAbonormalCount++;
                }
                if (room.equals(machineMap.get("room").toString())) {//同一个机房
                    transList.add(workStatus);
                } else {
                    room = machineMap.get("room").toString();
                    int listCountTemp = 0;
                    if (transList != null && transList.size() > 0)
                        listCountTemp = transList.size();
                    if (listCountTemp < 5) { //如果一个机房的机号不足5个,补充null
                        for (int i = listCountTemp; i < 5; i++) {
                            transList.add("null");
                        }
                    }
                    machineFinalList.add(transList);//每隔机房返回一个List,list中只显示接收机状态;
                    transList = new ArrayList();
                    transList.add(workStatus);
                }
            }
            resultJson.accumulate("stationType", stationBasicMap.get("unit_belong"));//台站类别
            resultJson.accumulate("sendBandType", stationBasicMap.get("unit_type"));// 类型
            resultJson.accumulate("address", stationBasicMap.get("address"));//地址
            resultJson.accumulate("stationManager", stationBasicMap.get("contacts_name"));//台长
            resultJson.accumulate("phone", stationBasicMap.get("contacts_phone"));//电话

            resultJson.accumulate("planTask", resMap.get("task_todo"));//计划任务
            resultJson.accumulate("stopTime", resMap.get("stop_time"));//停播时长
            resultJson.accumulate("planBroadTime", resMap.get("play_time_todo"));//计划播出时长
            resultJson.accumulate("broadTimeDone", resMap.get("play_time_done"));//已播出时长
            resultJson.accumulate("broadTaskCount", resMap.get("task_done"));//已播出任务数
            resultJson.accumulate("alarmCount", resMap.get("alarm_count"));//综合报警数

            resultJson.accumulate("dispatchOrder", resMap.get("busiorder_count"));//今日调度令个数
            resultJson.accumulate("broadCastPowerOn", resMap.get("power_doing"));//实时播出功率

            resultJson.accumulate("transmitterInUse", transmitterInUseCount);//播音发射机
            resultJson.accumulate("transmitterNoUse", transmitterNoUseCount);//空闲发射机
            resultJson.accumulate("transmitterRepair", transmitterRepairCount);//检修发射机
            resultJson.accumulate("transmitterAbonormal", transmitterAbonormalCount);//故障发射机
            resultJson.accumulate("transmitterUnknown", transmitterUnknownCount);//未知发射机

            resultJson.accumulate("currentPower", realTimePower);//当前发射机功率
            resultJson.accumulate("realTimeAM", realTimeAM);//调幅度
            resultJson.accumulate("transmitterName", transmitterName);

            resultJson.accumulate("machineList", machineFinalList);//机房状态

            return resultJson.toString();
        } catch (Exception e) {
            logger.error("FirstThemeServlet." + screenPosition + "后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }


    /**
     * 轮播调度单
     *
     * @Author Liuyr
     * @Date 2019-01-16
     */
    protected String getDispatchOrder(JSONObject json, String screenPosition) {
        try {
            JSONObject resultJson = new JSONObject();
            Map reqMap = new HashMap();
            List dispatchOrderList = (List) wirelessDB.qryList(FrontPageThemeDAO.getSlideShowBusiOrder(reqMap)).get("data");
            resultJson.accumulate("dispatchOrderList", dispatchOrderList); // 调度令  10条  轮播
            return resultJson.toString();
        } catch (Exception e) {
            logger.error("FirstThemeServlet." + screenPosition + "后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }


    /**
     * 中间屏幕两边的'球球'---实时信息
     * 调度令轮播
     *
     * @Author Liuyr
     * @Date 2019-01-16
     */
    protected String getRealTimeInfo(JSONObject json, String screenPosition) {
        try {
            JSONObject resultJson = new JSONObject();
            Map reqMap = new HashMap();
            Map resMap = (HashMap) wirelessDB.qryMap(FrontPageThemeDAO.getRealTimeRunplanInfo(reqMap)).get("data");//实时在播频率、发射机、功率
            Map resMap2 = (HashMap) wirelessDB.qryMap(FrontPageThemeDAO.getRealtimeBusiOrderInfo(reqMap)).get("data");


            resultJson.accumulate("realTimeFreq", resMap.get("freq_count"));//实时在播频率
            resultJson.accumulate("realTimeTrans", resMap.get("trans_count"));//实时在播发射机
            resultJson.accumulate("realTimePower", resMap.get("power_count"));//实时在播功率

            resultJson.accumulate("dispatchOrderTimeByYear", ((BigDecimal) resMap2.get("year_hour_count")).intValue() + ((BigDecimal) resMap2.get("today_hour_count")).intValue());//年累计调度令时长
            resultJson.accumulate("dispatchOrderTimeByDay", resMap2.get("today_hour_count"));//日累计调度令时长
            resultJson.accumulate("dispatchOrderNumByYear", ((BigDecimal) resMap2.get("year_order_count")).intValue() + ((BigDecimal) resMap2.get("today_order_count")).intValue());//年累计调度令个数
            resultJson.accumulate("dispatchOrderNumByDay", resMap2.get("today_order_count"));//日累计调度令个数

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
