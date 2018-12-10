const express = require('express');
const router = express.Router();

var User = require('../models/user');
var jwt = require('jwt-simple'); // jwt token 사용
var fs = require("fs");
var mkdirp = require('mkdirp'); // directory 만드는것

// 컨텐트 이미지파일 불러오기
router.get('/getContentImage/:contentName', function(req, res){
  console.log("getContentImage Start");
  console.log("요청된 컨텐츠 사진 이름"+ req.params.contentName);
  var contentName = req.params.contentName;
  var filename = './server/contentImage/'+contentName+'.jpg'; // C:\Users\hwang\Desktop\Capstone_Team4\simple-memo/server/contentImage/content.jpg
  var file = fs.createReadStream(filename, {flags: 'r'});

  file.pipe(res);
})

router.get('/getUserImage/:jwtToken', function(req, res){
  console.log("getUserImage Start");
  // console.log("Image jwt토큰 "+ req.params.jwtToken);
  var decoded = jwt.decode(req.params.jwtToken, req.app.get("jwtTokenSecret"));
  // console.log("Image jwt토큰 디코딩 "+ decoded.userCheck);
  var email = decoded.userCheck;

  User.findOne({ email : email }, function(err, user) {
    if(err){
      console.log("getUserImage err : "+err);
      res.send({success: false});
    }
    else{
      console.log("user.imagePath =" + user.imagePath);
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
  console.log("getOthersImage Start");
  var email = req.params.email;

  User.findOne({ email : email }, function(err, user) {
    if(err){
      console.log("getOthersImage err : "+err);
      res.send({success: false});
    }
    else{
      console.log("user.imagePath =" + user.imagePath);
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
  console.log("왜안되는거야?진짜로?");
  var deferred = Q.defer();
  console.log(user);

  console.log(JSON.parse(JSON.stringify(user)).imagePath);
  console.log(JSON.parse(JSON.stringify(user)).email);
  console.log(req);

  var storage = multer.diskStorage({
    // 서버에 저장할 폴더

    destination: function (req, file, cb) {
      cb(null, "./server/user/"+JSON.parse(JSON.stringify(user)).email);
    },

    // 서버에 저장할 파일 명
    filename: function (req, file, cb) {
      crypto.pseudoRandomBytes(16, function(err, raw) {
        console.log(req.body);
        console.log("photophoto3");
        cb(null, req.body.name + '.' + 'jpg');
      });
    }

  });

  var upload2 = multer({ storage: storage }).single('photo');

  upload2(req, res, function (err) {
    console.log("왜안되는거야?진짜로?2222");
    if (err) {
      console.log(err);
      console.log("왜안되는거야?진짜로?333");
      deferred.reject();
    }
    else {
      console.log("왜안되는거야?진짜로?4444");
      deferred.resolve(req.file.uploadedFile);
    }
  });
  return deferred.promise;
};
router.post('/photo', function(req, res, next) {
  console.log("PHOTO");
  User.findOne({ email : req.session.userCheck }, function(err, user) {

    console.log("photophoto");

    console.log("photophoto4");
    var path = '';
    console.log('파일이름바꾸기');
    // console.log(req);

    upload(req, res, user).then(function (file) {
      // console.log('aaffaaff');
      // console.log(file);
      res.json(file);
    }, function (err) {
      res.send(500, err);
    });
  });
});

var uploadOther = function (req, res, user) {
  console.log("왜안되는거야?진짜로?");
  console.log(req.params.email);
  console.log(req.params.name);

  var deferred = Q.defer();
  console.log(user);

  console.log(JSON.parse(JSON.stringify(user)).imagePath);
  console.log(JSON.parse(JSON.stringify(user)).email);


  var storage = multer.diskStorage({
    // 서버에 저장할 폴더

    destination: function (req, file, cb) {
      cb(null, "./server/user/"+req.params.email);
    },

    // 서버에 저장할 파일 명
    filename: function (req, file, cb) {
      crypto.pseudoRandomBytes(16, function(err, raw) {
        console.log("photophoto3");
        cb(null, req.params.name + '.' + 'jpg');
      });
    }

  });

  var uploadOther2 = multer({ storage: storage }).single('photoOther');

  uploadOther2(req, res, function (err) {
    console.log("왜안되는거야?진짜로?2222");
    if (err) {
      console.log(err);
      console.log("왜안되는거야?진짜로?333");
      deferred.reject();
    }
    else {
      console.log("왜안되는거야?진짜로?4444");
      deferred.resolve(req.file.uploadedFile);
    }
  });
  return deferred.promise;
};
router.post('/photoOther/:email/:name/:isAdd', function(req, res, next) {
  console.log("PHOTOOTHER");
  User.findOne({ email : req.session.userCheck }, function(err, user) {

    console.log("photophoto");
    console.log("photophoto4");
    console.log('파일이름바꾸기');
    console.log(req.params.isAdd);

    if(req.params.isAdd == "true"){

      console.log("왜 안만들어");
      mkdirp('./server/user/'+req.params.email+'/video', function (err) {
        if(err) console.log("create dir user err : "+err);
        else console.log("create dir ./user/" + req.params.email );
      }); //server폴더 아래 /user/useremail/video 폴더가 생김.
    }

    uploadOther(req, res, user).then(function (file) {
      // console.log('aaffaaff');
      // console.log(file);
      res.json(file);
    }, function (err) {
      console.log(err);
      res.send(500, err);
    });
  });
});

router.get('/getManagerImage/:email', function(req, res){

  console.log("getUserImage Start");
  var email = req.params.email;
  console.log("email : "+ email);

  User.findOne({ email : email }, function(err, user) {
    console.log("앙?" + JSON.stringify(user));
    if(err){
      console.log("getUserImage err : "+err);
      res.send({success: false});
    }
    else{
      // console.log("user.imagePath =" + user.imagePath);
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
