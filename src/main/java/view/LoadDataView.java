/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import entity.RedditAccount;
import entity.Subreddit;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.LogicFactory;
import logic.RedditAccountLogic;
import logic.SubredditLogic;
import reddit.DeveloperAccount;
import reddit.wrapper.AccountWrapper;
import reddit.wrapper.CommentSort;
import reddit.wrapper.PostWrapper;
import reddit.wrapper.RedditWrapper;
import reddit.wrapper.SubSort;
import reddit.wrapper.SubredditWrapper;

/**
 *
 * @author chrish
 */
@WebServlet( name = "LoadDataView", urlPatterns = { "/LoadDataView" } )
public class LoadDataView extends HttpServlet{
    
    protected void processRequest( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
            response.setContentType( "text/html;charset=UTF-8" );
        try( PrintWriter out = response.getWriter() ) {
            /* TODO output your page here. You may use following sample code. */
            out.println( "<!DOCTYPE html>" );
            out.println( "<html>" );
            out.println( "<head>" );
            out.println( "<title>Servlet Sample3Servlet</title>" );
            out.println( "</head>" );
            out.println( "<body>" );
            out.println( "<div style=\"text-align: center;\">" );
            out.println( "<div style=\"display: inline-block; text-align: left;\">" );
            out.println( "<form action=\"LoadDataView\" method=\"post\">" );
            out.println( "Enter Subreddit Name:<br>" );
            out.println( "<input type='text' name=\"subredditName\" value=\"\"><br>" );
            out.println( "Subreddit Account Name:<br>" );
            out.println( "<select name=\"subredditAccountName\">" );
            SubredditLogic logic = LogicFactory.getFor( "Subreddit" );
            List<Subreddit> entities = logic.getAll();
            for( Subreddit e: entities ) {
                //for other tables replace the code bellow with
                //extractDataAsList in a loop to fill the data.
                 out.println( "<option value="+e.getName()+">"+e.getName()+"</option>" );
            }
//            out.println( "<option value=\"saab\">Saab</option>" );
//            out.println( "<option value=\"opel\">Opel</option>" );
//            out.println( "<option value=\"audi\">Audi</option>" );
            out.println( "</select><br><br>" );
            out.println( "<input type=\"submit\" name=\"submit\" value=\"Submit\">" );
            out.println( "</form>" );
            out.println( "<pre>" );
            out.println( "Submitted keys and values:" );
            out.println( toStringMap( request.getParameterMap()) );

            out.println( "</pre>" );
            out.println( "</div>" );
            out.println( "</div>" );
            out.println( "</body>" );
            out.println( "</html>" );
        }
    }
    
    private String toStringMap( Map<String, String[]> values ) {
        StringBuilder builder = new StringBuilder();
        values.forEach( ( k, v ) -> builder.append( "Key=" ).append( k )
                .append( ", " )
                .append( "Value/s=" ).append( Arrays.toString( v ) )
                .append( System.lineSeparator() ) );
        return builder.toString();
    }
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        log( "GET" );
        processRequest( request, response );
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        log( "POST" );
        String clientID = "8NFVUc9eULFzeA";
        String clientSecret = "nNOWt3MU9waiq0wOR-pwY_D_5gQ";
        String redditUser = "ChrishMichael";
        String algonquinUser = "mich0216";

        DeveloperAccount dev = new DeveloperAccount()
                .setClientID( clientID )
                .setClientSecret( clientSecret )
                .setRedditUser( redditUser )
                .setAlgonquinUser( algonquinUser );

        //create a new scraper
        RedditWrapper scrap = new RedditWrapper();
        //authenticate and set up a page for wallpaper subreddit with 5 posts soreted by HOT order
        scrap.authenticate( dev ).setLogger( false );
        String subredditNameValue;
        if(request.getParameterMap().get("subredditName")[0]!= "")
            subredditNameValue = request.getParameterMap().get("subredditName")[0];
        else
            subredditNameValue  = request.getParameterMap().get("subredditAccountName")[0];
        scrap.configureCurentSubreddit( subredditNameValue, 2, SubSort.BEST );
        SubredditWrapper subw = scrap.getCurrentSubreddit();
        //String subredditName = subw.getName();
        SubredditLogic srLogic = LogicFactory.getFor("Subreddit");
        Subreddit sr = srLogic.getSubredditWithName(subw.getName());
          if (sr == null){
                Map<String, String[]> map = new HashMap<>(6);
                map.put(SubredditLogic.NAME, new String[]{subw.getName()});
                map.put(SubredditLogic.SUBSCRIBERS, new String[]{Integer.toString(subw.getSubscribers())});
                map.put(SubredditLogic.URL, new String[]{subw.getReletiveUrl()});
                sr= srLogic.createEntity(map);
                srLogic.add(sr);
            }
        RedditAccountLogic raLogic = LogicFactory.getFor("RedditAccount");
        //create a lambda that accepts post
        Consumer<PostWrapper> saveData = ( PostWrapper post ) -> {
            if( post.isPinned() ){
                return;
            }
            AccountWrapper aw = post.getAuthor();
            RedditAccount acc = raLogic.getRedditAccountWithName(aw.getName());
            if (acc == null){
                Map<String, String[]> map = new HashMap<>(6);
                map.put(RedditAccountLogic.NAME, new String[]{aw.getName()});
                map.put(RedditAccountLogic.COMMENT_POINTS, new String[]{Integer.toString(aw.getCommentKarma())});
                map.put(RedditAccountLogic.LINK_POINTS, new String[]{Integer.toString(aw.getLinkKarma())});
                map.put(RedditAccountLogic.CREATED, new String[]{raLogic.convertDateToString(aw.getCreated())});
                acc= raLogic.createEntity(map);
                raLogic.add(acc);
            }
            post.configComments( 2, 2, CommentSort.CONFIDENCE );
            post.processComments( comment -> {
                if( comment.isPinned() || comment.getDepth() == 0 ){
                    return;
                }
                AccountWrapper awC = comment.getAuthor();
                RedditAccount accC = raLogic.getRedditAccountWithName(awC.getName());
                    if (accC == null){
                        Map<String, String[]> map = new HashMap<>(6);
                        map.put(RedditAccountLogic.NAME, new String[]{aw.getName()});
                        map.put(RedditAccountLogic.COMMENT_POINTS, new String[]{Integer.toString(aw.getCommentKarma())});
                        map.put(RedditAccountLogic.LINK_POINTS, new String[]{Integer.toString(aw.getLinkKarma())});
                        map.put(RedditAccountLogic.CREATED, new String[]{raLogic.convertDateToString(aw.getCreated())});
                        accC= raLogic.createEntity(map);
                        raLogic.add(accC);
                    }
                } );
            };
        //get the next page and process every post
        scrap.requestNextPage().proccessCurrentPage( saveData );
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Sample of Account View Normal";
    }

    private static final boolean DEBUG = true;

    public void log( String msg ) {
        if( DEBUG ){
            String message = String.format( "[%s] %s", getClass().getSimpleName(), msg );
            getServletContext().log( message );
        }
    }

    public void log( String msg, Throwable t ) {
        String message = String.format( "[%s] %s", getClass().getSimpleName(), msg );
        getServletContext().log( message, t );
    }
}
