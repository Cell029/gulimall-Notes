package com.gulimall.gulimallauthserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class GulimallAuthServerApplicationTests {

    @Autowired
    RestTemplate restTemplate;

    @Test
    public void testGithub() {
        String result = restTemplate.getForObject("https://api.github.com", String.class);
        System.out.println(result);
    }


}
