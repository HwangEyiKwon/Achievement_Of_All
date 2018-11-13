const express = require('express');
const router = express.Router();

var User = require('../models/user');
var jwt = require('jwt-simple'); // jwt token 사용
var fs = require("fs");

router.get('/getUserImage', function(req, res){
  var filename = './userImage.jpg'; // C:\Users\hwang\Desktop\Capstone_Team4\simple-memo/userImage.jpg
  var file = fs.createReadStream(filename, {flags: 'r'});

  file.pipe(res);


  // var filename = './userImage.jpg'; // C:\Users\hwang\Desktop\Capstone_Team4\simple-memo/userImage.jpg
  // var file = fs.createReadStream(filename, {flags: 'r'});
  //
  // file.pipe(res);
})

router.get('/getUserImage/:jwtToken', function(req, res){
  console.log("Image jwt토큰 "+ req.params.token);
  var decoded = jwt.decode(req.params.jwtToken, req.app.get("jwtTokenSecret"));
  console.log("Image jwt토큰 디코딩 "+ decoded.userCheck);
  var email = decoded.userCheck;


  User.findOne({ email : email }, function(err, user) {
    console.log("user.imagePath =" + user.imagePath);
    var filename = user.imagePath;
    var file = fs.createReadStream(filename, {flags: 'r'});

    file.pipe(res);
  });
  //
  // var filename = './userImage.jpg'; // C:\Users\hwang\Desktop\Capstone_Team4\simple-memo/userImage.jpg
  // var file = fs.createReadStream(filename, {flags: 'r'});
  //
  // file.pipe(res);
})

module.exports = router ;
