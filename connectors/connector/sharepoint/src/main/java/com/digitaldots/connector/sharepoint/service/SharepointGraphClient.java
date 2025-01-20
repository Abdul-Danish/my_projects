/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.sharepoint.service;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Base64;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.digitaldots.connector.ConnectorException;
import com.digitaldots.connector.sharepoint.util.Method;
import com.digitaldots.connector.sharepoint.util.SharepointBean;
import com.digitaldots.connector.sharepoint.util.SharepointEntity;
import com.digitaldots.connector.sharepoint.util.SharepointHelper;
import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.models.FieldValueSet;
import com.microsoft.graph.models.Folder;
import com.microsoft.graph.models.List;
import com.microsoft.graph.models.ListInfo;
import com.microsoft.graph.models.ListItem;
import com.microsoft.kiota.ApiException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SharepointGraphClient {

    private static final int STATUS_CODE_SUCCESS = 200;
    private static final String STATUS = "status";

    public JSONObject execute(SharepointBean bean, SharepointHelper sharepointHelper) {
        JSONObject response = null;
        log.debug("-------------" + Method.CREATELIST.label);

        if (Method.CREATELIST.label.equals(bean.method)) {
            response = createList(bean.folderName, bean.description, sharepointHelper);
        } else if (Method.CREATELISTITEM.label.equals(bean.method)) {
            response = createListItem(bean.folderName, sharepointHelper);
        } else if (Method.GETALLLISTS.label.equals(bean.method)) {
            response = getAllLists(sharepointHelper);
        } else if (Method.GETLISTBYTITLE.label.equals(bean.method)) {
            response = getListByTitle(bean.folderName, sharepointHelper);
        } else if (Method.UPLOADFILETOLIST.label.equals(bean.method)) {
            response = uploadFileToList(bean.folderName, bean.listTitle, bean.file, bean.fileName, sharepointHelper);
        } else if (Method.CREATEFOLDER.label.equals(bean.method)) {
            response = createFolder(bean.serverRelatUrl, bean.folderName, bean.rootFolder, sharepointHelper);
        } else if (Method.UPLOADFILETOFOLDER.label.equals(bean.method)) {
            response = uploadFileToFolder(bean.serverRelatUrl, bean.folderName, bean.file, bean.fileName, sharepointHelper);
        } else {
            throw new ConnectorException("method not supported");
        }
        return response;

    }

    private static JSONObject createList(String folderName, String description, SharepointHelper sharepointHelper) {
        JSONObject response = new JSONObject();
        List list = new List();
        list.setDisplayName(folderName);
        ListInfo listInfo = new ListInfo();
        listInfo.setTemplate(description);
        List listObj = sharepointHelper.getGraphClient().sites().bySiteId(sharepointHelper.getSiteId()).lists().post(list);
        SharepointEntity entity = SharepointEntity.builder().id(listObj.getId()).name(listObj.getName())
            .description(listObj.getDescription()).displayName(listObj.getDisplayName()).build();
        response.put(STATUS, STATUS_CODE_SUCCESS);
        response.put("data", entity);
        return response;
    }

    private static JSONObject createListItem(String folderName, SharepointHelper sharepointHelper) {
        JSONObject response = new JSONObject();
        ListItem listItem = new ListItem();
        FieldValueSet fields = new FieldValueSet();
        listItem.setFields(fields);
        ListItem listItemObj = sharepointHelper.getGraphClient().sites().bySiteId(sharepointHelper.getSiteId()).lists().byListId(folderName)
            .items().post(listItem);
        SharepointEntity entity = SharepointEntity.builder().id(listItemObj.getId()).name(listItemObj.getName())
            .description(listItemObj.getDescription()).build();
        response.put(STATUS, STATUS_CODE_SUCCESS);
        response.put("data", entity);
        return response;
    }

    private static JSONObject getAllLists(SharepointHelper sharepointHelper) {
        /*
        JSONObject response = new JSONObject();
        LinkedList<Option> requestOptions = new LinkedList<>();
        requestOptions.add(new QueryOption("expand", "fields(select=Name,Color,Quantity)"));
        ListCollectionPage listCollection = sharepointHelper.getGraphClient().sites(sharepointHelper.getSiteId()).lists()
            .buildRequest(requestOptions).get();
        java.util.List<List> list = listCollection.getCurrentPage();
        */
        JSONObject response = new JSONObject();
        java.util.List<List> list = sharepointHelper.getGraphClient().sites().bySiteId(sharepointHelper.getSiteId()).lists()
            .get(req -> req.queryParameters.expand = new String[] { "fields(select=Name,Color,Quantity)" }).getValue();
        java.util.List<SharepointEntity> entityList = new ArrayList<>();
        for (List list2 : list) {
            SharepointEntity entity = SharepointEntity.builder().id(list2.getId()).name(list2.getName()).description(list2.getDescription())
                .displayName(list2.getDisplayName()).build();
            entityList.add(entity);
        }
        response.put(STATUS, STATUS_CODE_SUCCESS);
        response.put("data", entityList.toString());
        return response;
    }

    private static JSONObject getListByTitle(String folderName, SharepointHelper sharepointHelper) {
        /*
        JSONObject response = new JSONObject();
        LinkedList<Option> requestOptions = new LinkedList<>();
        requestOptions.add(new QueryOption("expand", "fields(select=Name,Color,Quantity)"));
        List list = sharepointHelper.getGraphClient().sites(sharepointHelper.getSiteId()).lists(folderName).buildRequest(requestOptions)
            .get();
        */
        JSONObject response = new JSONObject();
        List list = sharepointHelper.getGraphClient().sites().bySiteId(sharepointHelper.getSiteId()).lists().byListId(folderName)
            .get(req -> req.queryParameters.expand = new String[] { "fields(select=Name,Color,Quantity)" });
        SharepointEntity entity = SharepointEntity.builder().id(list.getId()).name(list.getName()).description(list.getDescription())
            .displayName(list.getDisplayName()).build();
        response.put(STATUS, STATUS_CODE_SUCCESS);
        response.put("data", entity);
        return response;
    }

    private static JSONObject uploadFileToList(String folderName, String listTitle, byte[] file, String fileName,
        SharepointHelper sharepointHelper) {
        JSONObject response = new JSONObject();
        byte[] stream = Base64.getDecoder().decode(file);
        // DriveItem driveItem = sharepointHelper.getGraphClient().sites(sharepointHelper.getSiteId()).lists(listTitle).drive().root()
        // .itemWithPath("/" + folderName + "/" + fileName).content().buildRequest().put(stream);

        String driveId = sharepointHelper.getGraphClient().sites().bySiteId(sharepointHelper.getSiteId()).lists().byListId(listTitle)
            .drive().get().getId();
        DriveItem driveItem = sharepointHelper.getGraphClient().drives().byDriveId(driveId).items().byDriveItemId(folderName).content()
            .put(new ByteArrayInputStream(stream));

        SharepointEntity entity = SharepointEntity.builder().id(driveItem.getId()).name(driveItem.getName())
            .description(driveItem.getDescription()).build();
        response.put(STATUS, STATUS_CODE_SUCCESS);
        response.put("data", entity);
        return response;
    }

    private static JSONObject createFolder(String serverRelatUrl, String folderName, String rootFolder, SharepointHelper sharepointHelper) {
        DriveItem driveItem = new DriveItem();
        driveItem.setName(folderName);
        Folder folder = new Folder();
        driveItem.setFolder(folder);
        try {
            String siteDriveId = sharepointHelper.getGraphClient().sites().bySiteId(sharepointHelper.getSiteId()).lists()
                .byListId(serverRelatUrl).drive().get().getId();
            if (null != rootFolder) {
                // driveItem = sharepointHelper.getGraphClient().sites(sharepointHelper.getSiteId()).lists(serverRelatUrl).drive().root()
                // .itemWithPath(rootFolder).children().buildRequest().post(driveItem);
                driveItem = sharepointHelper.getGraphClient().drives().byDriveId(siteDriveId).items().byDriveItemId(rootFolder).children()
                    .post(driveItem);
            } else {
                // driveItem = sharepointHelper.getGraphClient().sites(sharepointHelper.getSiteId()).lists(serverRelatUrl).drive().root()
                // .children().buildRequest().post(driveItem);
                driveItem = sharepointHelper.getGraphClient().drives().byDriveId(siteDriveId).items().post(driveItem);
            }
        } catch (ApiException gse) {
            if (HttpStatus.CONFLICT.value() != gse.getResponseStatusCode()) {
                throw gse;
            }
        }

        SharepointEntity entity = SharepointEntity.builder().id(driveItem.getId()).name(driveItem.getName())
            .description(driveItem.getDescription()).build();
        JSONObject response = new JSONObject();
        response.put(STATUS, STATUS_CODE_SUCCESS);
        response.put("data", entity);
        return response;
    }

    private static JSONObject uploadFileToFolder(String serverRelatUrl, String folderName, byte[] file, String fileName,
        SharepointHelper sharepointHelper) {
        JSONObject response = new JSONObject();
        byte[] stream = Base64.getDecoder().decode(file);
        // DriveItem driveItem = sharepointHelper.getGraphClient().sites(sharepointHelper.getSiteId()).lists(serverRelatUrl).drive().root()
        // .itemWithPath("/" + folderName + "/" + fileName).content().buildRequest().put(stream);

        String driveId = sharepointHelper.getGraphClient().sites().bySiteId(sharepointHelper.getSiteId()).lists().byListId(serverRelatUrl)
            .drive().get().getId();
        DriveItem driveItem = sharepointHelper.getGraphClient().drives().byDriveId(driveId).items().byDriveItemId(folderName).content()
            .put(new ByteArrayInputStream(stream));

        SharepointEntity entity = SharepointEntity.builder().id(driveItem.getId()).name(driveItem.getName())
            .description(driveItem.getDescription()).build();
        response.put(STATUS, STATUS_CODE_SUCCESS);
        response.put("data", entity);
        return response;
    }
}
