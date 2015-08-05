package com.javaee.crumbsOfAPI.jms.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Constantin Alin
 */
@WebServlet(name = "SendMessage", urlPatterns = {"/jms1"})
public class SendMessage extends HttpServlet {

    private static final long serialVersionUID = -7836094685706014092L;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        Destination dest;
        ConnectionFactory connectionFactory;
        Context jndiContext = null;
        Connection connection = null;

        try (PrintWriter out = response.getWriter()) {
            // Gets the JNDI context
            jndiContext = new InitialContext();

            // Looks up the administered objects
            connectionFactory = (ConnectionFactory) jndiContext.lookup("jms/testQueueFactory");
            dest = (Destination) jndiContext.lookup("jms/testQueue");

            // Creates the needed artifacts to connect to the queue
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(dest);

            // Sends a text message to the queue
            TextMessage message = session.createTextMessage();
            message.setText(request.getParameter("message"));
            producer.send(message);
            out.println("Servlet <b>SendMessage</b> (JMS 1.1) says: Message sent !");

        } catch (NamingException | JMSException ex) {
            Logger.getLogger(SendMessage.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException ex) {
                    Logger.getLogger(SendMessage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (jndiContext != null) {
                try {
                    jndiContext.close();
                } catch (NamingException ex) {
                    Logger.getLogger(SendMessage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
