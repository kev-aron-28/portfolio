package com.projects.job_tracker.domain.model;

public enum FollowUpSignal {
	UNAPPLIED,
	ACTIVE,
	WATCH,
	LIKELY_SILENT,
	CLOSED;

	public String label() {
		return switch (this) {
			case UNAPPLIED -> "Sin postular";
			case ACTIVE -> "En espera";
			case WATCH -> "Revisar";
			case LIKELY_SILENT -> "Probable silencio";
			case CLOSED -> "Cerrada";
		};
	}

	public boolean needsAttention() {
		return this == WATCH || this == LIKELY_SILENT;
	}
}
