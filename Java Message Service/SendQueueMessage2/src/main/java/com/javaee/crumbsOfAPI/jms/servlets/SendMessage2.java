package com.javaee.crumbsOfAPI.jms.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSConnectionFactoryDefinition;
import javax.jms.JMSContext;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSRuntimeException;
import javax.jms.Queue;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Constantin Alin
 */
@JMSConnectionFactoryDefinition(
        name = "java:global/jms/testQueueFactory2", maxPoolSize = 30, minPoolSize = 20,
        properties = {"addressList=mq://localhost:7676", "reconnectEnabled=true"}
)
@JMSDestinationDefinition(
        name = "java:global/jms/testQueue2", interfaceName = "javax.jms.Queue", destinationName = "testQueue2"
)
@WebServlet(name = "SendMessage2", urlPatterns = {"/jms2"})
public class SendMessage2 extends HttpServlet {

    private static final long serialVersionUID = 6442340177995274302L;

    @Resource(lookup = "java:global/jms/testQueue2")
    private Queue queue;

    @Inject
    @JMSConnectionFactory("java:global/jms/testQueueFactory2")    // Specific connection factory
    private JMSContext jmsContext;

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

        try (PrintWriter out = response.getWriter()) {
            jmsContext.createProducer().send(queue, request.getParameter("message"));

            // Received after 10 sec
            // jmsContext.createProducer().setDeliveryDelay(10000).send(queue, request.getParameter("message"));
            out.println("Servlet <b>SendMessage</b> (JMS 2.0) says: Message sent !");
        } catch (JMSRuntimeException ex) {
            Logger.getLogger(SendMessage2.class.getName()).log(Level.SEVERE, null, ex);
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
