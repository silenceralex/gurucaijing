package com.caijing.util;

public class GradeUtil {
	/**
	 * ͨ��grade���������ж����Ƽ��໹��������
	 * @param grade
	 * @return 2Ϊ�����࣬1Ϊ������
	 */
	public static int judgeStaus(String grade) {
		if (grade == null)
			return 0;
		String judgeStr = grade.replaceAll("\\s", "");
		if (!judgeStr.contains("����") && !judgeStr.contains("����")) {
			if (judgeStr.contains("����") || judgeStr.contains("����") || judgeStr.contains("�Ƽ�")) {
				return 2;
			} else if (judgeStr.contains("����")) {
				return 1;
			}
		} else {
			return 1;
		}
		return 0;
	}
}
