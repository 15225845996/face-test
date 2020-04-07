package com.face.facetest.entry;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @Auther: zs
 * @Date: 2020/4/2 16:55
 * @Description:
 */
@Data
public class FaceInfo implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private byte[] faceData;
}
