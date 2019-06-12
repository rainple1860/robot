package com.rainple.weChatRobot.httpClient;


import com.rainple.weChatRobot.pojo.TextMessage;
import com.rainple.weChatRobot.utils.MessageUtil;
import com.rainple.weChatRobot.utils.SignUtil;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

public class WeChatHttp extends HttpServlet{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 微信加密签名
        String signature = request.getParameter("signature");
        System.out.println(signature);
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");
        // 随机字符串
        String echostr = request.getParameter("echostr");

        PrintWriter out = response.getWriter();

        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        if (SignUtil.checkSignature(signature, timestamp, nonce)) {
            out.print(echostr);
        }

        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter writer = resp.getWriter();
        try {
            Map<String, String> map = MessageUtil.xmlToMap(req);
            String fromUserName = map.get("FromUserName");
            String toUserName = map.get("ToUserName");
            String msgType = map.get("MsgType");
            String content = map.get("Content");

            String msgStr = "";
            if ("text".equals(msgType)) {
                if ("1".equals(content)){
                    msgStr = MessageUtil.initMusicMessage(toUserName,fromUserName);
                    System.out.println(msgStr);
                }else if ("2".equals(content)){
                    TextMessage message = new TextMessage();
                    message.setFromUserName(toUserName);
                    message.setToUserName(fromUserName);
                    message.setCreateTime(new Date().getTime());
                    message.setMsgType("text");
                    message.setContent("收到消息 ：" + content);
                    msgStr = MessageUtil.textMessageToXml(message);
                }
            }
            writer.print(msgStr);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            writer.close();
        }

    }
}
