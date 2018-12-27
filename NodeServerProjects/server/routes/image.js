const express = require('express');
const router = express.Router();

var User = require('../models/user');
var jwt = require('jwt-simple'); // jwt token 사용
var fs = require("fs");
var mkdirp = require('mkdirp'); // directory 만드는것

// 컨텐트 이미지파일 불러오기
router.get('/getContentImage/:contentName', function(req, res){
  var contentName = req.params.contentName;
  var filename = './server/contentImage/'+contentName+'.jpg'; // C:\Users\hwang\Desktop\Capstone_Team4\simple-memo/server/contentImage/content.jpg
  var file = fs.createReadStream(filename, {flags: 'r'});
  file.pipe(res);
})

router.get('/getUserImage/:jwtToken', function(req, res){
  var decoded = jwt.decode(req.params.jwtToken, req.app.get("jwtTokenSecret"));
  var email = decoded.userCheck;

  User.findOne({ email : email }, function(err, user) {
    if(err){
      console.log("getUserImage err : "+err);
      res.send({success: false});
    }
    else if(user != null){
      if(user.imagePath == null ){
        var filename = './server/user/profile.png'; //기본 이미지
      }
      else{
        var filename = "./server/user/"+email+"/"+user.imagePath+".jpg";
      }
      var file = fs.createReadStream(filename, {flags: 'r'});
      file.pipe(res);
    }
  });
})

router.get('/getOtherUserImage/:email', function(req, res){
  var email = req.params.email;

  User.findOne({ email : email }, function(err, user) {
    if(err){
      console.log("getOthersImage err : "+err);
      res.send({success: false});
    }
    else{
      if(user.imagePath == null ){
        var filename = './server/user/profile.png'; //기본 이미지
      }
      else{
        var filename = "./server/user/"+email+"/"+user.imagePath+".jpg";
      }
      var file = fs.createReadStream(filename, {flags: 'r'});
      file.pipe(res);
    }
  });
});


// -----------------------------------------------------
// WEB 관리자 페이지 용

var multer = require('multer');
var crypto = require('crypto'); // 파일명 암호화
var mime=require('mime-types');

//define the type of upload multer would be doing and pass in its destination, in our case, its a single file with the name photo
var Q = require("q");

var upload = function (req, res, user) {
  var deferred = Q.defer();
  var storage = multer.diskStorage({
    // 서버에 저장할 폴더
    destination: function (req, file, cb) {
      cb(null, "./server/user/"+JSON.parse(JSON.stringify(user)).email);
    },

    // 서버에 저장할 파일 명
    filename: function (req, file, cb) {
      crypto.pseudoRandomBytes(16, function(err, raw) {
        cb(null, req.params.name + '.' + 'jpg');
      });
    }

  });

  var upload2 = multer({ storage: storage }).single('photo');

  upload2(req, res, function (err) {
    if (err) {
      deferred.reject();
    }
    else {
      deferred.resolve(req.file.uploadedFile);
    }
  });
  return deferred.promise;
};
router.post('/photo/:name', function(req, res, next) {
  User.findOne({ email : req.session.userCheck }, function(err, user) {

    var path = '';
    upload(req, res, user).then(function (file) {
      res.json(file);
    }, function (err) {
      res.send(500, err);
    });
  });
});

var uploadOther = function (req, res, user) {
  var deferred = Q.defer();
  var storage = multer.diskStorage({
    // 서버에 저장할 폴더

    destination: function (req, file, cb) {
      cb(null, "./server/user/"+req.params.email);
    },

    // 서버에 저장할 파일 명
    filename: function (req, file, cb) {
      crypto.pseudoRandomBytes(16, function(err, raw) {
        cb(null, req.params.name + '.' + 'jpg');
      });
    }

  });

  var uploadOther2 = multer({ storage: storage }).single('photoOther');

  uploadOther2(req, res, function (err) {
    if (err) {
      console.log("uploadOther2 err : "+err);
      deferred.reject();
    }
    else {
      deferred.resolve(req.file.uploadedFile);
    }
  });
  return deferred.promise;
};
router.post('/photoOther/:email/:name/:isAdd', function(req, res, next) {
  User.findOne({ email : req.session.userCheck }, function(err, user) {

    if(req.params.isAdd == "true"){
      mkdirp('./server/user/'+req.params.email+'/video', function (err) {
        if(err) console.log("create dir user err : "+err);
        else {}
      }); //server폴더 아래 /user/useremail/video 폴더가 생김.
    }

    uploadOther(req, res, user).then(function (file) {
      res.json(file);
    }, function (err) {
      console.log("uploadOther err : "+err);
      res.send(500, err);
    });
  });
});

router.get('/getManagerImage/:email', function(req, res){

  var email = req.params.email;

  User.findOne({ email : email }, function(err, user) {
    if(err){
      console.log("getUserImage err : "+err);
      res.send({success: false});
    }
    else{
      if(user.imagePath == null || user.imagePath == ""){
        var filename = './server/user/profile.png'; //기본 이미지
      }
      else{
        var filename = "./server/user/"+email+"/"+user.imagePath+".jpg";
      }
      var file = fs.createReadStream(filename, {flags: 'r'});
      file.pipe(res);
    }
  });
})
// -----------------------------------------------------

module.exports = router ;
