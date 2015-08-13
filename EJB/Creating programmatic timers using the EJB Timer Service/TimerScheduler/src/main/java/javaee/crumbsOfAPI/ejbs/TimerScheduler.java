package javaee.crumbsOfAPI.ejbs;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.interceptor.AroundTimeout;
import javax.interceptor.InvocationContext;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.stream.JsonParsingException;

/**
 *
 * @author Alin Constantin
 */
@Singleton
@LocalBean
public class TimerScheduler {

    private static final Logger LOG = Logger.getLogger(TimerScheduler.class.getName());

    @Resource
    private TimerService timerService;

    /**
     * Handle onMessage() messages.
     *
     * @param message String message
     * @return String response
     */
    public String processMessage(String message) {

        if (message.startsWith("params")) {
            JsonObject jsonObject;
            try {
                JsonReader reader = Json.createReader(new StringReader(message.substring(message.indexOf('{'))));
                jsonObject = reader.readObject();
                // Validate form params and register timer
                return validateInput(jsonObject);
            } catch (JsonParsingException e) {
                return "<span class='error'>SERVER: Invalid JSON format. " + e + "</span>";
            } catch (JsonException e) {
                return "<span class='error'>SERVER: JSON object cannot be created due to i/o error. " + e + "</span>";
            }
        } else if (message.equals("getAllTimers")) {
            // Get timers list
            return getAllTimers();
        } else if (message.startsWith("getTimerDescription")) {
            // Get timer description
            return getTimerDescription(message.substring(message.indexOf(':') + 1));
        } else if (message.equals("stopAllTimers")) {
            // Stop all timers
            return stopAllTimers();
        } else if (message.startsWith("stopTimer")) {
            // Stop timer
            return stopTimer(message.substring(message.indexOf(':') + 1));
        } else {
            return "<span class='default'>SERVER: Connection accepted..</span>";
        }
    }

    /**
     * Validate user input parameters and register timer
     *
     * @param jsonObject User input parameters as JSON
     * @return Confirmation/error message
     */
    private String validateInput(JsonObject jsonObject) {

        try {
            String userConfirmation = "";
            ScheduleExpression schedule = new ScheduleExpression();

            JsonValue value;

            boolean isWeekDay = false;
            value = jsonObject.get("availableWeekDays");
            if (value != null && !(value.toString().length() < 3)) {
                String str = ((JsonString) value).getString();
                if (str.contains("*")) {
                    userConfirmation += "On every day of a week; ";
                } else {
                    userConfirmation += "On every " + str + " of a week; ";
                }
                isWeekDay = true;
                schedule.dayOfWeek(str);
            } else {
                if (jsonObject.get("availableMonthsDays") == null) {
                    return "<span class='error'>SERVER: Select the name of the day or multiple names from a week "
                            + "when you wish the timer to be executed OR, select the day of month when you wish "
                            + "the timer to be executed. </span>";
                }
            }

            if (!isWeekDay) {
                value = jsonObject.get("availableMonthsDays");
                if (value != null && !(value.toString().length() < 3)) {
                    String str = ((JsonString) value).getString();
                    if (str.contains("Last")) {
                        userConfirmation += str + "; ";
                    } else {
                        userConfirmation += "On " + str + "; ";
                    }
                    schedule.dayOfMonth(str);
                } else {
                    return "<span class='error'>SERVER: Select the day of month when you wish the timer to be "
                            + "executed</span>";
                }
            }

            value = jsonObject.get("availableMonths");
            if (value != null && !(value.toString().length() < 3)) {
                String str = ((JsonString) value).getString();
                if (str.contains("*")) {
                    userConfirmation += "every month of a year; ";
                } else {
                    userConfirmation += "of month " + str + " of every year; ";
                }
                schedule.month(str);
            } else {
                return "<span class='error'>SERVER: Select the name of the month or multiple names from a year "
                        + "when you wish the timer to be executed.</span>";
            }

            value = jsonObject.get("hour");
            if (value != null && !(value.toString().length() < 3)) {
                String str = ((JsonString) value).getString();
                userConfirmation += "at " + str;

                schedule.hour(str);
            } else {
                return "<span class='error'>SERVER: Insert the hour when you wish the timer to be executed.</span>";
            }

            value = jsonObject.get("minute");
            if (value != null && !(value.toString().length() < 3)) {
                String str = ((JsonString) value).getString();
                userConfirmation += ":" + str;

                schedule.minute(str);
            } else {
                return "<span class='error'>SERVER: Insert the minute when you wish the timer to be executed.</span>";
            }

            value = jsonObject.get("second");
            if (value != null && !(value.toString().length() < 3)) {
                String str = ((JsonString) value).getString();
                userConfirmation += ":" + str;

                schedule.second(str);
            } else {
                return "<span class='error'>SERVER: Insert the second when you wish the timer to be executed.</span>";
            }

            TimerInfo info = new TimerInfo();
            value = jsonObject.get("name");
            String name;
            if (value != null && !(value.toString().length() < 3)) {
                name = ((JsonString) value).getString();
                if (!timerExists(name)) {
                    info.setName(name);
                    info.setDescription(userConfirmation);
                } else {
                    return "<span class='error'>SERVER: Timer with name already exists. Choose another name.</span>";
                }
            } else {
                return "<span class='error'>SERVER: Insert the name for this timer.</span>";
            }

            boolean persistent = false;
            value = jsonObject.get("persistent");
            if (value != null && !(value.toString().length() < 3)) {
                persistent = true;
            }

            schedule.year("*").timezone(TimeZone.getDefault().getID());
            timerService.createCalendarTimer(schedule, new TimerConfig(info, persistent));

            return "<span class='success'>SERVER: The \"" + name + "\" timer has been successfully configured and will run automatically "
                    + "at the specified date.</span><br/><span class='default'>\"" + userConfirmation + "\"</span>.";

        } catch (EJBException | IllegalStateException | IllegalArgumentException e) {
            return "<span class='error'>SERVER: There was an error at configuring the timer. " + e + "</span>";
        }
    }

    /**
     * Check if timer with name exists.
     *
     * @param name Timer name
     * @return true if timer exists, false otherwise
     */
    private boolean timerExists(String name) {

        try {
            for (Timer t : timerService.getTimers()) {
                TimerInfo timer = (TimerInfo) t.getInfo();
                if (timer.getName().equals(name)) {
                    return true;
                }
            }
        } catch (EJBException | IllegalStateException e) {
            LOG.log(SEVERE, e.getMessage());
        }
        return false;
    }

    /**
     * Get the list with all registered timers.
     *
     * @return <ul> list containing timers name
     */
    private String getAllTimers() {

        List<String> timersList = new ArrayList<>();
        StringBuilder timersNamesList = new StringBuilder("<span class='success'>SERVER:</span><ul class='default'>");
        try {
            for (Timer t : timerService.getTimers()) {
                TimerInfo timer = (TimerInfo) t.getInfo();
                timersList.add(timer.getName());
                timersNamesList.append("<li>").append(timer.getName()).append("</li>");
            }

            if (timersList.isEmpty()) {
                return "<span class='default'>SERVER: No timers found.</span>";
            } else {
                return timersNamesList.append("</ul>").toString();
            }
        } catch (EJBException | IllegalStateException e) {
            return "<span class='error'>SERVER: There was an error when trying to collect timers. " + e + "</span>";
        }
    }

    /**
     * Get timer description for the specified timer name.
     *
     * @param name Registered timer name
     * @return Timer description
     */
    private String getTimerDescription(String name) {

        try {
            for (Timer t : timerService.getTimers()) {
                TimerInfo timer = (TimerInfo) t.getInfo();
                if (timer.getName().equals(name)) {
                    return "<span class='success'>SERVER: \"" + name + "\" description: "
                            + "<span class='default'>\"" + timer.getDescription() + "\"</span></span>";
                }
            }
            return "<span class='default'>SERVER: Timer with name \"" + name + "\" not found.</span>";
        } catch (EJBException | IllegalStateException e) {
            return "<span class='error'>SERVER: There was an error when trying to get timer description. " + e + "</span>";
        }
    }

    /**
     * Stop the specified timer.
     *
     * @param name Timer name
     * @return String confirmation
     */
    private String stopTimer(String name) {

        try {
            for (Timer t : timerService.getTimers()) {
                TimerInfo timer = (TimerInfo) t.getInfo();
                if (timer.getName().equals(name)) {
                    t.cancel();
                    return "<span class='success'>SERVER: Timer \"" + name + "\" has been successfully stopped.</span>";
                }
            }
            return "<span class='default'>SERVER: Timer \"" + name + "\" not found.</span>";
        } catch (EJBException | IllegalStateException e) {
            return "<span class='error'>SERVER: There was an error when trying to stop the " + name + " timer. " + e + "</span>";
        }
    }

    /**
     * Stop all timers.
     *
     * @return String confirmation
     */
    private String stopAllTimers() {

        try {
            List<String> timersList = new ArrayList<>();
            for (Timer t : timerService.getTimers()) {
                TimerInfo timer = (TimerInfo) t.getInfo();
                t.cancel();
                timersList.add(timer.getName());
            }

            if (timersList.isEmpty()) {
                return "<span class='default'>SERVER: No timers found.</span>";
            } else {
                StringBuilder stoppedTimers = new StringBuilder();
                for (String name : timersList) {
                    stoppedTimers.append("\"").append(name).append("\", ");
                }
                return "<span class='success'>SERVER: Timer/s " + stoppedTimers.toString() + " has/have been successfully stopped.</span>";
            }
        } catch (EJBException | IllegalStateException e) {
            return "<span class='error'>SERVER: There was an error when trying to stop all timers. " + e + "</span>";
        }
    }

    /**
     * Method to be executed on timeout.
     */
    @Timeout
    public void timeout(Timer timer) {
        LOG.log(INFO, "Executing process..");
    }

    @AroundTimeout
    public Object interceptorTimeout(InvocationContext invocationContext) throws Exception {

        Timer t = (Timer) invocationContext.getTimer();
        TimerInfo timer = (TimerInfo) t.getInfo();

        String message = "<span class='success'>SERVER: Preparing for process execution.."
                + "<br/>Timer name: <span class='error'>" + timer.getName() + "</span>"
                + "<br/>Timer description: <span class='error'>" + timer.getDescription() + "</span>";

        Object object = invocationContext.proceed();

        message += "<br/>Process have been executed at: " + new Date().toString()
                + "<br/>Next execution will take place on: " + t.getNextTimeout() + "</span>";

        // Send message to WebSocket server endpoint
        WSEndpoint.sendMessage(message);

        return object;
    }

}
