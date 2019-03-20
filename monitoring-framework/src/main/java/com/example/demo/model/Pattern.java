package com.example.demo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Data
@NoArgsConstructor
public class Pattern {
    @Id @GeneratedValue
    private Long id;
    private @NonNull String name;
    @Lob
    private String pConstraint;

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

    public String getpConstraint() { return pConstraint; }

    public void setpConstraint(String pConstraint) { this.pConstraint = pConstraint; }

}
