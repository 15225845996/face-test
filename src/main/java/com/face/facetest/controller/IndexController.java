package com.face.facetest.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.face.facetest.entry.IsusFaceInfo;
import com.face.facetest.service.IsusFaceInfoServiceImpl;
import com.face.facetest.utils.FaceUtils;
import com.face.facetest.utils.ImageUtils;
import com.face.facetest.vo.FaceVo;
import com.sun.deploy.net.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.net.www.http.HttpClient;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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



    @RequestMapping("/sync")
    public Object sync(HttpServletRequest request) throws Exception {
        HashMap<Object, Object> result = new HashMap<>();
        result.put("msg","异常");
        String str = getStudentS();
        if(str != null){
            FaceEngine faceEngine = FaceUtils.getFaceEngine();
            JSONObject jsonObject = JSON.parseObject(str);
            if(jsonObject.get("data") != null){
                JSONArray students = (JSONArray)jsonObject.get("data");
                System.out.println(students.size());
                for (Object student : students) {
                    JSONObject stu = (JSONObject)student;
                    if(stu.get("faceId") == null || stu.get("faceUrl") == null || "".equals(stu.get("faceUrl").toString())){
                        continue;
                    }
                    String id = stu.get("faceId").toString();
                    String name = stu.get("name").toString();
                    String faceUrl = stu.get("faceUrl").toString();
                    byte[] bytes = null;
                    URL urlConet = null;
                    ByteArrayOutputStream out = null;
                    try {
                        urlConet = new URL(faceUrl);
                        URLConnection con = urlConet.openConnection();
                        InputStream in = con.getInputStream();
                        byte[] buffer = new byte[4 * 1024];
                        int n = 0;
                        out = new ByteArrayOutputStream();
                        while ((n = in.read(buffer)) != -1) {
                            out.write(buffer, 0, n);
                        }
                        bytes = out.toByteArray();
                    } catch (Exception e) {
                        e.printStackTrace();
                        if(out != null){
                            out.close();
                        }
                    }
                    if(bytes != null){
                        List<FaceInfo> faceInfos = FaceUtils.detectFaces(faceEngine, bytes);
                        if(faceInfos != null && faceInfos.size() > 0) {
                            byte[] feature = FaceUtils.feature(faceEngine, bytes, faceInfos.get(0));
                            if (feature != null) {
                                IsusFaceInfo isusFaceInfo = new IsusFaceInfo();
                                isusFaceInfo.setId(id);
                                isusFaceInfo.setName(name);
                                isusFaceInfo.setIsStrange(false);
                                isusFaceInfo.setFaceData(feature);
                                boolean save = faceInfoService.saveOrUpdate(isusFaceInfo);
                                System.out.println(id+"："+name+"]"+save);
                            }
                        }
                    }
                }
            }
            faceEngine.unInit();
        }
        return result;
    }


    public void fun(){

    }



    /**
     * POST---无参测试
     *
     * @date 2018年7月13日 下午4:18:50
     */
    public String getStudentS() {
        String result = null;

        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        // 创建Post请求
        HttpPost httpPost = new HttpPost("http://localhost:8001/face/baidu/getStudent");
        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Post请求
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();

            System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                result = EntityUtils.toString(responseEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
