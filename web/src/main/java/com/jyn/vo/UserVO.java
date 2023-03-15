package com.jyn.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author 10263
 */
@Schema(name = "UserVO", description = "用户信息")
@Data
public class UserVO {

    @Schema(description = "姓名")
    private String name;
    @Schema(description = "年龄")
    private Integer age;
}
