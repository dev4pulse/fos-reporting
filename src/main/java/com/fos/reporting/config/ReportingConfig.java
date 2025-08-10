package com.fos.reporting.config;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
@Configuration
public class ReportingConfig {
    @Value("${gcs.bucket.name}")
    private String bucketName;
    @Value("${gcs.bucket.serviceAccount.filename}")
    private String serviceAccountFileName;
    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(false);
        loggingFilter.setIncludeHeaders(true);
        loggingFilter.setMaxPayloadLength(500);
        return loggingFilter;
    }

    @Bean("serviceAccountStorage")
    public Storage storage(Storage storage) throws IOException {
        Blob blob = storage.get(BlobId.of(bucketName, serviceAccountFileName));
        byte[] content = blob.getContent();
        InputStream credentialsStream = new ByteArrayInputStream(content);
        return StorageOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.fromStream(credentialsStream))
                .build()
                .getService();
    }

    @Bean
    @Primary
    public Storage defaultStorage() {
        return StorageOptions.getDefaultInstance().getService();
    }
}
