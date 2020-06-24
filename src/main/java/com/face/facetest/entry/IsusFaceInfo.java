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
public class IsusFaceInfo implements Serializable {
    @TableId(type = IdType.NONE)
    private String id;

    private String name;

    private String imgInfo;

    private Boolean isStrange;

    private byte[] faceData;
    private byte[] faceData1;
    private byte[] faceData2;
}
