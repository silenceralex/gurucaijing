package com.caijing.util;

public class GradeUtil {
	/**
	 * 通过grade的评级来判断是推荐类还是卖出类
	 * @param grade
	 * @return 2为买入类，1为卖出类
	 */
	public static int judgeStaus(String grade) {
		if (grade == null)
			return 0;
		String judgeStr = grade.replaceAll("\\s", "");
		if (!judgeStr.contains("谨慎") && !judgeStr.contains("审慎")) {
			if (judgeStr.contains("买入") || judgeStr.contains("增持") || judgeStr.contains("推荐")) {
				return 2;
			} else if (judgeStr.contains("中性")) {
				return 1;
			}
		} else {
			return 1;
		}
		return 0;
	}
}
