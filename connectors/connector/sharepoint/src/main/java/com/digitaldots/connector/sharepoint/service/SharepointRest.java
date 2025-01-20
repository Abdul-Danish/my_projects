/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.sharepoint.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.entity.ByteArrayEntity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.digitaldots.api.client.RestClient;
import com.digitaldots.connector.ConnectorException;
import com.digitaldots.connector.sharepoint.util.Method;
import com.digitaldots.connector.sharepoint.util.SharepointBean;
import com.digitaldots.connector.sharepoint.util.SharepointConstants;
import com.digitaldots.connector.sharepoint.util.SharepointEntity;
import com.digitaldots.connector.sharepoint.util.SharepointHelper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SharepointRest {

    private static final String API_GETBYTITLE = "/_api/Web/Lists/getbytitle('";
    private static final String API_WEB_GET_FOLDER = "/_api/web/GetFolderByServerRelativeUrl('";
    private static final int TWO_HUNDRED = 200;
    private static final int HUNDRED = 100;
    private static final String STATUS = "status";
    private static final String METADATA = "__metadata";
    private static final String TITLE = "Title";
    private static final String EXCEPTION_MESSAGE = "Exception while request to sharepoint";
    private static final String DESCRIPTION = "Description";
    private static final String CREATED = "Created";

    @Autowired
    private RestClient restClient;

    public JSONObject execute(SharepointBean bean, SharepointHelper sharepointHelper) {

        JSONObject response = null;
        if (Method.CREATELIST.label.equals(bean.method)) {
            response = createList(bean, sharepointHelper);
        } else if (Method.CREATELISTITEM.label.equals(bean.method)) {
            response = createListItem(bean, sharepointHelper);
        } else if (Method.GETALLLISTS.label.equals(bean.method)) {
            response = getAllLists(bean, sharepointHelper);
        } else if (Method.GETLISTBYTITLE.label.equals(bean.method)) {
            response = getListByTitle(bean, sharepointHelper);
        } else if (Method.UPLOADFILETOLIST.label.equals(bean.method)) {
            response = uploadFileToList(bean, sharepointHelper);
        } else if (Method.CREATEFOLDER.label.equals(bean.method)) {
            response = createFolder(bean, sharepointHelper);
        } else if (Method.COUNTFOLDERITEMS.label.equals(bean.method)) {
            response = countFolderItems(bean, sharepointHelper);
        } else if (Method.UPLOADFILETOFOLDER.label.equals(bean.method)) {
            response = uploadFileToFolder(bean, sharepointHelper);
        } else if (Method.ADDUSERTOFOLDER.label.equals(bean.method)) {
            response = addUserToFolder(bean, sharepointHelper);
        } else if (Method.ADDUSERTOGROUP.label.equals(bean.method)) {
            response = addUserToGroup(bean, sharepointHelper);
        } else {
            throw new ConnectorException("method not supported");
        }
        return response;

    }

    private JSONObject createList(SharepointBean bean, SharepointHelper sharepointHelper) {
        URI listUrl = getSharepointSiteUrl(sharepointHelper, bean.siteName, "/_api/Web/Lists");

        JSONObject meta = new JSONObject();
        meta.put("type", "SP.List");
        JSONObject payload = new JSONObject();
        payload.put(METADATA, meta);
        payload.put("BaseTemplate", HUNDRED);
        payload.put(TITLE, bean.folderName);
        if (bean.description != null) {
            payload.put(DESCRIPTION, bean.description);
        }

        JSONObject response = getPostResponse(listUrl, payload.toString(), sharepointHelper);
        if (response.get("data") != null) {
            JSONObject result = response.getJSONObject("data");
            SharepointEntity entity = SharepointEntity.builder().id(result.get("Id").toString()).name(result.get(TITLE).toString())
                .description(result.get(DESCRIPTION).toString()).itemCount(result.get("ItemCount").toString())
                .createdAt(result.get(CREATED).toString()).type(result.getJSONObject(METADATA).get("type").toString())
                .webUrl(result.getJSONObject(METADATA).get("uri").toString()).build();
            response.put("data", entity);
        }
        return response;
    }

    private JSONObject createListItem(SharepointBean bean, SharepointHelper sharepointHelper) {
        URI listItemUrl = getSharepointSiteUrl(sharepointHelper, bean.siteName, API_GETBYTITLE + bean.folderName + "')/items");

        JSONObject meta = new JSONObject();
        meta.put("type", "SP.Data." + bean.folderName + "ListItem");
        JSONObject payload = new JSONObject();
        payload.put(METADATA, meta);
        payload.put(TITLE, bean.listTitle);

        JSONObject response = getPostResponse(listItemUrl, payload.toString(), sharepointHelper);
        if (response.get("data") != null) {
            JSONObject result = response.getJSONObject("data");
            SharepointEntity entity = SharepointEntity.builder().id(result.get("Id").toString()).name(result.get(TITLE).toString())
                .attachments(result.get("Attachments").toString()).createdAt(result.get(CREATED).toString())
                .type(result.getJSONObject(METADATA).get("type").toString()).webUrl(result.getJSONObject(METADATA).get("uri").toString())
                .build();
            response.put("data", entity);
        }
        return response;
    }

    private JSONObject getAllLists(SharepointBean bean, SharepointHelper sharepointHelper) {
        URI getAllItemUrl = getSharepointSiteUrl(sharepointHelper, bean.siteName, "/_api/web/lists");
        ResponseEntity<String> responseEntity = getGetResponseWithoutBody(getAllItemUrl, sharepointHelper);
        JSONObject response = new JSONObject();

        if (responseEntity.hasBody()) {
            JSONArray result = new JSONObject(responseEntity.getBody()).getJSONObject("d").getJSONArray("results");
            java.util.List<SharepointEntity> entityList = new ArrayList<>();
            for (int i = 0; i < result.length(); i++) {
                JSONObject innInnerObj = result.getJSONObject(i);
                SharepointEntity entity = SharepointEntity.builder().id(innInnerObj.get("Id").toString())
                    .name(innInnerObj.get(TITLE).toString()).createdAt(innInnerObj.get(CREATED).toString())
                    .description(innInnerObj.get(DESCRIPTION).toString()).type(innInnerObj.getJSONObject(METADATA).get("type").toString())
                    .webUrl(innInnerObj.getJSONObject(METADATA).get("uri").toString()).build();

                entityList.add(entity);
            }
            response.put(STATUS, responseEntity.getStatusCodeValue());
            response.put("data", entityList.toString());
        } else {
            response.put(STATUS, responseEntity.getStatusCodeValue());
        }
        return response;
    }

    private JSONObject getListByTitle(SharepointBean bean, SharepointHelper sharepointHelper) {
        URI getListUrl = getSharepointSiteUrl(sharepointHelper, bean.siteName, API_GETBYTITLE + bean.folderName + "')");

        JSONObject response = getGetResponse(getListUrl, sharepointHelper);

        if (response.get("data") != null) {
            JSONObject result = response.getJSONObject("data");
            SharepointEntity entity = SharepointEntity.builder().id(result.get("Id").toString()).name(result.get(TITLE).toString())
                .description(result.get(DESCRIPTION).toString()).createdAt(result.get(CREATED).toString())
                .type(result.getJSONObject(METADATA).get("type").toString()).webUrl(result.getJSONObject(METADATA).get("uri").toString())
                .build();
            response.put("data", entity);
        }
        return response;
    }

    private Object getList(SharepointBean bean, SharepointHelper sharepointHelper) {
        URI getListUrl = getSharepointSiteUrl(sharepointHelper, bean.siteName, API_GETBYTITLE + bean.folderName + "')");
        try {
            getGetResponseWithoutBody(getListUrl, sharepointHelper);
            return HttpStatus.SC_OK;
        } catch (RuntimeException e) {
            log.error("Exception occured while listing ", e);
            return HttpStatus.SC_NOT_FOUND;
        }
    }

    private JSONObject uploadFileToList(SharepointBean bean, SharepointHelper sharepointHelper) {

        if (getList(bean, sharepointHelper).equals(HttpStatus.SC_NOT_FOUND)) {
            createList(bean, sharepointHelper);
        }

        // calling create list item
        JSONObject jsonList = createListItem(bean, sharepointHelper);
        // fetching uri for attach the file from list metadata

        ModelMapper mapper = new ModelMapper();
        SharepointEntity entity = mapper.map(jsonList.get("data"), SharepointEntity.class);

        URI postUrl = getSharepointSiteUrl(entity.getWebUrl() + "/AttachmentFiles/add(FileName='" + bean.fileName + "')");

        // calling client method to post
        return getPostResponseClient(postUrl, bean.file, sharepointHelper);
    }

    private JSONObject createFolder(SharepointBean bean, SharepointHelper sharepointHelper) {
        URI createFolderUrl = getSharepointSiteUrl(sharepointHelper, bean.siteName,
            "/_api/web/GetFolderByServerRelativeUrl('/" + bean.serverRelatUrl + "')/folders");
        JSONObject meta = new JSONObject();
        meta.put("type", "SP.Folder");
        JSONObject payload = new JSONObject();
        payload.put(METADATA, meta);
        payload.put("ServerRelativeUrl", bean.serverRelatUrl + "/" + bean.folderName);

        JSONObject response = getPostResponse(createFolderUrl, payload.toString(), sharepointHelper);

        if (response.get("data") != null) {
            JSONObject result = response.getJSONObject("data");
            SharepointEntity entity = SharepointEntity.builder().id(result.get("UniqueId").toString()).name(result.get("Name").toString())
                .exists(result.get("Exists").toString()).createdAt(result.get("TimeCreated").toString())
                .itemCount(result.get("ItemCount").toString()).type(result.getJSONObject(METADATA).get("type").toString())
                .webUrl(result.getJSONObject(METADATA).get("uri").toString()).build();
            response.put("data", entity);
        }
        return response;
    }

    private JSONObject countFolderItems(SharepointBean bean, SharepointHelper sharepointHelper) {
        URI countFolderUrl = getSharepointSiteUrl(sharepointHelper, bean.siteName,
            String.format("/_api/web/GetFolderByServerRelativeUrl('%s')/itemcount", bean.serverRelatUrl));
        return getGetResponse(countFolderUrl, sharepointHelper);
    }

    private void getFolder(SharepointBean bean, SharepointHelper sharepointHelper) {
        final String SHARE_POINT_URL = "/_api/web/GetFolderByServerRelativeUrl('%s/%s')/Exists";
        URI getFolderUrl = getSharepointSiteUrl(sharepointHelper, bean.siteName,
            String.format(SHARE_POINT_URL, bean.serverRelatUrl, bean.folderName));
        RestTemplate restTemplate = new RestTemplate();
        LinkedMultiValueMap<String, String> headers = getHeaders(sharepointHelper);
        RequestEntity<String> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, getFolderUrl);
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
            JSONObject objJson = new JSONObject(responseEntity.getBody());
            if (Boolean.FALSE.equals(objJson.getJSONObject("d").get("Exists"))) {
                createFolder(bean, sharepointHelper);
            }
        } catch (RuntimeException e) {
            throw new ConnectorException(EXCEPTION_MESSAGE, e);
        }
    }

    private JSONObject uploadFileToFolder(SharepointBean bean, SharepointHelper sharepointHelper) {
        getFolder(bean, sharepointHelper);
        URI postUrl = getSharepointSiteUrl(sharepointHelper, bean.siteName,
            API_WEB_GET_FOLDER + bean.serverRelatUrl + "/" + bean.folderName + "')/Files/add(url='" + bean.fileName + "',overwrite=true)");
        return getPostResponseClient(postUrl, bean.file, sharepointHelper);
    }

    private JSONObject addUserToFolder(SharepointBean bean, SharepointHelper sharepointHelper) {

        // check forlder exist or not
        getFolder(bean, sharepointHelper);

        JSONObject response = new JSONObject();
        // url for get site user Id
        URI url = getSharepointSiteUrl(sharepointHelper, bean.siteName, "/_api/Web/SiteUsers/getByEmail('" + bean.loginName + "')");
        String userId = null;
        // getting userId using login email
        ResponseEntity<String> responseEntity = getPostResponseWithoutBody(url, sharepointHelper);
        JSONObject objJson = new JSONObject(responseEntity.getBody());
        userId = objJson.getJSONObject("d").get("Id").toString();

        // Break Role Inheritance for folder
        URI roleInheritance = getSharepointSiteUrl(sharepointHelper, bean.siteName,
            API_WEB_GET_FOLDER + bean.serverRelatUrl + "/" + bean.folderName + "')/ListItemAllFields/breakroleinheritance(true)");
        getPostResponseWithoutBody(roleInheritance, sharepointHelper);

        // add user to folder and gave permission
        URI addUserToFolder = getSharepointSiteUrl(sharepointHelper, bean.siteName,
            API_WEB_GET_FOLDER + bean.serverRelatUrl + "/" + bean.folderName
                + "')/ListItemAllFields/roleassignments/addroleassignment(principalid='" + userId + "',roledefid='" + bean.permission
                + "')");

        ResponseEntity<String> responseEntityAddUser = getPostResponseWithoutBody(addUserToFolder, sharepointHelper);

        if (responseEntityAddUser.hasBody()) {
            JSONObject result = new JSONObject(responseEntityAddUser.getBody());
            response.put(STATUS, TWO_HUNDRED);
            response.put("data", result);
        }
        return response;
    }

    private JSONObject addUserToGroup(SharepointBean bean, SharepointHelper sharepointHelper) {
        URI addUser = getSharepointSiteUrl(sharepointHelper, bean.siteName,
            "/_api/Web/SiteGroups/GetByName('" + bean.groupName + "')/Users");

        JSONObject meta = new JSONObject();
        meta.put("type", "SP.User");
        JSONObject payload = new JSONObject();
        payload.put(METADATA, meta);
        payload.put("LoginName", "i:0#.f|membership|" + bean.loginName);

        JSONObject response = getPostResponse(addUser, payload.toString(), sharepointHelper);
        if (response.get("data") != null) {
            JSONObject result = response.getJSONObject("data");
            SharepointEntity entity = SharepointEntity.builder().id(result.get("Id").toString()).name(result.get(TITLE).toString())
                .type(result.getJSONObject(METADATA).get("type").toString()).webUrl(result.getJSONObject(METADATA).get("uri").toString())
                .build();
            response.put("data", entity);
        }
        return response;

    }

    private ResponseEntity<String> getGetResponseWithoutBody(URI getUrl, SharepointHelper sharepointHelper) {
        RestTemplate restTemplate = new RestTemplate();
        RequestEntity<String> requestEntity = new RequestEntity<>(getHeaders(sharepointHelper), HttpMethod.GET, getUrl);
        return restTemplate.exchange(requestEntity, String.class);
    }

    private JSONObject getGetResponse(URI getUrl, SharepointHelper sharepointHelper) {
        RestTemplate restTemplate = new RestTemplate();
        RequestEntity<String> requestEntity = new RequestEntity<>(getHeaders(sharepointHelper), HttpMethod.GET, getUrl);
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
        JSONObject response = new JSONObject();

        if (responseEntity.hasBody()) {
            JSONObject result = new JSONObject(responseEntity.getBody()).getJSONObject("d");
            response.put(STATUS, responseEntity.getStatusCodeValue());
            response.put("data", result);
        } else {
            response.put(STATUS, responseEntity.getStatusCodeValue());
        }
        return response;
    }

    private JSONObject getPostResponse(URI postUrl, String bodyStr, SharepointHelper sharepointHelper) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = new HttpEntity<>(bodyStr, getHeaders(sharepointHelper));

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(postUrl, request, String.class);
        JSONObject response = new JSONObject();

        if (responseEntity.hasBody()) {
            JSONObject result = new JSONObject(responseEntity.getBody()).getJSONObject("d");
            response.put(STATUS, responseEntity.getStatusCodeValue());
            response.put("data", result);
        } else {
            response.put(STATUS, responseEntity.getStatusCodeValue());
        }
        return response;
    }

    private ResponseEntity<String> getPostResponseWithoutBody(URI postUrl, SharepointHelper sharepointHelper) {
        RestTemplate restTemplate = new RestTemplate();
        RequestEntity<String> requestEntity = new RequestEntity<>(getHeaders(sharepointHelper), HttpMethod.POST, postUrl);
        return restTemplate.exchange(requestEntity, String.class);
    }

    private JSONObject getPostResponseClient(URI postUrl, byte[] file, SharepointHelper sharepointHelper) {
        JSONObject response = new JSONObject();
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + sharepointHelper.getAccessToken());
            httpHeaders.add(HttpHeaders.ACCEPT, SharepointConstants.ACCEPTS);
            httpHeaders.add(HttpHeaders.CONTENT_TYPE, SharepointConstants.URL_ENCODED);

            ByteArrayEntity byteArrayEntity = new ByteArrayEntity(Base64.getDecoder().decode(file));
            ResponseEntity<Map> result = restClient.postServiceWithHeaders(postUrl, httpHeaders, Map.class, byteArrayEntity);

            response.put(STATUS, result.getStatusCode());
            response.put("data", result.getBody());

        } catch (RestClientException e) {
            throw new ConnectorException(EXCEPTION_MESSAGE, e);
        }
        return response;
    }

    public LinkedMultiValueMap<String, String> getHeaders(SharepointHelper sharepointHelper) {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.ACCEPT, SharepointConstants.ACCEPTS);
        headers.add(HttpHeaders.CONTENT_TYPE, SharepointConstants.CONTENT_TYPE);
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + sharepointHelper.getAccessToken());
        return headers;
    }

    public URI getSharepointSiteUrl(SharepointHelper sharepointHelper, String siteName, String apiPath) {
        try {
            return new URI("https", sharepointHelper.getSiteDomain(), SharepointConstants.SP_SITEURL + siteName + apiPath, null);
        } catch (URISyntaxException e) {
            throw new ConnectorException(EXCEPTION_MESSAGE, e);
        }
    }

    public URI getSharepointSiteUrl(String apiPath) {
        try {
            return new URI(apiPath);
        } catch (URISyntaxException e) {
            throw new ConnectorException(EXCEPTION_MESSAGE, e);
        }
    }

}
