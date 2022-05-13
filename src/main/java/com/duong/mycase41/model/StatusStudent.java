package com.duong.mycase41.model;

import javax.persistence.*;

@Entity
@Table(name = "status_student")
public class StatusStudent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public StatusStudent() {
    }

    public StatusStudent(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public StatusStudent(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
