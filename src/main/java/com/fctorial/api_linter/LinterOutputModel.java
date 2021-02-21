package com.fctorial.api_linter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

class Position {
    public Integer line_number;
    public Integer column_number;
}

class Location {
    public Position start_position;
    public Position end_position;
}

@JsonIgnoreProperties(ignoreUnknown = true)
class LinterWarning {
    public String message;
    public Location location;
}

@JsonIgnoreProperties(ignoreUnknown = true)
public class LinterOutputModel {
    public LinterWarning[] problems;
}
