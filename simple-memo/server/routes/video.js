const express = require('express');
const router = express.Router();

var User = require('../models/user');

var http = require("http");
var fs = require("fs");
// make sure the db instance is open before passing into `Grid`

// path설정 방식.. server -> /video/userEmail/contentName/date.mp4
// post로 변경
// jwt token이용 -> email 추출 -> ...진행
//만약, video path를 직접 받는다면, 상위의 단계 필요없이 바로!!! path만 filename으로 넣어주고 pipe 하면 됨
router.get('/video', function(req,res){
  console.log("video connected");

  /* video path읽어서 pipe해주는 코드.. 어떤 비디오 가져올 지 생각해서 추가하기
  var userEmail = req.body.email;
  User.findOne({email: userEmail}, function(err, user){
    console.log("video Path: " +user.videoPath);

    var filename = user.contentList[0].videoPath[0];
    var file = fs.createReadStream(filename, {flags: 'r'});
  });
  */
  var filename = './1.mp4'; //path : /video/userEmail/contentName/date.mp4
  var file = fs.createReadStream(filename, {flags: 'r'});

  file.pipe(res);
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
