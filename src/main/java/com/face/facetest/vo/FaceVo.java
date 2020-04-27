package com.face.facetest.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Auther: zs
 * @Date: 2020/4/7 16:57
 * @Description:
 */
@Data
public class FaceVo implements Serializable {
    private String name;

    private String faceStr;

    private Integer x;
    private Integer y;
}
