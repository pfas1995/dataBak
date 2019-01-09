package com.adc.mq.service;

import com.adc.mq.entity.SyncMessage;

import java.util.List;

public interface DBoptService {

    public List<Integer> processMessage(List<SyncMessage> syncMessages);
    public Boolean syncData(SyncMessage syncMessage);
}
