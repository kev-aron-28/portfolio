package com.projects.job_tracker.domain.model;

import java.util.List;

public enum ApplicationStatus {
	APPLIED,
	SCREENING,
	INTERVIEWING,
	OFFER,
	REJECTED,
	WITHDRAWN;

	public static List<ApplicationStatus> pipeline() {
		return List.of(APPLIED, SCREENING, INTERVIEWING, OFFER);
	}

	public static List<ApplicationStatus> outcomes() {
		return List.of(REJECTED, WITHDRAWN);
	}

	public String label() {
		return switch (this) {
			case APPLIED -> "Postulado";
			case SCREENING -> "Screening";
			case INTERVIEWING -> "Entrevista";
			case OFFER -> "Oferta";
			case REJECTED -> "Rechazado";
			case WITHDRAWN -> "Retirado";
		};
	}

	public String icon() {
		return switch (this) {
			case APPLIED -> "send";
			case SCREENING -> "file-search";
			case INTERVIEWING -> "messages-square";
			case OFFER -> "award";
			case REJECTED -> "x-circle";
			case WITHDRAWN -> "undo-2";
		};
	}
}
