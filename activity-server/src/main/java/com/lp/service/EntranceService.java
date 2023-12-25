package com.lp.service;

import com.lp.dto.RequestDTO;
import com.lp.vo.ResponseVO;

/**
 * @author lp
 */
public interface EntranceService {

    /**
     * 业务处理  不需要想客户端推送数据，直接返回null即可
     *
     * @param userId 用户ID
     * @param dto    数据
     * @return ResponseVO
     */
    ResponseVO operation(Long userId, RequestDTO dto);

}
