package com.orange.demo;

import com.orange.demo.view.PrimaryStageView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.stage.Stage;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.orange.demo.mapper")
public class DemoApplication extends AbstractJavaFxApplicationSupport{

    public static void main(String[] args) {
        launchApp(DemoApplication.class, PrimaryStageView.class, args);
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
    }

}
