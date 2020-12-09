module com.schwada.mpgame {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires java.logging;
    requires java.desktop;

    opens com.schwada.mpgame to javafx.fxml;
    opens com.schwada.mpgame.controller to javafx.fxml;
    opens com.schwada.mpgame.logic.entity to javafx.fxml;
    exports com.schwada.mpgame;
}
