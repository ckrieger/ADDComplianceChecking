package com.example.demo.model;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class InstrumentationTemplate {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String content;

    @ManyToMany(mappedBy = "linkedInstrumentationTemplates", fetch = FetchType.EAGER)
   // @JsonManagedReference(value="someName")
    @JsonIgnore
    List<Pattern> patterns;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<Pattern> patterns) {
        this.patterns = patterns;
    }
}
