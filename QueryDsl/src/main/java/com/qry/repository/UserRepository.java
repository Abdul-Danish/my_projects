package com.qry.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Component;

import com.qry.model.QUser;
import com.qry.model.User;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import java.util.List;


@Component
public interface UserRepository extends MongoRepository<User, String>, QuerydslPredicateExecutor<User>, QuerydslBinderCustomizer<QUser> {
    
    @Override
    default void customize(final QuerydslBindings bindings, final QUser user)
    {
//        bindings.bind(user.bid).first((bid, value) -> bid.eq(value));
//        bindings.bind(String.class).first((StringPath path, String value) -> path.containsIgnoreCase(value));
        
//        bindings.excluding(user.name, user.id);
//        bindings.bind(QUser.user.bid).first((bid, value) -> bid.like(value.toString()));
//        bindings.bind(String.class).first((StringPath path, String value) -> (path).like(value.toString()));
    }
    
}
