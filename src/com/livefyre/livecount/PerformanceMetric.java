package com.livefyre.livecount;

public class PerformanceMetric {
	public enum RequestType {
		Route, Count, Timeout
	}

	public RequestType requestType;
	public Exception exception = null;
	public long roundTripTime;
	public int returnCode = 0;

	public String toString() {
		StringBuilder sb = new StringBuilder().append(requestType).append(",").append(roundTripTime).append(",")
				.append(returnCode > 0 ? returnCode : "").append(",")
				.append(exception == null ? "" : exception.getMessage());
		return sb.toString();
	}
}
