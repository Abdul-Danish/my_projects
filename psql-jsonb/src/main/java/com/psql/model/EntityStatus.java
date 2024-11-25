package com.psql.model;

public enum EntityStatus {
    DRAFT, PENDING, APPROVED, ACTIVE, ARCHIVED, DISABLED, COMPLETED, ERROR, SUCCESS,
    // Solution Status
    NEW, MODIFYING,
    // Dataset Status
    READY, DEACTIVE, WAITING,
}
