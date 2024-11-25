package com.psql.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.psql.model.EntityStatus;
import com.psql.model.Solution;
import com.psql.model.SolutionDeployment;

@Repository
public interface PgRepository extends JpaRepository<Solution, Long> {

    Optional<Solution> findByName(String name);
    
    List<Solution> findByAlias(String alias);
    
    Optional<Solution> findByNameAndStatus(String name, EntityStatus status);
    
//    @Query(value = "SELECT deployment ->> 'alias', deployment -> 'nestedField' ->> 'tempField' FROM solutions WHERE id = :id AND deployment -> 'nestedField' ->> 'tempField' = :value", nativeQuery = true)
//    Object getNestedField(Long id, String value);

    /*
     * -> Operator returns JSON object field as JSON
     * ->> Operator returns JSON object field as text.
     */
    @Query(value = "SELECT * FROM solutions WHERE id = :id AND deployment -> 'nestedField' ->> 'tempField' = :value", nativeQuery = true)
    Solution getNestedField(Long id, String value);
    
    @Transactional
    @Modifying
    @Query(value = "UPDATE solutions SET deployment = jsonb_set(deployment, '{alias}', to_jsonb(:alias)) where id = :id", nativeQuery = true)
    int updateEnvAlias(Long id, String alias);
    
//    @Transactional
//    @Modifying
//    @Query(value = "UPDATE solutions sol set sol.deployment.alias = :alias where sol.id = :id", nativeQuery = true)
//    int updateEnvAlias(Long id, String alias);
    
    @Transactional
    @Modifying
    @Query(value = "UPDATE solutions SET deployment = jsonb_set(deployment, '{nestedField, tempField}', to_jsonb(:value)) where id = :id", nativeQuery = true)
    int updateNestedField(Long id, String value);

}
