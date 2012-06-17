package com.livefyre.livecount;

import java.util.HashMap;
import java.util.Map;

import com.livefyre.livecount.PerformanceMetric.RequestType;

public class MetricAggregator {

	private Map<RequestType, MetricGroup> aggregates = new HashMap<RequestType, MetricAggregator.MetricGroup>();
	private MetricGroup totals = new MetricGroup();

	public void add(PerformanceMetric metric) {
		if (!aggregates.containsKey(metric.requestType)) {
			aggregates.put(metric.requestType, new MetricGroup());
		}
		MetricGroup group = aggregates.get(metric.requestType);
		group.add(metric);
		totals.add(metric);

		aggregates.put(metric.requestType, group);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("****************************:\n");
		sb.append("Totals:\n");
		sb.append(totals);
		sb.append("\n");

		sb.append("By Type: \n");

		for (RequestType type : aggregates.keySet()) {
			sb.append(type.name() + ":\n");
			sb.append(aggregates.get(type));
			sb.append("\n");
		}
		return sb.toString();
	}

	class MetricGroup {
		public long average = 0;
		public long count = 0;
		public long total = 0;
		public long errors = 0;
		public long shortestTrip = Long.MAX_VALUE;
		public long longestTrip = Long.MIN_VALUE;

		public synchronized void add(PerformanceMetric metric) {
			errors += metric.exception == null ? 0 : 1;
			total += metric.roundTripTime;
			count++;
			average = (total / count);
			longestTrip = metric.roundTripTime > longestTrip ? metric.roundTripTime : longestTrip;
			shortestTrip = metric.roundTripTime < shortestTrip ? metric.roundTripTime : shortestTrip;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("        Requests:      " + count + "\n");
			sb.append("        Errors:        " + errors + "\n");
			sb.append("        Total Time:    " + total + " ms\n");
			sb.append("        Shortest Time: " + shortestTrip + " ms\n");
			sb.append("        Average Time:  " + average + " ms\n");
			sb.append("        Longest Time:  " + longestTrip + " ms\n");
			return sb.toString();
		}
	}
}
