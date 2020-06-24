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
    /*base64图片信息*/
    private String img;
    private String imgHeand;

    /*人员唯一标示*/
    private String id;
    /*人员姓名*/
    private String name;
    /*提示消息*/
    private String msg;
    /*结果标示  1：未重复，2：未重复*/
    private Integer code = -1;

    //是否陌生人
    private Boolean isStrange;

    /*相似度*/
    private float score;
    /*年级*/
    private String grade;
    /*班级*/
    private String clazz;



    private Integer x;
    private Integer y;
}
