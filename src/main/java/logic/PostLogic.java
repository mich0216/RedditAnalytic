/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package logic;

import common.ValidationException;
import dal.PostDAL;
import entity.Post;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;

/**
 *
 * @author sabiha
 */
public class PostLogic extends GenericLogic<Post, PostDAL> {

    public static final String CREATED = "created";
    public static final String TITLE = "title";
    public static final String COMMENT_COUNT = "comment_count";
    public static final String POINTS = "points";
    public static final String ID = "id";
    public static final String UNIQUE_ID = "unique_id";
    public static final String REDDIT_ACCOUNT_ID = "reddit";
    public static final String SUBREDDIT_ID = "subreddit_id";

    PostLogic() {
        super(new PostDAL());
    }

    @Override

    public List<Post> getAll() {
        return get(() -> dal().findAll());
    }

    @Override
    public Post getWithId(int id) {
        return get(() -> dal().findById(id));
    }

    public Post getPostWithUniqueId(String uniqueId) {
        return get(() -> dal().findByUniqueId(uniqueId));
    }

    public List<Post> getPostWithPoints(int points) {
        return get(() -> dal().findByPoints(points));
    }

    public List<Post> getPostsWithCommentCount(int commentCount) {
        return get(() -> dal().findByCommentCount(commentCount));
    }

    public List<Post> getPostsWithAuthorID(int id) {
        return get(() -> dal().findByAuthor(id));
    }

    public List<Post> getPostsWithTitle(String title) {
        return get(() -> dal().findByTitle(title));
    }

    public List<Post> getPostsWithCreated(Date created) {
        return get(() -> dal().findByCreated(convertDateToString(created)));
    }

    @Override
    public Post createEntity(Map<String, String[]> parameterMap) {

        Objects.requireNonNull(parameterMap, "parameterMap cannot be null");
        Post entity = new Post();
        if (parameterMap.containsKey(ID)) {
            try {
                entity.setId(Integer.parseInt(parameterMap.get(ID)[0]));

            } catch (java.lang.NumberFormatException ex) {
                throw new ValidationException(ex);
            }
        }
        ObjIntConsumer< String> validator = (value, length) -> {
            if (value == null || value.trim().isEmpty() || value.length() > length) {
                String error = "";
                if (value == null || value.trim().isEmpty()) {
                    error = "value cannot be null or empty: " + value;
                }
                if (value.length() > length) {
                    error = "string length is " + value.length() + " > " + length;
                }
                throw new ValidationException(error);
            }
        };
        String title = parameterMap.get(TITLE)[0];
        String id = parameterMap.get(ID)[0];
        String created = parameterMap.get(CREATED)[0];
        String points = parameterMap.get(POINTS)[0];
        String comment_count = parameterMap.get(COMMENT_COUNT)[0];
        String unique_id = parameterMap.get(UNIQUE_ID)[0];

        validator.accept(title, 255);
        validator.accept(unique_id, 10);

//set values on entity
        entity.setTitle(title);
        entity.setId(Integer.parseInt(parameterMap.get(ID)[0]));
        entity.setCreated(convertStringToDate(parameterMap.get(CREATED)[0]));
        entity.setPoints(Integer.parseInt(parameterMap.get(POINTS)[0]));
        entity.setCommentCount(Integer.parseInt(parameterMap.get(COMMENT_COUNT)[0]));
        entity.setUniqueId(unique_id);

        return entity;

    }

    @Override
    public List<String> getColumnNames() {
        return Arrays.asList("id", "created", "title", "points","comment_count", "unique_id", "reddit",
                "subreddit_id");
    }

    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList(ID, CREATED, TITLE, POINTS, COMMENT_COUNT, UNIQUE_ID,
                REDDIT_ACCOUNT_ID, SUBREDDIT_ID);
    }

    @Override
    public List<?> extractDataAsList(Post e) {
        return Arrays.asList(e.getId(), e.getCreated(), e.getTitle(), e.getPoints(), e.getCommentCount(),e.getUniqueID(),
                e.getRedditAccountId().getId(), e.getSubredditId().getId());
    }

}
