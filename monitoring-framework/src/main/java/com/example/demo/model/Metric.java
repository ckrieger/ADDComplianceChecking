package com.example.demo.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Metric {

    private String metric;
    private String value;
    private String timestamp;

}
