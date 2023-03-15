package com.jyn.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author 10263
 */
@Schema(name = "UserDTO", description = "用户信息description")
@Data
public class UserDTO {

    @Schema(description = "姓名", nullable = true, maxLength = 5)
    private String name;
    @Schema(description = "年龄")
    private Integer age;
}
