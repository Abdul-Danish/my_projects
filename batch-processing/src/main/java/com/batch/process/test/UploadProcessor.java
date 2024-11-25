package com.batch.process.test;

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

public class UploadProcessor {

    public static void main(String[] args) throws URISyntaxException, IOException {

        String presignedUrlJson = "http://localhost:9000/sandbox/SampleFile/pet_store_swagger_mod.json?response-content-type=application%2Fjson&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20241107%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20241107T084533Z&X-Amz-Expires=360000&X-Amz-SignedHeaders=host&X-Amz-Signature=a0a298935e2f8394646ebeb4cde1b5a52327386c07053ef0cd94afae821bff0e";
        String presignedUrlPdf = "http://127.0.0.1:9000/sandbox/SampleFile/pamela.pdf?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=E11K8L1E4523P1BFIG67%2F20241113%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20241113T124238Z&X-Amz-Expires=604800&X-Amz-Security-Token=eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJhY2Nlc3NLZXkiOiJFMTFLOEwxRTQ1MjNQMUJGSUc2NyIsImV4cCI6MTczMTUyMzM0MCwicGFyZW50IjoibWluaW9hZG1pbiJ9.8sWCq0O7r_8Nty4If3uPtvAkovE-vJcOzFN1YVzE6-cySNxvcsuj-bB3WhwoXyI0tfRI6r2SiA2Rco21P3tFaA&X-Amz-SignedHeaders=host&versionId=null&X-Amz-Signature=6bf3bc237f3b664baec6dd59810d22b4294314511d7a06f29737d240bbab69c2";

        String presignedUrlIndus = "https://minio-api.indus.apps.rayv.ai/sandbox/triggers/6720c01c27d52826bb69d2ef/pl1o6k5e2s6h87859821/ts1a7i3k1r5i7s8h9n5a6565/AdverseEvent.pdf?response-content-type=application%2Fpdf&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=admin%2F20241114%2Feastus2%2Fs3%2Faws4_request&X-Amz-Date=20241114T100918Z&X-Amz-Expires=360000&X-Amz-SignedHeaders=host&X-Amz-Signature=85d7762dc13d00090e1390f707393a003549eb184353fa3ea20e58a0b7328d8a";
        // save file in local directory

        URL url = new URL(presignedUrlIndus);
        String tempDir = System.getProperty("java.io.tmpdir");
        String fileName = "sample_file.pdf";
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

        String postUrl = "http://localhost:8080/api/v1/upload";
//        String postUrl = "http://20.51.205.118:8511/upload_pdf";
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {

            File file = new File(filePath);
            ContentType contentTypePDF = ContentType.create("application/pdf");
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addPart("pdf_file", new FileBody(file, contentTypePDF, fileName));
            
            builder.addTextBody("documentId", "123");
            builder.addTextBody("fileSize", "20");

            HttpPost post = new HttpPost(postUrl);
            post.setEntity(builder.build());

            CloseableHttpResponse execute = httpClient.execute(post);
            execute.getEntity().toString();
        } finally {
            // delete file
            File file = new File(filePath);
            file.delete();
        }

        /*
         * // String boundary = "*****"; // String postUrl = "http://localhost:8080/api/v1/upload"; try { HttpURLConnection conn =
         * (HttpURLConnection) new URL(postUrl).openConnection(); conn.setRequestMethod("POST"); conn.setDoOutput(true);
         * conn.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary); try (DataOutputStream os = new
         * DataOutputStream(conn.getOutputStream()); FileInputStream fileInputStream = new FileInputStream(filePath)) {
         * os.write("Content-Disposition: form-data; name=\"file\";".getBytes()); // os.writeBytes(boundary + "\r\n"); //
         * os.writeBytes("Content-Type: application/octet-stream\r\n"); // os.writeBytes("\r\n"); // os.write(content); //
         * os.writeBytes("\r\n");
         * 
         * byte[] dataBuffer = new byte[1024]; int bytesRead; while ((bytesRead = fileInputStream.read(dataBuffer, 0, 1024)) != -1) {
         * os.write(dataBuffer, 0, bytesRead); } }
         * 
         * StringBuilder response = new StringBuilder(); try (BufferedReader reader = new BufferedReader(new
         * InputStreamReader(conn.getInputStream()))) { String line; while ((line = reader.readLine()) != null) { response.append(line); } }
         * conn.disconnect(); } catch (Exception e) { System.out.printf("Exception occured while getting response %s", e); throw e; }
         * finally { // delete file File file = new File(filePath); file.delete(); System.out.println("File Removed"); }
         */
    }

}
