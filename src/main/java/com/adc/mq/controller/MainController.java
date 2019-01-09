package com.adc.mq.controller;

import com.adc.mq.entity.SyncMessage;
import com.adc.mq.service.ProductService;
import com.adc.mq.service.impl.DBoptServiceImpl;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private ProductService productService;


    @GetMapping("/add")
    public void send() {

        List<SyncMessage> syncMessages = new ArrayList<>();
        Map<String, Object> data = new HashMap<>();
        data.put("id", 1L);
        data.put("text", "test insert");

        SyncMessage syncMessage = SyncMessage.SyncMessageFactory("postgres", DBoptServiceImpl.OPT_ADD,
                "test", new Date().getTime(), null, data);
        syncMessages.add(syncMessage);

        data = new HashMap<>();
        data.put("id", 2L);
        data.put("text", "insert test 2");

        syncMessage = SyncMessage.SyncMessageFactory("postgres", DBoptServiceImpl.OPT_ADD,
                "test", new Date().getTime(), null, data);

        syncMessages.add(syncMessage);
        Gson gson = new Gson();
        String jsonMessage = gson.toJson(syncMessages);
        productService.send(jsonMessage);
    }

    @GetMapping("/update")
    public void update() {

        List<SyncMessage> syncMessages = new ArrayList<>();
        Map<String, Object> data = new HashMap<>();
        data.put("id", 1L);
        data.put("text", "update insert");
        data.put("time", new Date());

        Map<String, Object> pk = new HashMap<>();
        pk.put("id", 1L);

        SyncMessage syncMessage = SyncMessage.SyncMessageFactory("postgres", DBoptServiceImpl.OPT_UPDATE,
                "test", new Date().getTime(), pk, data);
        syncMessages.add(syncMessage);
        Gson gson = new Gson();
        String jsonMessage = gson.toJson(syncMessages);
        productService.send(jsonMessage);
    }

    @GetMapping("/delete")
    public void del() {

        List<SyncMessage> syncMessages = new ArrayList<>();
        Map<String, Object> data = new HashMap<>();
        data.put("id", 1L);
        data.put("text", "update insert");

        Map<String, Object> pk = new HashMap<>();
        pk.put("id", 1L);

        SyncMessage syncMessage = SyncMessage.SyncMessageFactory("postgres", DBoptServiceImpl.OPT_DEL,
                "test", new Date().getTime(), pk, data);
        syncMessages.add(syncMessage);
        Gson gson = new Gson();
        String jsonMessage = gson.toJson(syncMessages);
        productService.send(jsonMessage);
    }

}
