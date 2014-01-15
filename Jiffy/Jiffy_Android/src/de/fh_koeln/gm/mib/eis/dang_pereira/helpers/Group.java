package de.fh_koeln.gm.mib.eis.dang_pereira.helpers;

import java.util.ArrayList;
import java.util.List;

public class Group {
	public String string;
	public final List<String> children = new ArrayList<String>();

	public Group(String string) {
		this.string = string;
	}
}
