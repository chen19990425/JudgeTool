package chen.controller;

import chen.*;
import chen.entity.StudentGroup;
import chen.entity.StudentNode;
import chen.service.CreateFile;
import chen.service.ReadFile;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Chen
 */
public class MainController {

	private Stage thisFrame;
	private final ReadFile readFile;
	private final CreateFile createFile;
	private List<File> studentFiles;
	private List<String> teacherAnswer;
	private List<StudentNode> studentNodes;
	private List<StudentGroup> groupList;
	private static String path = "/";
	private  static final Logger logger = Logger.getLogger(MainController.class);

	{
		thisFrame = GUI.getStage();
		readFile = new ReadFile();
		createFile = new CreateFile();
		System.out.println(thisFrame);
	}


	@FXML
	private TextField testPrefix;

	@FXML
	private TextField studentDirText;

	@FXML
	private TextField answerFileText;

	@FXML
	private CheckBox importGroupListBox;

	@FXML
	private TextField groupListFileText;

	@FXML
	private Button chooseGroupListBtn;

	@FXML
	private CheckBox analyseBox;

	@FXML
	private TextField scoreOutText;

	@FXML
	private TextField analysePathText;

	@FXML
	private Label studentCountLabel;

	@FXML
	private Label answerLabel;

	@FXML
	private TextArea messageTextArea;

	@FXML
	private Button analyseBtn;

	@FXML
	void aboutAction(ActionEvent event) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION, "version 4.3\nauthor Chen");
		alert.setHeaderText("判卷、生成平均成绩、按分组求平均成绩、错题相似度分析...");
		alert.setTitle("About...");
		alert.show();
	}

	@FXML
	void chooseAnswerFileAction(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("请选择答案");
		fileChooser.setInitialDirectory(new File(path));
		File file = fileChooser.showOpenDialog(new Stage());
		if (file != null) {
			path = file.getParentFile().getAbsolutePath();
			answerFileText.setText(file.getAbsolutePath());
			answerLabel.setText("答案：已读入");
			try {
				teacherAnswer = readFile.getAnswer(file);
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	}

	@FXML
	void chooseGroupListAction(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("请选择分组名单");
		fileChooser.setInitialDirectory(new File(path));
		File file = fileChooser.showOpenDialog(new Stage());
		if (file != null) {
			path = file.getParentFile().getAbsolutePath();
			groupListFileText.setText(file.getAbsolutePath());
			messageTextArea.appendText("分组名单已读入！\n");
			try {
				groupList = readFile.getGroup(file.getAbsolutePath());
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	}

	@FXML
	void chooseImportGroupListAction(ActionEvent event) {
		if (importGroupListBox.isSelected()) {
			groupListFileText.setDisable(false);
			chooseGroupListBtn.setDisable(false);
		} else {
			groupListFileText.setDisable(true);
			chooseGroupListBtn.setDisable(true);
			groupListFileText.setText("");
		}
	}

	@FXML
	void chooseStudentDirAction(ActionEvent event) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("请选择学生试卷文件夹");
		directoryChooser.setInitialDirectory(new File(path));
		File dir = directoryChooser.showDialog(new Stage());
		if (dir != null) {
			path = dir.getAbsolutePath();
			logger.info("开始搜寻学生试卷文件...");
			studentDirText.setText(dir.getAbsolutePath());
			studentFiles = new ReadFile().findExcel(dir);
			studentCountLabel.setText("试卷数目：" + studentFiles.size());
			logger.info("搜寻完毕.    共搜寻到Excel文件: " + studentFiles.size() + "个.");
		}
	}

	@FXML
	void closeAction(ActionEvent event) {
		Platform.exit();
	}

	@FXML
	void mainAction(ActionEvent event) {
		if (testPrefix.getText().equals("")) {
			messageTextArea.appendText("请输入试卷前缀！\n");
		} else if (studentFiles == null) {
			messageTextArea.appendText("请选择学生文件夹！\n");
		} else if (teacherAnswer == null) {
			messageTextArea.appendText("请读入答案！\n");
		} else if (scoreOutText.getText().equals("")) {
			messageTextArea.appendText("请选择成绩单输出位置！\n");
		} else 	{
			String prefix = testPrefix.getText();
			logger.info("试卷前缀 '" + prefix + "'.");
			studentNodes = readFile.getStudentNodes(studentFiles, prefix, teacherAnswer, analyseBox.isSelected());
			if (importGroupListBox.isSelected()) {
				groupList = readFile.computeGroupScore(studentNodes, groupList);
			}
			try {
				createFile.createScoreExcel(prefix, studentNodes, groupList, ReadFile.getAvgScore(), scoreOutText.getText(), importGroupListBox.isSelected());
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
			if (analyseBox.isSelected()) {
				String[][] rate = readFile.analyse();
				try {
					createFile.createAnalyseExcel(prefix, analysePathText.getText(), studentNodes, rate);
					messageTextArea.appendText("相似度阈值：" + ReadFile.getThreshold() + "%\n");
					messageTextArea.appendText("文件'" + analysePathText.getText() + "\\相似度.xls'已生成\n");
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
			messageTextArea.appendText("文件'" + scoreOutText.getText() + "\\成绩.xls'已生成\n");
			logger.info("判卷结束.");
		}
	}

	@FXML
	void restartAction(ActionEvent event) {
		thisFrame.close();
		new GUI().start(new Stage());
	}


	@FXML
	void analyseBoxAction(ActionEvent event) {
		if (analyseBox.isSelected()) {
			analysePathText.setDisable(false);
			analyseBtn.setDisable(false);
		} else {
			analysePathText.setDisable(true);
			analyseBtn.setDisable(true);
			analysePathText.setText("");
		}
	}


	@FXML
	void chooseAnalyseAction(ActionEvent event) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("请选择相似度输出文件夹");
		directoryChooser.setInitialDirectory(new File(path));
		File dir = directoryChooser.showDialog(new Stage());
		if (dir != null) {
			path = dir.getAbsolutePath();
			analysePathText.setText(dir.getAbsolutePath());
		}
	}


	@FXML
	void chooseScoreOutAction(ActionEvent event) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("请选择成绩单输出文件夹");
		directoryChooser.setInitialDirectory(new File(path));
		File dir = directoryChooser.showDialog(new Stage());
		if (dir != null) {
			path = dir.getAbsolutePath();
			scoreOutText.setText(dir.getAbsolutePath());
		}
	}

}
