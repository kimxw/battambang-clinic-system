package com.orb.battambang.util;


import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;

public class Rectangles {

    public static void updateStatusRectangle(Rectangle rectangle, Label label, String message) {
        label.setText(message);
        if (message.equals("Complete")) {
            rectangle.setStyle("-fx-fill: #bdd9ba;");
        } else if (message.equals("Incomplete")) {
            rectangle.setStyle("-fx-fill: #e59295;");
        } else if (message.equals("Deferred")) {
            rectangle.setStyle("-fx-fill: #bea9df;");
        } else {
            rectangle.setStyle("-fx-fill: #E74C3C;"); //not found
        }
    }
}