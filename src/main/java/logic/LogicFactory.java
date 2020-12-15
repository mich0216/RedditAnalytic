package logic;

//TODO this class is just a skeleton it must be completed

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class LogicFactory {
    private static String PACKAGE = "logic.";
    private static String SUFFIX = "Logic";
    public static < T> T getFor( String entityName ) {
//        if(entityName.trim().equalsIgnoreCase("Account"))
//            return (T)new AccountLogic();
//        if(entityName.trim().equalsIgnoreCase("Subreddit"))
//            return (T)new SubredditLogic();
//        if(entityName.trim().equalsIgnoreCase("RedditAccount"))
//            return (T)new RedditAccountLogic();
//        if(entityName.trim().equalsIgnoreCase("Comment"))
//            return (T)new CommentLogic();
//        if(entityName.trim().equalsIgnoreCase("Post"))
//            return (T)new PostLogic();
        try {
           Class<T> type;
            type = (Class<T>)Class.forName(PACKAGE+entityName+SUFFIX);
            return getFor(type);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LogicFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }
    
    public static < T> T getFor( Class<T> type ) {
        try {
            Constructor<T> declaredConstructor = type.getDeclaredConstructor();
            return declaredConstructor.newInstance();
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(LogicFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(LogicFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(LogicFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(LogicFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(LogicFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(LogicFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
