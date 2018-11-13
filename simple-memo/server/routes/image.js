const express = require('express');
const router = express.Router();

var User = require('../models/user');
var jwt = require('jwt-simple'); // jwt token 사용
var fs = require("fs");

// 컨텐트 이미지파일 불러오기
router.get('/getContentImage/:contentName', function(req, res){
  console.log("요청된 컨텐츠 사진 이름"+ req.params.contentName);
  var contentName = req.params.contentName;
  var filename = './server/contentImage/'+contentName+'.jpg'; // C:\Users\hwang\Desktop\Capstone_Team4\simple-memo/server/contentImage/content.jpg
  var file = fs.createReadStream(filename, {flags: 'r'});

  file.pipe(res);
})

router.get('/getUserImage/:jwtToken', function(req, res){
  console.log("Image jwt토큰 "+ req.params.token);
  var decoded = jwt.decode(req.params.jwtToken, req.app.get("jwtTokenSecret"));
  console.log("Image jwt토큰 디코딩 "+ decoded.userCheck);
  var email = decoded.userCheck;

  User.findOne({ email : email }, function(err, user) {
    console.log("user.imagePath =" + user.imagePath);
    if(user.imagePath == null){
      var filename = './server/user/profile.png'; //기본 이미지
    }
    else{
      var filename = "./server/user/"+email+"/"+user.imagePath+".jpg";
    }
    var file = fs.createReadStream(filename, {flags: 'r'});
    file.pipe(res);
 // 유저에 이미지 패스를 사용할 필요가 있나? 그냥 ./server/user/user.email/user.name Or user.email .jpg 하면 될듯.
  });
})

module.exports = router ;
