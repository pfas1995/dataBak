package com.adc.mq.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "test")
public class Test {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "test")
    private String test;


    public static Test Factory(Long id, String test) {
        Test t = new Test();
        t.id = id;
        t.test = test;
        return t;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }
}
