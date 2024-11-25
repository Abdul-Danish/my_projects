package com.psql.views;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.ToString;

@Entity
@Immutable
@Table(name = "shop_sale_view")
@Getter
//@Builder
@ToString
//@NoArgsConstructor
//@AllArgsConstructor
//@Subselect("create view shop_sales as select row_number() over() as id, shop_id, transaction_year, transaction_month\n"
//    + "    , sum(amount) as total_amount\n"
//    + "    from (select shop.shop_id, shop.shop_location, trans.amount, EXTRACT(YEAR FROM transaction_date)\n"
//    + "                          as transaction_year, EXTRACT(MONTH FROM transaction_date) as transaction_month \n"
//    + "                          from shop shop, shop_transaction trans where shop.shop_id = trans.shop_id) \n"
//    + "                          shop_month_transaction \n"
//    + "                          group by shop_id, transaction_year, transaction_month")
public class ShopSaleView {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "shop_id")
    private int shopId;

//    @Column(name = "shop_location", length = 100)
//    private String shopLocation;

    @Column(name = "transaction_year")
    private int year;

    @Column(name = "transaction_month")
    private int month;

    @Column(name = "total_amount")
    private float totalAmount;
    
}
