package com.ibmmq.receiver.model;

public enum JmsOperator {
    EQ("="), NEQ("!="), LT("<"), GT(">"), LTE("<="), GTE(">=");
    
    private String symbol;
    
    JmsOperator(String symbol) {
        this.symbol = symbol;
    }
    
    public String getSymbol() {
        return symbol;
    }
}
