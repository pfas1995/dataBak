package com.adc.mq.service;

import org.springframework.stereotype.Component;

@Component
public interface ProductService {
    public void send(String msg);
}
