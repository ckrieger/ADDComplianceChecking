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

import com.fasterxml.jackson.annotation.JsonProperty;
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

    private String queueHost;

    private String queueName;

    @JsonProperty(value="isActive")
    private Boolean isActive;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
   // @JoinColumn(name = "pattern_instance_id")
    private Set<PatternInstance> patternInstances = new HashSet<>();

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

    public String getQueueHost() {
        return queueHost;
    }

    public void setQueueHost(String queueHost) {
        this.queueHost = queueHost;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
