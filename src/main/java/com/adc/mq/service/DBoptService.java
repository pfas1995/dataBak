
package com.adc.mq.service;

import com.adc.mq.entity.SyncMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface DBoptService {

    public void processMessage(List<SyncMessage> syncMessages);
    public Boolean syncData(SyncMessage syncMessage);
}