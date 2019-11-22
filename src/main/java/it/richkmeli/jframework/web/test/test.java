package it.richkmeli.jframework.web.test;

import it.richkmeli.jframework.auth.model.User;
import it.richkmeli.jframework.orm.DatabaseException;
import it.richkmeli.jframework.util.Logger;
import it.richkmeli.jframework.web.util.ServletException;
import it.richkmeli.jframework.web.util.ServletManager;
import it.richkmeli.jframework.web.util.Session;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/test")
public class test extends HttpServlet {

    public test() {
        super();
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        HttpSession httpSession = request.getSession();
        Session session = null;
        try {
            session = ServletManager.getServerSession(httpSession);
        } catch (ServletException e) {
            httpSession.setAttribute("error", e);
            request.getRequestDispatcher(ServletManager.ERROR_JSP).forward(request, response);

        }
        String out = "";

        try {
            session.getAuthDatabaseManager().addUser(new User("richk@i.it", "00000000", true));
            session.getAuthDatabaseManager().addUser(new User("er@fv.it", "00000000", false));
            //TODO da spostare nella creazione della tabella
            session.getAuthDatabaseManager().addUser(new User("", "00000000", false));
            session.getAuthDatabaseManager().addUser(new User("richk@i.it", "00000000", true));
        } catch (DatabaseException e) {
            e.printStackTrace();
            Logger.error("Session TEST USERS", e);
        }


        out = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "\t<head>\n" +
                "\t\t<script src=\"js/jquery/jquery.min.js\"></script>\n" +
                "\t\t\t<script>\n" +
                "\t\t\t$(document).ready(function() {\n" +
                "\t\t\t\tdocument.location.replace(\"login.html\")\n" +
                "\t\t\t});\n" +
                "\t\t</script>\n" +
                "\t\t</head>\n" +
                "\t\t<body></body>\n" +
                "</html>";

        PrintWriter printWriter = response.getWriter();
        printWriter.println(out);
        printWriter.flush();
        printWriter.close();
    }

}

