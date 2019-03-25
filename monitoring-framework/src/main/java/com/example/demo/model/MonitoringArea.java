package com.example.demo.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Data
@NoArgsConstructor
@Table(name = "monitoring_area")
public class MonitoringArea {
    @Id
    @GeneratedValue
    private Long id;

    private @NonNull String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
   // @JoinColumn(name = "pattern_instance_id")
    private Set<PatternInstance> patternInstances = new HashSet<>();

    private String queueUrl;

    public String getQueueUrl() {
        return queueUrl;
    }

    public void setQueueUrl(String queueUrl) {
        this.queueUrl = queueUrl;
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

    public Set<PatternInstance> getPatternInstances() {
        return patternInstances;
    }

    public void setPatternInstances(Set<PatternInstance> patternInstances) {
        this.patternInstances = patternInstances;
    }
}
