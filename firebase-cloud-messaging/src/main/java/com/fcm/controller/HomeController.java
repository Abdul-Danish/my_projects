package com.fcm.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.Date;
import java.time.Instant;
import java.util.Base64;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/v1")
@Slf4j
public class HomeController {

    private static final String CLIENT_EMAIL = "firebase-adminsdk-t5l6l@dd-studio-29057.iam.gserviceaccount.com";
    private static final String PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\\nMIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDBsWS8DJ1pSsIG\\nAWJqMQduvNOfMXLcHniylH06gFfgBnBW1ozQJRguRHi19lQ2iAqKTmUvmLjWXLlZ\\ncluHTk1c8Y4EEluOpdzWXq1020AelNf1FsFAX7EftXciMoGSTWWCumWGyeAgNWlW\\nDmUETHqDgwPTQVuJEMrzCjri/4bFHWZuKIaftebNaLLrvxN9ddQH4COyXb+PGXuX\\n665ZH0nfWJdthCgjUT5bQEe+QvGFZ6qmgNjoSu7WxncBvSlSGnXIGWJim9qh0uAV\\nnCVE/ZkgBVJfcNrYOAVkFDku8yDODqEddfxen/Dwj23s6p+7m3xHsxyuxehTva26\\n8TIawTn3AgMBAAECggEAUaJ3/+/bKkT5WHmd5n7KkKgiEx7Ot6paGX4O96ifIMrl\\n7xuXFqUDt4BhVeO/o4ob0yXubKyT/rju352cqP7eCVW/ksS/9PPAC1O1DKLZt+no\\nySHxijWqm+AqGW4rASQArCPuZWxRO5H3Vu9TMQggvKvr4U5TC4ZZ2yxNHF4xCYM6\\nOIMhBSUQ0ufQA7K3+QIs7vwfZp31/P4hs4o/PNTkjomePL9w1vP8yx90LNYwNpst\\n6omAvMje/rVcugKZTZaBxGelMYGSbO1rp7uRN7X2z0B4DBmuxXPpffM0sSNjjoRK\\nEv5XjCV2SbYpIucgf8r0yI/yDQ23ilmYHkHiViz9vQKBgQDqBZKfcqV/IUWKc12Z\\nuDUxdhuuXeWt2VpkkwzItmOOyDjcBTrL/8NQtPh5QasrSoUdOimY2D+G0jaLrPgq\\nVn5wcbnmooa0OidFUmXU4wzqEK6DB+hgyicuzxNybkiaTQXrzsmXnibcb8l5CDdm\\nB+d5T9ECGNDK8CrOtg5KAd+M5QKBgQDT4jjYj/DCWijLOl2O7EZNlDyifvu3sFxM\\ninw/vDXSM3psRJfiKW6YVwMtiI8uq4RejozfiEUX4U2IO5Iwlr77S9JvbUrfmdqn\\nS9mvGuS/C6N2xmDCZDwQuh2P2X12PnP+0wE16SfH0kl8o3ysjISTl8U8tzkWJAHE\\n+OvRLADZqwKBgQCD8/p1r3ZDlaYZZ+1aFLThm8AF9GniOdEvLn8h2T2Pr7Pn04cQ\\nqbkek7wa2v1B3rXqAfaceSpwwa0B2tjfPn/ytR1mPzQHAVdNTiWfARsyC4/q0BWm\\nJbYsPZSwjCCh6FYzXRjsRb+RwfJvLUPXYxOQooGuVgG8u+jXP24VKrM7RQKBgQCc\\nhJQxhcL4DtnrpmXOWkNks4hHET6o5qKH+BTokAPCDzz0FYeNDcYgysYSMLp0Y0cZ\\nAnyV83f2t/wqErdfJTxXLh95KGcS3fhjdOiNLXSkm9hYuRpo/tpQEOwdgy/m1SOi\\nrgRK6rz0Iycd5zcFz5dv38FXpJGLBXY5JxgsIDFQmQKBgQDBFx1w2vt827yphv5h\\nmbGzt+BOfCttymLDDKYayQEiw8Et0WTzuV+qQQ1xYq80Z3/uOAZsqgWZFaS/n9Qh\\n7G6vyrMuACLBCWVV0Gu7iemGc/oS+l/EL2XhYBMaRVz8UbHgEAaMWbUI2gjSo6t8\\nL1GjhvyRMhh3e5c5TvYR4ZjWAA==\\n-----END PRIVATE KEY-----\\n";
    private static final String TOKEN_URI = "https://oauth2.googleapis.com/token";

    @GetMapping("/token")
    public void getToken(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String jwt = createJWT(CLIENT_EMAIL, PRIVATE_KEY);
        String accessToken = getAccessToken(jwt);
        System.out.println("Access Token: " + accessToken);
    }

    private static String createJWT(String clientEmail, String privateKey) throws Exception {
        Algorithm algorithm = Algorithm.RSA256(null, (RSAPrivateKey) readPrivateKey(privateKey));
        Instant now = Instant.now();
        String jwt = JWT.create().withIssuer(clientEmail).withAudience(TOKEN_URI).withIssuedAt(Date.from(now))
            .withExpiresAt(Date.from(now.plusSeconds(3600))).withClaim("scope", "https://www.googleapis.com/auth/firebase.messaging")
            .sign(algorithm);

        return jwt;
    }

    private static String getAccessToken(String jwt) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(TOKEN_URI);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");

        StringEntity body = new StringEntity("grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer&assertion=" + jwt,
            StandardCharsets.UTF_8);
        post.setEntity(body);

        CloseableHttpResponse response = httpClient.execute(post);
        String responseBody = EntityUtils.toString(response.getEntity());

        return responseBody;
    }

    public static PrivateKey readPrivateKey(String privateKeyPEM) throws Exception {
        String modifiedPrivateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "")
            .replace("-----BEGIN RSA PRIVATE KEY-----", "").replace("-----END RSA PRIVATE KEY-----", "").replaceAll("\\n", "");
        
        System.out.println(modifiedPrivateKeyPEM);
        byte[] privateKeyBytes = Base64.getDecoder().decode(modifiedPrivateKeyPEM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

}
