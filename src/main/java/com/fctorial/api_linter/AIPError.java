package com.fctorial.api_linter;

public class AIPError extends AIPWarning {
    public AIPError(Integer y1, Integer x1, Integer y2, Integer x2, String reason) {
        super(y1, x1, y2, x2, reason);
    }
}
