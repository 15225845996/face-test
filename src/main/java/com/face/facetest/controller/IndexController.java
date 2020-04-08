package com.face.facetest.controller;

import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.face.facetest.service.FaceInfoServiceImpl;
import com.face.facetest.utils.FaceUtils;
import com.face.facetest.utils.ImageUtils;
import com.face.facetest.vo.FaceVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

/**
 * @Auther: zs
 * @Date: 2020/4/7 15:47
 * @Description:
 */
@RestController
public class IndexController {
    @Autowired
    private FaceInfoServiceImpl faceInfoService;

    @RequestMapping("/addUser")
    public Object addUser(HttpServletRequest request, FaceVo faceVo){
        HashMap<Object, Object> result = new HashMap<>();
        result.put("msg","异常");
        if(faceVo.getFaceStr() != null && faceVo.getName() != null){
            byte[] bytes = ImageUtils.base64ToByte(faceVo.getFaceStr());
            FaceEngine faceEngine = FaceUtils.getFaceEngine();
            List<FaceInfo> faceInfos = FaceUtils.detectFaces(faceEngine, bytes);
            if(faceInfos != null && faceInfos.size() > 0){
                byte[] feature = FaceUtils.feature(faceEngine, bytes, faceInfos.get(0));
                //查看是否已存在用户
                QueryWrapper<com.face.facetest.entry.FaceInfo> query = new QueryWrapper<>();
                query.lambda().eq(com.face.facetest.entry.FaceInfo::getName,faceVo.getName());
                com.face.facetest.entry.FaceInfo one = faceInfoService.getOne(query);
                if(one == null){
                    one = new com.face.facetest.entry.FaceInfo();
                    one.setName(faceVo.getName());
                }
                one.setFaceData(feature);
                boolean save = faceInfoService.saveOrUpdate(one);
                if(save){
                    result.put("msg","添加/修改成功");
                }
            }
            faceEngine.unInit();
        }
        return result;
    }


    @RequestMapping("/searchUser")
    public Object searchUser(HttpServletRequest request, FaceVo faceVo){
        HashMap<Object, Object> result = new HashMap<>();
        result.put("msg","异常");
        if(faceVo.getFaceStr() != null){
            byte[] bytes = ImageUtils.base64ToByte(faceVo.getFaceStr());
            FaceEngine faceEngine = FaceUtils.getFaceEngine();
            List<FaceInfo> faceInfos = FaceUtils.detectFaces(faceEngine, bytes);
            if(faceInfos != null && faceInfos.size() > 0){
                byte[] feature = FaceUtils.feature(faceEngine, bytes, faceInfos.get(0));
                if(feature != null){
                    List<com.face.facetest.entry.FaceInfo> list = faceInfoService.list();
                    if(list != null){
                        for (com.face.facetest.entry.FaceInfo faceInfo : list) {
                            if(faceInfo.getFaceData() != null){
                                float compare = FaceUtils.compare(faceEngine, feature, faceInfo.getFaceData());
                                if(compare >= 0.8){
                                    result.put("msg",faceInfo.getName());
                                }
                            }
                        }
                    }
                }

            }
            faceEngine.unInit();
        }
        return result;
    }
}
