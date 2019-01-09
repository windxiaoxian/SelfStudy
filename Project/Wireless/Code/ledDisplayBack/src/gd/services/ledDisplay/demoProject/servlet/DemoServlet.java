package gd.services.ledDisplay.demoProject.servlet;

import gd.services.ledDisplay.demoProject.dao.DemoDAO;
import gd.services.ledDisplay.demoProject.dataSource.ledDisplayDB;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet implementation class MainServlet
 */
@WebServlet("/DemoServlet")
public class DemoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(DemoServlet.class.getName());

    private JSONObject demoInfo = JSONObject.fromObject("{\"param1\":\"value1\",\"param2\":\"value2\"}");

    /**
     * @see HttpServlet#HttpServlet()
     */
    public DemoServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String key = request.getParameter("key");
        String param = request.getParameter("param");
        String jsonpCallback = request.getParameter("jsonpCallback");
        logger.info("接收到接口请求：" + "key:" + key + "; param:" + param + "; jsonpCallback:" + jsonpCallback);

        JSONObject json = null;
        if (null != param && !"".equals(param)) {
            json = (JSONObject) JSONSerializer.toJSON(param);
        }

        String callbackInfo = "";
        try {
            if (null != key && "left".equals(key)) {// 主题左屏信息获取
                callbackInfo = left(json);
            } else if (null != key && "middle".equals(key)) {// 主题中屏信息获取
                callbackInfo = middle(json);
            } else if (null != key && "right".equals(key)) {// 主题右屏信息获取
                callbackInfo = right(json);
            } else {
                callbackInfo = "9999|未匹配到后台接口，请核查参数！";
            }
        } catch (Exception e) {
            logger.error("[DemoServlet]后台报错：key=" + key + e.getMessage());
            callbackInfo = "9999|后台接口报错，请核查日志！";
        }

        response.setContentType("text/plain;charset=UTF-8");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        PrintWriter out = response.getWriter();
        out.println("jsonpCallback(" + callbackInfo + ")");
        out.flush();
        out.close();
    }

    //左屏信息获取
    private String left(JSONObject json) {
        try {
            JSONObject resultJson = new JSONObject();
            /*入参*/
            Map reqMap = new HashMap();
            if (json != null) {
                reqMap.put("dbType", json.getString("dbType"));
            }

            /*回参*/
            //List 结果集
//            List resLists = (List) ledDisplayDB.qryList(DemoDAO.getToday(reqMap)).get("data");
//            for (Object resList : resLists) {
//                Map resultMap = (Map) resList;
//                resultJson.accumulate("today", resultMap.get("today"));
//            }

            //Map 结果集
            Map resMap = (HashMap) ledDisplayDB.qryMap(DemoDAO.getToday(reqMap)).get("data");
            resultJson.accumulate("today", resMap.get("today"));

            resultJson.accumulate("location", "左");
            return resultJson.toString();
        } catch (Exception e) {
            logger.error("[DemoServlet.left]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

    private String middle(JSONObject json) {
        try {
            JSONObject resultJson = new JSONObject();
            /*入参*/
            Map reqMap = new HashMap();
            if (json != null) {
                reqMap.put("dbType", json.getString("dbType"));
            }

            /*回参*/
            //List 结果集
//            List resLists = (List) ledDisplayDB.qryList(DemoDAO.getToday(reqMap)).get("data");
//            for (Object resList : resLists) {
//                Map resultMap = (Map) resList;
//                resultJson.accumulate("today", resultMap.get("today"));
//            }

            //Map 结果集
            Map resMap = (HashMap) ledDisplayDB.qryMap(DemoDAO.getToday(reqMap)).get("data");
            resultJson.accumulate("today", resMap.get("today"));

            resultJson.accumulate("location", "中");
            return resultJson.toString();
        } catch (Exception e) {
            logger.error("[DemoServlet.middle]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

    private String right(JSONObject json) {
        try {
            JSONObject resultJson = new JSONObject();
            /*入参*/
            Map reqMap = new HashMap();
            if (json != null) {
                reqMap.put("dbType", json.getString("dbType"));
            }

            /*回参*/
            //List 结果集
//            List resLists = (List) ledDisplayDB.qryList(DemoDAO.getToday(reqMap)).get("data");
//            for (Object resList : resLists) {
//                Map resultMap = (Map) resList;
//                resultJson.accumulate("today", resultMap.get("today"));
//            }

            //Map 结果集
            Map resMap = (HashMap) ledDisplayDB.qryMap(DemoDAO.getToday(reqMap)).get("data");
            resultJson.accumulate("today", resMap.get("today"));

            resultJson.accumulate("location", "右");
            return resultJson.toString();
        } catch (Exception e) {
            logger.error("[DemoServlet.right]后台报错" + e.getMessage());
            return "9999|后台接口报错，请核查日志！";
        }
    }

    //socket 通信
    private String socketConnect(JSONObject json) {
        String info = null;
        try {
            String ipInfo = "10.242.20.32";
            int portInfo = 20141;
            String reqInfo = json.getString("reqInfo");
            Socket socket = new Socket(ipInfo, portInfo);
            OutputStream os = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(os);
            pw.write(reqInfo);
            pw.flush();
            socket.shutdownOutput();
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while ((info = br.readLine()) != null) {
                logger.info("返回参数：" + info);
            }
            br.close();
            is.close();
            pw.close();
            os.close();
            socket.close();
            return "0";
        } catch (Exception e) {
            logger.error("[DemoServlet.socketConnect]后台报错" + e.getMessage());
            return "9999|Socket通信失败，请核查日志！";
        }
    }
}
