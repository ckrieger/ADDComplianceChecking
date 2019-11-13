package com.example.demo.model;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Data
@NoArgsConstructor
public class Pattern {
    @Id
    @GeneratedValue
    private Long id;
    private @NonNull String name;
    @Lob
    private String pConstraint;


    @ManyToMany(cascade= CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "linked_templates",
            joinColumns = @JoinColumn(name="pattern_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "instrumentationTemplate_id", referencedColumnName = "id"))
    @JsonBackReference
    List<InstrumentationTemplate> linkedInstrumentationTemplates;

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

    public String getpConstraint() {
        return pConstraint;
    }

    public void setpConstraint(String pConstraint) {
        this.pConstraint = pConstraint;
    }

    public List<InstrumentationTemplate> getLinkedInstrumentationTemplates() {
        return linkedInstrumentationTemplates;
    }

    public void setLinkedInstrumentationTemplates(List<InstrumentationTemplate> linkedInstrumentationTemplates) {
        this.linkedInstrumentationTemplates = linkedInstrumentationTemplates;
    }
}
