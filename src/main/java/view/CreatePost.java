/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package view;

import entity.Post;
import entity.RedditAccount;
import entity.Subreddit;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.PostLogic;
import logic.LogicFactory;
import logic.RedditAccountLogic;
import logic.SubredditLogic;

/**
 *
 * @author  sabiha
 */
@WebServlet(name = "CreatePost", urlPatterns = {"/CreatePost"})
public class CreatePost extends HttpServlet {

    private String errorMessage = null;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Create Post</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div style=\"text-align: center;\">");
            out.println("<div style=\"display: inline-block; text-align: left;\">");
            out.println("<form method=\"post\">");
            out.println("title:<br>");
//instead of typing the name of column manualy use the static vraiable in logic
//use the same name as column id of the table. will use this name to get date
//from parameter map.
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>", PostLogic.TITLE);
            out.println("<br>");
            out.println("created:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>", PostLogic.CREATED);
            out.println("<br>");
            out.println("points:<br>");
            out.printf("<input type=\"password\" name=\"%s\" value=\"\"><br>", PostLogic.POINTS);
            out.println("<br>");
            out.println("comment_count:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>", PostLogic.COMMENT_COUNT);
            out.println("<br>");

            out.println("unique_id:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>", PostLogic.UNIQUE_ID);
            out.println("<br>");
            out.println("reddit:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>", PostLogic.REDDIT_ACCOUNT_ID
            );
            out.println("<br>");
            out.println("subreddit_id:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>", PostLogic.SUBREDDIT_ID);
            out.println("<br>");

            out.println("<input type=\"submit\" name=\"view\" value=\"Add and View\">");
            out.println("<input type=\"submit\" name=\"add\" value=\"Add\">");
            out.println("</form>");
            if (errorMessage != null && !errorMessage.isEmpty()) {
                out.println("<p color=red>");
                out.println("<font color=red size=4px>");
                out.println(errorMessage);
                out.println("</font>");
                out.println("</p>");
            }
            out.println("<pre>");
            out.println("Submitted keys and values:");
            out.println(toStringMap(request.getParameterMap()));
            out.println("</pre>");
            out.println("</div>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    private String toStringMap(Map<String, String[]> values) {
        StringBuilder builder = new StringBuilder();
        values.forEach((k, v) -> builder.append("Key=").append(k)
                .append(", ")
                .append("Value/s=").append(Arrays.toString(v))
                .append(System.lineSeparator()));
        return builder.toString();
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * get method is called first when requesting a URL. since this servlet will
     * create a host this method simple delivers the html code. creation will be
     * done in doPost method.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("GET");
        processRequest(request, response);
    }

    static int connectionCount = 0;

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * this method will handle the creation of entity. as it is called by user
     * submitting data through browser.
     *
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("POST");
        log("POST: Connection=" + connectionCount);

        PostLogic pLogic = LogicFactory.getFor("Post");
        String unique_id = request.getParameter(PostLogic.UNIQUE_ID);
        if (pLogic.getPostWithUniqueId(unique_id) == null) {
            try {
                Post post = pLogic.createEntity(request.getParameterMap());
                //create the two logics for reddit account and subreddit
                RedditAccountLogic rLogic = LogicFactory.getFor("RedditAccount");
                SubredditLogic sLogic = LogicFactory.getFor("Subreddit");
                //get the entities from logic using getWithId
                RedditAccount redditAccount = rLogic.getWithId(Integer.parseInt(PostLogic.REDDIT_ACCOUNT_ID));
                Subreddit subreddit = sLogic.getWithId(Integer.parseInt(PostLogic.SUBREDDIT_ID));
                //set the entities on your post object before adding them to db
                post.setRedditAccountId(redditAccount);
                post.setSubredditId(subreddit);      
                pLogic.add(post);
            } catch (Exception ex) {
                errorMessage = ex.getMessage();
            }
        } else {
//if duplicate print the error message
            errorMessage = "unique_id: \"" + unique_id + "\" already exists";
        }
        if (request.getParameter("add") != null) {
//if add button is pressed return the same page

            processRequest(request, response);
        } else if (request.getParameter("view") != null) {
//if view button is pressed redirect to the appropriate table
            response.sendRedirect("PostTable");
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Create a Post Entity";
    }

    private static final boolean DEBUG = true;

    public void log(String msg) {
        if (DEBUG) {
            String message = String.format("[%s] %s", getClass().getSimpleName(), msg);
            getServletContext().log(message);
        }
    }

    public void log(String msg, Throwable t) {
        String message = String.format("[%s] %s", getClass().getSimpleName(), msg);
        getServletContext().log(message, t);
    }
}
