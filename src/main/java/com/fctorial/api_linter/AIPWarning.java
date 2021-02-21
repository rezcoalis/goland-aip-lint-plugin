package com.fctorial.api_linter;

public class AIPWarning {
    public static final AIPWarning ERROR = new AIPWarning(null, null, null, null, null);
    // y = line, x = column
    public final Integer y1;
    public final Integer x1;
    public final Integer y2;
    public final Integer x2;
    public final String reason;

    public AIPWarning(
            Integer y1,
            Integer x1,
            Integer y2,
            Integer x2,
            String reason
    ) {
        this.y1 = y1;
        this.x1 = x1;
        this.y2 = y2;
        this.x2 = x2;
        this.reason = reason;
    }
}

