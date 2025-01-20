/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.mail.impl.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.digitaldots.connector.ConnectorException;
import com.digitaldots.connector.annotation.Connection;
import com.digitaldots.connector.annotation.Connector;
import com.digitaldots.connector.annotation.Execute;
import com.digitaldots.connector.annotation.Params;
import com.digitaldots.connector.mail.config.EmailApiHelper;
import com.digitaldots.connector.spi.ConnectorRequest;
import com.microsoft.graph.models.Attachment;
import com.microsoft.graph.models.BodyType;
import com.microsoft.graph.models.EmailAddress;
import com.microsoft.graph.models.FileAttachment;
import com.microsoft.graph.models.ItemBody;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.Recipient;
import com.microsoft.graph.users.item.messages.item.move.MovePostRequestBody;
import com.microsoft.graph.users.item.sendmail.SendMailPostRequestBody;
import com.microsoft.kiota.RequestInformation;

import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;

@Connector(id = "Email")
@Slf4j
public class GraphMailService {
    private static final String FILE_NAME = "fileName";
    private static final String READ_FROM = "Inbox";
    private static final String MOVE_TO = "Archive";

    @Params(name = "method")
    protected String method;

    @Params(name = "alias")
    protected String alias;

    @Params(name = "to")
    protected String to;

    @Params(name = "cc")
    protected String cc;

    @Params(name = "bcc")
    protected String bcc;

    @Connection
    protected EmailApiHelper graphApiHelper;

    @Params(name = "subject")
    protected String subject;

    @Params(name = "message")
    protected String message;

    @Params(name = "filter")
    protected Map<String, String> filter;

    @Params(name = "attachments")
    protected Map<String, String> attachments;

    @Value("${platform.auth.username}")
    private String userName;

    @Value("${platform.auth.password}")
    private String password;

    @Value("${platform.auth.url}")
    private String authUrl;

    @Value("${store.presigned.url}")
    private String presignedUrl;

    @Autowired
    private RestTemplate template;

    @Params(name = "CONTEXT")
    protected Map<String, String> context;

    @Execute
    public Object execute(ConnectorRequest<?> request) {
        try {
            if ("SEND".equalsIgnoreCase(method)) {
                return sendEmail();
            } else {
                return readEmail();
            }
        } catch (IOException | TemplateException ex) {
            log.error("Exception occured while processing Email", ex);
            throw new ConnectorException(ex.getMessage());

        }

    }

    private Object sendEmail() throws IOException, TemplateException {
        if (!StringUtils.hasLength(to)) {
            log.error("Exception occured while sending email, 'to; field should not be null or empty");
            throw new ConnectorException("to field should not be null");
        }
        String sendFrom = graphApiHelper.getUserMail();
        if (!StringUtils.hasLength(sendFrom)) {
            log.error("Exception occured while sending email, 'User Email' field should not be null or empty");
            throw new ConnectorException("'User Email' field should not be null or empty");
        }
        log.debug("Sending mail from : {}", sendFrom);
        Message sendMessage = new Message();
        sendMessage.setSubject(subject);
        ItemBody body = new ItemBody();
        body.setContentType(BodyType.Html);
        body.setContent(message);
        sendMessage.setBody(body);

        sendMessage.setToRecipients(new ArrayList<>());
        String[] toEmailAddresses = to.split(",");
        sendMessage.getToRecipients().addAll(getMailAddress(toEmailAddresses));
        if (cc != null) {
            sendMessage.setCcRecipients(new ArrayList<>());
            sendMessage.getCcRecipients().addAll(getMailAddress(cc.split(",")));
        }
        List<Attachment> attachmentsList = new LinkedList<>();
        if (Objects.nonNull(attachments)) {
            attachments.forEach((String key, String value) -> {
                FileAttachment fileAttachment = new FileAttachment();
                fileAttachment.setName(key);
                fileAttachment.setOdataType("#microsoft.graph.fileAttachment");
                fileAttachment.setContentBytes(Base64.getDecoder().decode(value));
                attachmentsList.add(fileAttachment);
            });
        }
        log.debug("Attaching {} documents", attachmentsList.size());
        // AttachmentCollectionResponse attachmentCollectionResponse = new AttachmentCollectionResponse();
        // attachmentCollectionResponse.setValue(attachmentsList);
        // AttachmentCollectionPage attachmentCollectionPage = new AttachmentCollectionPage(attachmentCollectionResponse, null);
        // sendMessage.attachments = attachmentCollectionPage;
        // graphApiHelper.getGraphClient().users(sendFrom)
        // .sendMail(UserSendMailParameterSet.newBuilder().withMessage(sendMessage).withSaveToSentItems(null).build()).buildRequest()
        // .post();

        sendMessage.setAttachments(attachmentsList);
        SendMailPostRequestBody smpr = new SendMailPostRequestBody();
        smpr.setMessage(sendMessage);
        graphApiHelper.getGraphClient().users().byUserId(sendFrom).sendMail().post(smpr);
        log.debug("Email sent SuccessFully");
        JSONObject response = new JSONObject();
        response.put("statusMessage", "Email Sent Sucessfully");
        return response;
    }

    private List<Recipient> getMailAddress(String[] toEmailAddresses) {
        List<Recipient> recipient = new ArrayList<>();
        for (String toRecp : toEmailAddresses) {
            Recipient toRecipients = new Recipient();
            EmailAddress toEmailAddress = new EmailAddress();
            toEmailAddress.setAddress(toRecp);
            toRecipients.setEmailAddress(toEmailAddress);
            recipient.add(toRecipients);
        }
        return recipient;
    }

    private Object readEmail() throws IOException {
        String receiveFrom = graphApiHelper.getUserMail();
        log.debug("Reading Mail from {}", receiveFrom);
        if (!StringUtils.hasLength(receiveFrom)) {
            log.error("Exception occured while reading email, 'User Mail' field should not be null or Empty");
            throw new ConnectorException("'User Mail' field should not be null or Empty");
        }
        StringBuilder filterCondition = filterApplied();

        // MessageCollectionPage messageCollectionPage =
        // graphApiHelper.getGraphClient().users(receiveFrom).mailFolders(READ_FROM).messages()
        // .buildRequest().filter(filterCondition.toString()).get();
        // List<Message> messages = messageCollectionPage.getCurrentPage();

        List<Message> messages = graphApiHelper.getGraphClient().users().byUserId(receiveFrom).mailFolders().byMailFolderId(READ_FROM)
            .messages().get(req -> req.queryParameters.filter = filterCondition.toString()).getValue();
        List<Map<String, Object>> payloads = new ArrayList<>();
        for (Message readMessage : messages) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("headers", getEmailInfo(readMessage));
            hasAttachment(receiveFrom, readMessage, payload);
            // graphApiHelper.getGraphClient().users(receiveFrom).messages(readMessage.id)
            // .move(MessageMoveParameterSet.newBuilder().withDestinationId(MOVE_TO).build()).buildRequest().post();
            MovePostRequestBody mpr = new MovePostRequestBody();
            mpr.setDestinationId(MOVE_TO);
            graphApiHelper.getGraphClient().users().byUserId(receiveFrom).messages().byMessageId(readMessage.getId()).move().post(mpr);
            payloads.add(payload);
        }
        return payloads;

    }

    private void hasAttachment(String receiveFrom, Message readMessage, Map<String, Object> payload) {
        if (readMessage.getHasAttachments()) {
            // List<Attachment> fileAttachments = graphApiHelper.getGraphClient().users(receiveFrom).messages(readMessage.id).attachments()
            // .buildRequest().get().getCurrentPage();
            List<Attachment> fileAttachments = graphApiHelper.getGraphClient().users().byUserId(receiveFrom).messages()
                .byMessageId(readMessage.getId()).attachments().get().getValue();

            for (Attachment attachment : fileAttachments) {
                // String requestUrl =
                // graphApiHelper.getGraphClient().users(receiveFrom).messages(readMessage.id).attachments(attachment.id)
                // .buildRequest().getRequestUrl().toString();

                RequestInformation getRequestInformation = graphApiHelper.getGraphClient().users().byUserId(receiveFrom).messages()
                    .byMessageId(readMessage.getId()).attachments().byAttachmentId(attachment.getId()).toGetRequestInformation();
                String requestUrl = null;
                try {
                    requestUrl = getRequestInformation.getUri().toString();
                } catch (IllegalStateException | URISyntaxException e) {
                    log.warn("Error occured while getting request url " + e);
                }
                hasFileAttachment(payload, attachment, requestUrl);
            }
        }
    }

    private void hasFileAttachment(Map<String, Object> payload, Attachment attachment, String requestUrl) {
        if ("#microsoft.graph.fileAttachment".equals(attachment.getOdataType())) {
            // FileAttachmentRequestBuilder requestBuilder = new FileAttachmentRequestBuilder(requestUrl, graphApiHelper.getGraphClient(),
            // null);
            // FileAttachment fileAttachment = requestBuilder.buildRequest().get();

            // InputStream stream = new ByteArrayInputStream(fileAttachment.contentBytes);
            // payload.put("attachments", uploadToS3(stream, fileAttachment.name, fileAttachment.contentBytes.length));

            InputStream stream = new ByteArrayInputStream(attachment.toString().getBytes());
            payload.put("attachments", uploadToS3(stream, attachment.getName(), attachment.toString().getBytes().length));

        }
    }

    private StringBuilder filterApplied() {
        StringBuilder filterCondition = new StringBuilder();
        filterCondition.append("isRead eq false");
        if (filter != null) {
            final String SUBJECT = "subject";
            final String AND = " and ";
            final String SENDER = "sender";
            if (StringUtils.hasLength(filter.get(SUBJECT))) {
                filterCondition.append(AND);
                filterCondition.append("contains(subject,").append("'").append(filter.get(SUBJECT)).append("')");
            }
            if (StringUtils.hasLength(filter.get(SENDER))) {
                filterCondition.append(AND);
                filterCondition.append("contains(from/emailAddress/address,").append("'").append(filter.get(SENDER)).append("')");
            }
            if (StringUtils.hasLength(filter.get("content"))) {
                filterCondition.append(AND);
                filterCondition.append("contains(body/content,").append("'").append(filter.get("content")).append("')");
            }
        }
        log.debug("Filter applied is {}", filterCondition.toString());
        return filterCondition;
    }

    private <T> T getService(String url, Class<T> responseClass, String... uriVariables) {
        try {
            if (uriVariables != null) {
                for (int i = 0; i < uriVariables.length; i++) {
                    url = url.replace("{" + i + "}", uriVariables[i]);
                }
            }
            log.debug("INVOKING_SERVICE", url);
            RequestEntity<?> requestEntity = RequestEntity.get(new URI(url)).headers(createHttpHeaders()).accept(MediaType.APPLICATION_JSON)
                .build();
            return this.template.exchange(requestEntity, responseClass).getBody();
        } catch (RestClientException | URISyntaxException e) {
            log.error("Exception occured while executing gett service", e);
            throw new RestClientException(e.getMessage());
        }

    }

    private <T> ResponseEntity<T> postServiceWithNoAuth(String url, Class<T> responseClass, Object request, String... uriVariables) {
        try {
            for (int i = 0; i < uriVariables.length; i++) {
                url = url.replace("{" + i + "}", uriVariables[i]);
            }
            log.debug("Invoking Service", url);
            RequestEntity<?> requestEntity = RequestEntity.post(new URI(url)).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).body(request);
            return this.template.exchange(requestEntity, responseClass);
        } catch (Exception e) {
            log.error("Exception occured while executing post service with no auth", e);
            throw new RestClientException(e.getMessage());
        }

    }

    private HttpHeaders createHttpHeaders() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object token = null;
        log.debug("auth context is {}", auth);
        if (Objects.isNull(auth) || Objects.isNull(auth.getCredentials())) {
            Map<String, String> jwtRequest = new HashMap<>();
            jwtRequest.put("username", userName);
            jwtRequest.put("password", password);
            ResponseEntity<Map> response = postServiceWithNoAuth(authUrl, Map.class, jwtRequest);
            log.debug("response frm auth is {}", response.getStatusCode());
            @SuppressWarnings("unchecked")
            Map<String, Object> body = response.getBody();
            if (Objects.nonNull(body)) {
                token = body.get("token");
            }
        } else {
            token = auth.getCredentials();
        }

        HttpHeaders headers = new HttpHeaders();
        if (!Objects.isNull(token)) {
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "BEARER " + token);
        }
        if (!StringUtils.hasLength((String) token)) {
            headers.remove("Authorization");
        }
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private Map<String, Object> uploadToS3(InputStream stream, String fileName, int fileSize) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("category", "triggers");
        params.add("scope", context.getOrDefault("solutionId", "system"));
        params.add(FILE_NAME, fileName);
        URI uri = UriComponentsBuilder.fromUriString(presignedUrl).queryParams(params).build().toUri();
        Map<String, Object> response = getService(uri.toString(), Map.class);
        String url = null;

        try {
            if (Objects.nonNull(response)) {
                url = (String) response.get("url");
                log.debug("File upload starting.{}", url);
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/p");
                connection.setRequestMethod("PUT");
                OutputStream outputStream = connection.getOutputStream();
                log.debug("Process started for {}", params.get(FILE_NAME));
                IOUtils.copy(stream, outputStream);
                stream.close();
                outputStream.close();
                connection.getResponseCode();
                log.debug("Process completed for {}", params.get(FILE_NAME));
                log.debug("HTTP response code is " + connection.getResponseCode());
                connection.disconnect();
                log.debug("File upload is completed. {}", url);
                response.put("filename", fileName);
                response.put("file_size", fileSize);
                // response.put("file_extension", com.google.common.io.Files.getFileExtension(fileName));
                response.put("file_extension", fileName.substring(Math.max(fileName.lastIndexOf("."), 0)));
                response.put("file_path", url.split("\\?")[0]);
                response.remove("url");
            }
            return response;
        } catch (Exception ex) {
            log.error("Exception occured while uploading to S3", ex);
            throw new ConnectorException(ex.getMessage());
        }
    }

    private Map<String, Object> getEmailInfo(Message message) {
        Map<String, Object> result = new HashMap<>();
        result.put("received", message.getReceivedDateTime().toLocalDateTime().toString());
        result.put("date", message.getSentDateTime().toLocalDateTime().toString());
        result.put("from", message.getFrom().getEmailAddress().getAddress());
        result.put("sender", message.getSender().getEmailAddress().getAddress());
        result.put("to",
            message.getToRecipients().stream().map(recipient -> recipient.getEmailAddress().getAddress()).collect(Collectors.joining(",")));
        result.put("cc",
            message.getCcRecipients().stream().map(recipient -> recipient.getEmailAddress().getAddress()).collect(Collectors.joining(",")));
        result.put("bcc", message.getBccRecipients().stream().map(recipient -> recipient.getEmailAddress().getAddress())
            .collect(Collectors.joining(",")));
        result.put("subject", message.getSubject());
        result.put("content-type", message.getOdataType());
        return result;
    }
}
