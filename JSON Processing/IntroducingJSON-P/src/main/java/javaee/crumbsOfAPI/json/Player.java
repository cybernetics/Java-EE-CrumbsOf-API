package javaee.crumbsOfAPI.json;

import java.util.Date;

/**
 *
 * @author Alin Constantin
 */
public class Player {

    private Boolean righthanded;
    private String name;
    private Integer age;
    private Date birthdate;

    public Player(Boolean righthanded, String name, Integer age, Date birthdate) {
        this.righthanded = righthanded;
        this.name = name;
        this.age = age;
        this.birthdate = birthdate;
    }

    public Boolean getRighthanded() {
        return righthanded;
    }

    public void setRighthanded(Boolean righthanded) {
        this.righthanded = righthanded;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

}
