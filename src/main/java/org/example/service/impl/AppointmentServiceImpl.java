package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.service.AppointmentService;
import org.springframework.stereotype.Service;

/**
 * @Author sunmengjin
 * @Create AppointmentServiceImpl.java 2024/1/5 15:56
 * @Description:
 */
@Slf4j
@Service
public class AppointmentServiceImpl implements AppointmentService {
    @Override
    public boolean returnNum(Integer numberId, Integer stock) {
        try {
            System.out.println("成功退订" + numberId + "号源");
            return true;
        } catch (Exception e) {
            log.error("退订失败" + numberId + "号源");
            saveFailed2Db(numberId, stock, e.getMessage());
            return false;
        }
    }

    @Override
    public void saveFailed2Db(Integer numberId, Integer stock, String message) {
        System.out.println("成功保存退订失败号源到数据库," + numberId + "号源");
    }
}
