package chen.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import chen.entity.StudentGroup;
import chen.entity.StudentNode;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

public class CreateFile {

	private static final Logger logger = Logger.getLogger(CreateFile.class);

	/**
	 * Create student score excel.<br>
	 * 生成学生成绩的报表。
	 * @param stuList Student Node List. 学生节点列表。
	 * @param grpList Group List. 分组列表。
	 * @param avgScore Student List Avg Score. 学生列表平均成绩。
	 * @param url Output Path. 输出路径。
	 * @throws IOException OutputStream Exception.
	 */
	public void createScoreExcel(String prefix, List<StudentNode> stuList, List<StudentGroup> grpList, int avgScore, String url, boolean isCreateGroup) throws IOException {
		OutputStream out = new FileOutputStream(new File(url + "\\" + prefix + "成绩.xls"));
		HSSFWorkbook book = new HSSFWorkbook();
		logger.info("成绩单文件开始输出...");
		logger.info("输出位置: " + url + "\\" + prefix + "成绩.xls");

		createStudentSheet(book, stuList, avgScore);
		if (isCreateGroup) {
			createGroupSheet(book, grpList);
			logger.info("已选择分组.");
		} else {
			logger.info("未选择分组.");
		}

		book.write(out);
		out.flush();
		book.close();
		out.close();
		logger.info("成绩单已完成输出.");
	}

	/**
	 * Create sheet of student score.<br>
	 * 生成学生个人成绩页。
	 * @param book Target Workbook. 目标工作报表。
	 * @param stuList Student Node List. 学生节点列表。
	 * @param avgScore Student List Avg Score. 学生列表平均成绩。
	 */
	private void createStudentSheet(HSSFWorkbook book, List<StudentNode> stuList, int avgScore) {
		logger.info("开始生成单人成绩...");
		HSSFSheet sheet = book.createSheet("单人成绩");
		HSSFRow row = sheet.createRow(0);
		HSSFCell no = row.createCell(0);
		no.setCellValue("名次");
		HSSFCell name = row.createCell(1);
		name.setCellValue("姓名");
		HSSFCell score = row.createCell(2);
		score.setCellValue("成绩");

		// 黄色背景。
		HSSFCellStyle style = book.createCellStyle();
		// POI3.15
//		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFillForegroundColor((short) 0x0D);

		for (int i = 0; i < stuList.size(); i++) {
			row = sheet.createRow(i + 1);
			no = row.createCell(0);
			no.setCellValue(i + 1);
			name = row.createCell(1);
			name.setCellValue(stuList.get(i).getName());
			score = row.createCell(2);
			score.setCellValue(stuList.get(i).getScore());
			if (stuList.get(i).getScore() < avgScore) {
				no.setCellStyle(style);
				name.setCellStyle(style);
				score.setCellStyle(style);
			}
		}

		row = sheet.createRow(stuList.size() + 1);
		name = row.createCell(1);
		name.setCellValue("平均分");
		score = row.createCell(2);
		score.setCellValue(avgScore);
		sheet.setColumnWidth(2, 15 * 256);
		row = sheet.createRow(stuList.size() + 2);
		name = row.createCell(1);
		name.setCellValue("时间");
		score = row.createCell(2);
		score.setCellValue(new SimpleDateFormat("MM-dd HH:mm:ss").format(new Date()));

		logger.info("单人成绩已生成.");
	}

	/**
	 * Create sheet of student group.<br>
	 * 生成学生分组页。
	 * @param book Target Workbook. 目标工作报表。
	 * @param grpList Group List. 分组列表。
	 */
	private void createGroupSheet(HSSFWorkbook book, List<StudentGroup> grpList) {
		if (grpList == null) {
			return ;
		}
		logger.info("开始生成分组成绩...");
		grpList.forEach(System.out::println);
		HSSFSheet sheet = book.createSheet("分组成绩");
		HSSFRow head = sheet.createRow(0);
		HSSFCell groupName = head.createCell(0);
		groupName.setCellValue("组名");
		HSSFCell stuName = head.createCell(1);
		stuName.setCellValue("姓名");
		HSSFCell stuScore = head.createCell(2);
		stuScore.setCellValue("成绩");
		HSSFCell grpScore = head.createCell(3);
		grpScore.setCellValue("组平均成绩");
		sheet.setColumnWidth(3, 15 * 256);

		// 居中样式。
		HSSFCellStyle centerStyle = book.createCellStyle();
		// POI3.15
//		centerStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
//		centerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		centerStyle.setAlignment(HorizontalAlignment.CENTER);

		int top;
		int bottom = 1;
		for (StudentGroup currentGroup : grpList) {
			List<StudentNode> currentNodes = currentGroup.getStuNodes();
			top = bottom;
			for (int j = 0; j < currentNodes.size(); j++) {
				if (j == 0) {
					// 组长
					HSSFRow row = sheet.createRow(top);
					groupName = row.createCell(0);
					groupName.setCellValue(currentGroup.getGroupName());
					stuName = row.createCell(1);
					stuName.setCellValue(currentNodes.get(j).getName());
					stuScore = row.createCell(2);
					stuScore.setCellValue(currentNodes.get(j).getScore());
					grpScore = row.createCell(3);
					grpScore.setCellValue(currentGroup.getGroupScore());

					groupName.setCellStyle(centerStyle);
					grpScore.setCellStyle(centerStyle);
				} else {
					HSSFRow row = sheet.createRow(j + top);
					stuName = row.createCell(1);
					stuName.setCellValue(currentNodes.get(j).getName());
					stuScore = row.createCell(2);
					stuScore.setCellValue(currentNodes.get(j).getScore());
				}
				bottom++;
			}
			// 合并单元格
			sheet.addMergedRegion(new CellRangeAddress(top, bottom - 1, 0, 0));
			sheet.addMergedRegion(new CellRangeAddress(top, bottom - 1, 3, 3));
		}
		logger.info("分组成绩已生成.");
	}

	/**
	 * Create excel of repeat rate.
	 * 生成重复率报表。
	 * @param url Analyse File Path. 相似度报表输出路径。
	 * @param list Student Node List. 学生节点列表。
	 * @param rate Matrix of Student repeat rate. 学生相似度的矩阵。
	 * @throws IOException OutputStream Exception.
	 */
	public void createAnalyseExcel(String prefix, String url, List<StudentNode> list, String[][] rate) throws IOException {
		logger.info("开始生成相似度文件...");
		logger.info("输出位置: " + url + "\\" + prefix + "相似度.xls");
		OutputStream out = new FileOutputStream(new File(url + "\\" + prefix + "相似度.xls"));
		HSSFWorkbook book = new HSSFWorkbook();
		HSSFSheet sheet = book.createSheet("相似度分析");
		HSSFRow nameRow = sheet.createRow(0);
		for (int i = 0; i < list.size(); i++) {
			HSSFCell cell = nameRow.createCell(i);
			cell.setCellValue(list.get(i).getName());
		}
		for (int i = 0; i < rate.length - 1; i++) {
			HSSFRow row = sheet.createRow(i + 1);
			for (int j = 0; j < rate[i].length; j++) {
				HSSFCell cell = row.createCell(j);
				cell.setCellValue(rate[i][j]);
			}
		}
		HSSFCellStyle style = book.createCellStyle();
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFillForegroundColor((short) 0x0D);
		int threshold = ReadFile.getThreshold();
		logger.info("相似度阈值: " + threshold + "%.");
		for (int i = 1; i < list.size(); i++) {
			HSSFRow row = sheet.getRow(i);
			for (int j = 1; j < list.size(); j++) {
				HSSFCell cell = row.getCell(j);
				String stuRate = cell.getStringCellValue();
				if (!"".equals(stuRate)) {
					if (Integer.parseInt((stuRate.substring(0, stuRate.indexOf('%')))) > threshold)
						cell.setCellStyle(style);
				}
			}
		}
		book.write(out);
		out.flush();
		logger.info("相似度文件已生成.");
	}
}
