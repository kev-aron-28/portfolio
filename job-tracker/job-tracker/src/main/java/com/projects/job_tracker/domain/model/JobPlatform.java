package com.projects.job_tracker.domain.model;

public enum JobPlatform {
	OCC("occ"),
	LINKEDIN("linkedin"),
	INDEED("indeed"),
	COMPUTRABAJO("computrabajo");

	private final String source;

	JobPlatform(String source) {
		this.source = source;
	}

	public String source() {
		return source;
	}

	public static JobPlatform fromSource(String source) {
		for (JobPlatform platform : values()) {
			if (platform.source.equalsIgnoreCase(source)) {
				return platform;
			}
		}
		throw new IllegalArgumentException("Unknown platform source: " + source);
	}
}
