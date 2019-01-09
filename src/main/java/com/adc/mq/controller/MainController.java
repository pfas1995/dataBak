package com.adc.mq.controller;

import com.adc.mq.dao.TestRepository;
import com.adc.mq.entity.Test;
import com.adc.mq.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private TestRepository testRepository;

    @GetMapping("/send")
    public void send() {
        Test t = Test.Factory(1L, "test");
        testRepository.save(t);
        productService.send("hello");
    }

}
