module PRA.TPs {
    requires junit;
    requires org.junit.jupiter.api;
    requires java.sql;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires morpion;
    exports mihan.sossou.morpion to javafx.graphics;
    exports mihan.sossou.crossword to javafx.graphics;
    opens mihan.sossou.tp4.test to junit;
}