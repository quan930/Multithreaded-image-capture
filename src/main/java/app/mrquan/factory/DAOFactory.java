package app.mrquan.factory;

import app.mrquan.dao.IDAOEmoji;
import app.mrquan.dao.impl.DAOEmojiImpl;

public class DAOFactory {
    public static IDAOEmoji getEmojis(){
        return new DAOEmojiImpl();
    }
}
