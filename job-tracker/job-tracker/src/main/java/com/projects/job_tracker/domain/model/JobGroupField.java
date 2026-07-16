package com.projects.job_tracker.domain.model;

public enum JobGroupField {
	SOURCE("source", "Plataforma"),
	LOCATION("location", "Ubicación"),
	WORK_MODE("workMode", "Modalidad"),
	APPLICATION_STATUS("applicationStatus", "Estado"),
	CATEGORY("category", "Categoría"),
	COMPANY("company", "Empresa"),
	TECHNOLOGY("technology", "Tecnología"),
	NONE("none", "Sin agrupar");

	private final String param;
	private final String label;

	JobGroupField(String param, String label) {
		this.param = param;
		this.label = label;
	}

	public String param() {
		return param;
	}

	public String label() {
		return label;
	}

	public static JobGroupField fromParam(String value) {
		if (value == null || value.isBlank()) {
			return SOURCE;
		}
		for (JobGroupField field : values()) {
			if (field.param.equalsIgnoreCase(value) || field.name().equalsIgnoreCase(value)) {
				return field;
			}
		}
		return SOURCE;
	}
}
