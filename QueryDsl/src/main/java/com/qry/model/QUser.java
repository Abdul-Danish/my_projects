package com.qry.model;

import javax.annotation.Generated;

import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.StringPath;

@Generated("com.querydsl.codegen.EntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -8522387929131202269L;
    public static final QUser user = new QUser("user");
    public final StringPath bid = createString("bid");
    public final StringPath name = createString("name");
    public final StringPath id = createString("id");

    public QUser(String variable) {
        super(User.class, variable);
    }

    public StringPath getStringPath(String element) {
        return createString(element);
    }

    public BooleanPath getBooleanPath(String element) {
        return createBoolean(element);
    }
}
