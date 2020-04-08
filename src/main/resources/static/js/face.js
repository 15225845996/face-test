
//成功回调函数
var video = document.getElementById("video");
var canvas = document.getElementById("canvas");
var context = canvas.getContext("2d");
var mediaStreamTrack=null;
function success(stream){
    //兼容webkit核心浏览器
    //将视频流转化为video的源
    mediaStreamTrack=stream;
    try {
        // video.src = CompatibleURL.createObjectURL(stream);
        video.srcObject=stream;
    }catch (e) {
        console.log("访问用户媒体设备失败：",error.name,error.message);
    }

    video.play();//播放视频
}
//错误回调函数
function error(error) {
    console.log('访问用户媒体失败：',error.name,error.message);
}
function getUserMediaToPhoto(constraints,success,error) {
    if(navigator.mediaDevices.getUserMedia){
        //最新标准API
        navigator.mediaDevices.getUserMedia(constraints).then(success).catch(error);
    }else if (navigator.webkitGetUserMedia) {
        //webkit核心浏览器
        navigator.webkitGetUserMedia(constraints,success,error);
    }else if(navigator.mozGetUserMedia){
        //firefox浏览器
        navigator.mozGetUserMedia(constraints,success,error);
    }else if(navigator.getUserMedia){
        //旧版API
        navigator.getUserMedia(constraints,success,error);
    }
}
//获取图片
function getFace() {
    context.drawImage(video,0,0,150,150);
    this.img=canvas.toDataURL('image/jpg')
    //获取完整的base64编码
    this.img=img.split(',')[1];
    return this.img;
}
//打开摄像头
function openUserMedia() {
    if(navigator.mediaDevices.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.getUserMedia){
        getUserMediaToPhoto({video:{width:150,height:150,facingMode: "user"}},success,error);
    }else{
        alert('你的浏览器不支持访问用户媒体设备');
    }
}
//关闭摄像头
function  offUserMedia() {
    if(mediaStreamTrack!=null)
        mediaStreamTrack.getTracks()[0].stop();
}



