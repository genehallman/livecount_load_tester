package com.livefyre.livecount;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import com.livefyre.livecount.LoadTester.Builder;

public class Main {

	public static void main(String[] args) throws Exception {
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = parser.parse(buildOptions(), args);

		List<String> hosts = new ArrayList<String>();

		Builder builder = LoadTester.newbuilder();

		if (cmd.hasOption("h")) {
			BufferedReader hostFile = new BufferedReader(new FileReader(cmd.getOptionValue("h")));
			while (hostFile.ready()) {
				hosts.add(hostFile.readLine());
			}
			hostFile.close();
			builder.setHosts(hosts);
		}

		if (cmd.hasOption("t")) {
			builder.setThreads(Integer.parseInt(cmd.getOptionValue("t")));
		}

		if (cmd.hasOption("r")) {
			builder.setRequests(Integer.parseInt(cmd.getOptionValue("r")));
		}

		builder.build().start();
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
