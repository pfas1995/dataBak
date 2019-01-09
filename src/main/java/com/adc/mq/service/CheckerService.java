package com.adc.mq.service;

import java.util.Map;

public interface CheckerService {

    /**
     * 在进行update之前update
     * @param pks
     * @param timeStamp
     * @return
     */
    public Boolean checkBeforeUpdate(Map<String, Object> pks, Long timeStamp);


}
