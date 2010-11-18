package com.caijing.util;

import java.math.BigDecimal;

public class FloatUtil {
	private static int twoScale = 2;// 设置位数
	private static int fourScale = 4;// 设置位数
	private static int roundingMode = 4;// 表示四舍五入，可以选择其他舍值方式，例如去尾，等等.

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
