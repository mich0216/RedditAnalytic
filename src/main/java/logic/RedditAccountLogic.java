/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.ValidationException;
import dal.RedditAccountDAL;
import entity.RedditAccount;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;

/**
 *
 * @author chrish
 */
public class RedditAccountLogic extends GenericLogic<RedditAccount, RedditAccountDAL> {
    public static final  String COMMENT_POINTS = "comment points";
    public static final String LINK_POINTS = "link points";
    public static final String CREATED = "created";
    public static final String NAME = "name";
    public static String ID = "id";
    
    RedditAccountLogic(){
        super(new RedditAccountDAL());
    }
//    public RedditAccountLogic(RedditAccountDAL dal) {
//        super(dal);
//    }

    @Override
    public List<String> getColumnNames() {
         return Arrays.asList( "id", "name", "created", "link points", "comment points" );
    }

    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList( ID, NAME,CREATED, LINK_POINTS,COMMENT_POINTS );
    }

    @Override
    public List<?> extractDataAsList(RedditAccount e) {
        return Arrays.asList( e.getId(), e.getName(), e.getCreated(), e.getLinkPoints(), e.getCommentPoints());
    }

    @Override
    public RedditAccount createEntity(Map<String, String[]> parameterMap) {
                Objects.requireNonNull( parameterMap, "parameterMap cannot be null" );
        //same as if condition below
//        if (parameterMap == null) {
//            throw new NullPointerException("parameterMap cannot be null");
//        }

        //create a new Entity object
        RedditAccount entity = new RedditAccount();

        //ID is generated, so if it exists add it to the entity object
        //otherwise it does not matter as mysql will create an if for it.
        //the only time that we will have id is for update behaviour.
        if( parameterMap.containsKey( ID ) ){
            try {
                entity.setId( Integer.parseInt( parameterMap.get( ID )[ 0 ] ) );
            } catch( java.lang.NumberFormatException ex ) {
                throw new ValidationException( ex );
            }
        }

        //before using the values in the map, make sure to do error checking.
        //simple lambda to validate a string, this can also be place in another
        //method to be shared amoung all logic classes.
        ObjIntConsumer< String> validator = ( value, length ) -> {
            if( value == null || value.trim().isEmpty() || value.length() > length ){
                String error = "";
                if( value == null || value.trim().isEmpty() ){
                    error = "value cannot be null or empty: " + value;
                }
                if( value.length() > length ){
                    error = "string length is " + value.length() + " > " + length;
                }
                throw new ValidationException( error );
            }
        };

        //extract the date from map first.
        //everything in the parameterMap is string so it must first be
        //converted to appropriate type. have in mind that values are
        //stored in an array of String; almost always the value is at
        //index zero unless you have used duplicated key/name somewhere.
        //ID, NAME,CREATED, LINK_POINTS,COMMENT_POINTS
        String name = parameterMap.get( NAME )[ 0 ];
        String created = parameterMap.get( CREATED )[ 0 ];
        String link_points = parameterMap.get( LINK_POINTS )[ 0 ];
        String comment_points = parameterMap.get( COMMENT_POINTS )[ 0 ];

        //validate the data
        validator.accept( name, 45 );
        validator.accept( created, 45 );
        validator.accept( link_points, 45 );
        validator.accept( comment_points, 45 );

        //set values on entity
        entity.setName(name);
        entity.setCreated(Date.valueOf(created));
        entity.setLinkPoints(Integer.parseInt(link_points));
        entity.setCommentPoints(Integer.parseInt(comment_points));
        return entity;
    }

    @Override
    public List<RedditAccount> getAll() {
        return get( () -> dal().findAll() );
    }

    @Override
    public RedditAccount getWithId(int id) {
        return get( () -> dal().findById( id ) );
    }
    
    public RedditAccount getRedditAccountWithName(String name){
        return get( () -> dal().findByName(name));
    }
    
    public List<RedditAccount> getRedditAccountsWithLinkPoints(int linkPoints){
        return get( () -> dal().findByLinkPoints(linkPoints) );
    }
    
    public List<RedditAccount> getRedditAccountsWithCommentPoints(int commentPoints){
        return get( () -> dal().findByCommentPoints(commentPoints) );
    }
    
    public List<RedditAccount> getRedditAccountsWithCreated(Date created){
        return get( () -> dal().findByCreated(created) );
    }
}
