package gd.services.ledDisplay.wirelessProject.servlet;

//import gd.services.ledDisplay.wirelessProject.dao.SecondPageThemeDAO;
import gd.services.ledDisplay.wirelessProject.dao.SecondPageThemeDao;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "SecondThemeServlet" )
public class SecondThemeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static Logger logger = LogManager.getLogger(SecondThemeServlet.class.getName());

    public SecondThemeServlet(){
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String screenPosition = request.getParameter("key");//获取主题二中的当前的屏幕位置：左、中、右
        String paramsInfo = request.getParameter("param");//前台传递参数
        JSONObject paramJson = null;//Json格式参数
        String interfaceName = JSONObject.fromObject(paramsInfo).getString("interfaceName");//前台调用的接口名称
        String callbackInfo = "";//返回结果

        if(null != paramsInfo && !"".equals(paramsInfo)){
            paramJson = (JSONObject) JSONSerializer.toJSON(paramsInfo);
        }
        try{
            //判断前台调用的是那个页面的接口
            if(null != screenPosition){
                if("left".equals(screenPosition)){//主题二@左侧屏幕
                    if("resourceCompare".equals(interfaceName)){//资源对比
                        callbackInfo = getResourceCompare( paramJson, screenPosition);
                    }else if("burstFreqAnalysis".equals(interfaceName)){//突发频率实时分析
                        callbackInfo = getBurstFreqAnalysis(paramJson,screenPosition);
                    }else if("burstFreqOrder".equals(interfaceName)){//突发频率排名
                        callbackInfo = getBurstFreqOrder(paramJson,screenPosition);
                    }else if("annualSituation".equals(interfaceName)){//年度情况
                        callbackInfo = getAnnualSituation(paramJson,screenPosition);
                    }
                }else if("middle".equals(screenPosition)){//主题二@中间屏幕
                    if("stationOrderTrendResource".equals(interfaceName)){//台站调度令走势信息
                        callbackInfo = getBroadCastResource(paramJson,screenPosition);
                    }
                    if("broadCastResource".equals(interfaceName)){//播出资源
                        callbackInfo = getBroadCastResource(paramJson,screenPosition);
                    }
                }else if("right".equals(screenPosition)){//主题二@右侧屏幕
                    if("stationResource".equals(interfaceName)){//全局台站资源总览
                        callbackInfo = getStationResource(paramJson,screenPosition);
                    }else if("broadResourceRealTime".equals(interfaceName)){//播出资源实时情况
                        callbackInfo = getBroadResourceRealTime(paramJson,screenPosition);
                    }else if("technologyCondition".equals(interfaceName)){//技术大修数据
                        callbackInfo = getTechnologyCondition(paramJson,screenPosition);
                    }else if("completeCondition".equals(interfaceName)){//总计数据
                        callbackInfo = getCompleteCondition(paramJson,screenPosition);
                    }
                }else if("important".equals(screenPosition)){//主题二@重保期
                    if("programPlaybackTime".equals(interfaceName)){//重保期实时累计播放时长
                        callbackInfo = getProgramPlaybackTime(paramJson,screenPosition);
                    }else if("programPlayStatus".equals(interfaceName)){//重保期节目播出状态
                        callbackInfo = getProgramPlayStatus(paramJson,screenPosition);
                    }
                }
				 if("".equals(callbackInfo))
                    callbackInfo = "9999|未匹配到后台接口，请核查参数！";
            }
        }catch (Exception e) {
            logger.error("[FirstThemeServlet]后台报错：key=" + request.getParameter("key") + e.getMessage());
            callbackInfo = "9999|后台接口报错，请核查日志！";
        }
        response.setContentType("text/plain;charset=UTF-8");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setDateHeader("Expires", 0);

        PrintWriter out = response.getWriter();
        out.println(callbackInfo);
        out.flush();
        out.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    //获取实时资源比对
    protected String getResourceCompare(JSONObject json,String screenPosition){
        try{
            JSONObject resultJSON = new JSONObject();

            HashMap reqMap = new HashMap();
            // 获取获取实时资源比对数据
            Map resMap = (HashMap) wirelessDB.qryMap(SecondPageThemeDao.getResourceCompareInfo(reqMap)).get("data");
            //获取实时资源比对
            // 无线局
            resultJSON.accumulate("transmitterWXJ",resMap.get("station_count"));//当前实验发射机
            resultJSON.accumulate("broadcastTimeWXJ",resMap.get("play_time_done"));//当日累计播音时间
            resultJSON.accumulate("accFreqencyWXJ",resMap.get("freq_time"));//当日累计频次
            resultJSON.accumulate("accFreqWXJ",resMap.get("freq_count"));//当日累计频率个数

            // 对象台
            resultJSON.accumulate("transmitterEnemy",resMap.get("target_station_count"));//当前实验发射机
            resultJSON.accumulate("broadcastTimeEnemy",resMap.get("target_play_time_done"));//当日累计播音时间
            resultJSON.accumulate("accFreqencyEnemy",resMap.get("target_freq_time"));//当日累计频次
            resultJSON.accumulate("accFreqEnemy",resMap.get("target_freq_count"));//当日累计频率个数

            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServlet.left.getResourceCompare]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

    //突发频率实时分析
    protected String getBurstFreqAnalysis(JSONObject json,String screenPosition){
        try{
            Map reqMap = new HashMap();
            JSONObject resultJSON = new JSONObject();
            //突发频率实时分析
            List brustFreqDBList = (List) wirelessDB.qryList(SecondPageThemeDao.getBurstFreqAnalysis(null)).get("data");
            List brustFreqList = new ArrayList();
            for (int i=0;i<brustFreqDBList.size();i++) {
                String time = ((Map) brustFreqDBList.get(i)).get("times").toString();
                brustFreqList.add(time);
            }

            resultJSON.accumulate("brustFreqList",brustFreqList);
            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServlet.left.getburstFreqAnalysis]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }


    //突发频率排名
    protected String getBurstFreqOrder(JSONObject json,String screenPosition){
        try{
            Map reqMap = new HashMap();
            JSONObject resultJSON = new JSONObject();
            //突发频率实时分析
            List brustFreqOrderList =  new ArrayList();

            //突发频率总次数排名
            List freqencyRankingList = (List) wirelessDB.qryList(SecondPageThemeDao.getFreqencyRanking(reqMap)).get("data");
            Map map = new HashMap();
            List freqencyDBList =  new ArrayList();
            List freqencyList =  new ArrayList();
            for (int i=0;i<5;i++){
                map = new HashMap();
                Integer freq = Integer.valueOf(((Map) freqencyRankingList.get(i)).get("freq").toString());
                map.put("freq", freq); // 频率
                map.put("times",((Map) freqencyRankingList.get(i)).get("times")); // 次数
                //突发频率实时分析
                freqencyDBList = (List) wirelessDB.qryList(SecondPageThemeDao.getBurstFreqAnalysis(freq)).get("data");
                freqencyList =  new ArrayList();
                for (int j=0;j<freqencyDBList.size();j++) {
                    String time = ((Map) freqencyDBList.get(j)).get("times").toString();
                    freqencyList.add(time);
                }
                map.put("freqencyList",freqencyList); // 24小时该突发频率实时分析
                brustFreqOrderList.add(map);
            }

            resultJSON.accumulate("brustFreqOrderList",brustFreqOrderList);
            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServlet.left.getBurstFreqOrder]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

    //年度情况
    protected String getAnnualSituation(JSONObject json,String screenPosition){
        try{
            Map reqMap = new HashMap();
            JSONObject  resultJSON = new JSONObject();
            List orderTimeList = new ArrayList();
            List generationBroadList = new ArrayList();

            List orderTimeDBList = (List) wirelessDB.qryList(SecondPageThemeDao.getOrderTimeList(reqMap)).get("data");
            Map map  = new HashMap();
            int size = orderTimeDBList.size();
            for (int i=0;i<size; i++){
                map  = new HashMap();
                map.put("station",((Map) orderTimeDBList.get(i)).get("station_code_dic")); // 台名

                Integer orderCount = Integer.valueOf(((Map) orderTimeDBList.get(i)).get("order_total").toString()); //调度次数
                map.put("orderCount",orderCount);   //调度次数

                Integer orderCountDone = Integer.valueOf(((Map) orderTimeDBList.get(i)).get("done_count").toString()); //调度完成次数
                BigDecimal completioRate = new BigDecimal(orderCountDone/orderCount);
                //保留2位小数，且四舍五入,调度令执行率
                double orderImplRateByAnnual = completioRate.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                map.put("orderRate", orderImplRateByAnnual+"%");       // 执行率
                orderTimeList.add(map);
            }

            List generationDBList = (List) wirelessDB.qryList(SecondPageThemeDao.getGenerationList(reqMap)).get("data");
            Map map2  = new HashMap();
            int size2 = generationDBList.size();
            for (int i=0;i<size2; i++){
                map2  = new HashMap();
                map2.put("station",((Map) generationDBList.get(i)).get("station_code_dic")); // 台名
                // TODO
                map2.put("applicationCount",80);//申请次数
                map2.put("applicationGeneral",3232);//申请代播时长
                map2.put("applicationOther",((Map) generationDBList.get(i)).get("play_time"));//代播他台时长

                Integer agentOrderCount = Integer.valueOf(((Map) generationDBList.get(i)).get("agent_order_count").toString()); //代播调度次数
                Integer agentOrderCountDone = Integer.valueOf(((Map) generationDBList.get(i)).get("agent_done_count").toString()); //调度完成次数
                BigDecimal completioRate = new BigDecimal(agentOrderCountDone/agentOrderCount);
                //保留2位小数，且四舍五入,调度令执行率
                double generalRate = completioRate.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                map2.put("generalRate", generalRate+"%");       // 执行率
                generationBroadList.add(map2);
            }

            // 获取年度至今临时代播
            Map resMap = (HashMap) wirelessDB.qryMap(SecondPageThemeDao.getAnnualSituationInfo(reqMap)).get("data");
            Integer orderCount = Integer.valueOf(resMap.get("order_count").toString()); //全局下发调度令总数
            Integer orderCountDone = Integer.valueOf(resMap.get("order_count_done").toString()); //全局下发调度令总数

            float completioRate =(float) orderCountDone/orderCount;
            //保留2位小数，且四舍五入, 全局下发调度令执行率
            double orderImplRateByAnnual = new BigDecimal(completioRate).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue()*100;
            resultJSON.accumulate("orderByAnnual", orderCount);//全局下发调度令总数
            resultJSON.accumulate("orderImplRateByAnnual",orderImplRateByAnnual);//全局下发调度令执行率
            resultJSON.accumulate("generalBroadTimesByAnnual",resMap.get("agent_freq_count"));//全局年度至今代播总频次
            resultJSON.accumulate("generalBroadTimeByAnnual",resMap.get("agent_order_duration"));//全局年度至今代播总时长

            resultJSON.accumulate("orderTimeList",orderTimeList);  // 全年至今调度令信息列表
            resultJSON.accumulate("generationBroadList",generationBroadList);
            return resultJSON.toString();
        }catch(Exception  e){
            logger.error("[SecondThemeServlet.left.getAnnualSituation]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

    // 台站调度令走势信息
    protected  String getStationOrderTrendInfo(JSONObject json,String screenPosition){
        try{
            Map reqMap = new HashMap();
            JSONObject  resultJSON = new JSONObject();
            List  orderTrendList = new ArrayList();
            Map map = new HashMap();
            map.put("receStationNm","491台"); // 接收台名称
            map.put("receStationX","39N53");   //纬度
            map.put("receStationY","116E36");  //经度
            map.put("orderStatus","DB");       // 调度令类型 TF：代表突发频率，作用于检测台 ；DB：代表台际代播，作用于其他台站
            map.put("targetStationNm","542台"); // 目标台名称
            map.put("targetStationX","39N45");   //目标台纬度
            map.put("targetStationY","116E14");  //目标台经度
            orderTrendList.add(map);

            map.put("receStationNm","582台"); // 接收台名称
            map.put("receStationX","39N56");   //纬度
            map.put("receStationY","116E21");  //经度
            map.put("orderStatus","TF");       // 调度令类型 TF：代表突发频率，作用于检测台 ；DB：代表台际代播，作用于其他台站
            map.put("targetStationNm","发射二台"); // 目标台名称
            map.put("targetStationX","39N55");   //目标台纬度
            map.put("targetStationY","116E23");  //目标台经度
            orderTrendList.add(map);

            resultJSON.accumulate("orderTrendList",orderTrendList);//台站调度令走势信息
            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServlet.middle.getBroadCastResource]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }


    //播出资源
    protected  String getBroadCastResource(JSONObject json,String screenPosition){
        try{
            Map reqMap = new HashMap();
            JSONObject  resultJSON = new JSONObject();
            List currentOrderList  = new ArrayList();

            Map map = new HashMap();
            List currentOrderDBList = (List) wirelessDB.qryList(SecondPageThemeDao.getBroadCastResourceList(reqMap)).get("data");
            int size = currentOrderDBList.size();
            for (int i=0;i<size; i++){
                map = new HashMap();
                map.put("order",((Map) currentOrderDBList.get(i)).get("order_code_dic"));
                map.put("freq",((Map) currentOrderDBList.get(i)).get("freq"));
                map.put("station",((Map) currentOrderDBList.get(i)).get("station_name"));
                map.put("transmitter",((Map) currentOrderDBList.get(i)).get("txname"));
                map.put("annate",((Map) currentOrderDBList.get(i)).get("antusedcode"));
                map.put("status",((Map) currentOrderDBList.get(i)).get("status"));
                currentOrderList.add(map);
            }

            Map resUpMap = (HashMap) wirelessDB.qryMap(SecondPageThemeDao.getBroadCastResourceUpInfo(reqMap)).get("data");
            resultJSON.accumulate("stationInUse", resUpMap.get("station_doing"));//在播台站
            resultJSON.accumulate("transmitterInUse",resUpMap.get("trans_doing"));//在播发射机
            resultJSON.accumulate("broadcastPower",resUpMap.get("power_doing"));//在播功率
            resultJSON.accumulate("broadcastTime",resUpMap.get("playtime_doing"));//当日在播时长
            resultJSON.accumulate("programOnPlay",resUpMap.get("program_doing"));//在播节目
            resultJSON.accumulate("suppressEnemy",resUpMap.get("task_doing"));//实验压制敌台次数

            Map resMap = (HashMap) wirelessDB.qryMap(SecondPageThemeDao.getBroadCastResourceDownInfo(reqMap)).get("data");
            resultJSON.accumulate("orderCountByDay",resMap.get("order_count"));//当日调度令总数
            resultJSON.accumulate("orderBroadTime",resMap.get("play_time"));//调度播出时长
            resultJSON.accumulate("orderAddFreq",resMap.get("newordercount"));//调度新开或增开频率

            resultJSON.accumulate("currentOrderList",currentOrderList);//当前小时调度令情况

            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServlet.middle.getBroadCastResource]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

    //全局台站资源总览
    protected String getStationResource(JSONObject json,String screenPosition){
        try{
            Map reqMap = new HashMap();
            JSONObject  resultJSON = new JSONObject();
            Map resMap = (HashMap) wirelessDB.qryMap(SecondPageThemeDao.getStationResourceOfTransmitter(reqMap)).get("data");

            resultJSON.accumulate("transmitterInUse",resMap.get("inuse"));  //全局发射机状态:在用
            resultJSON.accumulate("transmitterUsed",resMap.get("free"));   //全局发射机状态:可用
            //resultJSON.accumulate("transmitterFree",236);   //全局发射机状态:空闲
            resultJSON.accumulate("transmitterRepair",resMap.get("overhaul")); //全局发射机状态:抢修
            resultJSON.accumulate("transmitterClose",resMap.get("bad"));  //全局发射机状态:停用

            resultJSON.accumulate("antennaInUse",119);  //全局天线状态:在用
            resultJSON.accumulate("antennaUsed",209);   //全局天线状态:已用
           // resultJSON.accumulate("antennaFree",224);   //全局天线状态:空闲
            resultJSON.accumulate("antennaRepair",108); //全局天线状态:抢修
            resultJSON.accumulate("antennaClose",190);  //全局天线状态:停用

            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServlet.right.getStationResource]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }


    //对内对外实验播出资源实时情况
    protected  String getBroadResourceRealTime(JSONObject json,String screenPosition){
        try{
            List huazhongList = new ArrayList();
            List huabeiList = new ArrayList();
            List huananList = new ArrayList();
            List xibeiList = new ArrayList();
            List dongbeiList = new ArrayList();
            List xinanList = new ArrayList();
            List huadongList = new ArrayList();
            List dongnanList = new ArrayList();
            List neimengList = new ArrayList();
            List xizangList = new ArrayList();
            List xinjiangList = new ArrayList();

            Map map =  new HashMap();
            map.put("orangeCanuseInter",102); // 橙色可用
            map.put("blueCanuseInter",213);    // 蓝色可用
            map.put("greenCanuseInter",156);    // 绿色可用

            map.put("orangeInuseInter",231);  //橙色在用
            map.put("blueInuseInter",123);     // 蓝色在用
            map.put("greenInuseInter",85);   // 绿色在用
            huazhongList.add(map);
            huabeiList.add(map);
            huananList.add(map);
            xibeiList.add(map);
            dongbeiList.add(map);
            xinanList.add(map);
            dongnanList.add(map);
            huadongList.add(map);
            neimengList.add(map);
            xizangList.add(map);
            xinjiangList.add(map);

            JSONObject  resultJSON = new JSONObject();

            resultJSON.accumulate("huazhongList",huazhongList);// 华中地区
            resultJSON.accumulate("huabeiList",huabeiList);    // 华北地区
            resultJSON.accumulate("huananList",huananList);    // 华南地区
            resultJSON.accumulate("xibeiList",xibeiList);          // 西北地区
            resultJSON.accumulate("dongbeiList",dongbeiList);   // 东北地区
            resultJSON.accumulate("xinanList",xinanList);       // 西南地区
            resultJSON.accumulate("dongnanList",dongnanList);   // 东南地区
            resultJSON.accumulate("huadongList",huadongList);   // 华东地区
            resultJSON.accumulate("neimengList",102);// 内蒙
            resultJSON.accumulate("xizangList",102);// 西藏
            resultJSON.accumulate("xinjiangList",102);// 新疆

            Map worldMap =  new HashMap();
            worldMap.put("orangeCanuseOuter",202); // 橙色可用
            worldMap.put("blueCanuseOuter",313);    // 蓝色可用
            worldMap.put("greenCanuseOuter",256);    // 绿色可用
            worldMap.put("orangeInuseOuter",131);  //橙色在用
            worldMap.put("blueInuseOuter",223);     // 蓝色在用
            worldMap.put("greenInuseOuter",185);   // 绿色在用

            List AsiaList = new ArrayList();
            List EuropeList = new ArrayList();
            List AmericaList = new ArrayList();
            List AfricaList = new ArrayList();
            List OceaniaList = new ArrayList();
            AsiaList.add(worldMap);
            EuropeList.add(worldMap);
            AmericaList.add(worldMap);
            AfricaList.add(worldMap);
            OceaniaList.add(worldMap);

            resultJSON.accumulate("AsiaList",AsiaList);     // 亚洲地区
            resultJSON.accumulate("EuropeList",EuropeList);    // 欧洲地区
            resultJSON.accumulate("AmericaList",AmericaList);    // 美洲地区
            resultJSON.accumulate("AfricaList",AfricaList);          // 美洲地区
            resultJSON.accumulate("OceaniaList",OceaniaList);   // 大洋洲地区

            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServlet.right.getStationResource]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

    //  技术大修页面数据
    protected String getTechnologyCondition(JSONObject json,String screenPosition){
        try{
            Map reqMap = new HashMap();
            JSONObject resultJSON = new JSONObject();
            List doingList = new ArrayList();
            // 当前正在执行项目
            Map dingMap = new HashMap();
            List doingDBList = (List) wirelessDB.qryList(SecondPageThemeDao.getDoingProgramList(reqMap)).get("data");
            int size = doingDBList.size();
            for (int i=0;i<size; i++){
                dingMap = new HashMap();
                dingMap.put("stationName",((Map) doingDBList.get(i)).get("projdeptname"));
                dingMap.put("startDate",((Map) doingDBList.get(i)).get("dt_projplanstartdate"));
                dingMap.put("endDate",((Map) doingDBList.get(i)).get("dt_projplanenddate"));
                dingMap.put("projectName",((Map) doingDBList.get(i)).get("vc_projname"));
                doingList.add(dingMap);
            }
            // 即将在执行项目
            Map todoMap =  new HashMap();
            List todoList = new ArrayList();
            List todoDBList = (List) wirelessDB.qryList(SecondPageThemeDao.getTodoProgramList(reqMap)).get("data");
            int todoSize = todoDBList.size();
            for(int i=0 ; i<todoSize;i++){
                todoMap =  new HashMap();
                todoMap.put("stationName",((Map) todoDBList.get(i)).get("projdeptname"));
                todoMap.put("startDate",((Map) todoDBList.get(i)).get("dt_projplanstartdate"));
                todoMap.put("endDate",((Map) todoDBList.get(i)).get("dt_projplanenddate"));
                todoMap.put("projectName",((Map) todoDBList.get(i)).get("vc_projname"));
                todoList.add(todoMap);
            }
            resultJSON.accumulate("doingList",doingList); // 当前正在执行项目
            resultJSON.accumulate("toDoList",todoList);    // 即将在执行项目
            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServlet.right.getStationResource]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }


    protected String getCompleteCondition(JSONObject json,String screenPosition){
        try{
            Map reqMap = new HashMap();
            JSONObject  resultJSON = new JSONObject();
            List resList = new ArrayList();

            Map map =  new HashMap();
            List resDBList = (List) wirelessDB.qryList(SecondPageThemeDao.getCompleteConditionList(reqMap)).get("data");
            int size = resDBList.size();
            for(int i=0 ; i<size;i++){
                map =  new HashMap();
                map.put("stationName",((Map) resDBList.get(i)).get("projdeptname")); // 台名
                map.put("completeProject",((Map) resDBList.get(i)).get("projdeptname"));// 已完成项目数
                map.put("timeLength",((Map) resDBList.get(i)).get("time")); // 累计时间
                resList.add(map);
            }

            resultJSON.accumulate("projectList",resList);  // 列表数据

            Map resMap = (HashMap) wirelessDB.qryMap(SecondPageThemeDao.getCompleteConditionInfo(reqMap)).get("data");
            resultJSON.accumulate("stationCount",resMap.get("projdeptname"));      //台次
            resultJSON.accumulate("completeProject",resMap.get("donecount"));  //完成项目
            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServlet.right.getStationResource]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

    //重保期实时累计播放时长
    protected String getProgramPlaybackTime(JSONObject json,String screenPosition){
        try{
            Map reqMap = new HashMap();
            JSONObject  resultJSON = new JSONObject();
            List resList = new ArrayList();
            Map map =  new HashMap();
            List resDBList = (List) wirelessDB.qryList(SecondPageThemeDao.getProgramPlaybackTimeList(reqMap)).get("data");
            int size = 5;
            if(resDBList.size()<=5){
                size = resDBList.size();
            }
            for(int i=0 ; i < size;i++){
                map =  new HashMap();
                map.put("programName",((Map) resDBList.get(i)).get("PROGRAM_NAME")); // 节目名
                map.put("timeLength",((Map) resDBList.get(i)).get("play_time"));// 累计时间
                resList.add(map);
            }

            resultJSON.accumulate("programPlayTimeList",resList);  // 重保期实时累计播放时长
            return resultJSON.toString();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[SecondThemeServlet.right.getStationResource]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

    //重保期节目播出状态
    protected String getProgramPlayStatus(JSONObject json,String screenPosition){
        try{
            Map reqMap = new HashMap();
            JSONObject  resultJSON = new JSONObject();
            List resList = new ArrayList();
            Map map =  new HashMap();
            List resDBList = (List) wirelessDB.qryList(SecondPageThemeDao.getProgramPlayStatusList(reqMap)).get("data");
            int size = resDBList.size();
            for(int i=0 ; i<size;i++){
                map =  new HashMap();
                map.put("programName",((Map) resDBList.get(i)).get("program_name"));  // 节目名
                map.put("transmitter", ((Map) resDBList.get(i)).get("usedcode"));      // 在播发射机
                map.put("anterna", ((Map) resDBList.get(i)).get("antcode_bfd_dic"));      // 在播天线
                map.put("freq", ((Map) resDBList.get(i)).get("freq"));      // 频率
                map.put("excep", ((Map) resDBList.get(i)).get("excep"));      // 异态
                map.put("servArea", ((Map) resDBList.get(i)).get("serv_area"));      // 覆盖区域
                resList.add(map);
            }
            resultJSON.accumulate("programStatusList",resList);  // 重保期节目播出状态
            return resultJSON.toString();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[SecondThemeServlet.right.getStationResource]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

}
