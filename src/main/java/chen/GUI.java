package chen;

/**
 * @author Chen
 * @date 2020/11/3 - 11:09
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;

public class GUI extends Application {
	private static Stage stage;
	private static final Logger logger = Logger.getLogger(GUI.class);

	public static void main(String[] args) {
		logger.info("欢迎使用判卷系统");
		logger.info("作者：陈嘉奇");
		logger.info("软件最后更新时间：2020年11月11日");
		logger.info("判卷系统已启动...");
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		stage = primaryStage;
		try {
			Parent root = FXMLLoader.load(getClass().getResource( "/mainController.fxml"));
			Scene scene = new Scene(root);
			primaryStage.getIcons().add(new Image("/icon.png"));
			primaryStage.setTitle("阿巴阿巴阿巴巴");
			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Stage getStage() {
		return stage;
	}
}
