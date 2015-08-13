package javaee.crumbsOfAPI.ejbs;

import java.io.Serializable;

/**
 *
 * @author Alin Constantin
 */
public class TimerInfo implements Serializable {

    private static final long serialVersionUID = 3668768224557029880L;

    private String name;
    private String description;

    public TimerInfo() {
    }

    public TimerInfo(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "TimerInfo{" + "name=" + name + ", description=" + description + '}';
    }

}
