package com.livefyre.livecount;

import static com.livefyre.livecount.Util.p;
import static com.livefyre.livecount.Util.randomHost;
import static com.livefyre.livecount.Util.randomId;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LoadTester {
	public static MetricAggregator metrics = new MetricAggregator();

	private int nThreads;
	private int nRequests;
	private List<String> hosts;
	private int delay = 45;

	public static Builder newbuilder() {
		return new Builder();
	}

	public void start() throws InterruptedException {
		p("Starting Load Tester { threads: %d, requests: %d }", nThreads, nRequests);
		ScheduledExecutorService workers = Executors.newScheduledThreadPool(nThreads);
		for (int i = 0; i < nRequests; i++) {
			Client client = new Client(randomHost(hosts), randomId(3), randomId(100000));
			workers.scheduleWithFixedDelay(client, 0, delay, TimeUnit.SECONDS);
			Thread.sleep(delay * 1000 / nRequests);
		}
		workers.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				p(metrics);
			}
		}, 0, 10, TimeUnit.SECONDS);
	}

	static class Builder {
		private boolean isBuilt = false;
		private LoadTester loadTester;

		public Builder() {
			loadTester = new LoadTester();
		}

		private void checkBuilt() {
			if (isBuilt) {
				throw new IllegalStateException("The object cannot be modified after built");
			}
		}

		public Builder setThreads(int val) {
			checkBuilt();
			loadTester.nThreads = val;
			return this;
		}

		public Builder setHosts(List<String> val) {
			checkBuilt();
			loadTester.hosts = val;
			return this;
		}

		public Builder setRequests(int val) {
			checkBuilt();
			loadTester.nRequests = val;
			return this;
		}

		public LoadTester build() {
			checkBuilt();
			isBuilt = true;
			return loadTester;
		}
	}
}
