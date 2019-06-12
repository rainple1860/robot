package com.rainple.weChatRobot.utils;

import com.rainple.weChatRobot.pojo.AccessToken;
import com.rainple.weChatRobot.pojo.Music;
import com.rainple.weChatRobot.pojo.MusicMessage;
import com.rainple.weChatRobot.pojo.TextMessage;
import com.thoughtworks.xstream.XStream;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageUtil {

    public static final String MESSAGE_TEXT = "text";
    public static final String MESSAGE_NEWS = "news";
    public static final String MESSAGE_IMAGE = "image";
    public static final String MESSAGE_VOICE = "voice";
    public static final String MESSAGE_MUSIC = "music";
    public static final String MESSAGE_VIDEO = "video";
    public static final String MESSAGE_LINK = "link";
    public static final String MESSAGE_LOCATION = "location";
    public static final String MESSAGE_EVNET = "event";
    public static final String MESSAGE_SUBSCRIBE = "subscribe";
    public static final String MESSAGE_UNSUBSCRIBE = "unsubscribe";
    public static final String MESSAGE_CLICK = "CLICK";
    public static final String MESSAGE_VIEW = "VIEW";
    public static final String MESSAGE_SCANCODE= "scancode_push";

    public static Map<String,String> xmlToMap(HttpServletRequest request){
        Map<String, String> map = new HashMap();
        SAXReader reader = new SAXReader();
        InputStream ins = null;
        try {
            ins = request.getInputStream();
            Document doc = reader.read(ins);

            Element root = doc.getRootElement();

            List<Element> list = root.elements();

            for(Element e : list){
                map.put(e.getName(), e.getText());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (ins != null){
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

    /**
     * 将文本消息对象转为xml
     * @param textMessage
     * @return
     */
    public static String textMessageToXml(TextMessage textMessage){
        XStream xstream = new XStream();
        xstream.alias("xml", textMessage.getClass());
        return xstream.toXML(textMessage);
    }

    /**
     * 音乐消息转为xml
     * @param musicMessage
     * @return
     */
    public static String musicMessageToXml(MusicMessage musicMessage){
        XStream xstream = new XStream();
        xstream.alias("xml", musicMessage.getClass());
        return xstream.toXML(musicMessage);
    }

    /**
     * 组装音乐消息
     * @param toUserName
     * @param fromUserName
     * @return
     */
    public static String initMusicMessage(String toUserName,String fromUserName) throws IOException, ParseException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {

        Music music = new Music();
        AccessToken accessToken = WeixinUtil.getAccessToken();
        String mediaId = WeixinUtil.upload("F:\\07206.jpg", accessToken.getToken(), "thumb");
        music.setThumbMediaId(mediaId);
        music.setTitle("see you again");
        music.setDescription("速7片尾曲");
        music.setMusicUrl("http://e6be2bf0.ngrok.io/resource/See You Again.mp3");
        music.setHQMusicUrl("http://e6be2bf0.ngrok.io/resource/See You Again.mp3");

        MusicMessage musicMessage = new MusicMessage();
        musicMessage.setFromUserName(toUserName);
        musicMessage.setToUserName(fromUserName);
        musicMessage.setMsgType(MESSAGE_MUSIC);
        musicMessage.setCreateTime(new Date().getTime());
        musicMessage.setMusic(music);
        String  message = musicMessageToXml(musicMessage);
        return message;
    }
}
