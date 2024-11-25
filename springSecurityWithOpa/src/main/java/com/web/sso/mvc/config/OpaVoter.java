package com.web.sso.mvc.config;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OpaVoter implements AccessDecisionVoter<Object> {

    private String opaAuthUrl;
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    private AntPathMatcher antPathMatcher;

    public OpaVoter(String opaAuthUrl) {
        this.opaAuthUrl = opaAuthUrl;
        restTemplate = new RestTemplate();
        objectMapper = new ObjectMapper();
        antPathMatcher = new AntPathMatcher();
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, Object object, Collection attributes) {

        if (!(object instanceof FilterInvocation)) {
            return ACCESS_ABSTAIN;
        }

        FilterInvocation filter = (FilterInvocation) object;

        Map<String, String> headers = new HashMap<>();
        for (Enumeration<String> headerNames = filter.getRequest().getHeaderNames(); headerNames.hasMoreElements();) {
            String header = headerNames.nextElement();
            headers.put(header, filter.getRequest().getHeader(header));
        }

        Map<String, Object> input = new HashMap<>();
//        input.put("principle", ((DefaultOidcUser) authentication.getPrincipal()).getAttribute("email"));
        System.out.println("#################################");
        System.out.println("PRINCIPLE " + authentication.getPrincipal());
        System.out.println("##################################");
        System.out.println("CREDENTIALS " + authentication.getCredentials().toString());
        System.out.println("##################################");
        System.out.println("DETAILS " + authentication.getDetails());
        System.out.println("##################################");
        System.out.println("GRANTED AUTHORITY " + authentication.getAuthorities());
        System.out.println("##################################");

        if (!(authentication.getPrincipal().equals("anonymousUser"))) {

            if (!(authentication.getCredentials() instanceof Jwt)) {
                log.info("IF BLOCK");
                input.put("access_token", authentication.getCredentials().toString().replace("\"", ""));
            } else if (authentication.getCredentials() instanceof Jwt) {
                log.info("ELSE IF BLOCK");
                Jwt credentials = (Jwt) authentication.getCredentials();
                input.put("access_token", credentials.getTokenValue());
            }
            String requestMethod = filter.getRequest().getMethod();
            String requestUrl = filter.getRequest().getRequestURI();
            Map<String, String> action = getAction(requestMethod, requestUrl);

            if (!action.isEmpty()) {
                input.put("action", action.get("action"));
            } else {
                input.put("action", null);
            }
        }
        //

        ObjectNode requestNode = objectMapper.createObjectNode();
        requestNode.set("input", objectMapper.valueToTree(input));
        log.info("Input Info {}", requestNode.toPrettyString());

        JsonNode responseNode = restTemplate.postForObject(opaAuthUrl, requestNode, JsonNode.class);
        log.info("response Info {}", responseNode.toPrettyString());

        //

        // Getting permissions of users from Auth0

//        String url = "https://dev-ru75shw6v5e3ddlq.us.auth0.com/api/v2/users/google-oauth2|102612900864088661003/permissions";
//        HttpHeaders headers2 = new HttpHeaders();
//        headers2.set("Authorization",
//            "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImZVM0hXOTdsTFpqMHBTUmtvQ285TCJ9.eyJpc3MiOiJodHRwczovL2Rldi1ydTc1c2h3NnY1ZTNkZGxxLnVzLmF1dGgwLmNvbS8iLCJzdWIiOiJwbUxLN1VTMkVPdkNXSk04cExYeWhRblU3VnM4a2FYZEBjbGllbnRzIiwiYXVkIjoiaHR0cHM6Ly9kZXYtcnU3NXNodzZ2NWUzZGRscS51cy5hdXRoMC5jb20vYXBpL3YyLyIsImlhdCI6MTY4NDc2MDgyNywiZXhwIjoxNjg0ODQ3MjI3LCJhenAiOiJwbUxLN1VTMkVPdkNXSk04cExYeWhRblU3VnM4a2FYZCIsInNjb3BlIjoicmVhZDpjbGllbnRfZ3JhbnRzIGNyZWF0ZTpjbGllbnRfZ3JhbnRzIGRlbGV0ZTpjbGllbnRfZ3JhbnRzIHVwZGF0ZTpjbGllbnRfZ3JhbnRzIHJlYWQ6dXNlcnMgdXBkYXRlOnVzZXJzIGRlbGV0ZTp1c2VycyBjcmVhdGU6dXNlcnMgcmVhZDp1c2Vyc19hcHBfbWV0YWRhdGEgdXBkYXRlOnVzZXJzX2FwcF9tZXRhZGF0YSBkZWxldGU6dXNlcnNfYXBwX21ldGFkYXRhIGNyZWF0ZTp1c2Vyc19hcHBfbWV0YWRhdGEgcmVhZDp1c2VyX2N1c3RvbV9ibG9ja3MgY3JlYXRlOnVzZXJfY3VzdG9tX2Jsb2NrcyBkZWxldGU6dXNlcl9jdXN0b21fYmxvY2tzIGNyZWF0ZTp1c2VyX3RpY2tldHMgcmVhZDpjbGllbnRzIHVwZGF0ZTpjbGllbnRzIGRlbGV0ZTpjbGllbnRzIGNyZWF0ZTpjbGllbnRzIHJlYWQ6Y2xpZW50X2tleXMgdXBkYXRlOmNsaWVudF9rZXlzIGRlbGV0ZTpjbGllbnRfa2V5cyBjcmVhdGU6Y2xpZW50X2tleXMgcmVhZDpjb25uZWN0aW9ucyB1cGRhdGU6Y29ubmVjdGlvbnMgZGVsZXRlOmNvbm5lY3Rpb25zIGNyZWF0ZTpjb25uZWN0aW9ucyByZWFkOnJlc291cmNlX3NlcnZlcnMgdXBkYXRlOnJlc291cmNlX3NlcnZlcnMgZGVsZXRlOnJlc291cmNlX3NlcnZlcnMgY3JlYXRlOnJlc291cmNlX3NlcnZlcnMgcmVhZDpkZXZpY2VfY3JlZGVudGlhbHMgdXBkYXRlOmRldmljZV9jcmVkZW50aWFscyBkZWxldGU6ZGV2aWNlX2NyZWRlbnRpYWxzIGNyZWF0ZTpkZXZpY2VfY3JlZGVudGlhbHMgcmVhZDpydWxlcyB1cGRhdGU6cnVsZXMgZGVsZXRlOnJ1bGVzIGNyZWF0ZTpydWxlcyByZWFkOnJ1bGVzX2NvbmZpZ3MgdXBkYXRlOnJ1bGVzX2NvbmZpZ3MgZGVsZXRlOnJ1bGVzX2NvbmZpZ3MgcmVhZDpob29rcyB1cGRhdGU6aG9va3MgZGVsZXRlOmhvb2tzIGNyZWF0ZTpob29rcyByZWFkOmFjdGlvbnMgdXBkYXRlOmFjdGlvbnMgZGVsZXRlOmFjdGlvbnMgY3JlYXRlOmFjdGlvbnMgcmVhZDplbWFpbF9wcm92aWRlciB1cGRhdGU6ZW1haWxfcHJvdmlkZXIgZGVsZXRlOmVtYWlsX3Byb3ZpZGVyIGNyZWF0ZTplbWFpbF9wcm92aWRlciBibGFja2xpc3Q6dG9rZW5zIHJlYWQ6c3RhdHMgcmVhZDppbnNpZ2h0cyByZWFkOnRlbmFudF9zZXR0aW5ncyB1cGRhdGU6dGVuYW50X3NldHRpbmdzIHJlYWQ6bG9ncyByZWFkOmxvZ3NfdXNlcnMgcmVhZDpzaGllbGRzIGNyZWF0ZTpzaGllbGRzIHVwZGF0ZTpzaGllbGRzIGRlbGV0ZTpzaGllbGRzIHJlYWQ6YW5vbWFseV9ibG9ja3MgZGVsZXRlOmFub21hbHlfYmxvY2tzIHVwZGF0ZTp0cmlnZ2VycyByZWFkOnRyaWdnZXJzIHJlYWQ6Z3JhbnRzIGRlbGV0ZTpncmFudHMgcmVhZDpndWFyZGlhbl9mYWN0b3JzIHVwZGF0ZTpndWFyZGlhbl9mYWN0b3JzIHJlYWQ6Z3VhcmRpYW5fZW5yb2xsbWVudHMgZGVsZXRlOmd1YXJkaWFuX2Vucm9sbG1lbnRzIGNyZWF0ZTpndWFyZGlhbl9lbnJvbGxtZW50X3RpY2tldHMgcmVhZDp1c2VyX2lkcF90b2tlbnMgY3JlYXRlOnBhc3N3b3Jkc19jaGVja2luZ19qb2IgZGVsZXRlOnBhc3N3b3Jkc19jaGVja2luZ19qb2IgcmVhZDpjdXN0b21fZG9tYWlucyBkZWxldGU6Y3VzdG9tX2RvbWFpbnMgY3JlYXRlOmN1c3RvbV9kb21haW5zIHVwZGF0ZTpjdXN0b21fZG9tYWlucyByZWFkOmVtYWlsX3RlbXBsYXRlcyBjcmVhdGU6ZW1haWxfdGVtcGxhdGVzIHVwZGF0ZTplbWFpbF90ZW1wbGF0ZXMgcmVhZDptZmFfcG9saWNpZXMgdXBkYXRlOm1mYV9wb2xpY2llcyByZWFkOnJvbGVzIGNyZWF0ZTpyb2xlcyBkZWxldGU6cm9sZXMgdXBkYXRlOnJvbGVzIHJlYWQ6cHJvbXB0cyB1cGRhdGU6cHJvbXB0cyByZWFkOmJyYW5kaW5nIHVwZGF0ZTpicmFuZGluZyBkZWxldGU6YnJhbmRpbmcgcmVhZDpsb2dfc3RyZWFtcyBjcmVhdGU6bG9nX3N0cmVhbXMgZGVsZXRlOmxvZ19zdHJlYW1zIHVwZGF0ZTpsb2dfc3RyZWFtcyBjcmVhdGU6c2lnbmluZ19rZXlzIHJlYWQ6c2lnbmluZ19rZXlzIHVwZGF0ZTpzaWduaW5nX2tleXMgcmVhZDpsaW1pdHMgdXBkYXRlOmxpbWl0cyBjcmVhdGU6cm9sZV9tZW1iZXJzIHJlYWQ6cm9sZV9tZW1iZXJzIGRlbGV0ZTpyb2xlX21lbWJlcnMgcmVhZDplbnRpdGxlbWVudHMgcmVhZDphdHRhY2tfcHJvdGVjdGlvbiB1cGRhdGU6YXR0YWNrX3Byb3RlY3Rpb24gcmVhZDpvcmdhbml6YXRpb25zIHVwZGF0ZTpvcmdhbml6YXRpb25zIGNyZWF0ZTpvcmdhbml6YXRpb25zIGRlbGV0ZTpvcmdhbml6YXRpb25zIGNyZWF0ZTpvcmdhbml6YXRpb25fbWVtYmVycyByZWFkOm9yZ2FuaXphdGlvbl9tZW1iZXJzIGRlbGV0ZTpvcmdhbml6YXRpb25fbWVtYmVycyBjcmVhdGU6b3JnYW5pemF0aW9uX2Nvbm5lY3Rpb25zIHJlYWQ6b3JnYW5pemF0aW9uX2Nvbm5lY3Rpb25zIHVwZGF0ZTpvcmdhbml6YXRpb25fY29ubmVjdGlvbnMgZGVsZXRlOm9yZ2FuaXphdGlvbl9jb25uZWN0aW9ucyBjcmVhdGU6b3JnYW5pemF0aW9uX21lbWJlcl9yb2xlcyByZWFkOm9yZ2FuaXphdGlvbl9tZW1iZXJfcm9sZXMgZGVsZXRlOm9yZ2FuaXphdGlvbl9tZW1iZXJfcm9sZXMgY3JlYXRlOm9yZ2FuaXphdGlvbl9pbnZpdGF0aW9ucyByZWFkOm9yZ2FuaXphdGlvbl9pbnZpdGF0aW9ucyBkZWxldGU6b3JnYW5pemF0aW9uX2ludml0YXRpb25zIHJlYWQ6b3JnYW5pemF0aW9uc19zdW1tYXJ5IGNyZWF0ZTphY3Rpb25zX2xvZ19zZXNzaW9ucyBjcmVhdGU6YXV0aGVudGljYXRpb25fbWV0aG9kcyByZWFkOmF1dGhlbnRpY2F0aW9uX21ldGhvZHMgdXBkYXRlOmF1dGhlbnRpY2F0aW9uX21ldGhvZHMgZGVsZXRlOmF1dGhlbnRpY2F0aW9uX21ldGhvZHMiLCJndHkiOiJjbGllbnQtY3JlZGVudGlhbHMifQ.j0Cp-tMXluK9QnEfFO9N_8MnQNzlgmUJ8LjuX8HHnrUaTsrwOM5sBd2aRXDuQpdYc50TFa1RKy36N8duESFA91TzSJsVvM3VN8jEEW-puoICwOJwfbh03gSb2tuYucls9YifMyfD20Q0wPVN9BMgf79yBuZnMTlZjidejwUozSYKGBfOvqYhmyqIDyoLKDzKayXVxUgcLSKH7H4eWa-YcAK380fcaxVKZlacjxxshuBsRObBZpSVTgfAzNGvVMSmVHC1rOTxVLQmNNbRtpWoNqP9jE0vQi7Pu-aiKhV2tt_gRkBlV05YnP2ZOTfC18sHevZ0BYpbF0YHTwKpxf6f3Q");
//        HttpEntity<String> entity = new HttpEntity<>(headers2);
//        JsonNode responseNode2 = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class).getBody();
//        log.info("Response Node: {}", responseNode2.toPrettyString());

        // setting permissions of users in Auth0

//        String url = "https://dev-ru75shw6v5e3ddlq.us.auth0.com/api/v2/users/google-oauth2|102612900864088661003/permissions";
//        HttpHeaders headers3 = new HttpHeaders();
//        headers3.set("Authorization",
//            "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImZVM0hXOTdsTFpqMHBTUmtvQ285TCJ9.eyJpc3MiOiJodHRwczovL2Rldi1ydTc1c2h3NnY1ZTNkZGxxLnVzLmF1dGgwLmNvbS8iLCJzdWIiOiJwbUxLN1VTMkVPdkNXSk04cExYeWhRblU3VnM4a2FYZEBjbGllbnRzIiwiYXVkIjoiaHR0cHM6Ly9kZXYtcnU3NXNodzZ2NWUzZGRscS51cy5hdXRoMC5jb20vYXBpL3YyLyIsImlhdCI6MTY4NDg0MjgzMSwiZXhwIjoxNjg0OTI5MjMxLCJhenAiOiJwbUxLN1VTMkVPdkNXSk04cExYeWhRblU3VnM4a2FYZCIsInNjb3BlIjoicmVhZDpjbGllbnRfZ3JhbnRzIGNyZWF0ZTpjbGllbnRfZ3JhbnRzIGRlbGV0ZTpjbGllbnRfZ3JhbnRzIHVwZGF0ZTpjbGllbnRfZ3JhbnRzIHJlYWQ6dXNlcnMgdXBkYXRlOnVzZXJzIGRlbGV0ZTp1c2VycyBjcmVhdGU6dXNlcnMgcmVhZDp1c2Vyc19hcHBfbWV0YWRhdGEgdXBkYXRlOnVzZXJzX2FwcF9tZXRhZGF0YSBkZWxldGU6dXNlcnNfYXBwX21ldGFkYXRhIGNyZWF0ZTp1c2Vyc19hcHBfbWV0YWRhdGEgcmVhZDp1c2VyX2N1c3RvbV9ibG9ja3MgY3JlYXRlOnVzZXJfY3VzdG9tX2Jsb2NrcyBkZWxldGU6dXNlcl9jdXN0b21fYmxvY2tzIGNyZWF0ZTp1c2VyX3RpY2tldHMgcmVhZDpjbGllbnRzIHVwZGF0ZTpjbGllbnRzIGRlbGV0ZTpjbGllbnRzIGNyZWF0ZTpjbGllbnRzIHJlYWQ6Y2xpZW50X2tleXMgdXBkYXRlOmNsaWVudF9rZXlzIGRlbGV0ZTpjbGllbnRfa2V5cyBjcmVhdGU6Y2xpZW50X2tleXMgcmVhZDpjb25uZWN0aW9ucyB1cGRhdGU6Y29ubmVjdGlvbnMgZGVsZXRlOmNvbm5lY3Rpb25zIGNyZWF0ZTpjb25uZWN0aW9ucyByZWFkOnJlc291cmNlX3NlcnZlcnMgdXBkYXRlOnJlc291cmNlX3NlcnZlcnMgZGVsZXRlOnJlc291cmNlX3NlcnZlcnMgY3JlYXRlOnJlc291cmNlX3NlcnZlcnMgcmVhZDpkZXZpY2VfY3JlZGVudGlhbHMgdXBkYXRlOmRldmljZV9jcmVkZW50aWFscyBkZWxldGU6ZGV2aWNlX2NyZWRlbnRpYWxzIGNyZWF0ZTpkZXZpY2VfY3JlZGVudGlhbHMgcmVhZDpydWxlcyB1cGRhdGU6cnVsZXMgZGVsZXRlOnJ1bGVzIGNyZWF0ZTpydWxlcyByZWFkOnJ1bGVzX2NvbmZpZ3MgdXBkYXRlOnJ1bGVzX2NvbmZpZ3MgZGVsZXRlOnJ1bGVzX2NvbmZpZ3MgcmVhZDpob29rcyB1cGRhdGU6aG9va3MgZGVsZXRlOmhvb2tzIGNyZWF0ZTpob29rcyByZWFkOmFjdGlvbnMgdXBkYXRlOmFjdGlvbnMgZGVsZXRlOmFjdGlvbnMgY3JlYXRlOmFjdGlvbnMgcmVhZDplbWFpbF9wcm92aWRlciB1cGRhdGU6ZW1haWxfcHJvdmlkZXIgZGVsZXRlOmVtYWlsX3Byb3ZpZGVyIGNyZWF0ZTplbWFpbF9wcm92aWRlciBibGFja2xpc3Q6dG9rZW5zIHJlYWQ6c3RhdHMgcmVhZDppbnNpZ2h0cyByZWFkOnRlbmFudF9zZXR0aW5ncyB1cGRhdGU6dGVuYW50X3NldHRpbmdzIHJlYWQ6bG9ncyByZWFkOmxvZ3NfdXNlcnMgcmVhZDpzaGllbGRzIGNyZWF0ZTpzaGllbGRzIHVwZGF0ZTpzaGllbGRzIGRlbGV0ZTpzaGllbGRzIHJlYWQ6YW5vbWFseV9ibG9ja3MgZGVsZXRlOmFub21hbHlfYmxvY2tzIHVwZGF0ZTp0cmlnZ2VycyByZWFkOnRyaWdnZXJzIHJlYWQ6Z3JhbnRzIGRlbGV0ZTpncmFudHMgcmVhZDpndWFyZGlhbl9mYWN0b3JzIHVwZGF0ZTpndWFyZGlhbl9mYWN0b3JzIHJlYWQ6Z3VhcmRpYW5fZW5yb2xsbWVudHMgZGVsZXRlOmd1YXJkaWFuX2Vucm9sbG1lbnRzIGNyZWF0ZTpndWFyZGlhbl9lbnJvbGxtZW50X3RpY2tldHMgcmVhZDp1c2VyX2lkcF90b2tlbnMgY3JlYXRlOnBhc3N3b3Jkc19jaGVja2luZ19qb2IgZGVsZXRlOnBhc3N3b3Jkc19jaGVja2luZ19qb2IgcmVhZDpjdXN0b21fZG9tYWlucyBkZWxldGU6Y3VzdG9tX2RvbWFpbnMgY3JlYXRlOmN1c3RvbV9kb21haW5zIHVwZGF0ZTpjdXN0b21fZG9tYWlucyByZWFkOmVtYWlsX3RlbXBsYXRlcyBjcmVhdGU6ZW1haWxfdGVtcGxhdGVzIHVwZGF0ZTplbWFpbF90ZW1wbGF0ZXMgcmVhZDptZmFfcG9saWNpZXMgdXBkYXRlOm1mYV9wb2xpY2llcyByZWFkOnJvbGVzIGNyZWF0ZTpyb2xlcyBkZWxldGU6cm9sZXMgdXBkYXRlOnJvbGVzIHJlYWQ6cHJvbXB0cyB1cGRhdGU6cHJvbXB0cyByZWFkOmJyYW5kaW5nIHVwZGF0ZTpicmFuZGluZyBkZWxldGU6YnJhbmRpbmcgcmVhZDpsb2dfc3RyZWFtcyBjcmVhdGU6bG9nX3N0cmVhbXMgZGVsZXRlOmxvZ19zdHJlYW1zIHVwZGF0ZTpsb2dfc3RyZWFtcyBjcmVhdGU6c2lnbmluZ19rZXlzIHJlYWQ6c2lnbmluZ19rZXlzIHVwZGF0ZTpzaWduaW5nX2tleXMgcmVhZDpsaW1pdHMgdXBkYXRlOmxpbWl0cyBjcmVhdGU6cm9sZV9tZW1iZXJzIHJlYWQ6cm9sZV9tZW1iZXJzIGRlbGV0ZTpyb2xlX21lbWJlcnMgcmVhZDplbnRpdGxlbWVudHMgcmVhZDphdHRhY2tfcHJvdGVjdGlvbiB1cGRhdGU6YXR0YWNrX3Byb3RlY3Rpb24gcmVhZDpvcmdhbml6YXRpb25zIHVwZGF0ZTpvcmdhbml6YXRpb25zIGNyZWF0ZTpvcmdhbml6YXRpb25zIGRlbGV0ZTpvcmdhbml6YXRpb25zIGNyZWF0ZTpvcmdhbml6YXRpb25fbWVtYmVycyByZWFkOm9yZ2FuaXphdGlvbl9tZW1iZXJzIGRlbGV0ZTpvcmdhbml6YXRpb25fbWVtYmVycyBjcmVhdGU6b3JnYW5pemF0aW9uX2Nvbm5lY3Rpb25zIHJlYWQ6b3JnYW5pemF0aW9uX2Nvbm5lY3Rpb25zIHVwZGF0ZTpvcmdhbml6YXRpb25fY29ubmVjdGlvbnMgZGVsZXRlOm9yZ2FuaXphdGlvbl9jb25uZWN0aW9ucyBjcmVhdGU6b3JnYW5pemF0aW9uX21lbWJlcl9yb2xlcyByZWFkOm9yZ2FuaXphdGlvbl9tZW1iZXJfcm9sZXMgZGVsZXRlOm9yZ2FuaXphdGlvbl9tZW1iZXJfcm9sZXMgY3JlYXRlOm9yZ2FuaXphdGlvbl9pbnZpdGF0aW9ucyByZWFkOm9yZ2FuaXphdGlvbl9pbnZpdGF0aW9ucyBkZWxldGU6b3JnYW5pemF0aW9uX2ludml0YXRpb25zIHJlYWQ6b3JnYW5pemF0aW9uc19zdW1tYXJ5IGNyZWF0ZTphY3Rpb25zX2xvZ19zZXNzaW9ucyBjcmVhdGU6YXV0aGVudGljYXRpb25fbWV0aG9kcyByZWFkOmF1dGhlbnRpY2F0aW9uX21ldGhvZHMgdXBkYXRlOmF1dGhlbnRpY2F0aW9uX21ldGhvZHMgZGVsZXRlOmF1dGhlbnRpY2F0aW9uX21ldGhvZHMiLCJndHkiOiJjbGllbnQtY3JlZGVudGlhbHMifQ.jUYmJiz62LxC_WTHLh0GTZ5Mhaa6bU4nn-WrS-CjlFY50InzZ161R_o8JUY3oJFSjCrlfyexwwyHQg6HFuP9Xu3PoRCdtDTvVOohCx2bdQVbOo9PMMphYBHLA_ZhyZOjdvy_kVB7ajgF4IR3hKWg_ArmlGyAZophjA915OOAs2xI2FuU4_itWSrbhYOXljPHuXlSWik9NOBHIVa-MXBbc6LOagyMZ5hwchVg3VksmJdQznMFrMtzx7G4nRtX5ZjhSV7oMHX5OjSr_3DlFbicuQsRnzUIRBv2-Qq-9M_Z0LSKBAOAXIKSq1Xn6i0xzlZ2wj1UebYiLUt5wSBB3n-zbg");
//
//        Map<String, String> permissions = new HashMap<>();
//        permissions.put("resource_server_identifier", "http://localhost:8080/authorize");
//        permissions.put("permission_name", "read:messages");
//
//        Map<String, List<Map<String, String>>> requestMap = new HashMap<>();
//        requestMap.put("permissions", Arrays.asList(permissions));
//
//        HttpEntity<Object> entity2 = new HttpEntity<>(requestMap, headers3);
//        JsonNode responseNode3 = restTemplate.postForObject(url, entity2, JsonNode.class);
//        log.info("Response Node: {}", responseNode3.toPrettyString());

        //

        if (responseNode.has("result") && responseNode.get("result").get("authorized").asBoolean()) {
            return ACCESS_GRANTED;
        }

        return ACCESS_DENIED;

        /*
         * // Map<String, String> headers = new HashMap<>(); // for (Enumeration<String> headerNames = filter.getRequest().getHeaderNames();
         * headerNames.hasMoreElements();) { // String header = headerNames.nextElement(); // headers.put(header,
         * filter.getRequest().getHeader(header)); // }
         * 
         * Map<String, Object> input = new HashMap<>(); input.put("principle", filter.getHttpRequest().getParameter("principle"));
         * input.put("authorities", filter.getHttpRequest().getParameter("authorities")); input.put("uri",
         * filter.getHttpRequest().getParameter("uri")); log.info("Input Info: {}", input);
         * 
         * ObjectNode requestNode = objectMapper.createObjectNode(); requestNode.set("input", objectMapper.valueToTree(input));
         * log.info("Authorization Request, {}", requestNode.toPrettyString());
         * 
         * JsonNode responseNode = restTemplate.postForObject(this.opaAuthUrl, requestNode, JsonNode.class); log.info("Response Node: {}",
         * responseNode.toPrettyString());
         * 
         * if (responseNode.has("result") && responseNode.get("result").get("authorized").asBoolean()) { return ACCESS_GRANTED; }
         * 
         * return ACCESS_DENIED;
         */

        /*
         * List<String> authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
         * .collect(Collectors.toList()); System.out.println(authorities);
         * 
         * Map<String, Object> input = new HashMap<>(); input.put("principle", filter.getHttpRequest().getHeader("principle"));
         * input.put("authorities", filter.getHttpRequest().getHeader("authorities")); input.put("uri",
         * filter.getHttpRequest().getHeader("uri")); log.info("Input Info: {}", input);
         * 
         * HttpEntity<?> request = new HttpEntity<>(new OPADataRequest(input)); log.info("Request Info: {}", request);
         * 
         * @SuppressWarnings("unchecked") Map<String, Object> response = restTemplate.postForObject(this.opaAuthUrl, request, Map.class);
         * log.info("Response Info: {}", response);
         * 
         * @SuppressWarnings("unchecked") Map<String, Object> result = (Map<String, Object>) response.get("result");
         * log.info("Result Info: {}", result);
         * 
         * boolean isAuthorized = (Boolean) result.get("authorized"); if (!isAuthorized) { return ACCESS_DENIED; }
         * 
         * return ACCESS_GRANTED;
         * 
         */
    }

    private Map<String, String> getAction(String requestMethod, String requestUrl) {
        String urlPattern1 = "/api/v1/{resource}";

        Map<String, String> urlParameters = new HashMap<>();

        if (antPathMatcher.match(urlPattern1, requestUrl)) {
            Map<String, String> uriVariables = antPathMatcher.extractUriTemplateVariables(urlPattern1, requestUrl);
            String resource = uriVariables.get("resource");

            urlParameters.put("action", String.format("%s:%s", requestMethod, resource));
            return urlParameters;
        }

        return urlParameters;
    }

}
