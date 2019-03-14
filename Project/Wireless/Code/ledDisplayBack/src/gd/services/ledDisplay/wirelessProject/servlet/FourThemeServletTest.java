package gd.services.ledDisplay.wirelessProject.servlet;

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
@WebServlet(name = "FourThemeServletTest")
public class FourThemeServletTest<main> extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static Logger logger = LogManager.getLogger(SecondThemeServlet.class.getName());

    public FourThemeServletTest(){
        super();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String screenPosition = request.getParameter("key");//获取主题四中的当前的屏幕位置：左、中、右
        String paramsInfo = request.getParameter("param");//前台传递参数
        JSONObject paramJson = null;//Json格式参数
        String interfaceName = JSONObject.fromObject(paramsInfo).getString("interfaceName");//前台调用的接口名称
        String stationCode = JSONObject.fromObject(paramsInfo).getString("stationCode");//前台传递台站编码
        String callbackInfo = "";//返回结果
        
        if(null != paramsInfo && !"".equals(paramsInfo)){
            paramJson = (JSONObject) JSONSerializer.toJSON(paramsInfo);
        }

        try{
            //判断前台调用的是那个页面的接口
            if(null != screenPosition) {
                if ("left".equals(screenPosition)) {//主题四@左侧屏幕

                } else if ("middle".equals(screenPosition)) {//主题四@中间屏幕
                        if ("temporalDynamicsAndMapInfo".equals(interfaceName)) {//时间动态和地图坐标信息（包括报警总字数）
                            callbackInfo = getTemporalDynamicsAndMapInfo(paramJson, screenPosition);
                        }else if("emergencyMonthAndYearInfo".equals(interfaceName)) {//近12个月每月应急事件趋势图 和 年度应急排名
                            callbackInfo = getEmergencyMonthAndYearInfo(paramJson, screenPosition);
                        }
                } else if ("right".equals(screenPosition)) {//主题四@右侧屏幕
                    if ("emergencyInfo".equals(interfaceName)) {//应急事件
                        callbackInfo = getEmergencyInfo(stationCode);
                    }else if("emergencyContactInfo".equals(interfaceName)) {//应急联系信息
                        callbackInfo = getEmergencyContactInfo(stationCode);
                    }else if("beAffectedInfo".equals(interfaceName)) {//本次事件受影响信息
                        callbackInfo = getBeAffectedInfo(stationCode);
                    }else if("emergencyResourcesInfo".equals(interfaceName)) {//应急调度资源
                        callbackInfo = getEmergencyResourcesInfo(stationCode);
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
     * 应急调度资源
     * @param stationCode
     * @return
     */
    private String getEmergencyResourcesInfo(String stationCode) {
        try{
            Map reqMap = new HashMap();
            JSONObject  resultJSON = new JSONObject();

            resultJSON.accumulate("transmitterCount",22);// 发射机数量
            resultJSON.accumulate("antennaCount",244);// 天线数量
            resultJSON.accumulate("compRoomCount",22);// 机房数量
            resultJSON.accumulate("powerReserve",1000);// 电力储备
            resultJSON.accumulate("vehicleCount",101);// 车辆数量
            resultJSON.accumulate("fireFequCount",50);// 消防设备资产
            resultJSON.accumulate("safetyDefense",100);// 安全防卫资产
            resultJSON.accumulate("armedForces",81);//武装力量
            resultJSON.accumulate("dutyOfficerCount",30);// 值班人员
            resultJSON.accumulate("inJobPeoCount",108);// 在职人员
            resultJSON.accumulate("furloughPeo",3);// 休假人员
            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServletTest.middle.getBroadCastResource]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

    /**
     * 本次事件受影响信息
     * @param stationCode
     * @return
     */
    private String getBeAffectedInfo(String stationCode) {
        try{
            Map reqMap = new HashMap();
            JSONObject  resultJSON = new JSONObject();
            List networkStatusList = new ArrayList();   // 网络状态信息
            List personnelInfoList  = new ArrayList();   // 人员信息list
            List broadcastStatusList  = new ArrayList();   // 播出状态信息
            List materialInfoList  = new ArrayList();   // 物资信息ist

            Map map = new HashMap();
            for(int i=0 ; i<5;i++) {
                map = new HashMap();
                map.put("name", "交换机" + i); // 名称
                map.put("status", "损坏"); // 状态
                networkStatusList.add(map);// 网络状态信息
            }

            for(int i=0 ; i<5;i++) {
                map = new HashMap();
                map.put("name", "人员" + i); // 名称
                map.put("status", "食物中毒"); // 状态
                personnelInfoList.add(map);// 人员信息l
            }

            for(int i=0 ; i<5;i++) {
                map = new HashMap();
                map.put("name", "节目" + i); // 名称
                map.put("status", "代播"); // 状态
                broadcastStatusList.add(map);// 播出状态信息
            }

            for(int i=0 ; i<5;i++) {
                map = new HashMap();
                map.put("name", "车辆" + i); // 名称
                map.put("status", "损坏"); // 状态
                materialInfoList.add(map);// 物资信息
            }

            resultJSON.accumulate("networkStatusList",networkStatusList);// 网络状态信息
            resultJSON.accumulate("personnelInfoList",personnelInfoList);// 人员信息
            resultJSON.accumulate("broadcastStatusList",broadcastStatusList);// 播出状态信息
            resultJSON.accumulate("materialInfoList",materialInfoList);// 物资信息
            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServletTest.middle.getBroadCastResource]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

    /**
     * 应急联系信息
     * @param stationCode
     * @return
     */
    private String getEmergencyContactInfo(String stationCode) {
        try{
            Map reqMap = new HashMap();
            JSONObject  resultJSON = new JSONObject();
            List beOnDutyList = new ArrayList();   // 总局值班信息
            List inTaiwanContactsList = new ArrayList();   // 台内联系人
            List outTaiwanCompanyInfoList = new ArrayList();   // 台外单位

            Map map = new HashMap();
            map.put("beOnDutyDate", "2019.3.2"); // 值班日期
            map.put("beOnDutyNm", "张工"); // 值班人
            map.put("beOnDutyTel", "13644088765"); // 电话
            beOnDutyList.add(map);
            map = new HashMap();
            map.put("beOnDutyDate", "2019.3.3"); // 值班日期
            map.put("beOnDutyNm", "刘工"); // 值班人
            map.put("beOnDutyTel", "13643846565"); // 电话
            beOnDutyList.add(map);
            map = new HashMap();
            map.put("beOnDutyDate", "2019.3.4"); // 值班日期
            map.put("beOnDutyNm", "王工"); // 值班人
            map.put("beOnDutyTel", "18644088765"); // 电话
            beOnDutyList.add(map);

            map = new HashMap();
            map.put("leaderNm", "李总"); // 领导姓名
            map.put("leaderTel", "13644909876"); // 领导电话
            map.put("beOnDutyTel", "010-36925564"); // 台内值班电话
            map.put("satelliteTel", "010-36922347"); // 台内卫星电话
            inTaiwanContactsList.add(map);

            map = new HashMap();
            map.put("companyNm", "572台"); // 单位名称
            map.put("companyTel", "010-36925564"); // 关联单位电话
            map.put("beOnDutyTel", "010-36922347"); // 台内武警单位电话
            inTaiwanContactsList.add(map);

            resultJSON.accumulate("beOnDutyList",beOnDutyList);// 总局值班信息
            resultJSON.accumulate("inTaiwanContactsList",inTaiwanContactsList);// 台内联系人
            resultJSON.accumulate("outTaiwanCompanyInfoList",outTaiwanCompanyInfoList);// 台内联系人
            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServletTest.middle.getBroadCastResource]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }
    /**
     * 应急事件
     * @param stationCode
     * @return
     */
    private String getEmergencyInfo(String stationCode) {
        try{
            Map reqMap = new HashMap();
            JSONObject  resultJSON = new JSONObject();

            resultJSON.accumulate("broadcastFailure",8);// 播出事故
            resultJSON.accumulate("accidentalInjury",3);// 意外伤害
            resultJSON.accumulate("publicHealth",0);// 公共卫生
            resultJSON.accumulate("naturalDisaster",0);// 自然灾害
            resultJSON.accumulate("accidentCalamity",2);// 事故灾害
            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServletTest.middle.getBroadCastResource]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

    /**
     * 近12个月每月应急事件趋势图 和 年度应急排名
     * @param paramJson
     * @param screenPosition
     * @return
     */
    private String getEmergencyMonthAndYearInfo(JSONObject paramJson, String screenPosition) {
        try{
            Map reqMap = new HashMap();
            JSONObject  resultJSON = new JSONObject();
            List monthEmergencyInfoList = new ArrayList();   // 近12个月每月应急事件趋势图
            List yearEmergencyInfoList = new ArrayList();   // 年度应急排名

            for(int i=0 ; i<12;i++){
                int random=(int)(Math.random()*10+1);
                monthEmergencyInfoList.add(500*random);  // 应急事件次数
            }
            for(int i=0;i<5;i++) {
                Map map = new HashMap();
                map.put("stationNm", 491+i+"台"); // 台名称
                map.put("times", 8-i*2);   //次数
                yearEmergencyInfoList.add(map);
            }

            resultJSON.accumulate("monthEmergencyInfoList",monthEmergencyInfoList);// 近12个月每月应急事件趋势图
            resultJSON.accumulate("yearEmergencyInfoList",yearEmergencyInfoList);// 年度应急排名
            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServletTest.middle.getBroadCastResource]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

    /**
     * 时间动态和地图坐标信息（包括报警总字数）
     * @param paramJson
     * @param screenPosition
     * @return
     */
    private String getTemporalDynamicsAndMapInfo(JSONObject paramJson, String screenPosition) {
        try{
            Map reqMap = new HashMap();
            JSONObject  resultJSON = new JSONObject();
            List mapStationaLarmInfoList = new ArrayList();   // 地图台站报警频率状态信息
            List temporalDynamicsInfoList = new ArrayList();   // 时间动态信息

            Map map = new HashMap();
            map.put("stationNm","491台"); // 台名称
            map.put("stationX","39N53");   //纬度
            map.put("stationY","116E36");  //经度
            map.put("status",0);       // 报警状态 0：偶尔报警  1：一般报警 2：报警高发
            mapStationaLarmInfoList.add(map);

            map.put("stationNm","542台"); // 台名称
            map.put("stationX","39N45");   //纬度
            map.put("stationY","116E14");  //经度
            map.put("status",1);      // 报警状态 0：偶尔报警  1：一般报警 2：报警高发
            mapStationaLarmInfoList.add(map);

            map.put("stationNm","582台"); // 台名称
            map.put("stationX","39N56");   //纬度
            map.put("stationY","116E21");  //经度
            map.put("status",2);       // 报警状态 0：偶尔报警  1：一般报警 2：报警高发
            mapStationaLarmInfoList.add(map);

            map.put("stationNm","发射二台"); // 台名称
            map.put("stationX","39N55");   //纬度
            map.put("stationY","116E23");  //经度
            map.put("status",0);        // 报警状态 0：偶尔报警  1：一般报警 2：报警高发
            mapStationaLarmInfoList.add(map);

            for(int i=1;i<8;i++) {
                Map map1 = new HashMap();
                map1.put("stationNm", 491+i+"台"); // 台名称
                map1.put("eventNm", "应急故障事件");   //事件名称
                map1.put("eventDesc", "台站系统宕机");  //事件描述
                map1.put("eventDate", "2018/1/8 19:01:01");  //日期
                map1.put("processFlag", i);       // 流程 1：接报警情 2：启动 3：请示汇报 4：请示保障 5：方案制定 6：方案实施 7：结束
                temporalDynamicsInfoList.add(map1);
            }


            resultJSON.accumulate("yearTotalLarmCountList","0033363");// 年度监测报警次数
            resultJSON.accumulate("mapStationaLarmInfoList",mapStationaLarmInfoList);// 地图台站报警频率状态信息
            resultJSON.accumulate("temporalDynamicsInfoList",temporalDynamicsInfoList);// 时间动态信息
            return resultJSON.toString();
        }catch (Exception e){
            logger.error("[SecondThemeServletTest.middle.getBroadCastResource]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }



}
