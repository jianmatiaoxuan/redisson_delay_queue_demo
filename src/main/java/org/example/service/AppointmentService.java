package org.example.service;

import org.example.vo.ReturnNumVO;

/**
 * @Author sunmengjin
 * @Create AppointmentService.java 2024/1/5 15:56
 * @Description:
 */
public interface AppointmentService {
    boolean returnNum(Integer numberId, Integer stock);

    void saveFailed2Db(Integer numberId, Integer stock, String message);
}
