package com.livefyre.livecount;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

public class Main {

	public static void main(String[] args) throws Exception {
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = parser.parse(buildOptions(), args);

		int threads = 500;
		int requests = 100000;

		List<String> hosts = new ArrayList<String>();

		if (cmd.hasOption("h")) {
			BufferedReader hostFile = new BufferedReader(new FileReader(cmd.getOptionValue("h")));
			while (hostFile.ready()) {
				hosts.add(hostFile.readLine());
			}
			hostFile.close();
		} else {
			hosts.add("http://genes-macbook-pro.local:8905");
			hosts.add("http://genes-macbook-pro.local:8906");
			hosts.add("http://genes-macbook-pro.local:8907");
		}

		if (cmd.hasOption("t")) {
			if (cmd.hasOption("t")) {
				threads = (Integer.parseInt(cmd.getOptionValue("t")));
			}
		}

		if (cmd.hasOption("r")) {
			if (cmd.hasOption("r")) {
				requests = (Integer.parseInt(cmd.getOptionValue("r")));
			}
		}

		LoadTester.newbuilder().setHosts(hosts).setRequests(requests).setThreads(threads).build().start();
	}

	private static Options buildOptions() {
		Options options = new Options();
		options.addOption("help", false, "prints this message");
		options.addOption("t", true, "number of simultaneous threads");
		options.addOption("r", true, "number of requests");
		options.addOption("h", true, "host file location");
		return options;
	}
}
