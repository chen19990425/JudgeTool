package chen;

import chen.entity.StudentGroup;
import chen.entity.StudentNode;
import chen.service.CreateFile;
import chen.service.ReadFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Test {

	public static void main(String[] args) {
//		Scanner in = new Scanner(System.in);
//		System.out.println("请输入文件夹路径:");
//		String dir = in.next();
//		System.out.println("请输入成绩输出文件路径（路径不要与试卷文件夹相同）:");
//		String outDir = in.next();

		try {
			ReadFile testRead = new ReadFile();
			CreateFile testCreate = new CreateFile();
			List<String> answer = testRead.getAnswer(new File("D:\\Desktop\\判卷4.0test\\java第三次答案.xls"));
			List<StudentNode> stuList = testRead.getStudentNodes(testRead.findExcel(new File("D:\\Desktop\\判卷4.0test\\20201102")), "javase第3周周考_", answer, true);
			List<StudentGroup> grpList = testRead.getGroup("D:\\Desktop\\判卷4.0test\\分组.xls");
			grpList = testRead.computeGroupScore(stuList, grpList);
			testCreate.createScoreExcel("javase第3周周考_", stuList, grpList, ReadFile.getAvgScore(), "D:\\Desktop\\判卷4.0test\\", true);
			System.out.println("试卷总数：" + stuList.size());
			String[][] rate = testRead.analyse();
			testCreate.createAnalyseExcel("javase第3周周考_", "D:\\Desktop\\判卷4.0test\\", stuList, rate);
			System.out.println("相似度阈值：" + ReadFile.getThreshold());
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("SUCCESS:文件'" + "D:\\Desktop\\判卷4.0test\\" + "成绩.xls'已生成");
		System.out.println("SUCCESS:文件'" + "D:\\Desktop\\判卷4.0test\\" + "相似度.xls'已生成");
	}

}
