package org.example.controller;

import com.alibaba.fastjson.JSONObject;
import org.example.service.RedissonDelayQueue;
import org.example.vo.ReturnNumVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @Author sunmengjin
 * @Create AppointmentController.java 2024/1/5 16:02
 * @Description:
 */
@RestController
@RequestMapping("/appointment")
public class AppointmentController {
    @Autowired
    private RedissonDelayQueue redissonDelayQueue;

    @GetMapping("/returnNum")
    public ResponseEntity<Object> returnNum(Integer numberId, Integer stock, Long seconds) {
        ReturnNumVO vo = new ReturnNumVO();
        vo.setNumberId(numberId);
        vo.setStock(stock);
        redissonDelayQueue.offerTask(JSONObject.toJSONString(vo), seconds);
        return new ResponseEntity<>("success", null, HttpStatus.OK);
    }

    @GetMapping("/removeTask")
    public ResponseEntity<Object> removeTask(Integer numberId, Integer stock) {
        ReturnNumVO vo = new ReturnNumVO();
        vo.setNumberId(numberId);
        vo.setStock(stock);
        redissonDelayQueue.removeTask(JSONObject.toJSONString(vo));
        return new ResponseEntity<>("success", null, HttpStatus.OK);
    }
}
