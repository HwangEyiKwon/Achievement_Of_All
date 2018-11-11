const express = require('express');
const router = express.Router();

var User = require('../models/user');

var http = require("http");
var fs = require("fs");
// make sure the db instance is open before passing into `Grid`

router.get('/video', function(req,res){
  console.log("video connected");

  /* video path읽어서 pipe해주는 코드.. 어떤 비디오 가져올 지 생각해서 추가하기
  var userEmail = req.body.email;
  User.findOne({email: userEmail}, function(err, user){
    console.log("video Path: " +user.videoPath);

    var filename = user.videoPath[0];
    var file = fs.createReadStream(filename, {flags: 'r'});
  });
  */
  var filename = './1.mp4';
  var file = fs.createReadStream(filename, {flags: 'r'});

  file.pipe(res);
});

router.get('/getVideoList', function(req, res){
  var userEmail = req.body.email;
  User.findOne({email: userEmail}, function(err, user){
    console.log("video Path: " +user.videoPath);

    res.send(user.videoPath);
  });
});

router.post('/:contentID/authorizeVideo', function(req, res){
  var userEmail = req.body.email;
  User.findOne({email: userEmail}, function(err, user){
    //video 몇번째인지 체크해서 index수정해야 함..
    user.videoPath[0] = req.body.videoPath;
  });
});

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
