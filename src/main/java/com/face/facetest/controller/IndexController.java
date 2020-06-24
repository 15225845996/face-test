package com.face.facetest.controller;

import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.face.facetest.entry.IsusFaceInfo;
import com.face.facetest.service.IsusFaceInfoServiceImpl;
import com.face.facetest.utils.FaceUtils;
import com.face.facetest.utils.ImageUtils;
import com.face.facetest.vo.FaceVo;
import org.springframework.beans.factory.annotation.Autowired;
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
    private IsusFaceInfoServiceImpl faceInfoService;

    @RequestMapping("/addUser")
    public Object addUser(HttpServletRequest request, FaceVo faceVo){
        HashMap<Object, Object> result = new HashMap<>();
        result.put("msg","异常");
        if(faceVo.getImg() != null && faceVo.getId() != null){
            byte[] bytes = ImageUtils.base64ToByte(faceVo.getImg());
            FaceEngine faceEngine = FaceUtils.getFaceEngine();
            FaceInfo faceInfo = null;
            List<FaceInfo> faceInfos = FaceUtils.detectFaces(faceEngine, bytes);
            if(faceInfos != null && faceInfos.size() > 0){
                byte[] feature = FaceUtils.feature(faceEngine, bytes, faceInfos.get(0));
                //查看是否已存在用户
                QueryWrapper<IsusFaceInfo> query = new QueryWrapper<>();
                query.lambda().eq(IsusFaceInfo::getId,faceVo.getId());
                IsusFaceInfo one = faceInfoService.getOne(query);
                if(one == null){
                    one = new IsusFaceInfo();
                    one.setId(faceVo.getId());
                }
                one.setFaceData(feature);
                one.setName(faceVo.getName());
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
        if(faceVo.getImg() != null){
            byte[] bytes = ImageUtils.base64ToByte(faceVo.getImg());
            FaceEngine faceEngine = FaceUtils.getFaceEngine();
            List<FaceInfo> faceInfos = FaceUtils.detectFaces(faceEngine, bytes);
            if(faceInfos != null && faceInfos.size() > 0){
                byte[] feature = FaceUtils.feature(faceEngine, bytes, faceInfos.get(0));
                if(feature != null){
                    List<IsusFaceInfo> list = faceInfoService.list();
                    if(list != null){
                        for (IsusFaceInfo faceInfo : list) {
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
