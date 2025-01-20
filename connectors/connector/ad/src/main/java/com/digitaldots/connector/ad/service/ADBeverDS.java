/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.ad.service;

import org.springframework.stereotype.Component;

import com.digitaldots.connector.graphapi.service.GraphApiBeverDS;

@Component
public class ADBeverDS extends GraphApiBeverDS {

    @Override
    public String getDBTye() {
        return "AD";
    }

}
