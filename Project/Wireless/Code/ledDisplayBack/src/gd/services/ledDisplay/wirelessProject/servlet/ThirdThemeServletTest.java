package gd.services.ledDisplay.wirelessProject.servlet;

import com.sun.net.httpserver.HttpServer;
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

/**
 * Created by liubaoyun on 2019/3/1215:07
 */
@WebServlet(name = "ThirdThemeServletTest")
public class ThirdThemeServletTest<main> extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static Logger logger = LogManager.getLogger(SecondThemeServlet.class.getName());

    public ThirdThemeServletTest(){
        super();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String screenPosition = request.getParameter("key");//获取主题三中的当前的屏幕位置：左、中、右
        String paramsInfo = request.getParameter("param");//前台传递参数
        JSONObject paramJson = null;//Json格式参数
        String interfaceName = JSONObject.fromObject(paramsInfo).getString("interfaceName");//前台调用的接口名称
        String callbackInfo = "";//返回结果
        
        if(null != paramsInfo && !"".equals(paramsInfo)){
            paramJson = (JSONObject) JSONSerializer.toJSON(paramsInfo);
        }

        try{
            //判断前台调用的是那个页面的接口
            if(null != screenPosition) {
                if ("left".equals(screenPosition)) {//主题三@左侧屏幕
                    if ("automaticSystem".equals(interfaceName)) {//自动化系统
                        callbackInfo = getAutomaticSystem(paramJson, screenPosition);
                    } else if ("burstFreqAnalysis".equals(interfaceName)) {//局台数据传输监控
                        callbackInfo = getDataMonitoring(paramJson, screenPosition);
                    }else if ("systemDetailsInfo".equals(interfaceName)) {//系统详细信息列表
                        callbackInfo = getSystemDetailsInfo(paramJson, screenPosition);
                    }
                } else if ("middle".equals(screenPosition)) {//主题三@中间屏幕
                        if ("stationOrderTrendResource".equals(interfaceName)) {//实时网络拓扑图
                            callbackInfo = getMapStationStatusInfo(paramJson, screenPosition);
                        }else if ("flowAndBandwidthInfo".equals(interfaceName)) {//流量、威胁数、带宽等四个数值
                            callbackInfo = getFlowAndBandwidthInfo(paramJson, screenPosition);
                        }else if ("top5AndNetworkFlowInfo".equals(interfaceName)) {//top5信息4张图和网络流量趋势图
                            callbackInfo = getTop5AndNetworkFlowInfo(paramJson, screenPosition);
//                            callbackInfo = getStationFlowTop5(paramJson, screenPosition);
                        }
//                        else if ("stationThreatQuantityTop5".equals(interfaceName)) {//台站威胁top5
//                            callbackInfo = getStationThreatQuantityTop5(paramJson, screenPosition);
//                        }else if ("appFlowsTop5".equals(interfaceName)) {//应用流量top5
//                            callbackInfo = getAppFlowsTop5(paramJson, screenPosition);
//                        }else if ("threatTypeTop5".equals(interfaceName)) {//威胁类型top5
//                            callbackInfo = getThreatTypeTop5(paramJson, screenPosition);
//                        }else if ("".equals(interfaceName)) {//网络流量趋势图
//                            callbackInfo = getNetworkFlowTrendInfo(paramJson, screenPosition);
//                        }
                } else if ("right".equals(screenPosition)) {//主题三@右侧屏幕
                        if ("dataCenterInfo".equals(interfaceName)) {//全局台站资源总览
                            callbackInfo = getDataCenterInfo(paramJson, screenPosition);
                        }else if ("databaseMonitoringInfo".equals(interfaceName)) {//数据库监控
                            callbackInfo = getDatabaseMonitoringInfo(paramJson, screenPosition);
                        }else if ("compRoomMonitoringInfo".equals(interfaceName)) {//机房监控信息
                            callbackInfo = getCompRoomMonitoringInfo(paramJson, screenPosition);
                        }
                 }
                 if ("".equals(callbackInfo))
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
    /**
     * 机房监控信息
     * @param paramJson
     * @param screenPosition
     * @return
     */
    private String getCompRoomMonitoringInfo(JSONObject paramJson, String screenPosition) {
        try{
            Map reqMap = new HashMap();
            JSONObject  resultJSON = new JSONObject();

            List humidityAndTemperatureInfoList = new ArrayList(); // 湿温度性能
            for (int i = 0 ; i<10 ; i++){
                Map map = new HashMap();
                map.put("configureNm","(动环)温湿度" +i); // 配置顶名
                map.put("configureType","温湿度");   //配置类型
                int random=(int)(Math.random()*10000+1);
                BigDecimal completioRate = new BigDecimal(random/100);
                double orderImplRateByAnnual = completioRate.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                map.put("humidity",orderImplRateByAnnual);  //湿度
                map.put("temperature",25);       // 温度
                map.put("status","0");       // 状态 0：正常  1：不正常
                humidityAndTemperatureInfoList.add(map); // 湿温度性能
            }

            List airConditionerInfoList = new ArrayList(); // 空调信息
            for (int i = 0 ; i<10 ; i++){
                Map map = new HashMap();
                map.put("configureNm","(动环)空调" +i); // 配置顶名
                map.put("configureType","精密空调");   //配置类型
                map.put("settingHumidity",20);  //湿度设定
                map.put("settingTemperature",25);       // 回风温度设定
                int random=(int)(Math.random()*10000+1);
                BigDecimal completioRate = new BigDecimal(random/100);
                double orderImplRateByAnnual = completioRate.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                map.put("humidity",orderImplRateByAnnual);  //室内湿度
                map.put("temperature",25);       // 室内温度
                map.put("blowingInTemp",20);       // 送风温度
                map.put("status","0");       // 状态 0：正常  1：不正常
                airConditionerInfoList.add(map); // 空调信息
            }

            List UPSInfoList = new ArrayList(); // UPS信息
            for (int i = 0 ; i<10 ; i++){
                Map map = new HashMap();
                map.put("configureNm","(动环)UPS" +i); // 配置顶名
                map.put("configureType","UPS");   //配置类型
                map.put("inverterHumidity",26);  //逆变器湿度
                map.put("rectifierHumidity",30);  //整流器湿度
                map.put("environmentHumidity",26);  // 机内环境湿度

                map.put("surplusElectricity",32767+i);       // 电池剩余电量
                map.put("status","0");       // 状态 0：正常  1：不正常
                UPSInfoList.add(map); // UPS信息
            }

            resultJSON.accumulate("humidityAndTemperatureInfoList",humidityAndTemperatureInfoList);// 数据库信息
            resultJSON.accumulate("airConditionerInfoList",airConditionerInfoList);// 空调信息
            resultJSON.accumulate("UPSInfoList",UPSInfoList);// UPS信息
            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServletTest.middle.getBroadCastResource]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

    /**
     * 数据库监控
     * @param paramJson
     * @param screenPosition
     * @return
     */
    private String getDatabaseMonitoringInfo(JSONObject paramJson, String screenPosition) {
        try{
            Map reqMap = new HashMap();
            JSONObject  resultJSON = new JSONObject();

            List DatabaseInfoList = new ArrayList(); // 数据库信息
            for (int i = 0 ; i<10 ; i++){
                Map map = new HashMap();
                map.put("configureNm","MYSQL-10.6.3.122"); // 配置顶名
                map.put("configureType","数据库");   //配置类型
                map.put("name","MYSQL");  //名称
                map.put("status",1);       // 状态 0：正常  1：不正常
                map.put("version","10.0.1");       // 版本
                map.put("warningLevel",2);       // 警告等级
                DatabaseInfoList.add(map); // 数据库信息
            }

            List lastMonthDataList = new ArrayList(); // 上月数据存储
            for(int i=0 ; i<12;i++){
                int random=(int)(Math.random()*10+1);
                lastMonthDataList.add(500*random);  // 上月数据存储
            }

            List thisMonthAdditionalDataList = new ArrayList(); // 本月新增数据存储
            for(int i=0 ; i<12;i++){
                int random=(int)(Math.random()*10+1);
                thisMonthAdditionalDataList.add(500*random);  // 本月新增数据存储
            }

            resultJSON.accumulate("DatabaseInfoList",DatabaseInfoList);// 数据库信息
            resultJSON.accumulate("lastMonthDataList",lastMonthDataList);// 上月数据存储
            resultJSON.accumulate("thisMonthAdditionalDataList",thisMonthAdditionalDataList);// 本月新增数据存储
            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServletTest.middle.getBroadCastResource]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }


    /**
     * 数据中心信息
     * @param paramJson
     * @param screenPosition
     * @return
     */
    private String getDataCenterInfo(JSONObject paramJson, String screenPosition) {
        try{
            Map reqMap = new HashMap();
            JSONObject  resultJSON = new JSONObject();

            List ODSList = new ArrayList();// ODS信息
            List DWList = new ArrayList();// DW信息
            List DMList = new ArrayList();// DM信息

            Map map1 = new HashMap();
            map1.put("storage", 3857);
            map1.put("tableCount", 3857);
            map1.put("fieldsCount", 385346);
            ODSList.add(map1);

            Map map2 = new HashMap();
            map2.put("storage", 2847);
            map2.put("tableCount", 2858);
            map2.put("fieldsCount", 455346);
            DWList.add(map2);

            Map map3 = new HashMap();
            map3.put("storage", 1857);
            map3.put("tableCount", 2234);
            map3.put("fieldsCount", 125346);
            DMList.add(map3);

            resultJSON.accumulate("bureauBusinessSysCount ", 18);//局端业务系统数量
            resultJSON.accumulate("stationBusinessSysCount", 49);//台站业务系统数量
            resultJSON.accumulate("manualImportDataCount", 18);//人工导入数据数量
            resultJSON.accumulate("totalDataSize", 520);// 接入总数据量
            resultJSON.accumulate("realTimeDataSize", 450);// 接入实时数据量
            resultJSON.accumulate("questionCount", 45690);// 问题条数
            resultJSON.accumulate("recordCount", 84569);// 记录条数
            resultJSON.accumulate("ODSList", ODSList);// 记录条数
            resultJSON.accumulate("DWList", DWList);// 记录条数
            resultJSON.accumulate("DMList", DMList);// 记录条数

            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServletTest.middle.getBroadCastResource]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

    /**
     * top5信息4张图和网络流量趋势图
     * @param paramJson
     * @param screenPosition
     * @return
     */
    private String getTop5AndNetworkFlowInfo(JSONObject paramJson, String screenPosition) {
        try{
            Map reqMap = new HashMap();
            JSONObject resultJSON = new JSONObject();

            List stationFlowTopList = new ArrayList();// 实时台站流量top5
            List stationThreatQuantityTopList = new ArrayList();//台站威胁top5
            List appFlowsTopList = new ArrayList();//应用流量top5
            List threatTypeTopList = new ArrayList();//威胁类型top5
            List networkFlowTrendList = new ArrayList();//网络流量趋势图
            //应用流量top5
            for(int i=0 ; i<5;i++){
                Map map =  new HashMap();
                map.put("appName","应用程序"+i);
                map.put("flowPercentage",51.24 - (i*5)); // 流量百分比
                map.put("flowTotal",10);      // 流量总量
                appFlowsTopList.add(map);  //应用流量top5
            }
            //威胁类型top5
            for(int i=0 ; i<5;i++){
                Map map =  new HashMap();
                map.put("threatTypeName","威胁名称"+i);
                map.put("threatQuantity",99-(i*5)); // 威胁数
                threatTypeTopList.add(map);  //威胁类型top5
            }
            //网络流量趋势图
            for(int i=0 ; i<12;i++){
                int random=(int)(Math.random()*10+1);
                networkFlowTrendList.add(500*random);  //网络流量趋势图
            }
            //台站威胁top5
            for(int i=0 ; i<5;i++){
                Map map =  new HashMap();
                map.put("stationName",491 + i +"台");
                map.put("threatQuantity",59 - (i*4));
                stationThreatQuantityTopList.add(map);  //台站威胁top5
            }
            // 实时台站流量top5
            for(int i=0 ; i<5;i++){
                Map map =  new HashMap();
                map.put("stationName",491 + i +"台");
                map.put("frequency",45-i);
                stationFlowTopList.add(map);  // 实时台站流量top5
            }
            resultJSON.accumulate("stationFlowTopList",stationFlowTopList); // 实时台站流量top5
            resultJSON.accumulate("stationThreatQuantityTopList",stationThreatQuantityTopList); //台站威胁top5
            resultJSON.accumulate("threatTypeTopList",threatTypeTopList); //威胁类型top5
            resultJSON.accumulate("networkFlowTrendList",networkFlowTrendList); //网络流量趋势图
            resultJSON.accumulate("appFlowsTopList",appFlowsTopList); //应用流量top5
            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServletTest.right.getStationResource]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

    /**
     * 流量、威胁数、带宽等四个数值
     * @param paramJson
     * @param screenPosition
     * @return
     */
    private String getFlowAndBandwidthInfo(JSONObject paramJson, String screenPosition) {
        try{
            Map reqMap = new HashMap();
            JSONObject  resultJSON = new JSONObject();
            resultJSON.accumulate("totalFlow", "036708");//实时总流量
            resultJSON.accumulate("threatQuantity", "000337");//实时安全威胁数
            resultJSON.accumulate("usedBandwidth", "370");//已使用带宽
            resultJSON.accumulate("totalBandwidth", "1000");//总带宽
            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServletTest.middle.getBroadCastResource]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

    /**
     * 实时拓扑图信息
     * @param paramJson
     * @param screenPosition
     * @return
     */
    private String getMapStationStatusInfo(JSONObject paramJson, String screenPosition) {
        try{
            Map reqMap = new HashMap();
            JSONObject  resultJSON = new JSONObject();
            List mapStationStatusInfoList = new ArrayList();
            Map map = new HashMap();
            map.put("stationNm","491台"); // 台名称
            map.put("stationX","39N53");   //纬度
            map.put("stationY","116E36");  //经度
            map.put("status",0);       // 状态 0：正常  1：不正常
            mapStationStatusInfoList.add(map);

            map.put("stationNm","542台"); // 台名称
            map.put("stationX","39N45");   //纬度
            map.put("stationY","116E14");  //经度
            map.put("status",1);       // 两系统之间状态 0：正常  1：不正常
            mapStationStatusInfoList.add(map);

            map.put("stationNm","582台"); // 台名称
            map.put("stationX","39N56");   //纬度
            map.put("stationY","116E21");  //经度
            map.put("status",0);       // 两系统之间状态 0：正常  1：不正常
            mapStationStatusInfoList.add(map);

            map.put("stationNm","发射二台"); // 台名称
            map.put("stationX","39N55");   //纬度
            map.put("stationY","116E23");  //经度
            map.put("status",0);       // 两系统之间状态 0：正常  1：不正常
            mapStationStatusInfoList.add(map);

            resultJSON.accumulate("mapStationStatusInfoList",mapStationStatusInfoList);//实时拓扑图信息
            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServletTest.middle.getBroadCastResource]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

    /**
     * 自动化系统
     * @param paramJson
     * @param screenPosition
     * @return
     */
    private String getAutomaticSystem(JSONObject paramJson, String screenPosition) {
        try{
            Map reqMap = new HashMap();
            JSONObject resultJSON = new JSONObject();
            //音频调度系统
            List audioSystemList =  new ArrayList();
            Map map = new HashMap();
            for(int i=0;i<5;i++){
                map = new HashMap();
                map.put("stationNm",3012+i+"台");
                map.put("code",123+i);
                map.put("status", "故障");
                audioSystemList.add(map);
            }

            // 发射机自动化系统
            List transmitterSystemList =  new ArrayList();
            for(int i=0;i<5;i++){
                map = new HashMap();
                map.put("stationNm",3012+i+"台");
                map.put("code",123+i);
                map.put("status", "故障");
                transmitterSystemList.add(map);
            }
            // 播音质量监测系统
            List broadcastingSystemList =  new ArrayList();
            for(int i=0;i<6;i++){
                map = new HashMap();
                map.put("stationNm",534+i+"台");
                map.put("code",221+i);
                map.put("status", "故障");
                broadcastingSystemList.add(map);
            }

            // 天线自动化系统
            List antennaSystemList =  new ArrayList();
            for(int i=0;i<2;i++){
                map = new HashMap();
                map.put("stationNm",2932+i+"台");
                map.put("code",212+i);
                map.put("status", "故障");
                antennaSystemList.add(map);
            }
            resultJSON.accumulate("audioSystemList",audioSystemList);              //音频调度系统
            resultJSON.accumulate("transmitterSystemList",transmitterSystemList); // 发射机自动化系统
            resultJSON.accumulate("broadcastingSystemList",broadcastingSystemList);// 播音质量监测系统
            resultJSON.accumulate("antennaSystemList",antennaSystemList);          // 天线自动化系统

            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServletTest.left.getResourceCompare]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

    private String getDataMonitoring(JSONObject paramJson, String screenPosition) {
        try{
            Map reqMap = new HashMap();
            JSONObject resultJSON = new JSONObject();
            //局智能运维系统——台技术业务管理系统（第一个图）
            HashMap firstMonitoringMap =  new HashMap();

            //上传数据实时信息
            List uploadDateInfoList = new ArrayList();
            Map map = new HashMap();
            for(int i=0;i<3;i++){
                map.put("stationNm",2045+i+"台");
                map.put("status", "正常");
                map.put("info", 2045+i+"台 正在上传");
                uploadDateInfoList.add(map);
            }
            //服务器状态信息
            List systemStatusInfoList = new ArrayList();
            for(int i=0;i<4;i++){
                map.put("stationNm",1066+i+"台");       //台站名
                map.put("status", "正常");                // 状态
                map.put("info", 1066+i+"台 服务器正常");  // 详细信息
                systemStatusInfoList.add(map);
            }
            //链路状态信息
            List linkStatusInfoList = new ArrayList();
            for(int i=0;i<4;i++){
                map.put("stationNm",3120+i+"台");
                map.put("status", "正常");
                map.put("info", 3120+i+"台 链路正常");
                linkStatusInfoList.add(map);
            }
            firstMonitoringMap.put("uploadDateInfoList", uploadDateInfoList);     //上传数据实时信息
            firstMonitoringMap.put("systemStatusInfoList", systemStatusInfoList); //服务器状态信息
            firstMonitoringMap.put("linkStatusInfoList", linkStatusInfoList);     //链路状态信息
            firstMonitoringMap.put("status", 0);// 两系统之间状态 0：正常  1：不正常

            //局智能运行系统—-台运行管理系统（第二个图）
            HashMap secondMonitoringMap =  new HashMap();
            //链路状态信息
            linkStatusInfoList = new ArrayList();
            for(int i=0;i<4;i++){
                map.put("stationNm",3120+i+"台");
                map.put("status", "不正常");
                map.put("info", 3120+i+"台 链路不正常");
                linkStatusInfoList.add(map);
            }

            secondMonitoringMap.put("uploadDateInfoList", uploadDateInfoList);     //上传数据实时信息
            secondMonitoringMap.put("systemStatusInfoList", systemStatusInfoList); //服务器状态信息
            secondMonitoringMap.put("linkStatusInfoList", linkStatusInfoList);     //链路状态信息
            secondMonitoringMap.put("status", 1);// 两系统之间状态 0：正常  1：不正常

            resultJSON.accumulate("firstMonitoringMap",firstMonitoringMap); //局智能运维系统——台技术业务管理系统（第一个图）
            resultJSON.accumulate("secondMonitoringMap",secondMonitoringMap); //局智能运行系统—-台运行管理系统（第二个图）
            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServletTest.left.getResourceCompare]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

    /**
     * 系统详细信息
     * @param paramJson
     * @param screenPosition
     * @return
     */
    private String getSystemDetailsInfo(JSONObject paramJson, String screenPosition) {
        try{
            Map reqMap = new HashMap();
            JSONObject resultJSON = new JSONObject();

            // 系统详细信息
            List systemDetailsInfoList = new ArrayList();
            Map map = new HashMap();
            for(int i=1;i<24;i++){
                map.put("systemNm","应用系统" + i);  //应用系统名称
                map.put("status", (i%2==1 ? 1 : 0 ));// 系统状态 0：正常  1：不正常
                map.put("diskSpace", 64+i);          // 磁盘空间
                map.put("processNumber", 234+i);     // 进程数
                systemDetailsInfoList.add(map);
            }
            resultJSON.accumulate("systemDetailsInfoList",systemDetailsInfoList); // 系统详细信息
            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServletTest.left.getResourceCompare]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }


}
