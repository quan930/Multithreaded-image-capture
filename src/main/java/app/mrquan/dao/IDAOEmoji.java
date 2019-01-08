package app.mrquan.dao;

import java.util.List;

public interface IDAOEmoji {
    /**
     * 解析url 获取图片url 地址
     * @param url 需要解析的url
     * @return 图片url 地址
     */
    List<String> getEmojis(String url);
}
