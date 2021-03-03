package dev.alis.os.api_linter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
class Position {
    public Integer line_number;
    public Integer column_number;
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Location {
    public Position start_position;
    public Position end_position;
}

@JsonIgnoreProperties(ignoreUnknown = true)
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
