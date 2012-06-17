package com.livefyre.livecount;

import static com.livefyre.livecount.Util.p;
import static com.livefyre.livecount.Util.randomHost;
import static com.livefyre.livecount.Util.randomId;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LoadTester implements Runnable {
	public static MetricAggregator metrics = new MetricAggregator();

	private int nThreads = 8;
	private int nRequests = 8000;
	private List<String> hosts;
	private int delay = 5;

	public static Builder newbuilder() {
		return new Builder();
	}

	private LoadTester() {
		hosts = new ArrayList<String>();
		hosts.add("http://genes-macbook-pro.local:8905");
		hosts.add("http://genes-macbook-pro.local:8906");
		hosts.add("http://genes-macbook-pro.local:8907");
	}

	public void run() {
		Client[] clients = new Client[(int) Math.floor(nRequests / nThreads)];

		for (int i = 0; i < clients.length; i++) {
			clients[i] = new Client(randomHost(hosts), randomId(1), randomId(100000));
		}
		int i = 0;
		while (true) {
			clients[i].run();
			i++;
			if (i >= clients.length) {
				i = 0;
			}

			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
			}
		}

	}

	public void start() throws InterruptedException {
		p("Starting Load Tester { threads: %d, requests: %d }", nThreads, (int) Math.floor(nRequests / nThreads)
				* nThreads);

		for (int i = 0; i < nThreads; i++) {
			new Thread(this).start();
		}
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
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
