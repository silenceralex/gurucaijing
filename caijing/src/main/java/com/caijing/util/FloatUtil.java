package com.caijing.util;

import java.math.BigDecimal;

public class FloatUtil {
	private static int twoScale = 2;// ����λ��
	private static int fourScale = 4;// ����λ��
	private static int roundingMode = 4;// ��ʾ�������룬����ѡ��������ֵ��ʽ������ȥβ���ȵ�.

	public static float getTwoDecimal(float f) {
		BigDecimal bd = new BigDecimal(f);
		bd = bd.setScale(twoScale, roundingMode);
		return bd.floatValue();
	}

	public float getTwoDecimalNumber(float f) {
		BigDecimal bd = new BigDecimal(f);
		bd = bd.setScale(twoScale, roundingMode);
		return bd.floatValue();
	}

	public static float getFourDecimal(float f) {
		BigDecimal bd = new BigDecimal(f);
		bd = bd.setScale(fourScale, roundingMode);
		return bd.floatValue();
	}
}
