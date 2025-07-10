package com.dsa.practice;

import java.util.HashMap;
import java.util.Map;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class UploadFile {

    public static void main(String[] args) throws Exception {
        
        String presignedUrlPdf = "https://minio-api.indus.apps.rayv.ai/sandbox/triggers/67a98fc1a8f8aa47a0eea66e/pl1o6k5e2s6h87859821/tn1i7t4i1n268736230/FY24_Q1_Consolidated_Financial_Statements%20%281%29.pdf?response-content-type=application%2Fpdf&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=admin%2F20250306%2Feastus2%2Fs3%2Faws4_request&X-Amz-Date=20250306T134541Z&X-Amz-Expires=360000&X-Amz-SignedHeaders=host&X-Amz-Signature=7782ef625a388458c75a5d745425d0ff23511a51e3c7b66907010d9a71623b0f";
        String fileName = "Sample";
//        String postUrl = "https://e24b-182-73-157-21.ngrok-free.app/upload_pdf";
        String postUrl = "";
        String documentId = "123";
        String fileSize = "3021";
        Map<String, Object> response = new HashMap<>(); 
        // save file in local directory
        URL url = new URL(presignedUrlPdf);
        String tempDir = System.getProperty("java.io.tmpdir");
        String filePath = tempDir + File.separator + fileName;
        boolean created = new File(filePath).createNewFile();
        if (created) {
            System.out.println("file Created");
        }
        try (InputStream in = new BufferedInputStream(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        }
        // make api call
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            File file = new File(filePath);
            ContentType contentTypePDF = ContentType.create("application/pdf");
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addPart("pdf_file", new FileBody(file, contentTypePDF, fileName));
            builder.addTextBody("documentId", documentId);
            builder.addTextBody("fileSize", fileSize);
            HttpPost post = new HttpPost(postUrl);
            post.setEntity(builder.build());
            CloseableHttpResponse res = httpClient.execute(post);
            System.out.println(res.getStatusLine().getStatusCode());
            response.put("res", res.getEntity().toString());
        } finally {
            // delete file
            File file = new File(filePath);
            file.delete();
        }
        System.out.println(response);

    }

}
