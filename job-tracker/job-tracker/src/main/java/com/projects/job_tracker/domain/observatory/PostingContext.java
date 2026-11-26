package com.projects.job_tracker.domain.observatory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

/** Manual context; null is unknown. Evidence supports the transcription, not a semantic guarantee. */
public record PostingContext(Instant publishedAt, String countryCode, String region, String city,
        WorkMode workMode, BigDecimal weeklyHours, ContractType contractType, Seniority seniority,
        RemoteScope remoteScope, String remoteDetails, String evidence) {
    public enum WorkMode { ONSITE, HYBRID, REMOTE }
    public enum ContractType { PERMANENT, FIXED_TERM, INDEPENDENT, INTERNSHIP }
    public enum Seniority { TRAINEE, JUNIOR, MID, SENIOR, LEAD }
    public enum RemoteScope { COUNTRY_ONLY, WORLDWIDE, RESTRICTED }
    public static PostingContext unknown() { return new PostingContext(null,null,null,null,null,null,null,null,null,null,null); }
    public PostingContext {
        countryCode = clean(countryCode); region = clean(region); city = clean(city);
        remoteDetails = clean(remoteDetails); evidence = clean(evidence);
        if (countryCode != null) {
            countryCode = countryCode.toUpperCase(Locale.ROOT);
            if (!Set.of(Locale.getISOCountries()).contains(countryCode))
                throw new IllegalArgumentException("Indica un país ISO de dos letras válido");
        }
        if (weeklyHours != null && (weeklyHours.signum() <= 0 || weeklyHours.compareTo(BigDecimal.valueOf(168)) > 0 || weeklyHours.scale() > 2))
            throw new IllegalArgumentException("La jornada debe estar entre más de 0 y 168 horas semanales, con hasta 2 decimales");
        if (weeklyHours != null) weeklyHours = weeklyHours.stripTrailingZeros();
        if (remoteScope != null && workMode != WorkMode.REMOTE && workMode != WorkMode.HYBRID)
            throw new IllegalArgumentException("Las restricciones remotas requieren modalidad remota o híbrida");
        if (remoteScope == RemoteScope.RESTRICTED && remoteDetails == null)
            throw new IllegalArgumentException("Describe las restricciones del trabajo remoto");
        if (remoteDetails != null && remoteScope != RemoteScope.RESTRICTED)
            throw new IllegalArgumentException("Selecciona alcance restringido para describir restricciones remotas");
        if (remoteScope == RemoteScope.COUNTRY_ONLY && countryCode == null)
            throw new IllegalArgumentException("El alcance nacional requiere país");
        if ((publishedAt != null || countryCode != null || region != null || city != null || workMode != null || weeklyHours != null
                || contractType != null || seniority != null || remoteScope != null) && evidence == null)
            throw new IllegalArgumentException("El contexto necesita una cita del texto original");
    }
    private static String clean(String value) { return value == null || value.isBlank() ? null : value.strip(); }
    public List<String> salaryMissingFields() {
        var missing = new ArrayList<String>();
        if (countryCode == null) missing.add("countryCode");
        if (workMode == null) missing.add("workMode");
        if (weeklyHours == null) missing.add("weeklyHours");
        if (contractType == null) missing.add("contractType");
        if (seniority == null) missing.add("seniority");
        if (workMode == WorkMode.ONSITE || workMode == WorkMode.HYBRID) {
            if (region == null) missing.add("region");
            if (city == null) missing.add("city");
        }
        if ((workMode == WorkMode.REMOTE || workMode == WorkMode.HYBRID) && remoteScope == null) missing.add("remoteScope");
        return List.copyOf(missing);
    }
}
