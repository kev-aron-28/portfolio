package com.projects.job_tracker.domain.observatory;

import java.math.BigDecimal;
import java.util.Currency;

/** Manual transcription, never automatic inference. Null means unknown. */
public record PostingData(String title, String company, String location, String requirements,
        BigDecimal salaryMin, BigDecimal salaryMax, String currency, Period period, TaxBasis taxBasis, Boolean requirementsReviewed,
        java.util.List<SkillRequirement> skillRequirements, PostingContext context) {
    public PostingData(String title, String company, String location, String requirements,
            BigDecimal salaryMin, BigDecimal salaryMax, String currency, Period period, TaxBasis taxBasis) {
        this(title, company, location, requirements, salaryMin, salaryMax, currency, period, taxBasis, false, java.util.List.of(), PostingContext.unknown());
    }
    public PostingData(String title, String company, String location, String requirements,
            BigDecimal salaryMin, BigDecimal salaryMax, String currency, Period period, TaxBasis taxBasis,
            Boolean requirementsReviewed, java.util.List<SkillRequirement> skillRequirements) {
        this(title, company, location, requirements, salaryMin, salaryMax, currency, period, taxBasis,
            requirementsReviewed, skillRequirements, PostingContext.unknown());
    }
    public enum Period { HOUR, DAY, WEEK, MONTH, YEAR }
    public enum TaxBasis { GROSS, NET }
    public PostingData {
        context = context == null ? PostingContext.unknown() : context;
        requirementsReviewed = Boolean.TRUE.equals(requirementsReviewed);
        if (title == null || title.isBlank() || title.length() > 500)
            throw new IllegalArgumentException("El título es obligatorio y admite hasta 500 caracteres");
        if (skillRequirements != null && skillRequirements.stream().anyMatch(java.util.Objects::isNull))
            throw new IllegalArgumentException("No se admiten requisitos nulos");
        skillRequirements = skillRequirements == null ? java.util.List.of() : java.util.List.copyOf(skillRequirements);
        if (!requirementsReviewed && !skillRequirements.isEmpty())
            throw new IllegalArgumentException("Marca los requisitos como revisados antes de guardar habilidades");
        var skillIds = new java.util.HashSet<java.util.UUID>();
        for (var requirement : skillRequirements)
            if (!skillIds.add(requirement.skillId())) throw new IllegalArgumentException("Una habilidad sólo puede aparecer una vez por versión del anuncio");
        skillRequirements = skillRequirements.stream().sorted(java.util.Comparator.comparing(SkillRequirement::skillId)).toList();
        title = title.strip();
        company = clean(company); location = clean(location); requirements = clean(requirements);
        currency = clean(currency);
        if (currency != null) {
            currency = currency.toUpperCase(java.util.Locale.ROOT);
            try { Currency.getInstance(currency); }
            catch (IllegalArgumentException ex) { throw new IllegalArgumentException("Moneda ISO no válida"); }
        }
        for (BigDecimal salary : new BigDecimal[]{salaryMin, salaryMax})
            if (salary != null && (salary.signum() <= 0 || salary.scale() > 2 || salary.precision() - salary.scale() > 12))
                throw new IllegalArgumentException("El salario debe ser positivo, con hasta 12 enteros y 2 decimales");
        if (salaryMin != null && salaryMax != null && salaryMin.compareTo(salaryMax) > 0)
            throw new IllegalArgumentException("El mínimo no puede superar al máximo");
        if (salaryMin != null) salaryMin = salaryMin.stripTrailingZeros();
        if (salaryMax != null) salaryMax = salaryMax.stripTrailingZeros();
    }
    private static String clean(String value) { return value == null || value.isBlank() ? null : value.strip(); }
    public record Quality(boolean skillAnalysisReady, java.util.List<String> salaryMissingFields, String scope, boolean salaryCohortReady, boolean publicationDateKnown) {}
    public Quality getQuality() {
        var missing = new java.util.ArrayList<String>();
        if (salaryMin == null && salaryMax == null) missing.add("salaryBounds");
        if (currency == null) missing.add("currency");
        if (period == null) missing.add("period");
        if (taxBasis == null) missing.add("taxBasis");
        missing.addAll(context.salaryMissingFields());
        return new Quality(requirementsReviewed, java.util.List.copyOf(missing), "CONTEXT_COMPLETENESS_ONLY",
            missing.isEmpty(), context.publishedAt() != null);
    }
    /** Complete contextual metadata, not proof of equal work, compensation or market representativeness. */
    public boolean salaryComparable() { return getQuality().salaryCohortReady(); }
    public record SalaryCohortKey(String title, String currency, Period period, TaxBasis taxBasis,
            String countryCode, String region, String city, PostingContext.WorkMode workMode,
            BigDecimal weeklyHours, PostingContext.ContractType contractType, PostingContext.Seniority seniority,
            PostingContext.RemoteScope remoteScope, String remoteDetails) {}
    public SalaryCohortKey salaryCohortKey() {
        if (!salaryComparable()) return null;
        return new SalaryCohortKey(fold(title), currency, period, taxBasis, context.countryCode(),
            fold(context.region()), fold(context.city()), context.workMode(), context.weeklyHours().stripTrailingZeros(),
            context.contractType(), context.seniority(), context.remoteScope(), fold(context.remoteDetails()));
    }
    public boolean canCompareSalaryWith(PostingData other) {
        return other != null && salaryCohortKey() != null && salaryCohortKey().equals(other.salaryCohortKey());
    }
    private static String fold(String value) { return value == null ? null : value.strip().toLowerCase(java.util.Locale.ROOT).replaceAll("\\s+", " "); }
}
