package chen.tool;

import chen.entity.StudentNode;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Chen
 */
public class Judge {

	private static final Logger logger = Logger.getLogger(Judge.class);

	/**
	 * Judge current student exam.
	 * Create 'Result' sheet of student exam.<br>
	 * 评判当前的学生试卷，在学生试卷中创建“Result”页。
	 * 并且返回含有学生全部信息的学生节点。
	 * @param file Current Student File. 当前学生文件。
	 * @param answer Teacher Answer List. 教师答案列表。
	 * @param student Current Student Node. 当前学生节点。
	 * @return Student Node. 学生节点。
	 * @throws IOException InputStream Exception.
	 */
	public StudentNode judge(File file, List<String> answer, StudentNode student) throws IOException {
		logger.info("开始处理 " + student.getName() + " 的试卷...");
		logger.info("学生文件路径: " + file.getAbsolutePath());
		InputStream in = new FileInputStream(file);
		HSSFWorkbook book = new HSSFWorkbook(in);
		HSSFSheet sheet;
		if (book.getSheetIndex("Result") != -1) {
			System.out.println("删了");
			book.removeSheetAt(book.getSheetIndex("Result"));
		}
		sheet = book.createSheet("Result");
		HSSFRow row;
		HSSFCell cell;
		row = sheet.createRow(3);
		cell = row.createCell(0);
		cell.setCellValue("题号");
		cell = row.createCell(1);
		cell.setCellValue("正确答案");
		cell = row.createCell(2);
		cell.setCellValue("正误");
		int wrongCount = 0;
		// 居中样式
		HSSFCellStyle centerStyle = book.createCellStyle();
		centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		centerStyle.setAlignment(HorizontalAlignment.CENTER);
		// 错误样式
		// 红色居中
		HSSFCellStyle wrongStyle = book.createCellStyle();
		wrongStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		wrongStyle.setFillForegroundColor((short) 0x0A);
		wrongStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		wrongStyle.setAlignment(HorizontalAlignment.CENTER);
		List<Boolean> result = new ArrayList<>();
		for (int i = 0; i < answer.size(); i++) {
			row = sheet.createRow(i + 4);
			cell = row.createCell(0);
			cell.setCellStyle(centerStyle);
			cell.setCellValue(i + 1);
			cell.setCellStyle(centerStyle);
			cell = row.createCell(1);
			cell.setCellValue(answer.get(i));
			cell.setCellStyle(centerStyle);
			cell = row.createCell(2);
			cell.setCellStyle(centerStyle);
			if (answer.get(i).equals(student.getAnswer().get(i))) {
				result.add(true);
				cell.setCellValue("T");
			} else {
				result.add(false);
				cell.setCellValue("F");
				cell.setCellStyle(wrongStyle);
				wrongCount++;
			}
		}
		row = sheet.createRow(0);
		cell = row.createCell(2);
		cell.setCellValue("试卷总分：");
		cell = row.createCell(3);
		int score = 100 - wrongCount * 100 / answer.size();
		cell.setCellValue(score);
		StudentNode node = new StudentNode(student.getName(), student.getAnswer());
		node.setResult(result);
		node.setScore(score);
		book.write(file);
		logger.info(student.getName() + " 的试卷处理完成.");
		return node;
	}
}
