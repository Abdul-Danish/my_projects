package com.psql.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.psql.views.ShopSaleView;

//@NoRepositoryBean
public interface ShopSaleRepository extends JpaRepository<ShopSaleView, Long> {
//    extends Repository<ShopSaleView, Long> {

//    long count();
//
//    boolean existsById(long id);
//
//    List<ShopSaleView> findAll();
//
//    List<ShopSaleView> findAllById(List ids);
//
//    Optional<ShopSaleView> findById(long id);

    
}
