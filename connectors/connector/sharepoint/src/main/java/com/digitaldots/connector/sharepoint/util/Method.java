/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.sharepoint.util;

public enum Method {
    CREATELIST("createList"), CREATELISTITEM("createListItem"), GETALLLISTS("getAllLists"), GETLISTBYTITLE("getListByTitle"),
    UPLOADFILETOLIST("uploadFileToList"), CREATEFOLDER("createFolder"), COUNTFOLDERITEMS("countFolderItems"),
    UPLOADFILETOFOLDER("uploadFileToFolder"), ADDUSERTOFOLDER("addUserToFolder"), ADDUSERTOGROUP("addUserToGroup");

    public final String label;

    Method(String string) {
        this.label = string;
    }
}
