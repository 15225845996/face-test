package com.face.facetest.utils;

import com.arcsoft.face.*;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectOrient;
import com.arcsoft.face.enums.ErrorInfo;
import com.arcsoft.face.toolkit.ImageInfo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.arcsoft.face.toolkit.ImageFactory.getRGBData;

/**
 * @Auther: zs
 * @Date: 2020/4/7 15:51
 * @Description:
 */
@Data
@Component
public class FaceUtils {

    @Value("${face.appId}")
    private String appId1;
    @Value("${face.sdkKey}")
    private String sdkKey1;

    public static String appId;
    public static String sdkKey;

    @PostConstruct
    public void init(){
        appId = this.appId1;
        sdkKey = this.sdkKey1;
    }

    //特征比对
    public static float compare(FaceEngine faceEngine,byte[] sources,byte[] target){
        FaceFeature targetFaceFeature = new FaceFeature();
        targetFaceFeature.setFeatureData(target);
        FaceFeature sourceFaceFeature = new FaceFeature();
        sourceFaceFeature.setFeatureData(sources);
        FaceSimilar faceSimilar = new FaceSimilar();
        int errorCode = faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, faceSimilar);
        return faceSimilar.getScore();
    }

    //特征
    public static byte[] feature(FaceEngine faceEngine,byte[] bytes,FaceInfo faceInfo){
        ImageInfo imageInfo = getRGBData(bytes);
        //特征提取
        FaceFeature faceFeature = new FaceFeature();
        int errorCode = faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfo, faceFeature);
        System.out.println("特征值大小：" + faceFeature.getFeatureData().length);
        return faceFeature.getFeatureData();
    }

    //获取人脸位置
    public static List<FaceInfo> detectFaces(FaceEngine faceEngine,byte[] bytes){
        //人脸检测
        ImageInfo imageInfo = getRGBData(bytes);
        List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
        int errorCode = faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);
        return faceInfoList;
    }


    public static FaceEngine getFaceEngine(){

        /*
        * 此路径为sdk压缩包中 libs文件夹下的dll文件路径
        * */
        FaceEngine faceEngine = new FaceEngine("/usr/local/nhzn/arcsoft_lib");
        //激活引擎
        int errorCode = faceEngine.activeOnline(appId, sdkKey);

        if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
            System.out.println("引擎激活失败");
        }

        ActiveFileInfo activeFileInfo=new ActiveFileInfo();
        errorCode = faceEngine.getActiveFileInfo(activeFileInfo);
        if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
            System.out.println("获取激活文件信息失败");
        }

        //引擎配置
        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);
        engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_ALL_OUT);
        engineConfiguration.setDetectFaceMaxNum(10);
        engineConfiguration.setDetectFaceScaleVal(16);
        //功能配置
        FunctionConfiguration functionConfiguration = new FunctionConfiguration();
        functionConfiguration.setSupportAge(true);
        functionConfiguration.setSupportFace3dAngle(true);
        functionConfiguration.setSupportFaceDetect(true);
        functionConfiguration.setSupportFaceRecognition(true);
        functionConfiguration.setSupportGender(true);
        functionConfiguration.setSupportLiveness(true);
        functionConfiguration.setSupportIRLiveness(true);
        engineConfiguration.setFunctionConfiguration(functionConfiguration);


        //初始化引擎
        errorCode = faceEngine.init(engineConfiguration);

        if (errorCode != ErrorInfo.MOK.getValue()) {
            System.out.println("初始化引擎失败");
        }
        return faceEngine;
    }
}
