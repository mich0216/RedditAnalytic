
package dal;

import entity.Comment;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ahmed_8ll428a
 */
public class CommentDAL extends GenericDAL<Comment> {
    
    public CommentDAL()
    {
        super( Comment.class ); 
    }

    @Override
    public List<Comment> findAll() {
        return findResults( "Comment.findAll", null );
    }

    @Override
    public Comment findById(int id) {
        Map<String, Object> map = new HashMap<>();
        map.put( "id", id );//
         return findResult( "Comment.findById", map );
    }
    
  
    public List<Comment> findByIsReply(boolean isReply)
    {
         Map<String, Object> map = new HashMap<>();
         map.put("isReply", isReply);
         return findResults( "Comment.findByIsReply", map );
    }
    
    public List<Comment> findByPoints(int points)
    {
         Map<String, Object> map = new HashMap<>();
         map.put("points", points);
         return findResults( "Comment.findByPoints", map );
        
    }
    
     public List<Comment> findByReplys(int replys)
    {
         Map<String, Object> map = new HashMap<>();
         map.put("replys", replys);
         return findResults( "Comment.findByReplys", map );
        
    }
    
    public List<Comment> findByCreated(Date created)
    {
         Map<String, Object> map = new HashMap<>();
         map.put("created", created);
         return findResults( "Comment.findByCreated", map );
        
    }
    
    public Comment findByUniqueId(String uniqueId)
    {
         Map<String, Object> map = new HashMap<>();
         map.put("uniqueId", uniqueId);
         return findResult( "Comment.findByUniqueId", map );
        
    }
    
     public List<Comment> findByText(String text)
    {
         Map<String, Object> map = new HashMap<>();
         map.put("text", text);
         return findResults( "Comment.findByText", map );
        
    }
    
    
    
}
