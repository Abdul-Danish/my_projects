/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.cache;

import java.util.Map;

public interface Store<D, C> {

    D getDataSource(Map<String, Object> configProperties);

    String getDBTye();

    C getConnection(D datasource);

    default Object validate(D dataSource, Map<String, Object> parameters) {
        return "Not Implemented";
    }
}
