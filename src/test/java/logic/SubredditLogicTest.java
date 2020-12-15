/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.TomcatStartUp;
import common.ValidationException;
import dal.EMFactory;
import entity.Subreddit;
import java.util.Arrays;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author vetty
 */
public class SubredditLogicTest {
    private SubredditLogic logic;
    private Subreddit expectedEntity;

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

        logic = LogicFactory.getFor( "Subreddit" );
        /* **********************************
         * ***********IMPORTANT**************
         * **********************************/
        //we only do this for the test.
        //always create Entity using logic.
        //we manually make the account to not rely on any logic functionality , just for testing
        Subreddit entity = new Subreddit();
        entity.setName("Junit 5 Test" );
        entity.setSubscribers(10);
        entity.setUrl("google.com");
        entity.setId(12);

        //get an instance of EntityManager
        EntityManager em = EMFactory.getEMF().createEntityManager();
        //start a Transaction
        em.getTransaction().begin();
        //add an account to hibernate, account is now managed.
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
        //get all the accounts from the DB
        List<Subreddit> list = logic.getAll();
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
    private void assertAccountEquals( Subreddit expected, Subreddit actual ) {
        //assert all field to guarantee they are the same
        assertEquals( expected.getId(), actual.getId() );
        assertEquals( expected.getName(), actual.getName() );
        assertEquals( expected.getSubscribers(), actual.getSubscribers());
        assertEquals( expected.getUrl(), actual.getUrl() );
    }

    @Test
    final void testGetWithId() {
        //using the id of test account get another account from logic
        Subreddit returnedAccount = logic.getWithId( expectedEntity.getId() );

        //the two accounts (testAcounts and returnedAccounts) must be the same
        assertAccountEquals( expectedEntity, returnedAccount );
    }

    @Test
    final void testGetAccountWithName() {
        Subreddit returnedAccount = logic.getSubredditWithName(expectedEntity.getName() );

        //the two accounts (testAcounts and returnedAccounts) must be the same
        assertAccountEquals( expectedEntity, returnedAccount );
    }

    @Test
    final void testGetSubscribers() {
        List<Subreddit> returnedAccounts =  logic.getSubredditsWithSubscribers(expectedEntity.getSubscribers());

        //the two accounts (testAcounts and returnedAccounts) must be the same
        for( Subreddit account: returnedAccounts ) {
            assertAccountEquals( expectedEntity, account );
        }
    }


    @Test
    final void testGetAccountsWithLinkPoints() {
        Subreddit returnedAccount = logic.getSubredditWithUrl(expectedEntity.getUrl());
            //all accounts must have the same password
//            assertEquals( expectedEntity.getUrl(), returnedAccount.getUrl());
    }




    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put( SubredditLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
        sampleMap.put( SubredditLogic.NAME, new String[]{ expectedEntity.getName() } );
        sampleMap.put( SubredditLogic.SUBSCRIBERS, new String[]{ String.valueOf(expectedEntity.getSubscribers())});
        sampleMap.put( SubredditLogic.URL, new String[]{ String.valueOf(expectedEntity.getUrl() )} );

        Subreddit returnedAccount = logic.createEntity( sampleMap );

        assertAccountEquals( expectedEntity, returnedAccount );
    }

    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( SubredditLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
            map.put( SubredditLogic.NAME, new String[]{ expectedEntity.getName() } );
            map.put( SubredditLogic.SUBSCRIBERS, new String[]{  String.valueOf(expectedEntity.getSubscribers()) } );
            map.put( SubredditLogic.URL, new String[]{  String.valueOf(expectedEntity.getUrl()) } );
        };

        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace( SubredditLogic.ID, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( SubredditLogic.ID, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( SubredditLogic.NAME, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( SubredditLogic.NAME, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( SubredditLogic.URL, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( SubredditLogic.URL, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( SubredditLogic.SUBSCRIBERS, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( SubredditLogic.SUBSCRIBERS, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
    }

    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( SubredditLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
            map.put( SubredditLogic.NAME, new String[]{ expectedEntity.getName() } );
            map.put( SubredditLogic.URL, new String[]{ String.valueOf(expectedEntity.getUrl()) } );
            map.put( SubredditLogic.SUBSCRIBERS, new String[]{ String.valueOf(expectedEntity.getSubscribers()) } );
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
        sampleMap.replace( SubredditLogic.ID, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( SubredditLogic.ID, new String[]{ "12b" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( SubredditLogic.NAME, new String[]{ "" } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( SubredditLogic.NAME, new String[]{ generateString.apply( 101 ) } );
        assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
    }

    @Test
    final void testCreateEntityEdgeValues() {
        IntFunction<String> generateString = ( int length ) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            return new Random().ints( 'a', 'z' + 1 ).limit( length )
                    .collect( StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append )
                    .toString();
        };

        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put( SubredditLogic.ID, new String[]{ Integer.toString( 1 ) } );
        sampleMap.put( SubredditLogic.NAME, new String[]{ generateString.apply( 1 ) } );
        sampleMap.put( SubredditLogic.URL, new String[]{ generateString.apply( 45 ) } );
        sampleMap.put( SubredditLogic.SUBSCRIBERS, new String[]{ String.valueOf((int)(Math.random()*(200-100+1)+100))  } );

        //idealy every test should be in its own method
        Subreddit returnedAccount = logic.createEntity( sampleMap );
        assertEquals( Integer.parseInt( sampleMap.get( RedditAccountLogic.ID )[ 0 ] ), returnedAccount.getId() );
        assertEquals( sampleMap.get( SubredditLogic.NAME )[ 0 ], returnedAccount.getName() );

        sampleMap = new HashMap<>();
        sampleMap.put( SubredditLogic.ID, new String[]{ Integer.toString( 2 ) } );
        sampleMap.put( SubredditLogic.NAME, new String[]{ generateString.apply( 45 ) } );
        sampleMap.put( SubredditLogic.URL, new String[]{ generateString.apply( 45 ) } );
        sampleMap.put( SubredditLogic.SUBSCRIBERS, new String[]{ Integer.toString(33) } );

        //idealy every test should be in its own method
//        returnedAccount = logic.createEntity( sampleMap );
//        assertEquals( Integer.parseInt( sampleMap.get( RedditAccountLogic.ID )[ 0 ] ), returnedAccount.getId() );
//        assertEquals( sampleMap.get( RedditAccountLogic.NAME )[ 0 ], returnedAccount.getName());
//        assertEquals( sampleMap.get( RedditAccountLogic.LINK_POINTS )[ 0 ], returnedAccount.getLinkPoints() );
//        assertEquals( sampleMap.get( RedditAccountLogic.COMMENT_POINTS )[ 0 ], returnedAccount.getCommentPoints());
    }

    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames(); 
        assertEquals( Arrays.asList( "id", "name", "url", "subscribers"), list );
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals( Arrays.asList( SubredditLogic.ID, SubredditLogic.NAME, SubredditLogic.URL, SubredditLogic.SUBSCRIBERS ), list );
    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList( expectedEntity );
        assertEquals( expectedEntity.getId(), list.get( 0 ) );
        assertEquals( expectedEntity.getName(), list.get( 1 ) );
        assertEquals( expectedEntity.getSubscribers(), list.get( 3 ) );
        assertEquals( expectedEntity.getUrl(), list.get( 2 ) );
    } 
}
