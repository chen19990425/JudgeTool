package chen.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import chen.tool.DataAnalyse;
import chen.tool.Judge;
import chen.entity.StudentGroup;
import chen.entity.StudentNode;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ReadFile {

	private final List<File> excelList;
	private static List<StudentNode> stuList;
	private final List<StudentGroup> grpList;
	private static int avgScore;
	private static DataAnalyse dataAnalyse;
	private static final Logger logger = Logger.getLogger(ReadFile.class);

	public ReadFile() {
		excelList = new ArrayList<>();
		stuList =  new ArrayList<>();
		grpList = new ArrayList<>();
		avgScore = 0;
	}

	public static int getAvgScore() {
		return avgScore;
	}

	public String[][] analyse() {
		return dataAnalyse.analyse(stuList);
	}

	public static int getThreshold() {
		return dataAnalyse.getThreshold(ReadFile.avgScore);
	}

	/**
	 * Get Teacher Answer List.<br>
	 * 获取教师答案。
	 * @param file Answer File. 答案文件。
	 * @return Teacher Answer. 教师答案。
	 * @throws IOException InputStream Exception.
	 */
	public List<String> getAnswer (File file) throws IOException {
		logger.info("开始读取答案文件...");
		logger.info("答案文件路径: " + file.getAbsolutePath());
		HSSFWorkbook book = new HSSFWorkbook(new FileInputStream(file));
		HSSFSheet sheet = book.getSheetAt(0);
		List<String> teacherAnswer = new ArrayList<>();
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			String answer = sheet.getRow(i).getCell(1).getStringCellValue();
			if (answer.equals("")) {
				break;
			}
			teacherAnswer.add(answer);
		}
		book.close();
		logger.info("答案读取完毕.");
		return teacherAnswer;
	}

	/**
	 * Judge Student Exam, Sort and Compute Avg Score.
	 * Init Analyse.<br>
	 * 评判学生试卷, 排序并计算平均分。初始化分析器。
	 * @param files All Student File. 获取学生试卷文件列表。
	 * @param prefix File Prefix. 考试文件前缀。
	 * @param teacherAnswer Teacher Answer List. 教师答案。
	 * @param analyse Use Analyse. 是否启用分析器。
	 * @return Student Node List. 学生节点列表。
	 */
	public List<StudentNode> getStudentNodes(List<File> files, String prefix, List<String> teacherAnswer, boolean analyse) {
		if (files == null || files.get(0) == null) {
			System.err.println("未读取到学生文件");
			logger.error("未读取到学生文件");
		}
		// 遍历files中所有的文件。
		assert files != null;
		files.forEach((f) -> {
			// 获取姓名，去掉多余信息。
			String name = (!f.getName().contains(prefix) ? f.getName() : f.getName().replace(prefix, "")).replace(".xls", "");
			InputStream in = null;
			try {
				in = new FileInputStream(f);
				HSSFWorkbook currentBook = new HSSFWorkbook(in);
				HSSFSheet stuSheet = currentBook.getSheetAt(0);
				List<String> stuAnswer = getStuAnswer(stuSheet);
				// 生成学生节点。
				StudentNode node = new Judge().judge(f, teacherAnswer, new StudentNode(name, stuAnswer));
				logger.info(name + " 试卷已读入.");
				avgScore += node.getScore();
				stuList.add(node);
				currentBook.close();
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("处理 " + f.getAbsolutePath() + " 学生文件时出错！");
			} finally {
				try {
					assert in != null;
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		// 去重。
		stuList = stuList.stream().distinct().collect(Collectors.toList());
		// 排序。
		stuList.sort((StudentNode sn1, StudentNode sn2) -> sn1.getScore() == sn2.getScore() ? 0 : sn2.getScore() - sn1.getScore());
		avgScore /= stuList.size();
		if (analyse) {
			// 初始化分析器。
			dataAnalyse = new DataAnalyse(teacherAnswer);
			logger.info("分析器已加载...");
		}
		return stuList;
	}

	/**
	 * Get Student Answers.<br>
	 * 获取一名学生答案列表。
	 * @param stuSheet Student Sheet. 学生当前页。
	 * @return answer. 学生答案列表。
	 */
	private List<String> getStuAnswer(HSSFSheet stuSheet) {
		List<String> stuAnswer = new ArrayList<>();
		for (int i = 5; ; i++) {
			if (stuSheet.getRow(i) == null || stuSheet.getRow(i).getCell(1) == null) {
				break;
			}
			String answer = stuSheet.getRow(i).getCell(1).getStringCellValue().trim();
			if (answer.equals("")) {
				stuAnswer.add(" ");
			} else {
				stuAnswer.add(answer);
			}
		}
		return stuAnswer;
	}

	/**
	 * Get group form excel.<br>
	 * 获取学生分组列表。
	 * @param url Group excel path. 分组文件路径。
	 * @return grpList All student group. 学生分组列表。
	 * @throws IOException InputStream Exception.
	 */
	public List<StudentGroup> getGroup(String url) throws IOException {
		logger.info("开始读取学生分组文件...");
		HSSFWorkbook book = new HSSFWorkbook(new FileInputStream(new File(url)));
		HSSFSheet sheet = book.getSheetAt(0);
		// 获取组名。
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			grpList.add(new StudentGroup(sheet.getRow(0).getCell(i).getStringCellValue()));
		}
		// 按分组依次读取学生姓名。
		List<StudentGroup> groupList = new ArrayList<>();
		for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
			StudentGroup currentGroup = grpList.get(i);
			ArrayList<StudentNode> stuNodes = new ArrayList<>();
			for (int j = 1; j <= sheet.getLastRowNum(); j++) {
				// 单元格不存在或单元格为空的判断。
				if (sheet.getRow(j).getCell(i) != null) {
					String stuName = sheet.getRow(j).getCell(i).getStringCellValue();
					if (!"".equals(stuName)) {
						stuNodes.add(new StudentNode(stuName, 0));
					}
				}
			}
			currentGroup.setStuNodes(stuNodes);
			groupList.add(currentGroup);
		}
		logger.info("分组文件读取完毕.");
		return groupList;
	}

	/**
	 * Replace score in stuList.
	 * (Override StudentNode.equals(), StudentNode.hashCode().)<br>
	 * 替换学生列表中的分数。
	 * （重写学生结点中的equals()与hashCode()方法。）
	 * @param stuList Student Node List. 学生节点列表。
	 * @param grpList Group List. 学生分组列表。
	 * @return Group List. 学生分组列表。
	 */
	public List<StudentGroup> computeGroupScore(List<StudentNode> stuList, List<StudentGroup> grpList) {
		// 使用contains()查找，替换分数，按组计算平均分。
		for (StudentGroup sg: grpList) {
			int count = 0;
			int notZero = 0;
			for (StudentNode sn: sg.getStuNodes()) {
				if (!stuList.contains(new StudentNode(sn.getName(), 0))) {
					sn.setScore(0);
				} else {
					sn.setScore(stuList.get(stuList.indexOf(new StudentNode(sn.getName(), 0))).getScore());
					notZero++;
				}
				count += sn.getScore();
			}
//			sg.setGroupScore(count / sg.getStuNodes().size());
			sg.setGroupScore(count / notZero);
		}
		logger.info("分组成绩已替换.");
		return grpList;
	}

	/**
	 * Search all suffix ".xls" file.<br>
	 * 深度搜寻所有“.xls”结尾的文件
	 * @param file Need Search directory. 需要搜寻的目标目录。
	 * @return Student Excel List. 全部搜寻到的报表文件。
	 */
	public List<File> findExcel (File file) {
		File[] dirs;
		// 如果当前文件夹中是目录的数目不为0，递归。
		if ((Objects.requireNonNull(dirs = file.listFiles(File::isDirectory))).length != 0) {
			Arrays.asList(dirs).forEach(this::findExcel);
		}
		List<String> s = Arrays.asList(Objects.requireNonNull(file.list((File dir, String name) -> name.contains(".xls"))));
		s.forEach((str) -> excelList.add(new File(file.getAbsolutePath() + "\\" + str)));
		return excelList;
	}

}
