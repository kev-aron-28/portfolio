package com.projects.job_tracker.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.microsoft.playwright.BrowserType;

final class PlaywrightLaunchHelper {

	private PlaywrightLaunchHelper() {
	}

	static BrowserType.LaunchOptions headedInteractive() {
		BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(false);
		if (isWsl()) {
			options.setArgs(wslHeadedArgs());
		}
		return options;
	}

	private static List<String> wslHeadedArgs() {
		List<String> args = new ArrayList<>();
		args.add("--disable-gpu");
		args.add("--disable-dev-shm-usage");
		args.add("--no-sandbox");
		args.add("--disable-software-rasterizer");
		args.add("--disable-features=VizDisplayCompositor");
		String display = System.getenv("WAYLAND_DISPLAY");
		if (display != null && !display.isBlank()) {
			args.add("--ozone-platform=wayland");
		}
		return args;
	}

	static boolean isWsl() {
		try {
			return Files.readString(Path.of("/proc/version")).toLowerCase().contains("microsoft");
		}
		catch (IOException e) {
			return false;
		}
	}

}
