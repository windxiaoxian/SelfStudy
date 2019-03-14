package gd.services.ledDisplay.wirelessProject.servlet;

//import gd.services.ledDisplay.wirelessProject.dao.SecondPageThemeDAO;
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

@WebServlet(name = "SecondThemeServletTest" )
public class SecondThemeServletTest extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static Logger logger = LogManager.getLogger(SecondThemeServletTest.class.getName());

    public SecondThemeServletTest(){
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
            Map reqMap = new HashMap();
            JSONObject resultJSON = new JSONObject();
            //获取实时资源比对
            // 无线局
            resultJSON.accumulate("transmitterWXJ",1234);//当前实验发射机
            resultJSON.accumulate("broadcastTimeWXJ",123456);//当日累计播音时间
            resultJSON.accumulate("accFreqencyWXJ",345);//当日累计频次
            resultJSON.accumulate("accFreqWXJ",56);//当日累计频率个数

            // 对象台
            resultJSON.accumulate("transmitterEnemy",23);//当前实验发射机
            resultJSON.accumulate("broadcastTimeEnemy",2345);//当日累计播音时间
            resultJSON.accumulate("accFreqencyEnemy",45);//当日累计频次
            resultJSON.accumulate("accFreqEnemy",34);//当日累计频率个数

            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServletTest.left.getResourceCompare]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

    //突发频率实时分析
    protected String getBurstFreqAnalysis(JSONObject json,String screenPosition){
        try{
            Map reqMap = new HashMap();
            JSONObject resultJSON = new JSONObject();
            //突发频率实时分析
            List brustFreqList =  new ArrayList();
            for(int i=0;i<24;i++){
                brustFreqList.add((int)(Math.random()*100)+100);
            }
            resultJSON.accumulate("brustFreqList",brustFreqList);
            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServletTest.left.getburstFreqAnalysis]后台报错" + e.getMessage());
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
            List freqencyList =  new ArrayList();
            for(int i=0;i<24;i++){
                freqencyList.add((int)(Math.random()*100)+100);
            }
            Map map = new HashMap();
            map.put("freq",6990);
            map.put("times",1232);
            map.put("freqencyList",freqencyList);

            map.put("freq",10800);
            map.put("times",1200);
            brustFreqOrderList.add(map);

            map.put("freq",7550);
            map.put("times",897);
            brustFreqOrderList.add(map);

            map.put("freq",960);
            map.put("times",799);
            brustFreqOrderList.add(map);

            map.put("freq",14400);
            map.put("times",666);
            brustFreqOrderList.add(map);
            resultJSON.accumulate("brustFreqOrderList",brustFreqOrderList);
            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServletTest.left.getBurstFreqOrder]后台报错" + e.getMessage());
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
            Map map  = new HashMap();
            map.put("station","491台");
            map.put("orderCount",23);
            map.put("orderRate","86%");
            orderTimeList.add(map);

            map.put("station","624台");
            map.put("orderCount",34);
            map.put("orderRate","68%");
            orderTimeList.add(map);

            map.put("station","2021台");
            map.put("orderCount",12);
            map.put("orderRate","48%");
            orderTimeList.add(map);

            map.put("station","573台");
            map.put("orderCount",67);
            map.put("orderRate","82%");
            orderTimeList.add(map);

            Map map2 = new HashMap();
            map2.put("station","491台");
            map2.put("applicationCount",80);//申请次数
            map2.put("applicationGeneral",3232);//申请代播时长
            map2.put("applicationOther",212);//代播他台时长
            map2.put("generalRate","90%");//代播执行率
            generationBroadList.add(map2);

            map2.put("station","491台");
            map2.put("applicationCount",80);//申请次数
            map2.put("applicationGeneral",3232);//申请代播时长
            map2.put("applicationOther",212);//代播他台时长
            map2.put("generalRate","90%");//代播执行率
            generationBroadList.add(map2);

            map2.put("station","282台");
            map2.put("applicationCount",6);//申请次数
            map2.put("applicationGeneral",130);//申请代播时长
            map2.put("applicationOther",80);//代播他台时长
            map2.put("generalRate","95%");//代播执行率
            generationBroadList.add(map2);

            map2.put("station","2021台");
            map2.put("applicationCount",12);//申请次数
            map2.put("applicationGeneral",20);//申请代播时长
            map2.put("applicationOther",119);//代播他台时长
            map2.put("generalRate","98%");//代播执行率
            generationBroadList.add(map2);

            map2.put("station","293台");
            map2.put("applicationCount",56);//申请次数
            map2.put("applicationGeneral",108);//申请代播时长
            map2.put("applicationOther",246);//代播他台时长
            map2.put("generalRate","89%");//代播执行率
            generationBroadList.add(map2);

            resultJSON.accumulate("orderByAnnual",1234);//全局下发调度令总数
            resultJSON.accumulate("orderImplRateByAnnual",99.19);//全局下发调度令执行率
            resultJSON.accumulate("generalBroadTimesByAnnual",678);//全局年度至今代播总频次
            resultJSON.accumulate("generalBroadTimeByAnnual",9807);//全局年度至今代播总时长
            resultJSON.accumulate("orderTimeList",orderTimeList);
            resultJSON.accumulate("generationBroadList",generationBroadList);
            return resultJSON.toString();
        }catch(Exception  e){
            logger.error("[SecondThemeServletTest.left.getAnnualSituation]后台报错" + e.getMessage());
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
            logger.error("[SecondThemeServletTest.middle.getBroadCastResource]后台报错" + e.getMessage());
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
            map.put("order","2019[987号]调度令");
            map.put("freq",14400);
            map.put("station","572台");
            map.put("transmitter","A02");
            map.put("annate",301);
            map.put("status","未下发");
            currentOrderList.add(map);

            map.put("order","2019[001号]调度令");
            map.put("freq",11800);
            map.put("station","2021台");
            map.put("transmitter","A02");
            map.put("annate",301);
            map.put("status","未下发");
            currentOrderList.add(map);

            map.put("order","2019[007号]调度令");
            map.put("freq",960);
            map.put("station","293台");
            map.put("transmitter","A01");
            map.put("annate",301);
            map.put("status","已下发");
            currentOrderList.add(map);

            resultJSON.accumulate("stationInUse",222);//在播台站
            resultJSON.accumulate("transmitterInUse",444);//在播发射机
            resultJSON.accumulate("broadcastPower",333);//在播功率
            resultJSON.accumulate("broadcastTime",123445);//当日在播时长
            resultJSON.accumulate("programOnPlay",76);//在播节目
            resultJSON.accumulate("suppressEnemy",687);//实验压制敌台次数

            resultJSON.accumulate("orderCountByDay",56);//当日调度令总数
            resultJSON.accumulate("orderBroadTime",687);//调度播出时长
            resultJSON.accumulate("orderAddFreq",687);//调度新开或增开频率
            resultJSON.accumulate("currentOrderList",currentOrderList);//当前小时调度令情况

            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServletTest.middle.getBroadCastResource]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

    //全局台站资源总览
    protected String getStationResource(JSONObject json,String screenPosition){
        try{
            Map reqMap = new HashMap();
            JSONObject  resultJSON = new JSONObject();
            resultJSON.accumulate("transmitterInUse",126);  //全局发射机状态:在用
            resultJSON.accumulate("transmitterUsed",135);   //全局发射机状态:可用
            //resultJSON.accumulate("transmitterFree",236);   //全局发射机状态:空闲
            resultJSON.accumulate("transmitterRepair",32); //全局发射机状态:抢修
            resultJSON.accumulate("transmitterClose",147);  //全局发射机状态:停用

            resultJSON.accumulate("antennaInUse",119);  //全局天线状态:在用
            resultJSON.accumulate("antennaUsed",209);   //全局天线状态:可用
           // resultJSON.accumulate("antennaFree",224);   //全局天线状态:空闲
            resultJSON.accumulate("antennaRepair",108); //全局天线状态:抢修
            resultJSON.accumulate("antennaClose",190);  //全局天线状态:停用

            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServletTest.right.getStationResource]后台报错" + e.getMessage());
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
            logger.error("[SecondThemeServletTest.right.getStationResource]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

    //  技术大修页面数据
    protected String getTechnologyCondition(JSONObject json,String screenPosition){
        try{
            Map reqMap = new HashMap();
            JSONObject resultJSON = new JSONObject();
            List doingList = new ArrayList();
            for(int i=0 ; i<5;i++){
                Map map =  new HashMap();
                map.put("stationName","491台");
                map.put("startDate","2019/01/05");
                map.put("endDate","2019/01/05");
                map.put("projectName","XXXXXXXXXXXXXXXXXXXXX");
                doingList.add(map);  // 当前正在执行项目
            }
            List todoList = new ArrayList();
            for(int i=0 ; i<5;i++){
                Map map =  new HashMap();
                map.put("stationName","491台");
                map.put("startDate","2019/01/05");
                map.put("endDate","2019/01/05");
                map.put("projectName","XXXXXXXXXXXXXXXXXXXXX");
                todoList.add(map);
            }
            resultJSON.accumulate("doingList",doingList); // 当前正在执行项目
            resultJSON.accumulate("toDoList",todoList);    // 即将在执行项目
            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServletTest.right.getStationResource]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }


    protected String getCompleteCondition(JSONObject json,String screenPosition){
        try{
            Map reqMap = new HashMap();
            JSONObject  resultJSON = new JSONObject();
            List resList = new ArrayList();
            Map map =  new HashMap();
            map.put("stationName","491");  // 台名
            map.put("completeProject",33); // 已完成项目数
            map.put("timeLength",112);      // 累计时间
            resList.add(map);

            map.put("stationName","582");
            map.put("completeProject",16);
            map.put("timeLength",221);
            resList.add(map);

            map.put("stationName","2021");
            map.put("completeProject",54);
            map.put("timeLength",240);
            resList.add(map);

            resultJSON.accumulate("projectList",resList);  // 列表数据
            resultJSON.accumulate("stationCount",234);      //台次
            resultJSON.accumulate("completeProject",456);  //完成项目
            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServletTest.right.getStationResource]后台报错" + e.getMessage());
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
            map.put("programName","中一");  // 节目名
            map.put("timeLength", 1836);      // 累计时间
            resList.add(map);

            map.put("programName","中二");  // 节目名
            map.put("timeLength", 1336);      // 累计时间
            resList.add(map);

            map.put("programName","中三");  // 节目名
            map.put("timeLength", 1006);      // 累计时间
            resList.add(map);

            map.put("programName","中四");  // 节目名
            map.put("timeLength", 836);      // 累计时间
            resList.add(map);

            map.put("programName","中五");  // 节目名
            map.put("timeLength", 776);      // 累计时间
            resList.add(map);

            resultJSON.accumulate("programPlayTimeList",resList);  // 重保期实时累计播放时长
            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServletTest.right.getStationResource]后台报错" + e.getMessage());
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
            map.put("programName","中一");  // 节目名
            map.put("transmitter", "A04");      // 在播发射机
            map.put("anterna", "10号天线");      // 在播天线
            map.put("freq", 111650);      // 频率
            map.put("excep", 1836);      // 异态
            map.put("servArea", 1836);      // 覆盖区域
            for (int i =0 ; i < 9 ; i++){
                resList.add(map);
            }

            resultJSON.accumulate("programStatusList",resList);  // 重保期节目播出状态
            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServletTest.right.getStationResource]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

    public static void main(String[] args) {
//        Integer orderCount = Integer.valueOf("7200"); //全局下发调度令总数
//        Integer orderCountDone = Integer.valueOf("7030"); //全局下发调度令总数
//
//        float completioRate =(float) orderCountDone/orderCount;
//        //保留2位小数，且四舍五入, 全局下发调度令执行率
//        double result = new BigDecimal(completioRate).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue()*100;
//        double orderImplRateByAnnual = completioRate.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
//        int random=(int)(Math.random()*10+1);
        Map map = new HashMap();
        List list = new ArrayList();

        map.put("a", "123");
        list.add(map);
        map = new HashMap();
        map.put ("a", "456");
        list.add(map);
        System.out.println(((Map)list.get(0)).get("a"));
        System.out.println(((Map)list.get(1)).get("a"));

    }


}
