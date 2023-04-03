package com.orange.demo;

import com.orange.demo.view.PrimaryStageView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Optional;

@SpringBootApplication
public class DemoApplication extends AbstractJavaFxApplicationSupport{

    public static void main(String[] args) {
        launchApp(DemoApplication.class, PrimaryStageView.class, args);
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                //增加退出提示框
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.titleProperty().set("退出");
                alert.headerTextProperty().set("确定要退出吗？");
                Optional<ButtonType> result = alert.showAndWait();
                if(result.get() == ButtonType.OK){
                    System.exit(0);
                }else{
                    event.consume();
                }
            }
        });
    }

}
