const express = require('express');
const router = express.Router();
var passport = require('passport');
const passportConfig = require('../../config/passport');
var User = require('../models/user');
var jwt = require('jwt-simple'); // jwt token 사용

router.post('/jwtCheck', function(req, res){

  console.log("jwtCheck jwt토큰 "+ req.body.token);
  var decoded = jwt.decode(req.body.token,req.app.get("jwtTokenSecret"));
  console.log("jwtCheck jwt토큰 디코딩 "+ decoded.userCheck);
  var email = decoded.userCheck;

  User.findOne({ email : email }, function(err, user) {
    console.log(user);
    if(err){
      console.log(err);
      res.send({success: false});
    }
    res.send({success: true});
  });

});
router.post('/login', function(req,res,next){

  passport.authenticate('login', function (err, user, info) {

    if(err) console.log(err);
    if(user){
      // var expires = moment().add('days', 7).valueOf();

      // jwt 토큰 생성
      var token = jwt.encode({
        userCheck: req.body.email,
        // exp: expires
      }, req.app.get("jwtTokenSecret"));

      // response header에 추가
      res.setHeader("token", token)
      res.send({success: true});
    }
    else res.send({success: false});
  })(req,res,next);
});

router.post('/logout', function(req, res){

  console.log("logout jwt토큰 "+ req.body.token);
  var decoded = jwt.decode(req.body.token,req.app.get("jwtTokenSecret"));
  console.log("logout jwt토큰 디코딩 "+ decoded.userCheck);
  var email = decoded.userCheck;

  User.findOne({ email : email }, function(err, user) {
    console.log(user);
    if(err){
      console.log(err);
      res.send({success: false});
    }
    user.pushToken = null;
    console.log('user token is ='+ user.pushToken + '!!');


  });
  res.send({success: true});
});

router.post('/signup', function (req, res, next) {

  passport.authenticate('signup', function (err, user, info) {
    // console.log(user+"s");
    console.log("signUPPPPPPPP");
    if(err) console.log(err);
    if(user) res.send({success: true});
    else res.send({success: false});
  })(req,res,next);
});


router.post('/getUserInfo', function (req,res) {

  console.log("get User Info: "+JSON.stringify(req.body));
  console.log("받은 jwt토큰 "+ req.body.token);
  var decoded = jwt.decode(req.body.token, req.app.get("jwtTokenSecret"));
  console.log("받은 jwt토큰 디코딩 "+ decoded.userCheck);
  var email = decoded.userCheck;
  console.log(email);
  User.findOne({email: email}, function(err, info){
    if(err) console.log(err);
    if(info == null) {
      console.log("사용자 아님");
    }
    else {
      console.log("사용자 찾음");
      res.send(info);
    }
  })
})

module.exports = router ;
