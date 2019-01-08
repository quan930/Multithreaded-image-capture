package app.mrquan.dao.impl;

import app.mrquan.dao.IDAOEmoji;
import app.mrquan.setting.Config;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class DAOEmojiImpl implements IDAOEmoji {
    public List<String> getEmojis(String ur) {
        List<String> urls = null;
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");//设置https协议访问
        try {
            long start = new Date().getTime();
            URL url = new URL(ur);
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()),1024);
            StringBuilder builder = new StringBuilder();
            char buffer[]=new char[1024];
            int len = 0;
            while ((len=reader.read(buffer,0,1024))!= -1){
//                System.out.println(len);
                builder.append(buffer,0,len);
            }
            reader.close();
            long mid = new Date().getTime();
            System.out.println("读取完成:"+(mid-start));
//            System.out.println(builder.toString());
            JSONObject jsonObject = new JSONObject(builder.toString());
            Iterator<String> keys = jsonObject.keys();
            urls = new ArrayList<>();
            while (keys.hasNext()){
                urls.add(jsonObject.getString(keys.next()));
            }
            System.out.println("解析完成:"+(new Date().getTime()-mid)+"\t数据量:"+urls.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return urls;
    }
}