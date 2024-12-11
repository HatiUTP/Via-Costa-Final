module com.example.viacostafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires static lombok;
    requires org.hibernate.orm.core;
    requires spring.data.jpa;
    requires spring.beans;
    requires spring.context;
    requires transitive javafx.base;
    requires transitive jakarta.persistence;
    requires javafx.graphics;
    requires org.json;
    requires java.net.http;
    requires itextpdf;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires jbcrypt;


    opens com.example.viacostafx to javafx.fxml;
    opens com.example.viacostafx.Controller to javafx.fxml;
    opens com.example.viacostafx.Modelo to org.hibernate.orm.core, jakarta.persistence;
    opens com.example.viacostafx.dao to org.hibernate.orm.core;
    opens com.example.viacostafx.Auxiliar to javafx.base;

    exports com.example.viacostafx;
    exports com.example.viacostafx.Controller;
    exports com.example.viacostafx.Modelo;
    exports com.example.viacostafx.dao;
    exports com.example.viacostafx.Servicio;
}
