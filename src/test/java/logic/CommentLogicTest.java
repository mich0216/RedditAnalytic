package logic;

import common.TomcatStartUp;
import dal.EMFactory;
import entity.Comment;
import entity.Post;
import entity.RedditAccount;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Shariar
 */
class CommentLogicTest {

    private CommentLogic logic;
    private Comment expectedEntity;

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

        logic = LogicFactory.getFor( "Comment" );
        /* **********************************
         * ***********IMPORTANT**************
         * **********************************/
        //we only do this for the test.
        //always create Entity using logic.
        //we manually make the account to not rely on any logic functionality , just for testing
        Comment entity = new Comment();
        entity.setUniqueId("junit");
        Date testDate =  new Date();
        entity.setCreated(testDate);
        entity.setIsReply(false);
        entity.setPoints(5);
        entity.setText("junit5");
        entity.setReplys(2);
        
        // asume the post and rediit accoont (id =1) is going to be there
        // otherwize agree with othe student in the group to use a specific
        // string field with a specific value like junit 
        RedditAccountLogic redditLogic = LogicFactory.getFor("RedditAccount");
        PostLogic postLogic = LogicFactory.getFor("Post");
        Post post = postLogic.getWithId(1);
        RedditAccount redit = redditLogic.getWithId(1);
        
        entity.setPostId(post);
        entity.setRedditAccountId(redit);
     
        //get an instance of EntityManager
        EntityManager em = EMFactory.getEMF().createEntityManager();
        //start a Transaction
        em.getTransaction().begin();
       
        //add an comment to hibernate, comment is now managed.
        //we use merge instead of add so we can get the updated generated ID.
        expectedEntity = em.merge( entity );
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
        //get all the comments from the DB
        List<Comment> list = logic.getAll();
        //store the size of list, this way we know how many comments exits in DB
        int originalSize = list.size();

        //make sure comment was created successfully
        assertNotNull( expectedEntity );
        //delete the new account
        logic.delete( expectedEntity );

        //get all comments again
        list = logic.getAll();
        //the new size of comments must be one less
        assertEquals( originalSize - 1, list.size() );
    }

    /**
     * helper method for testing all comment fields
     *
     * @param expected
     * @param actual
     */
    private void assertCommentEquals( Comment expected, Comment actual ) {
        //assert all field to guarantee they are the same
        assertEquals( expected.getId(), actual.getId() );
        assertEquals( expected.getPoints(), actual.getPoints() );
        assertEquals( expected.getReplys(), actual.getReplys() );
        assertEquals( expected.getText(), actual.getText() );
        assertEquals( expected.getUniqueId(), actual.getUniqueId());
        assertEquals( expected.getIsReply(), actual.getIsReply());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
        String strExpectedDate = formatter.format(expected.getCreated());  
        String strActualDate = formatter.format(actual.getCreated());  
        assertEquals( strExpectedDate, strActualDate );
        
    }

    @Test
    final void testGetWithId() {
      
        Comment returnedAccount = logic.getWithId( expectedEntity.getId() );
        assertCommentEquals( expectedEntity, returnedAccount );
    }

    @Test
    final void testgetCommentWithUniqueId() {
       // Comment returnedComment = logic.getCommentithUniqueId(expectedEntity.getUniqueId());
       // assertCommentEquals( expectedEntity, returnedComment );
    }



    @Test
    final void testgetCommentsWithText() {
        int foundFull = 0;
        List<Comment> returnedComments = logic.getCommentsWithText(expectedEntity.getText() );
        for( Comment comment: returnedComments ) {
            
            assertEquals( expectedEntity.getText(), comment.getText() );
           
            if( comment.getId().equals( expectedEntity.getId() ) ){
                assertCommentEquals( expectedEntity, comment );
                foundFull++;
            }
        }
        assertEquals( 1, foundFull, "if zero means not found, if more than one means duplicate" );
    }
    
     @Test
    final void testgetCommentsWithPoints() {
        int foundFull = 0;
        List<Comment> returnedComments = logic.getCommentsWithPoints(expectedEntity.getPoints() );
        for( Comment comment: returnedComments ) {
            
            assertEquals( expectedEntity.getPoints(), comment.getPoints() );
            
            if( comment.getId().equals( expectedEntity.getId() ) ){
                assertCommentEquals( expectedEntity, comment );
                foundFull++;
            }
        }
        assertEquals( 1, foundFull, "if zero means not found, if more than one means duplicate" );
    }
    
    @Test
    final void testgetCommentsWithReplys() {
        int foundFull = 0;
        List<Comment> returnedComments = logic.getCommentsWithReplys(expectedEntity.getReplys() );
        for( Comment comment: returnedComments ) {
          
            assertEquals( expectedEntity.getReplys(), comment.getReplys() );
           
            if( comment.getId().equals( expectedEntity.getId() ) ){
                assertCommentEquals( expectedEntity, comment );
                foundFull++;
            }
        }
        assertEquals( 1, foundFull, "if zero means not found, if more than one means duplicate" );
    }
    
    @Test
    final void testgetCommentsWithIsReply() {
        int foundFull = 0;
        List<Comment> returnedComments = logic.getCommentsWithIsReply(expectedEntity.getIsReply() );
        for( Comment comment: returnedComments ) {
          
            assertEquals( expectedEntity.getIsReply(), comment.getIsReply() );
           
            if( comment.getId().equals( expectedEntity.getId() ) ){
                assertCommentEquals( expectedEntity, comment );
                foundFull++;
            }
        }
        assertEquals( 1, foundFull, "if zero means not found, if more than one means duplicate" );
    }
    
    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        // 
        assertEquals( Arrays.asList( "ID", "Unique_ID", "Text", "Created","Points","Replys","Is_Reply","Post_ID", "Reddit_Account_ID" ), list );
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        //ID, UNIQUE_ID, TEXT, CREATED,POINTS,REPLYS,IS_REPLY,POST_ID, REDDIT_ACCOUNT_ID 
        assertEquals( Arrays.asList( CommentLogic.ID, CommentLogic.UNIQUE_ID,CommentLogic.TEXT, CommentLogic.CREATED, CommentLogic.POINTS , CommentLogic.REPLYS, CommentLogic.IS_REPLY, CommentLogic.POST_ID, CommentLogic.REDDIT_ACCOUNT_ID), list );
    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList( expectedEntity );
        assertEquals( expectedEntity.getId(), list.get( 0 ) );
        assertEquals( expectedEntity.getUniqueId(), list.get( 1 ) );
        assertEquals( expectedEntity.getText(), list.get( 2 ) );
        assertEquals( expectedEntity.getCreated(), list.get( 3 ) );
        assertEquals( expectedEntity.getPoints(), list.get( 4 ) );
        assertEquals( expectedEntity.getReplys(), list.get( 5 ) );
        assertEquals( expectedEntity.getIsReply(), list.get( 6 ) );
     
    }
  

}
