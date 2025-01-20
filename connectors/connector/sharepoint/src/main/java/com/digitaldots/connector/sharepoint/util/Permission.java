/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.sharepoint.util;

public enum Permission {

    FULLCONTROL("1073741829"), DESIGN("1073741828"), EDIT("1073741830"), CONTRIBUTE("1073741827"), READ("1073741826"),
    VIEWONLY("1073741924");

    private final String roles;

    Permission(String string) {
        this.roles = string;
    }

    @Override
    public String toString() {
        return roles;
    }
}
