package org.n.riesgos.asyncwrapper.filestorage;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class FileStorage {

    private final String endpoint;
    private final String user;
    private final String password;

    public FileStorage(final String endpoint, final String user, final String password) {
        this.endpoint = endpoint;
        this.user = user;
        this.password = password;
    }

    // needed as extra method in java, as kotlin has trouble using the .object method
    // of the PutObjectArgs.Builder :-(
    public String upload(final String bucket, final String name, final byte[] content, final String mimeType) throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
        final MinioClient client = MinioClient.builder().endpoint(this.endpoint).credentials(this.user, this.password).build();
        final PutObjectArgs.Builder putObjectArgsBuilder = PutObjectArgs.builder();
        putObjectArgsBuilder.stream(new ByteArrayInputStream(content), content.length, -1);
        putObjectArgsBuilder.object(name);
        putObjectArgsBuilder.bucket(bucket);
        if (mimeType != null) {
            putObjectArgsBuilder.contentType(mimeType);
        }
        final PutObjectArgs putObjectArgs = putObjectArgsBuilder.build();
        client.putObject(putObjectArgs);
        return name;
    }
}
