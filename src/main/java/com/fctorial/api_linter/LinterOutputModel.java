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

class LinterWarning {
    public String message;
    public Location location;
    public String rule_id;
    public String rule_doc_uri;
}

@JsonIgnoreProperties(ignoreUnknown = true)
public class LinterOutputModel {
    public LinterWarning[] problems;
}
