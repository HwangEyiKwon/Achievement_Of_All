const express = require('express');
const router = express.Router();

var User = require('../models/user');
var jwt = require('jwt-simple'); // jwt token 사용
var fs = require("fs");
// make sure the db instance is open before passing into `Grid`


// jwt token이용 -> email 추출 -> ...진행
//to.승현) get->post로 변경하여 이메일 인증을 위해 req.body에서 token가져올 수 있게끔 함
router.post('/video', function(req,res){
  console.log("video connected");
  console.log("video jwt토큰 "+ req.body.token);
  var decoded = jwt.decode(req.body.token,req.app.get("jwtTokenSecret"));
  console.log("video jwt토큰 디코딩 "+ decoded.userCheck);
  var userEmail = decoded.userCheck;

  User.findOne({email: userEmail}, function(err, user){
    var joinContentCount = user.contentList.length;

    console.log("video Path: " +user.videoPath);
    for(var i = 0; i < joinContentCount ;  i++) {
      var numOfVideo = user.contentList[i].videoPath.length;
      if(numOfVideo != null){
        for(var j = 0; j < numOfVideo; j++) {
          var filename = user.contentList[i].videoPath[j];
          var file = fs.createReadStream(filename, {flags: 'r'});
          file.pipe(res);
        }
      }
    }
  });
});
router.get('/getVideo/:jwtToken/:contentName/:videoPath', function(req,res){

  console.log("Image jwt토큰 "+ req.params.token);
  var decoded = jwt.decode(req.params.jwtToken, req.app.get("jwtTokenSecret"));
  console.log("Image jwt토큰 디코딩 "+ decoded.userCheck);
  var userEmail = decoded.userCheck;
  var contentName = req.params.contentName;
  var videoPath = req.params.videoPath;

  console.log("ddd"+userEmail+contentName+videoPath);

  User.findOne({email: userEmail}, function(err, user){

    // 내 비디오가 맞는지 검사해야할 듯
    var filename = "./server/user/"+userEmail+"/video/"+contentName+"/"+videoPath+".mp4"
    var file = fs.createReadStream(filename, {flags: 'r'});
    file.pipe(res);

  });
});

//jwt토큰 필요 -> email 받아옴
//email로 해당 유저의 user.contentList[0].videoPath를 가져옴
router.get('/getVideoList', function(req, res){
  var userEmail = req.body.email;
  User.findOne({email: userEmail}, function(err, user){
    console.log("video Path: " +user.contentList[0].videoPath);

    res.send(user.contentList[0].videoPath);
  });
});

//contents정보 받아야 하고, 현재 date를 넣어줘야 하고, path를 지정해서 해당 user의 video경로에 path를 저장한다.
router.post('/:contentID/authorizeVideo', function(req, res){
  var userEmail = req.body.email;
  User.findOne({email: userEmail}, function(err, user){
    //video 몇번째인지 체크해서 index수정해야 함..
    user.contentList[0].videoPath[0] = req.body.videoPath;
  });
});

//남에게 videolist를 줘야하는 것... -> 같은 컨텐츠의 참여하는 다른 사람에게 전송 or 같은 방 참여 다른 사람에게 전송
//인증여부에 대한 것도 post로 만들어야 하지 않나?????
//단계 -> server야 나 인증할거야! -> client에게 videopath보내주기 -> 인증 성공 여부 server에게 보내기
router.post('/:contentID/checkVideo', function(req, res){
  /* video path읽어서 pipe해주는 코드.. 어떤 비디오 가져올 지 생각해서 추가하기
  var userEmail = req.body.email;
  User.findOne({email: userEmail}, function(err, user){
    console.log("video Path: " +user.videoPath);

    var filename = user.videoPath[0];
    var file = fs.createReadStream(filename, {flags: 'r'});
  });
  */
  file.pipe(res);
});

module.exports = router ;
