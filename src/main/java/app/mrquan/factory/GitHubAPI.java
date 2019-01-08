package app.mrquan.factory;

import app.mrquan.setting.Config;
import app.mrquan.util.ImageHandleHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class GitHubAPI {
    /**
     * 抓取https://api.github.com/emojis图片并拼接成一张图片
     * @param path 文件路径
     */
    public static void getEmojis(String path){
        try {
            ImageHandleHelper handleHelper = new ImageHandleHelper();
            List<String> urls = DAOFactory.getEmojis().getEmojis(Config.url(Config.EMOJIS));//获取图片url
            List<BufferedImage> images = handleHelper.getImages(urls);//url转为 BufferedImage
            BufferedImage bufferedImage = handleHelper.mergeImage(images);//图片拼接
            File outputfile  = new File(path);
            ImageIO.write(bufferedImage,"png",outputfile);//图片写入
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
