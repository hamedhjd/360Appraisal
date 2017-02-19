package model;

/**
 * Created with IntelliJ IDEA.
 * User: h.hosseininejad
 * Date: 9/2/15
 * Time: 10:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class Person {
    private String firstName;
    private String lastName;
    private String name;
    private Role role;

    public Person() {
    }

    public Person(String firstName, String lastName, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public Person(String name, Role role) {
        this.name = name;
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
