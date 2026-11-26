package com.projects.job_tracker.domain.observatory;

import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class SalaryCohortTest {
    private PostingData posting(String title, String currency, String country, String hours, PostingContext.WorkMode mode,
            PostingContext.ContractType contract, PostingContext.RemoteScope remoteScope) {
        return new PostingData(title,null,null,null,new BigDecimal("30000"),null,currency,PostingData.Period.MONTH,
            PostingData.TaxBasis.GROSS,false,List.of(), new PostingContext(null,country,"Jalisco","Guadalajara", mode,
            new BigDecimal(hours),contract,PostingContext.Seniority.SENIOR,remoteScope,null,"Texto de respaldo"));
    }
    private PostingData baseline() { return posting("Java backend","MXN","MX","40",PostingContext.WorkMode.ONSITE,PostingContext.ContractType.PERMANENT,null); }
    @Test void normalizesWhitespaceCaseAndDecimalScaleButDoesNotConvertCurrencyHoursOrContract() {
        assertThat(baseline().canCompareSalaryWith(posting(" JAVA   BACKEND ","mxn","mx","40.00",PostingContext.WorkMode.ONSITE,PostingContext.ContractType.PERMANENT,null))).isTrue();
        assertThat(baseline().canCompareSalaryWith(posting("Java backend","USD","MX","40",PostingContext.WorkMode.ONSITE,PostingContext.ContractType.PERMANENT,null))).isFalse();
        assertThat(baseline().canCompareSalaryWith(posting("Java backend","MXN","MX","20",PostingContext.WorkMode.ONSITE,PostingContext.ContractType.PERMANENT,null))).isFalse();
        assertThat(baseline().canCompareSalaryWith(posting("Java backend","MXN","MX","40",PostingContext.WorkMode.ONSITE,PostingContext.ContractType.INDEPENDENT,null))).isFalse();
        assertThat(baseline().canCompareSalaryWith(posting("Java backend","MXN","US","40",PostingContext.WorkMode.ONSITE,PostingContext.ContractType.PERMANENT,null))).isFalse();
        assertThat(baseline().canCompareSalaryWith(posting("Data engineer","MXN","MX","40",PostingContext.WorkMode.ONSITE,PostingContext.ContractType.PERMANENT,null))).isFalse();
    }
    @Test void keepsUnknownAndIncompleteRemoteRestrictionsOutOfCohorts() {
        var legacy = new PostingData("Java backend",null,null,null,new BigDecimal("30000"),null,"MXN",PostingData.Period.MONTH,PostingData.TaxBasis.GROSS);
        assertThat(legacy.salaryComparable()).isFalse();
        assertThat(legacy.salaryCohortKey()).isNull();
        assertThat(legacy.canCompareSalaryWith(legacy)).isFalse();
        var remoteUnknown = posting("Java backend","MXN","MX","40",PostingContext.WorkMode.REMOTE,PostingContext.ContractType.PERMANENT,null);
        assertThat(remoteUnknown.getQuality().salaryMissingFields()).contains("remoteScope");
        var national = posting("Java backend","MXN","MX","40",PostingContext.WorkMode.REMOTE,PostingContext.ContractType.PERMANENT,PostingContext.RemoteScope.COUNTRY_ONLY);
        var worldwide = posting("Java backend","MXN","MX","40",PostingContext.WorkMode.REMOTE,PostingContext.ContractType.PERMANENT,PostingContext.RemoteScope.WORLDWIDE);
        assertThat(national.salaryComparable()).isTrue();
        assertThat(national.canCompareSalaryWith(worldwide)).isFalse();
    }
}
