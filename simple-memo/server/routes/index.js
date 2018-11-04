const express = require('express');
const router = express.Router();
var passport = require('passport');
const passportConfig = require('../../config/passport');
var User = require('../models/user');

router.post('/login', function(req,res,next){
  passport.authenticate('login', function (err, user, info) {
    console.log(user+"asdfasdf");
    if(err) console.log(err);
    if(user) res.send({success: true});
    else res.send({success: false});
  })(req,res,next);
});

router.post('/logout', function(req, res){
  User.findone({ email : req.body.email }, function(err, user) {
    if(err){
      console.log(err);
      res.send({success: false});
    }
    user.pushToken = null;
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

  var email = req.body.email ;

  user.fineOne({email: email}, function(err, info){
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
