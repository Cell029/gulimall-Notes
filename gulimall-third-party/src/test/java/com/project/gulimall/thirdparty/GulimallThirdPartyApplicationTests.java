package com.project.gulimall.thirdparty;

import com.aliyun.oss.OSSClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@SpringBootTest
public class GulimallThirdPartyApplicationTests {

    @Autowired
    private OSSClient ossClient;

    @Test
    void testUpload() throws FileNotFoundException {
        InputStream inputStream = new FileInputStream("F:\\pictures\\100.jpg");
        ossClient.putObject("cell-gmall", "102.jpg", inputStream);
        ossClient.shutdown();
        System.out.println("上传成功！");
    }
}
