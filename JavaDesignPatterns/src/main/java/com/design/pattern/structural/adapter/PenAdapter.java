package com.design.pattern.structural.adapter;

public class PenAdapter implements Pen {

    private BallPen ballPen = new BallPen();

    @Override
    public void write(String str) {
        ballPen.mark(str);
    }

}
