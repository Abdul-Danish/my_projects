/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.ad.service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;

import com.digitaldots.connector.ConnectorException;
import com.digitaldots.connector.annotation.Connection;
import com.digitaldots.connector.annotation.Connector;
import com.digitaldots.connector.annotation.Execute;
import com.digitaldots.connector.annotation.Params;
import com.digitaldots.connector.gen.GenericResponse;
import com.digitaldots.connector.graphapi.util.GraphApiHelper;
import com.digitaldots.connector.spi.ConnectorRequest;
import com.microsoft.graph.models.Group;
import com.microsoft.graph.models.Invitation;
import com.microsoft.graph.models.InvitedUserMessageInfo;
import com.microsoft.graph.models.ObjectIdentity;
import com.microsoft.graph.models.PasswordProfile;
import com.microsoft.graph.models.User;
import com.microsoft.graph.serviceclient.GraphServiceClient;

import lombok.extern.slf4j.Slf4j;

@Connector(id = "AD")
@Slf4j
public class ADService {

    private static final String DIRECTORY_ID_IS = "creating user in directory {}";
    private static final String PASSWORD = "password";
    private static final String PATTERN_REMOVE = "[^a-zA-Z0-9]";

    @Params(name = "directory")
    public String directory;

    @Connection
    public GraphApiHelper graphApiHelper;

    @Params(name = "groups")
    public List<String> groups;

    @Params(name = "messageBody")
    public String messageBody;

    @Params(name = "method")
    public String method;

    @Params(name = "redirectUrl")
    public String redirectUrl;

    @Params(name = "user")
    public Map<String, String> userDetails;

    @Params(name = "usermail")
    public String usermail;

    private static String generateCommonLangPassword() {
        final int COUNT = 2;
        final int START_UPPER = 65;
        final int END_UPPER = 90;
        final int START_LOWER = 97;
        final int END_LOWER = 122;
        final int START_SPECIAL = 33;
        final int END_SPECIAL = 47;
        String upperCaseLetters = RandomStringUtils.random(COUNT, START_UPPER, END_UPPER, true, true);
        String lowerCaseLetters = RandomStringUtils.random(COUNT, START_LOWER, END_LOWER, true, true);
        String numbers = RandomStringUtils.randomNumeric(COUNT);
        String specialChar = RandomStringUtils.random(COUNT, START_SPECIAL, END_SPECIAL, false, false);
        String totalChars = RandomStringUtils.randomAlphanumeric(COUNT);
        String combinedChars = upperCaseLetters.concat(lowerCaseLetters).concat(numbers).concat(specialChar).concat(totalChars);
        List<Character> pwdChars = combinedChars.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
        Collections.shuffle(pwdChars);
        return pwdChars.stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
    }

    private Group checkExistsAndCreate(String grpName, Group rootDir) {
        GraphServiceClient graphServiceClient = graphApiHelper.getGraphClient();
        // List<Group> groupList = graphServiceClient.groups().buildRequest().filter("displayName eq '" + grpName +
        // "'").get().getCurrentPage();
        List<Group> groupList = graphServiceClient.groups().get(req -> req.queryParameters.filter = "displayName eq '" + grpName + "'")
            .getValue();
        log.debug("creating user in group {} query is {}", grpName, groupList);
        log.debug("size of the group is {} or {}", groupList.isEmpty(), groupList.size());

        if (groupList.isEmpty()) {
            Group grp = new Group();
            grp.setDescription(grpName);
            grp.setDisplayName(grpName);
            LinkedList<String> groupTypesList = new LinkedList<>();
            grp.setGroupTypes(groupTypesList);
            grp.setMailEnabled(false);
            grp.setMailNickname(removeSplCharacters(grpName));
            grp.setSecurityEnabled(true);
            // grp = graphServiceClient.groups().buildRequest().post(grp);
            grp = graphServiceClient.groups().post(grp);
            log.debug(DIRECTORY_ID_IS, grp.getId());
            // if (rootDir != null) {
            // DirectoryObject directoryObject = new DirectoryObject();
            // directoryObject.setId(grp.getId());
            // graphServiceClient.groups(rootDir.getId()).members().references().buildRequest().post(directoryObject);
            // }
            if (rootDir != null) {
                grp = graphServiceClient.groups().post(rootDir);

            }
            return grp;
        } else {
            return groupList.get(0);
        }

    }

    private User createUser(String userPassword) {
        GraphServiceClient graphServiceClient = graphApiHelper.getGraphClient();
        User user = new User();
        if (userDetails.get(ADConstants.B2CUSER) != null) {
            ObjectIdentity oidentity = new ObjectIdentity();
            oidentity.setIssuer(userDetails.get(ADConstants.ISSUER));
            oidentity.setIssuerAssignedId(userDetails.get(ADConstants.USER_PRINCIPAL_NAME));
            oidentity.setSignInType("emailAddress");
            user.setIdentities(Collections.singletonList(oidentity));
        } else {
            if (userDetails.get(ADConstants.USER_PRINCIPAL_NAME) != null) {
                user.setUserPrincipalName(userDetails.get(ADConstants.USER_PRINCIPAL_NAME));
            }
        }
        PasswordProfile passwordProfile = new PasswordProfile();
        passwordProfile.setForceChangePasswordNextSignIn(true);
        // passwordProfile.forceChangePasswordNextSignInWithMfa = false;
        passwordProfile.setPassword(userPassword);
        user.setPasswordProfile(passwordProfile);
        user.setPasswordPolicies("DisablePasswordExpiration");
        // if (userDetails.get(ADConstants.USER_PRINCIPAL_NAME) != null) {
        // user.userPrincipalName = userDetails.get(ADConstants.USER_PRINCIPAL_NAME);
        // }
        if (userDetails.get("mobilePhone") != null) {
            user.setMobilePhone(userDetails.get("mobilePhone"));
        }
        return graphServiceClient.users().post(setUserDetails(user));
    }

    private Invitation createUserInvitation() {
        GraphServiceClient graphServiceClient = graphApiHelper.getGraphClient();
        Invitation invitation = new Invitation();
        invitation.setInvitedUserEmailAddress(usermail);
        if (Objects.nonNull(redirectUrl)) {
            invitation.setInviteRedirectUrl(redirectUrl);
        } else {
            invitation.setInviteRedirectUrl("https://studio.demo.beverapps.com/");
        }
        invitation.setSendInvitationMessage(false);
        invitation.setInvitedUserMessageInfo(new InvitedUserMessageInfo());
        invitation.getInvitedUserMessageInfo().setCustomizedMessageBody(this.messageBody);
        invitation.setInvitedUserType("Guest");
        return graphServiceClient.invitations().post(invitation);
    }

    @Execute
    public JSONObject execute(ConnectorRequest<GenericResponse> request) {
        log.trace("Executing Ad connector and the method is {} ", method);
        GraphServiceClient graphServiceClient = graphApiHelper.getGraphClient();
        try {
            JSONObject resp = new JSONObject();
            if ("create".equals(method)) {
                String userPassword = "";
                if (Objects.nonNull(userDetails.get(PASSWORD))) {
                    userPassword = userDetails.get(PASSWORD);
                } else {
                    userPassword = generateCommonLangPassword();
                }
                User user = createUser(userPassword);
                if (Objects.nonNull(directory)) {
                    createDirectory(graphServiceClient, user);
                }
                resp.put("id", user.getId());
                resp.put("mail", user.getUserPrincipalName());
                resp.put("name", user.getDisplayName());
                resp.put(PASSWORD, userPassword);
            } else if ("invite".equals(method)) {
                Invitation user = createUserInvitation();
                // DirectoryObject directoryObject = new DirectoryObject();
                // directoryObject.setId(user.getInvitedUser().getId());
                log.debug("processing the AD connector {}", graphServiceClient);
                Group rootDir = checkExistsAndCreate(this.directory, null);
                log.debug(DIRECTORY_ID_IS, rootDir.getId());
                for (String group : groups) {
                    Group groupObject = checkExistsAndCreate(group, rootDir);
                    log.debug("adding user to the group {}", groupObject.getId());
                    // graphServiceClient.groups(groupObject.getId()).members().references().buildRequest().post(directoryObject);
                    graphServiceClient.groups().post(groupObject);
                }
                resp.put("id", user.getInvitedUser().getId());
                resp.put("mail", user.getInvitedUser().getMailNickname());
                resp.put("inviteRedeemUrl", user.getInviteRedeemUrl());
            } else if ("update".equals(method)) {
                User user = getUser();
                user = updateUser(user);
                resp.put("id", user.getId());
                resp.put("mail", user.getUserPrincipalName());
                resp.put("name", user.getDisplayName());
            } else if ("delete".equals(method)) {
                User user = getUser();
                // graphServiceClient.users(user.id).buildRequest().delete();
                graphServiceClient.users().byUserId(user.getId()).delete();
            } else {
                throw new IllegalArgumentException("Invalid method is configured");
            }
            return resp;
        } catch (Exception e) {
            log.error("Exception occured whilw executing Ad Service", e);
            throw new ConnectorException("exception from AD || " + e.getMessage(), e);
        }
    }

    private void createDirectory(GraphServiceClient graphServiceClient, User user) {
        // DirectoryObject directoryObject = new DirectoryObject();
        // directoryObject.setId(user.getId());
        log.debug("processing the AD connector with method create --{}", graphServiceClient);
        Group rootDir = checkExistsAndCreate(this.directory, null);
        log.debug(DIRECTORY_ID_IS, rootDir.getId());
        for (String group : groups) {
            Group groupObject = checkExistsAndCreate(group, rootDir);
            log.debug("group added to user {}", groupObject.getId());
            // graphServiceClient.groups(groupObject.getId()).members().references().buildRequest().post(directoryObject);
            graphServiceClient.groups().post(groupObject);
        }
    }

    private User getUser() {
        GraphServiceClient graphServiceClient = graphApiHelper.getGraphClient();
        return graphServiceClient.users().byUserId(userDetails.get(ADConstants.USER_PRINCIPAL_NAME)).get();
    }

    private static String removeSplCharacters(String keyString) {
        return keyString.replaceAll(PATTERN_REMOVE, "");
    }

    private User setUserDetails(User user) {
        String userType = "Member";
        if (user.getUserType() != null) {
            userType = user.getUserType();
        }
        user.setUserType(userDetails.getOrDefault("userType", userType));
        user.setAccountEnabled(true);
        if (userDetails.get("jobTitle") != null) {
            user.setJobTitle(userDetails.get("jobTitle"));
        }
        if (userDetails.get("displayName") != null) {
            user.setDisplayName(userDetails.get("displayName"));
        }
        if (userDetails.get("mailNickname") != null) {
            user.setMailNickname(removeSplCharacters(userDetails.get("mailNickname")));
        }
        if (userDetails.get("country") != null) {
            user.setCountry(userDetails.get("country"));
        }
        if (userDetails.get("preferredLanguage") != null) {
            user.setPreferredLanguage(userDetails.get("preferredLanguage"));
        }
        if (userDetails.get("givenName") != null) {
            user.setGivenName(userDetails.get("givenName"));
        }
        if (userDetails.get("employeeType") != null) {
            user.setEmployeeType(userDetails.get("employeeType"));
        }
        if (userDetails.get("department") != null) {
            user.setDepartment(userDetails.get("department"));
        }
        if (userDetails.get("city") != null) {
            user.setCity(userDetails.get("city"));
        }
        if (userDetails.get("company") != null) {
            user.setCompanyName(userDetails.get("company"));
        }
        if (userDetails.get("active") != null) {
            user.setAccountEnabled("true".equals(userDetails.get("active").toLowerCase()));
        }

        return user;
    }

    private User updateUser(User user) {
        GraphServiceClient graphServiceClient = graphApiHelper.getGraphClient();
        return graphServiceClient.users().byUserId(user.getId()).patch(setUserDetails(user));
    }

}
