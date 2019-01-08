package app.mrquan.util;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 图片处理类
 * @author daquan
 *
 */
public class ImageHandleHelper {
    /**
     * url 转BufferedImage list
     * @param urls 图片地址 list
     * @return bufferedImage list
     */
    public List<BufferedImage> getImages(List<String> urls){
        long start = new Date().getTime();
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");////设置https协议访问
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(80);//定长线程池
//        BlockingQueue<BufferedImage> queue = new LinkedBlockingQueue<>();
        BlockingQueue<BufferedImage> queue = new ArrayBlockingQueue<>(urls.size());
        AtomicInteger m = new AtomicInteger();//计数器 线程安全
        urls.forEach((u)-> fixedThreadPool.execute(() -> {
            try {
                URL url = new URL(u);
                URLConnection connection = url.openConnection();
//                connection.setRequestProperty("Content-type", "application/json");//SocketException: Unexpected end of file from server
                InputStream in = connection.getInputStream();
                queue.add(ImageIO.read(in));
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                m.addAndGet(1);
//                System.out.println(m.get());
            }
        }));
        while (m.get()!=urls.size()){

        }
        fixedThreadPool.shutdown();
        long end = new Date().getTime();
        System.out.println("url解析bufferImage:"+(end-start)+"\t数据量:"+queue.size());
        return new ArrayList<>(queue);
    }

    /**
     * 合并图片 矩形合并
     * @param images 要拼接的图片 list
     * @return 拼接后的图片
     */
    public BufferedImage mergeImage(List<BufferedImage> images){
        long start = new Date().getTime();
        BufferedImage bufferedImage = null;
        int max = images.stream().max(Comparator.comparingInt(BufferedImage::getWidth)).get().getWidth();//获取最大尺寸
        for (int i = 0; i < images.size(); i++) {//将图片尺寸统一
            if (images.get(i).getHeight()!=max){
                images.set(i,resizeBufferedImage(images.get(i),max,max,true));//更改尺寸
            }
        }
        int row = (int)Math.sqrt(images.size())+1;
        int i=0,j=0;
        List<BufferedImage> newImageList = new ArrayList<>();
        while (i<images.size()){
            for (j = 0; j < row&&i < images.size(); j++,i++) { }
            newImageList.add(mergeImage(images.subList(i-j,i),1));//横向拼接
        }
        bufferedImage = mergeImage(newImageList,2);//纵向拼接
        System.out.println("图片拼接完成:"+(new Date().getTime()-start));
        return bufferedImage;
    }

    /**
     *
     * @param images 要拼接的图片
     * @param type 1 横向拼接， 2 纵向拼接
     * @return 拼接后的图片
     */
    private BufferedImage mergeImage(List<BufferedImage> images, int type) {
        if (images.size() < 1) {
            throw new RuntimeException("图片数量小于1");
        }
        int[][] imageArrays = new int[images.size()][];
        for (int i = 0; i < images.size(); i++) {
            int width = images.get(i).getWidth();
            int height = images.get(i).getHeight();
            imageArrays[i] = new int[width * height];
            imageArrays[i] = images.get(i).getRGB(0, 0, width, height, imageArrays[i], 0, width);
        }
        int newHeight = 0;
        int newWidth = 0;
        for (BufferedImage image : images) {
            // 横向
            if (type == 1) {
                newHeight = newHeight > image.getHeight() ? newHeight : image.getHeight();
                newWidth += image.getWidth();
            } else if (type == 2) {// 纵向
                newWidth = newWidth > image.getWidth() ? newWidth : image.getWidth();
                newHeight += image.getHeight();
            }
        }
        if (type == 1 && newWidth < 1) {
            return null;
        }
        if (type == 2 && newHeight < 1) {
            return null;
        }
        // 生成新图片
        BufferedImage imageNew = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        int height_i = 0;
        int width_i = 0;
        int i = 0;
        for (i = 0; i < images.size(); i++) {
            if (type == 1) {
                //填充没有的颜色
                imageNew.setRGB(width_i, 0,images.get(i).getWidth(),imageNew.getHeight(), imageArrays[i], 0, images.get(i).getWidth());
                width_i += images.get(i).getWidth();
            } else if (type == 2) {
                imageNew.setRGB(0, height_i, images.get(i).getWidth(), images.get(i).getHeight(), imageArrays[i], 0,images.get(i).getWidth());
                height_i += images.get(i).getHeight();
            }
        }
        return imageNew;
    }

    /**
     * 调整bufferedimage大小
     * @param source BufferedImage 原始image
     * @param targetW int  目标宽
     * @param targetH int  目标高
     * @param flag boolean 是否同比例调整
     * @return BufferedImage  返回新image
     */
    private BufferedImage resizeBufferedImage(BufferedImage source, int targetW, int targetH, boolean flag) {
        int type = source.getType();
        BufferedImage target = null;
        double sx = (double) targetW / source.getWidth();
        double sy = (double) targetH / source.getHeight();
        if (flag && sx > sy) {
            sx = sy;
            targetW = (int) (sx * source.getWidth());
        } else if(flag && sx <= sy){
            sy = sx;
            targetH = (int) (sy * source.getHeight());
        }
        if (type == BufferedImage.TYPE_CUSTOM) { // handmade
            ColorModel cm = source.getColorModel();
            WritableRaster raster = cm.createCompatibleWritableRaster(targetW, targetH);
            boolean alphaPremultiplied = cm.isAlphaPremultiplied();
            target = new BufferedImage(cm, raster, alphaPremultiplied, null);
        } else {
            target = new BufferedImage(targetW, targetH, type);
        }
        Graphics2D g = target.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
        g.dispose();
        return target;
    }


}