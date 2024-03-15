module com.example.im_exam_m3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;

    opens com.example.im_exam_m3 to javafx.fxml;
    exports com.example.im_exam_m3;
}