
package dal;

import entity.Comment;
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
    
}
