package com.face.facetest.controller;

import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.Rect;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.face.facetest.service.FaceInfoServiceImpl;
import com.face.facetest.utils.FaceUtils;
import com.face.facetest.utils.ImageUtils;
import com.face.facetest.vo.FaceVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
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
        boolean isSuccess = false;
        HashMap<Object, Object> result = new HashMap<>();
        result.put("msg","参数异常：faceStr、name都不能为空");
        if(faceVo.getFaceStr() != null && faceVo.getName() != null){
            byte[] bytes = ImageUtils.base64ToByte(faceVo.getFaceStr());
            FaceEngine faceEngine = FaceUtils.getFaceEngine();
            FaceInfo faceInfo = null;
            if(faceVo.getX() == null || faceVo.getY() == null){//未处理的图片
                List<FaceInfo> faceInfos = FaceUtils.detectFaces(faceEngine, bytes);
                if(faceInfos != null && faceInfos.size() > 0){
                    faceInfo = faceInfos.get(0);
                }
            }else{//处理后的图片  只有头像
               /* ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                BufferedImage image = ImageIO.read(in);
                int width = image.getWidth();
                int height = image.getHeight();
                in.close();*/

                faceInfo = new FaceInfo();
                Rect rect = new Rect();
                rect.setBottom(1);
                rect.setLeft(1);
                rect.setRight(faceVo.getX() - 1);
                rect.setBottom(faceVo.getY() - 1);
                faceInfo.setRect(rect);
                faceInfo.setOrient(1);
            }
            if(faceInfo != null){
                byte[] feature = FaceUtils.feature(faceEngine, bytes, faceInfo);
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
                    isSuccess = true;
                    result.put("msg","添加/修改成功");
                }
            }else{
                result.put("msg","没找到人脸");
                System.out.println(isSuccess+"】添加Base64："+faceVo.getFaceStr());
            }
            faceEngine.unInit();
        }
        return result;
    }


    @RequestMapping("/searchUser")
    public Object searchUser(HttpServletRequest request, FaceVo faceVo) throws IOException {
        boolean isSuccess = false;
        HashMap<Object, Object> result = new HashMap<>();
        result.put("msg","参数异常，faceStr不能为空");
        List<String> strList = new ArrayList<>();
        if(faceVo.getFaceStr() != null){
            byte[] bytes = ImageUtils.base64ToByte(faceVo.getFaceStr());
            FaceEngine faceEngine = FaceUtils.getFaceEngine();
            FaceInfo faceInfo = null;
            if(faceVo.getX() == null || faceVo.getY() == null){//未处理的图片
                List<FaceInfo> faceInfos = FaceUtils.detectFaces(faceEngine, bytes);
                if(faceInfos != null && faceInfos.size() > 0){
                    faceInfo = faceInfos.get(0);
                }
            }else{//处理后的图片  只有头像
               /* ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                BufferedImage image = ImageIO.read(in);
                int width = image.getWidth();
                int height = image.getHeight();
                in.close();*/

                faceInfo = new FaceInfo();
                Rect rect = new Rect();
                rect.setBottom(1);
                rect.setLeft(1);
                rect.setRight(faceVo.getX() - 1);
                rect.setBottom(faceVo.getY() - 1);
                faceInfo.setRect(rect);
                faceInfo.setOrient(1);
            }
            if(faceInfo != null ){
                byte[] feature = FaceUtils.feature(faceEngine, bytes, faceInfo);
                if(feature != null){
                    result.put("msg","未获取到匹配的人脸（>=0.8）");
                    List<com.face.facetest.entry.FaceInfo> list = faceInfoService.list();
                    if(list != null){
                        for (com.face.facetest.entry.FaceInfo faceData : list) {
                            if(faceData.getFaceData() != null){
                                float compare = FaceUtils.compare(faceEngine, feature, faceData.getFaceData());
                                System.out.println("查找人脸，与【"+faceData.getName()+"】相似度："+compare);
                                if(compare >= 0.8){
                                    strList.add(faceData.getName()+"["+compare+"]");
                                }
                            }
                        }
                    }
                }

            }else{
                result.put("msg","获取人脸失败");
                System.out.println(isSuccess+"】查找Base64："+faceVo.getFaceStr());
            }
            if(strList.size() > 0){
                result.put("msg",strList);
                isSuccess = true;
            }
            faceEngine.unInit();
        }
        return result;
    }
}
