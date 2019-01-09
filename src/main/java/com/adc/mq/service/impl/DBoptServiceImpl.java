package com.adc.mq.service.impl;

import com.adc.mq.entity.SyncMessage;
import com.adc.mq.service.DBoptService;
import org.springframework.beans.factory.annotation.Autowired;

public class DBoptServiceImpl implements DBoptService {

    @Autowired


    
    @Override
    public void saveSyncMessage(SyncMessage syncMessage) {
        String dataSource = syncMessage.getDbName();
        //Todo 切换数据源



    }
}
