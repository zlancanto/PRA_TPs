module PRA.TPs {
    requires junit;
    requires org.junit.jupiter.api;
    requires java.sql;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires morpion;
    exports mihan.sossou.tp5 to javafx.graphics;
    exports mihan.sossou.morpion to javafx.graphics;
}