package com.zheng;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@SpringBootTest
public class miniotest {
    @Autowired
    private MinioClient minioClient;
    @Test
    public void test_create_bucket() throws Exception{
        minioClient.makeBucket(MakeBucketArgs.builder().bucket("myfile").build());
    }
    @Test
    public void test_download() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.downloadObject(DownloadObjectArgs.builder()
                .bucket("netdisk")
                .build());
    }
    @Test
    public void test_download_url() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String test1 = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs
                .builder()
                .bucket("netdisk")
                .object("VID_20231003_140942.mp4")
                .method(Method.GET)
                .build());
        System.out.println(test1);
    }
    @Test
    public void test_list_object(){
        Iterable<Result<Item>> netdisk = minioClient.listObjects(ListObjectsArgs.builder().bucket("netdisk").build());
        netdisk.forEach(itemResult -> {
            Item item = null;
            try {
                item = itemResult.get();
            } catch (ErrorResponseException e) {
                throw new RuntimeException(e);
            } catch (InsufficientDataException e) {
                throw new RuntimeException(e);
            } catch (InternalException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            } catch (InvalidResponseException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (ServerException e) {
                throw new RuntimeException(e);
            } catch (XmlParserException e) {
                throw new RuntimeException(e);
            }
            System.out.println(item.objectName());
        });
    }
}
