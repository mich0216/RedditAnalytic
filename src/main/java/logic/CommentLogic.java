package logic;

import common.ValidationException;
import dal.CommentDAL;
import dal.PostDAL;
import dal.RedditAccountDAL;
import entity.Comment;
import entity.Post;
import entity.RedditAccount;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;

/**
 *
 * @author Shariar (Shawn) Emami
 */
public class CommentLogic extends GenericLogic<Comment, CommentDAL> {

    /**
     * create static final variables with proper name of each column. this way you will never manually type it again,
     * instead always refer to these variables.
     *
     * by using the same name as column id and HTML element names we can make our code simpler. this is not recommended
     * for proper production project.
     * 
     *      */
    public static final String UNIQUE_ID  = "unique_id";
    public static final String TEXT = "text";
    public static final String CREATED = "created";
    public static final String POINTS = "points";
    public static final String REPLYS = "replys";
    public static final String IS_REPLY = "is_reply";
    public static final String POST_ID  = "post_id";
    public static final String REDDIT_ACCOUNT_ID  = "reddit_account_id";
   
    public static final String ID = "id";

    CommentLogic() {
        super( new CommentDAL() );
    }

    @Override
    public List<Comment> getAll() {
        return get( () -> dal().findAll() );
    }

    @Override
    public Comment getWithId( int id ) {
        return get( () -> dal().findById( id ) );
    }
   

    /**
     * this method is used to send a list of all names to be used form table column headers. by having all names in one
     * location there is less chance of mistakes.
     *
     * this list must be in the same order as getColumnCodes and extractDataAsList
     *
     * @return list of all column names to be displayed.
     */
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList( "ID", "Unique_ID", "Text", "Created","Points","Replys","Is_Reply","Post_ID", "Reddit_Account_ID" );
    }

    /**
     * this method returns a list of column names that match the official column names in the db. by having all names in
     * one location there is less chance of mistakes.
     *
     * this list must be in the same order as getColumnNames and extractDataAsList
     *
     * @return list of all column names in DB.
     */
    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList( ID, UNIQUE_ID, TEXT, CREATED,POINTS,REPLYS,IS_REPLY,POST_ID, REDDIT_ACCOUNT_ID );
    }

    /**
     * return the list of values of all columns (variables) in given entity.
     *
     * this list must be in the same order as getColumnNames and getColumnCodes
     *
     * @param e - given Entity to extract data from.
     *
     * @return list of extracted values
     */
    @Override
    public List<?> extractDataAsList( Comment e ) {
        return Arrays.asList( e.getId(), e.getUniqueId(), e.getText(), e.getCreated(),
                e.getPoints(),e.getReplys(),e.getIsReply(),e.getPostId().getId(),e.getRedditAccountId().getId());
    }

    @Override
    public Comment createEntity(Map<String, String[]> parameterMap) {
      //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    
       Objects.requireNonNull( parameterMap, "parameterMap cannot be null" );
       
        Comment entity = new Comment();
        ////ID, UNIQUE_ID, TEXT, CREATED,POINTS,REPLYS,IS_REPLY,POST_ID, REDDIT_ACCOUNT_ID )
        
        String uniqe_id = parameterMap.get( UNIQUE_ID)[ 0 ];
        String txt = parameterMap.get( TEXT )[ 0 ];
        Date created = new Date();
        
        int points = Integer.parseInt(parameterMap.get( POINTS)[ 0 ]);
        int replys = Integer.parseInt(parameterMap.get( REPLYS )[ 0 ]);
        int isreplyInt = Integer.parseInt(parameterMap.get( IS_REPLY )[ 0 ]);
        boolean is_reply = (isreplyInt==1)?true:false;
        
        int postId = Integer.parseInt(parameterMap.get( POST_ID)[ 0 ]);
        int reditId = Integer.parseInt(parameterMap.get( REDDIT_ACCOUNT_ID)[ 0 ]);
        
        entity.setCreated(created);
        entity.setUniqueId(uniqe_id);
        entity.setIsReply(is_reply);
        entity.setText(txt);
        entity.setPoints(points);
        entity.setReplys(replys);
        
        
       // PostLogic postLogic = LogicFactory.getFor("Post");
      //  RedditAccountLogic redditLogic = LogicFactory.getFor("RedditAccount");
        
        PostDAL postDal = new PostDAL();
        RedditAccountDAL reditDal = new RedditAccountDAL();
         
       // Post post = postLogic.getWithId(postId);
       // RedditAccount redditAccount = redditLogic.getWithId(reditId);
        
         Post post = postDal.findById(postId);
        RedditAccount redditAccount = reditDal.findById(postId);
        
        
        
        entity.setPostId(post);
        entity.setRedditAccountId(redditAccount);
      

        return entity;
    
    }
}
