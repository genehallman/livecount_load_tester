package com.livefyre.livecount;

import java.util.List;

public abstract class Util {
	public static void p(Object format, Object... vars) {
		System.out.println(String.format(format.toString(), vars));
	}

	public static int randomId(int outOf) {
		return (int) Math.floor(Math.random() * outOf);
	}

	public static String randomHost(List<String> hosts) {
		return hosts.get((int) Math.floor(Math.random() * hosts.size()));
	}

}
