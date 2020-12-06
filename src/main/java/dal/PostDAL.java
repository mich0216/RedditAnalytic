/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

import entity.Post;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author chris
 */
public class PostDAL extends GenericDAL<Post> {

    public PostDAL() {
        super(Post.class);
    }

    @Override
    public List<Post> findAll() {

        return findResults("Post.findAll", null);
    }

    @Override
    public Post findById(int id) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
//first argument is a name given to a named query defined in appropriate entity
//second argument is map used for parameter substitution.

//parameters are names starting with : in named queries, :[name]
//in this case the parameter is named "id" and value for it is put in map
        return findResult("Post.findById", map);

    }

    public Post findByUniqueId(String uniqueId) {
        Map<String, Object> map = new HashMap<>();
        map.put("unique_id", uniqueId);
        return findResult("Post.findByUniqueId", map);
    }

    public List<Post> findByPoints(int points) {
        Map<String, Object> map = new HashMap<>();
        map.put("points", points);
        return (List<Post>) findResult("Post.findByPoints", map);
    }

    public List<Post> findByCommentCount(int commentCount) {
        Map<String, Object> map = new HashMap<>();
        map.put("comment_count", commentCount);
        return findResults("Post.findByCommentCount", map);
    }

    public List<Post> findByTitle(String title) {
        Map<String, Object> map = new HashMap<>();
        map.put("comment_count", title);
        return findResults("Post.findByTitle", map);
    }

    public List<Post> findByCreated(String created) {
        Map<String, Object> map = new HashMap<>();
        map.put("created", created);
        return findResults("Post.findByCreated", map);
    }

    public List<Post> findByAuthor(int id) {
        Map<String, Object> map = new HashMap<>();
        map.put("Author", id);
        return findResults("Post.findByAuthor", map);
    }

}
