package com.psql.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.psql.dto.SolutionDto;
import com.psql.model.EntityStatus;
import com.psql.model.Solution;
import com.psql.model.SolutionDeployment;
import com.psql.repository.PgRepository;
import com.psql.repository.ShopSaleRepository;
import com.psql.views.ShopSaleView;

@RestController
@RequestMapping("api/v1/solutions")
public class PgController {

    @Autowired
    private PgRepository pgRepository;
    
    @Autowired
    private ShopSaleRepository saleRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @PostMapping
    public ResponseEntity<Solution> save(@RequestBody SolutionDto solutionDto) {
        Solution solutionObj = objectMapper.convertValue(solutionDto, Solution.class);
        return ResponseEntity.ok(pgRepository.save(solutionObj));
    }
    
    @GetMapping("/findById/{id}")
    public ResponseEntity<Solution> findById(@PathVariable Long id) {
        return ResponseEntity.ok(pgRepository.findById(id).get());
    }
    
    @GetMapping("/findByName/{name}")
    public ResponseEntity<Solution> findByName(@PathVariable String name) {
        return ResponseEntity.ok(pgRepository.findByName(name).get());
    }
    
    @GetMapping("/findByNameAndStatus/{name}/{status}")
    public ResponseEntity<Solution> findByNameAndStatus(@PathVariable String name, @PathVariable String status) {
        return ResponseEntity.ok(pgRepository.findByNameAndStatus(name, EntityStatus.valueOf(status)).get());
    }
    
    @GetMapping("/findByEnvAlias/{alias}")
    public ResponseEntity<List<Solution>> findByEnvAlias(@PathVariable String alias) {
        return ResponseEntity.ok(pgRepository.findByAlias(alias));
    }
    
    @GetMapping("{id}/getNestedField/{value}")
    public ResponseEntity<Object> getNestedField(@PathVariable Long id, @PathVariable String value) {
        return ResponseEntity.ok(pgRepository.getNestedField(id, value));
    }
    
    @PutMapping("/updateSolution")
    public ResponseEntity<Solution> updateSolution( @RequestBody Solution solution) {
        return ResponseEntity.ok(pgRepository.save(solution));
    }
    
    @PutMapping("{id}/updateSolutionDeployment")
    public ResponseEntity<Solution> updateSolutionDeployment(@PathVariable Long id, @RequestBody SolutionDeployment solutionDeployment) {
        Solution solution = pgRepository.findById(id).get();
        solution.setDeployment(solutionDeployment);
        return ResponseEntity.ok(pgRepository.save(solution));
    }
    
    @PutMapping("{id}/updateEnvAlias/{envAlias}")
    public ResponseEntity<Integer> updateEnvAlias(@PathVariable Long id, @PathVariable String envAlias) {
        return ResponseEntity.ok(pgRepository.updateEnvAlias(id, envAlias));
    }
    
    @PutMapping("{id}/updatNestedField/{value}")
    public ResponseEntity<Integer> updatNestedField(@PathVariable Long id, @PathVariable String value) {
        return ResponseEntity.ok(pgRepository.updateNestedField(id, value));
    }
    
    /*
     * Views
     */
    
    @GetMapping("/sales")
    public ResponseEntity<List<ShopSaleView>> getViewData() {
        return ResponseEntity.ok(saleRepository.findAll());
    }
    
    /*
     * Test View Update (org.postgresql.util.PSQLException: ERROR: cannot update view "shop_sale_view")
     */
    @PutMapping("/sales")
    public ResponseEntity<ShopSaleView> updateView(@RequestBody ShopSaleView shopSaleView) {
        return ResponseEntity.ok(saleRepository.save(shopSaleView));
    }
    
    
//    @GetMapping("/findByEnvAlias/{alias}")
//    public ResponseEntity<List<Solution>> findByEnvAlias(@PathVariable String alias) {
//        return ResponseEntity.ok(pgRepository.findByAlias(alias));
//    }
//    
//    @GetMapping("/findByEnvAlias/{alias}")
//    public ResponseEntity<List<Solution>> findByEnvAlias(@PathVariable String alias) {
//        return ResponseEntity.ok(pgRepository.findByAlias(alias));
//    }
//    
//    @GetMapping("/findByEnvAlias/{alias}")
//    public ResponseEntity<List<Solution>> findByEnvAlias(@PathVariable String alias) {
//        return ResponseEntity.ok(pgRepository.findByAlias(alias));
//    }
    
    
    
}
