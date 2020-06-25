package com.face.facetest.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.Rect;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.face.facetest.entry.IsusFaceInfo;
import com.face.facetest.service.IsusFaceInfoServiceImpl;
import com.face.facetest.utils.ExpiryMap;
import com.face.facetest.utils.FaceUtils;
import com.face.facetest.utils.FileUtils;
import com.face.facetest.utils.ImageUtils;
import com.face.facetest.vo.FaceVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Auther: zs
 * @Date: 2020/6/17 16:37
 * @Description:
 */
@Scope("prototype")
@RestController
@RequestMapping("/face")
public class FaceController {


    public static ExpiryMap<String, String> CacheMap = new ExpiryMap<>();// 简单缓存

    public static SimpleDateFormat sdf = new SimpleDateFormat("HHmm");

    @Value("${school}")
    private String schoolId;
    @Value("${strange.prefix}")
    private String strangePrefix;
    @Value("${face.score}")
    private Integer scoreNum;
    @Value("${cache.time}")
    private Integer cacheTime;


    @Autowired
    private IsusFaceInfoServiceImpl faceInfoService;

    @RequestMapping("/search")
    public Object search(HttpServletRequest request, FaceVo faceVo){
        String callback = request.getParameter("callback");
        FaceVo result = new FaceVo();
        if(StringUtils.isNotBlank(faceVo.getImg())){
            byte[] bytes = ImageUtils.base64ToByte(faceVo.getImg());
            FaceEngine faceEngine = FaceUtils.getFaceEngine();
            FaceInfo faceInfo = null;
            if(faceVo.getX() == null || faceVo.getY() == null){//未处理的图片
                List<FaceInfo> faceInfos = FaceUtils.detectFaces(faceEngine, bytes);
                if(faceInfos != null && faceInfos.size() > 0){
                    faceInfo = faceInfos.get(0);
                }
            }else{//处理后的图片  只有头像
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
                if(feature != null){
                    QueryWrapper<IsusFaceInfo> query = new QueryWrapper<>();
                    query.orderByAsc("is_strange");
                    query.select("id","name","is_strange","face_data","face_data1","face_data2");
                    List<IsusFaceInfo> list = faceInfoService.list(query);
                    if(list != null){
                        for (IsusFaceInfo faceData : list) {
                            if(faceData.getFaceData() != null){
                                float compare = FaceUtils.compare(faceEngine, feature, faceData.getFaceData());
                                if(compare >= 0.8){
                                    result.setCode(1);
                                    result.setIsStrange(faceData.getIsStrange());
                                    result.setId(faceData.getId());
                                    result.setName(faceData.getName());
                                    result.setScore(compare);
                                    break;
                                }
                            }
                        }
                    }
                    if(result.getCode() == -1){//库里没有改人脸信息
                        IsusFaceInfo isusFaceInfo = new IsusFaceInfo();
                        isusFaceInfo.setId(System.currentTimeMillis()+""+(int)(1+Math.random()*(100-1+1)));
                        isusFaceInfo.setName("陌生人");
                        isusFaceInfo.setIsStrange(true);
                        isusFaceInfo.setFaceData(feature);
                        isusFaceInfo.setImgInfo(faceVo.getImgHeand()+","+faceVo.getImg());
                        boolean save = faceInfoService.saveOrUpdate(isusFaceInfo);
                        if(save){
                            result.setCode(1);
                            result.setId(isusFaceInfo.getId());
                            result.setName(isusFaceInfo.getName());
                            result.setScore(1);
                            result.setIsStrange(isusFaceInfo.getIsStrange());
                        }
                    }
                }
                if(result.getCode() == 1){
                    synchronized (FaceController.class){
                        if (CacheMap.containsKey(result.getId())) {//重复
                            result.setCode(2);
                        }
                        CacheMap.put(result.getId(), result.getName(), cacheTime);
                    }
                }
            }else{
                System.out.println("特征值异常："+faceVo.getImgHeand()+","+faceVo.getImg());
            }
            faceEngine.unInit();
        }
        if(result == null){
            result = new FaceVo();
        }
        return callback + "(" + JSON.toJSONString(result) + ")";
    }
}
