package com.lp.task;


import cn.hutool.json.JSONUtil;
import com.lp.dto.Message;
import com.lp.enums.WebSocketEnum;
import com.lp.util.WebSocketUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务
 *
 * @author lp
 */
@Component
public class WsTask {

    /**
     * socket心跳
     * 每5秒执行一次
     */
    @Scheduled(cron = "0/5 * *  * * ? ")
    public void heartbeatTask() {
        WebSocketEnum heartbeat = WebSocketEnum.HEARTBEAT;
        Message demo = new Message();
        demo.setCode(heartbeat.getCode());
        demo.setMessage(heartbeat.getMessage());
        demo.setServerName(heartbeat.getServerName());
        demo.setData(JSONUtil.toJsonStr(heartbeat.getData()));

        WebSocketUtil.sendMessage(demo);
    }
}
