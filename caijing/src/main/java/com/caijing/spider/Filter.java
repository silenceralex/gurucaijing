package com.caijing.spider;

import java.util.regex.Pattern;

public class Filter {
	private boolean usePatter = false;
	private String filterStr = null;

	public boolean isUsePatter() {
		return usePatter;
	}

	public void setUsePatter(boolean usePatter) {
		this.usePatter = usePatter;
	}

	public String filter(String input) {
		if (!usePatter) {
			return input.replace(filterStr, "");
		} else {
			Pattern filterPattern = Pattern.compile(filterStr, Pattern.CASE_INSENSITIVE | Pattern.DOTALL
					| Pattern.UNIX_LINES);
			return filterPattern.matcher(input).replaceAll("");
		}
	}

	public String getFilterStr() {
		return filterStr;
	}

	public void setFilterStr(String filterStr) {
		this.filterStr = filterStr;
	}
}
