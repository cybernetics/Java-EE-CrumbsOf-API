package javaee.crumbsOfAPI.ejbs;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author Alin Constantin
 */
@Singleton
@ServerEndpoint("/scheduler")
public class WSEndpoint {

    private static final Logger LOG = Logger.getLogger(WSEndpoint.class.getName());

    /* Queue for all open WebSocket sessions */
    private static Queue<Session> queue = new ConcurrentLinkedQueue<>();

    public static void sendMessage(String msg) {

        try {
            /* Send updates to all open WebSocket sessions */
            for (Session session : queue) {
                LOG.log(INFO, "Send to {0}", session.getId());
                session.getBasicRemote().sendText(msg);
            }
        } catch (IOException e) {
            LOG.log(SEVERE, e.getMessage());
        }
    }

    @Inject
    private TimerScheduler scheduler;

    @OnOpen
    public void onOpen(Session session) {
        if (queue.add(session)) {
            LOG.log(INFO, "Open session: {0}", session.getId());
        } else {
            LOG.log(SEVERE, "Error: Session {0} could not be added to WebSocket sessions.", session.getId());
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason c) {
        LOG.log(INFO, "Closing session:{0}", session.getId());
        try {
            queue.remove(session);
        } catch (ClassCastException | NullPointerException | UnsupportedOperationException e) {
            LOG.log(SEVERE, e.getMessage());
        }
    }

    @OnError
    public void onError(Session session, Throwable t) {
        LOG.log(SEVERE, "Error: {0}", t.getMessage());
    }

    @OnMessage
    public String onMessage(String message, Session session) {
        LOG.log(INFO, "Received : {0}, session:{1}", new Object[]{message, session.getId()});
        return scheduler.processMessage(message);
    }

}
