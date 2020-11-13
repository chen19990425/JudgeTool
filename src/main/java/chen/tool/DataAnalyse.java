package chen.tool;

import chen.entity.StudentNode;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataAnalyse {

	private final List<String> answer;
	private final List<List<String>> stuAnswer;
	private static final Logger logger = Logger.getLogger(DataAnalyse.class);
	private int allWrongAnswer = 0;
	private int allSameAnswer = 0;
//	private int allSimilarAnswer = 0;
	/**
	 * Undefined character.<br>
	 * 未定义字符。
	 */
	private final static String undefined = "1a234%sd3f1sa54e@%$#";

	public DataAnalyse(List<String> answer) {
		// 加载答案
		this.answer = answer;
		stuAnswer = new ArrayList<>();
	}

	/**
	 * Core analyse function.<br>
	 * 核心分析算法。
	 * @param list Student Node List. 学生节点列表。
	 * @return Repeat Rate. 重复率。
	 */
	public String[][] analyse(List<StudentNode> list) {
		logger.info("开始分析全体学生试卷相似度...");
		// 初始化存储答案的二维数组
		for (StudentNode s : list) {
			List<String> aStudent = new ArrayList<>();
			for (int i = 0; i < answer.size(); i++) {
				// 如果有答案优先读入答案
				if (!s.getAnswer().get(i).equals(answer.get(i))) {
					aStudent.add(s.getAnswer().get(i));
				} else {
					//如果没有错误，则在二维数组中加入自己定义的未定义字符
					aStudent.add(undefined);
				}
			}
			stuAnswer.add(aStudent);
		}
		// 初始化相似率二维数组
		String[][] rate = new String[list.size()][list.size() + 1];
		for (String[] strings : rate) {
			Arrays.fill(strings, "");
//			for (int j = 0; j < rate[i].length; j++) {
//				rate[i][j] = "";
//			}
		}

		for (int i = 0; i < stuAnswer.size() - 1; i++) {
			for (int k = i + 1; k < stuAnswer.size(); k++) {
				List<String> preStu = stuAnswer.get(i);
				List<String> nextStu = stuAnswer.get(k);
				// 错误且答案相同数目
				int sameAnswer = 0;
				// 错误且答案不同
				int similarAnswer = 0;
				// 两人共错误答案数目
				int wrongAnswer = 0;
				for (int j = 0; j < answer.size(); j++) {
					if (preStu.get(j).equals(undefined) && nextStu.get(j).equals(undefined)) {
						// 先判断俩人是否正确，若都正确则无操作。
					} else if (!preStu.get(j).equals(undefined) && !nextStu.get(j).equals(undefined)) {
						// 两个人都错了，错题数加一
						wrongAnswer++;
						allWrongAnswer += 2;
						if (preStu.get(j).equals(nextStu.get(j))) {
							// 如果两个人错误的答案一样，相同数目加一
							sameAnswer++;
							allSameAnswer += 2;
						} else {
							// 两个人错误答案不一样，答案不同数目加一
							similarAnswer++;
						}
					} else {
						// 只有一个人错了错题数加一
						wrongAnswer++;
						allWrongAnswer += 1;
					}
				}
				if (wrongAnswer == 0) {
					rate[i][k] = 0 + "%";
				} else {
					double bigWeight = 100 / wrongAnswer;
					double smallWeight = bigWeight / 10;
					int weight = (int) (bigWeight * sameAnswer + smallWeight * similarAnswer);
					rate[i][k] = weight + "%, " + sameAnswer;
				}
				rate[i][k + 1] = list.get(i).getName();
			}

		}

//		list.forEach((l) -> {System.out.print(l.getName() + "\t");});
		// 打印相似度
//		System.out.println();
//		for (int i = 0; i < rate.length - 1; i++) {
//			for (int j = 0; j < rate[i].length; j++) {
//				System.out.print(rate[i][j] + "\t");
//			}
//			System.out.println();
//		}

		logger.info("分析完成.");
		return rate;
	}

	/**
	 * Compute threshold.<br>
	 * 计算阈值。
	 * @param avgScore Avg Score. 平均成绩。
	 * @return threshold 阈值。
	 */
	public int getThreshold(int avgScore) {
//		// 自定义阈值算法
//		int wrongAnswer = (100 - avgScore) / (100 / answer.size());
//		double bigWeight = 100 / wrongAnswer;
//		double smallWeight = bigWeight / 10;
//		return (int) (bigWeight * wrongAnswer * 0.2 + smallWeight * wrongAnswer * 0.8);

		// 正态分布去修正
		// 此处未实际应用
//		int[] array = new int[20];
//		for (int i = 0; i < rate.length; i++) {
//			for(int j = 0; j < rate[i].length; j++) {
//				if (rate[i][j].contains("%")) {
//					int temp = Integer.parseInt(rate[i][j].replace("%", "").trim());
//					array[temp / 5]++;
//				}
//			}
//		}
//		// 打印正态分布， 范围为0 - 100，每5一个区间。
//		for (int item: array) {
//			System.out.print(item + "\t");
//		}

		// 数据校准替代之前的常数 1 / 5 与 4 / 5
		// 使用真实数据比对实际值接近之前的预想常数值
		System.out.println("\n完全相同" + allSameAnswer + "\n错题" + allWrongAnswer);

		// 自定义阈值算法第二版
		int wrongAnswer = (100 - avgScore) / (100 / answer.size());
		double bigWeight = 100 / wrongAnswer;
		double smallWeight = bigWeight / 10;
		// 阈值公式 = 较大阈值 * 错题数 * 完全相同比率 + 较小阈值 * 错题数 * 不完全相同比率
		return (int) (bigWeight * wrongAnswer * allSameAnswer / allWrongAnswer + smallWeight * wrongAnswer * (allWrongAnswer - allSameAnswer) / allWrongAnswer);
	}

}
