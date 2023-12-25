package com.lp.service.impl;

import com.lp.dto.RequestDTO;
import com.lp.enums.RequestEnum;
import com.lp.enums.ServerEnum;
import com.lp.service.EntranceService;
import com.lp.vo.ResponseVO;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author lp
 */
@Service
public class EntranceServiceImpl implements EntranceService {
    @Override
    public ResponseVO<?> operation(Long userId, RequestDTO dto) {
        if (!Objects.equals(ServerEnum.ACTIVITY, ServerEnum.getEnum(dto.getServerName()))) {
            return null;
        }
        //匹配请求
        RequestEnum anEnum = RequestEnum.getEnum(dto.getCode());
        if (anEnum == null) {
            return null;
        }
        //业务处理 在入口处已经做了同步，在此处其实可以不用再次同步
        switch (anEnum) {
            case ONE -> {
                ResponseVO vo = new ResponseVO();
                return vo;
            }
            case TWO -> {
                return new ResponseVO();
            }
            default -> {
                System.out.println("处理ddd业务");
                return null;
            }
        }
    }
}
