/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.TomcatStartUp;
import common.ValidationException;
import dal.EMFactory;
import entity.Post;
import entity.RedditAccount;
import entity.Subreddit;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sabih
 */

class PostLogicTest {

    private PostLogic logic;
    private RedditAccountLogic rlogic;
    private Post expectedEntity;

    @BeforeAll
    final static void setUpBeforeClass() throws Exception {
        TomcatStartUp.createTomcat( "/RedditAnalytic", "common.ServletListener" );
    }

    @AfterAll
    final static void tearDownAfterClass() throws Exception {
        TomcatStartUp.stopAndDestroyTomcat();
    }

    @BeforeEach
    final void setUp() throws Exception {

        logic = LogicFactory.getFor( "Post" );
        rlogic = LogicFactory.getFor( "RedditAccount" );
        /* **********************************
         * ***********IMPORTANT**************
         * **********************************/
        //we only do this for the test.
        //always create Entity using logic.
        //we manually make the post to not rely on any logic functionality , just for testing
        Post post = new Post();
        post.setUniqueId("eee");
       // post.setId(10);
        post.setTitle( "Junit 5 Test" );
        post.setCreated(new Date());
        post.setPoints(55);
        post.setCommentCount(55);
        
        RedditAccountLogic redditLogic = LogicFactory.getFor("RedditAccount");
        SubredditLogic subredditLogic = LogicFactory.getFor("Subreddit");
        Subreddit sr = subredditLogic.getWithId(1);
        RedditAccount ra = redditLogic.getWithId(1);
        
        post.setRedditAccountId(ra);
        post.setSubredditId(sr);
        //get an instance of EntityManager
        EntityManager em = EMFactory.getEMF().createEntityManager();
        //start a Transaction
        em.getTransaction().begin();
        //add an account to hibernate, account is now managed.
        //we use merge instead of add so we can get the updated generated ID.
        expectedEntity = em.merge( post );
        //commit the changes
        em.getTransaction().commit();
        //close EntityManager
        em.close();
        
    }

 @AfterEach
    final void tearDown() throws Exception {
        if( expectedEntity != null ){
            logic.delete( expectedEntity );
        }
    }

    @Test
    final void testGetAll() {
        //get all the accounts from the DB
        List<Post> list = logic.getAll();
        //store the size of list, this way we know how many accounts exits in DB
        int originalSize = list.size();

        //make sure account was created successfully
        assertNotNull( expectedEntity );
        //delete the new account
        logic.delete( expectedEntity );

        //get all accounts again
        list = logic.getAll();
        //the new size of accounts must be one less
        assertEquals( originalSize - 1, list.size() );
    }

    /**
     * helper method for testing all account fields
     *
     * @param expected
     * @param actual
     */
    private void assertPosttEquals( Post expected, Post actual ) {
        //assert all field to guarantee they are the same
        assertEquals( expected.getId(), actual.getId() );
        assertEquals( expected.getUniqueID(), actual.getUniqueID() );
        assertEquals( expected.getTitle(), actual.getTitle() );
        assertEquals( expected.getCreated(), actual.getCreated() );
        assertEquals( expected.getPoints(), actual.getPoints() );
        assertEquals( expected.getCommentCount(), actual.getCommentCount() );
        assertEquals( expected.getRedditAccountId(), actual.getRedditAccountId() );
        assertEquals( expected.getSubredditId(), actual.getSubredditId() );
    }

    @Test
    final void testGetPostWithId() {
        //using the id of test account get another account from logic
        Post post = logic.getWithId( expectedEntity.getId() );
            //all accounts must have the same password
            assertEquals( expectedEntity.getId().intValue(), post.getId().intValue() );
    }

    @Test
    final void testGetPostWithTitle() {
        List<Post> returnedPost = logic.getPostsWithTitle( expectedEntity.getTitle() );

        for( Post post: returnedPost ) {
            //all accounts must have the same password
            assertEquals( expectedEntity.getTitle(), post.getTitle() );
            //exactly one account must be the same
  
        }  
    }
    @Test
    final void testGetPostWIthAuthorID() {
       List<Post> returnedPost = logic.getPostsWithAuthorID( expectedEntity.getRedditAccountId().getId());

       for( Post post: returnedPost ) {
            //all accounts must have the same password
            assertEquals( expectedEntity.getRedditAccountId().getId(), post.getRedditAccountId().getId() );
            //exactly one account must be the same
  
        }     
    }


    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put( PostLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
        sampleMap.put( PostLogic.UNIQUE_ID, new String[]{ expectedEntity.getUniqueID() } );
        sampleMap.put( PostLogic.TITLE, new String[]{ expectedEntity.getTitle() } );
        sampleMap.put( PostLogic.COMMENT_COUNT, new String[]{ Integer.toString(expectedEntity.getCommentCount()) } );
        sampleMap.put( PostLogic.CREATED, new String[]{ logic.convertDateToString(expectedEntity.getCreated())} );
        sampleMap.put( PostLogic.POINTS, new String[]{ Integer.toString(expectedEntity.getPoints()) } );
      

        Post returnedPost = logic.createEntity( sampleMap );

        assertPostEquals( expectedEntity, returnedPost );
    }

    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( PostLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
            map.put( PostLogic.UNIQUE_ID, new String[]{ expectedEntity.getUniqueID() } );
            map.put( PostLogic.TITLE, new String[]{ expectedEntity.getTitle() } );
            map.put( PostLogic.COMMENT_COUNT, new String[]{ Integer.toString(expectedEntity.getCommentCount()) } );
            map.put( PostLogic.CREATED, new String[]{logic.convertDateToString(expectedEntity.getCreated()) } );
            map.put( PostLogic.POINTS, new String[]{ Integer.toString(expectedEntity.getPoints()) } );
            
            
        };

        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace( PostLogic.ID, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( PostLogic.ID, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace(PostLogic.UNIQUE_ID, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace(PostLogic.UNIQUE_ID, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( PostLogic.TITLE, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( PostLogic.TITLE, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( PostLogic.COMMENT_COUNT, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( PostLogic.COMMENT_COUNT, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
        
        fillMap.accept( sampleMap );
        sampleMap.replace( PostLogic.CREATED, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( PostLogic.CREATED, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
        
        fillMap.accept( sampleMap );
        sampleMap.replace( PostLogic.POINTS, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( PostLogic.POINTS, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
        
//        fillMap.accept( sampleMap );
//        sampleMap.replace( PostLogic.REDDIT_ACCOUNT_ID, null );
//        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
//        sampleMap.replace( PostLogic.REDDIT_ACCOUNT_ID, new String[]{} );
//        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
        
//        fillMap.accept( sampleMap );
//        sampleMap.replace( PostLogic.SUBREDDIT_ID, null );
//        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
//        sampleMap.replace( PostLogic.SUBREDDIT_ID, new String[]{} );
//        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
    }

    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( PostLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
            map.put( PostLogic.UNIQUE_ID, new String[]{ expectedEntity.getUniqueID() } );
            map.put(PostLogic.TITLE, new String[]{ expectedEntity.getTitle() } );  
            map.put( PostLogic.POINTS, new String[]{ Integer.toString(expectedEntity.getPoints()) } );
            map.put( PostLogic.CREATED, new String[]{ logic.convertDateToString(expectedEntity.getCreated()) } );   
            map.put( PostLogic.COMMENT_COUNT, new String[]{ Integer.toString(expectedEntity.getCommentCount()) } );
            map.put( PostLogic.REDDIT_ACCOUNT_ID, new String[]{ (String.valueOf(expectedEntity.getRedditAccountId().getId())) } );
            map.put( PostLogic.SUBREDDIT_ID, new String[]{(String.valueOf(expectedEntity.getSubredditId().getId()) )} );
           
            
        };

        IntFunction<String> generateString = ( int length ) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            //from 97 inclusive to 123 exclusive
            return new Random().ints( 'a', 'z' + 1 ).limit( length )
                    .collect( StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append )
                    .toString();
        };

        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace( AccountLogic.ID, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( AccountLogic.ID, new String[]{ "12b" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( PostLogic.UNIQUE_ID, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( PostLogic.UNIQUE_ID, new String[]{ generateString.apply( 46 ) } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        
        fillMap.accept( sampleMap );
        sampleMap.replace( PostLogic.CREATED, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( PostLogic.CREATED, new String[]{ generateString.apply( 46 ) } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );          
        }


    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals( Arrays.asList( "id", "created", "title", "points", "comment_count", "unique_id" ,"reddit", "subreddit_id"), list );
        assertEquals(list.size(), 8);
    }

        @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals( Arrays.asList( PostLogic.ID, PostLogic.CREATED, PostLogic.TITLE, PostLogic.POINTS, PostLogic.COMMENT_COUNT,
                PostLogic.UNIQUE_ID, PostLogic.REDDIT_ACCOUNT_ID, PostLogic.SUBREDDIT_ID), list );
    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList( expectedEntity );
        assertEquals( expectedEntity.getId(), list.get( 0 ) );
    }
    
        /**
     * helper method for testing all comment fields
     *
     * @param expected
     * @param actual
     */
    private void assertPostEquals( Post expected, Post actual ) {
        //assert all field to guarantee they are the same
        assertEquals( expected.getId(), actual.getId() );
        assertEquals( expected.getPoints(), actual.getPoints() );
        assertEquals( expected.getCommentCount(), actual.getCommentCount() );
        assertEquals( expected.getTitle(), actual.getTitle() );
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
        String strExpectedDate = formatter.format(expected.getCreated());  
        String strActualDate = formatter.format(actual.getCreated());  
        assertEquals( strExpectedDate, strActualDate );
        
    }
}
