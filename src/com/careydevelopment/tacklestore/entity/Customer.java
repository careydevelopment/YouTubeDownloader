package com.careydevelopment.tacklestore.entity;

public class Customer {

    private Long id;
    private String firstName;
    private String lastName;
    private Integer age;

    
    public Customer(Long id, String firstName, String lastName, Integer age) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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
    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(lastName);
        builder.append(", ");
        builder.append(firstName);
        builder.append(" Age: ");
        builder.append(age);
        builder.append(" [ID=");
        builder.append(id);
        builder.append("]");
        
        return builder.toString();
    }
}
