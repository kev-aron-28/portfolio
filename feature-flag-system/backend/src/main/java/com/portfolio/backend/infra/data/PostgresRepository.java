package com.portfolio.backend.infra.data;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.portfolio.backend.domain.FeatureFlag;
import com.portfolio.backend.domain.FeatureFlagRepository;

@Repository
public class PostgresRepository implements FeatureFlagRepository {

    private NamedParameterJdbcTemplate template;

    public PostgresRepository(NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    @Override
    public void save(FeatureFlag featureFlag) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }
    
}
