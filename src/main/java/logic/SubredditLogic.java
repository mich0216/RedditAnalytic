/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.ValidationException;
import dal.SubredditDAL;
import entity.Subreddit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;

/**
 *
 * @author vetty
 */
public class SubredditLogic extends GenericLogic<Subreddit, SubredditDAL> {

    public static final  String SUBSCRIBERS = "subscribers";
    public static final String NAME = "name";
    public static final String URL = "url";
    public static String ID = "id";
    
    SubredditLogic() {
        super(new SubredditDAL());
    }

    @Override
    public List<String> getColumnNames() {
        return Arrays.asList( "id", "name", "url", "subscribers" );
    }

    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList( ID, NAME,URL, SUBSCRIBERS );
    }

    @Override
    public List<?> extractDataAsList(Subreddit e) {
        return Arrays.asList( e.getId(), e.getName(), e.getUrl(),e.getSubscribers());
    }

    @Override
    public Subreddit createEntity(Map<String, String[]> parameterMap) {
        Objects.requireNonNull( parameterMap, "parameterMap cannot be null" );

        //create a new Entity object
        Subreddit entity = new Subreddit();

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
        //ID, NAME,URL, SUBSCRIBERS
        String name = parameterMap.get( NAME )[ 0 ];
        String url = parameterMap.get( URL )[ 0 ];
        String subscribers = parameterMap.get( SUBSCRIBERS )[ 0 ];

        //validate the data
        validator.accept( name, 100 );
        validator.accept( url, 255 );

        //set values on entity
        entity.setName(name);
        entity.setUrl(url);
        entity.setSubscribers(Integer.parseInt(subscribers));
        return entity;
    }

    @Override
    public List<Subreddit> getAll() {
        return get( () -> dal().findAll() );
    }

    @Override
    public Subreddit getWithId(int id) {
        return get( () -> dal().findById( id ) );
    }
    
    public Subreddit getSubredditWithName(String name){
       return get( () -> dal().findByName(name));
    }
    public Subreddit getSubredditWithUrl(String url){
       return get( () -> dal().findByURL(url));
    }
    
    public List<Subreddit> getSubredditsWithSubscribers(int subscribers){
       return get( () -> dal().findBySubscribers(subscribers));

    }
    
}
