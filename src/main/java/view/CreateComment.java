package view;

import entity.Comment;
import entity.Post;
import entity.RedditAccount;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.CommentLogic;
import static logic.CommentLogic.POST_ID;
import static logic.CommentLogic.REDDIT_ACCOUNT_ID;
import logic.LogicFactory;
import logic.PostLogic;
import logic.RedditAccountLogic;

/**
 *
 * @author ahmed
 */
@WebServlet( name = "CreateComment", urlPatterns = { "/CreateComment" } )
public class CreateComment extends HttpServlet {

    private String errorMessage = null;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        response.setContentType( "text/html;charset=UTF-8" );
        try( PrintWriter out = response.getWriter() ) {
            /* TODO output your page here. You may use following sample code. */
            out.println( "<!DOCTYPE html>" );
            out.println( "<html>" );
            out.println( "<head>" );
            out.println( "<title>Create Comment</title>" );
            out.println( "</head>" );
            out.println( "<body>" );
            out.println( "<div style=\"text-align: center;\">" );
            out.println( "<div style=\"display: inline-block; text-align: left;\">" );
            out.println( "<form method=\"post\">" );
            out.println( CommentLogic.UNIQUE_ID + ":<br>" );
            //instead of typing the name of column manualy use the static vraiable in logic
            //use the same name as column id of the table. will use this name to get date
            //from parameter map.
            //ID, UNIQUE_ID, TEXT, CREATED,POINTS,REPLYS,IS_REPLY,POST_ID, REDDIT_ACCOUNT_ID )
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>", CommentLogic.UNIQUE_ID );
            out.println( "<br>" );
            out.println( CommentLogic.TEXT +":<br>" );
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>", CommentLogic.TEXT );
            out.println( "<br>" );
            out.println( CommentLogic.POINTS +":<br>" );
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>", CommentLogic.POINTS );
            out.println( "<br>" );
            out.println( CommentLogic.REPLYS +":<br>" );
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>", CommentLogic.REPLYS );
            out.println( "<br>" );
            out.println( CommentLogic.IS_REPLY +":<br>" );
            //<input type="checkbox" name="nameOfChoice" value="1"
            out.printf("<input type=\"checkbox\" name=\"%s\" value=\"1\"><br>", CommentLogic.IS_REPLY );
            out.println( "<br>" );
            out.println( CommentLogic.POST_ID +":<br>" );
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>", CommentLogic.POST_ID );
            out.println( "<br>" );
            out.println( CommentLogic.REDDIT_ACCOUNT_ID +":<br>" );
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>", CommentLogic.REDDIT_ACCOUNT_ID );
            out.println( "<br>" );
            out.println( "<input type=\"submit\" name=\"view\" value=\"Add and View\">" );
            out.println( "<input type=\"submit\" name=\"add\" value=\"Add\">" );
            out.println( "</form>" );
            if( errorMessage != null && !errorMessage.isEmpty() ){
                out.println( "<p color=red>" );
                out.println( "<font color=red size=4px>" );
                out.println( errorMessage );
                out.println( "</font>" );
                out.println( "</p>" );
            }
            out.println( "<pre>" );
            out.println( "Submitted keys and values:" );
            out.println( toStringMap( request.getParameterMap() ) );
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
     * get method is called first when requesting a URL. since this servlet will create a host this method simple
     * delivers the html code. creation will be done in doPost method.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        log( "GET" );
        processRequest( request, response );
    }

    static int connectionCount = 0;

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * this method will handle the creation of entity. as it is called by user submitting data through browser.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        log( "POST" );
        log( "POST: Connection=" + connectionCount );
     /*   if( connectionCount < 3 ){
            connectionCount++;
            try {
                TimeUnit.SECONDS.sleep( 60 );
            } catch( InterruptedException ex ) {
                Logger.getLogger( CreateAccount.class.getName() ).log( Level.SEVERE, null, ex );
            }
        }*/
       
       CommentLogic commentLogic = LogicFactory.getFor( "Comment" );
      RedditAccountLogic redditLogic = LogicFactory.getFor("RedditAccount");
        PostLogic postLogic = LogicFactory.getFor("Post");
         
        
        int postId = Integer.parseInt(request.getParameterMap().get( POST_ID)[ 0 ]);
        int reditId = Integer.parseInt(request.getParameterMap().get( REDDIT_ACCOUNT_ID)[ 0 ]);
        Post post = postLogic.getWithId(postId);
        RedditAccount redditAccount = redditLogic.getWithId(reditId);
       // String username = request.getParameter(AccountLogic.USERNAME );
       // if( aLogic.getAccountWithUsername( username ) == null ){
            try {
                Comment comment = commentLogic.createEntity( request.getParameterMap() );
                comment.setRedditAccountId(redditAccount);
                comment.setPostId(post);
              
             
              commentLogic.add(comment);
            } catch( Exception ex ) {
                errorMessage = ex.getMessage();
            }
      //  } else {
            //if duplicate print the error message
       //     errorMessage = "Username: \"" + username + "\" already exists";
       // }
        if( request.getParameter( "add" ) != null ){
            //if add button is pressed return the same page
            processRequest( request, response );
        } else if( request.getParameter( "view" ) != null ){
            //if view button is pressed redirect to the appropriate table
            response.sendRedirect( "CommentTable" );
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Create a Comment Entity";
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
