const express = require('express');
const router = express.Router();
var User = require('../models/user');
var Content = require('../models/content');
var passport = require('passport');
var jwt = require('jwt-simple'); // jwt token 사용
const passportConfig = require('../../config/passport');

// -----------------------------------------------------
// WEB 관리자 페이지 용

router.get('/getManagersInfo', function(req, res) {
  if(req.session.userCheck == undefined) { // 사용자 세션 체크, 세션 없으면 오류페이지
    res.send({error:true});
  } else {
    User.findOne({email: req.session.userCheck}, function (err, user) {
      if (err) throw err;

      if (JSON.parse(JSON.stringify(user)).authority == 'manager') {
        User.find({}, function (err, user) {
          res.send(user);
        });
      } else {  }
    });
  }
});
router.post('/deleteManagerInfo', function(req, res) {
  if(req.session.userCheck == undefined) { // 사용자 세션 체크, 세션 없으면 오류페이지
    res.send({error:true});
  } else {
    User.findOne({email: req.session.userCheck}, {password: 0}, function (err, user) {
      if (err) throw err;

      if (JSON.parse(JSON.stringify(user)).authority == 'manager') {
        User.remove({name: req.body.name, email: req.body.email}, function (err, result) {
          if (err) throw err;
          console.log('delete success');
          res.send({});
        });
      } else { }
    });
  }
});

router.post('/updateManagerInfo', function(req, res) {
  if(req.session.userCheck == undefined) { // 사용자 세션 체크, 세션 없으면 오류페이지
    res.send({error:true});
  } else {
    User.findOne({email: req.session.userCheck}, function (err, user) {
      if (err) throw err;

      if (JSON.parse(JSON.stringify(user)).authority == 'manager') {

        User.findOne({email: req.body.userInfo.email}, function (err, ur) {

          console.log(ur);
          console.log("vzcvzxcvzxcvzxcvzxcvzxcvz");
          if (ur.password == req.body.userNewInfo.password) {
            User.findOneAndUpdate({email: req.body.userInfo.email},
              {
                name: req.body.userNewInfo.name,
                email: req.body.userNewInfo.email,
                phoneNumber: req.body.userNewInfo.phoneNumber,
                authority: req.body.userNewInfo.authority,
                imagePath: req.body.userNewInfo.image
              },
              function (err, userUpdate) {
                if (err) throw err;
                console.log(userUpdate);
                console.log('update success');
                res.send({success:true});
              });
          } else {
            User.findOneAndUpdate({email: req.body.userInfo.email},
              {
                name: req.body.userNewInfo.name,
                email: req.body.userNewInfo.email,
                phoneNumber: req.body.userNewInfo.phoneNumber,
                authority: req.body.userNewInfo.authority,
                imagePath: req.body.userNewInfo.image,
                password: user.generateHash(req.body.userNewInfo.password)
              },
              function (err, userUpdate) {
                if (err) throw err;
                console.log(userUpdate);
                console.log('update success with password');
                res.send({success:true});
              });
          }
        })

      } else { }
    });
  }
});

router.post('/addUserInfo', function(req, res, next) {
  req.body = req.body.userNewInfo;
  passport.authenticate('add-newuser', function(err, user, info) {
    if (err) { return next(err); }
    if (!user) { res.json({success:false}); }
    else { res.json({success:true}); }
  })(req, res, next);
});



router.get('/getContentsInfo', function(req, res) {
  if(req.session.userCheck == undefined) { // 사용자 세션 체크, 세션 없으면 오류페이지
    res.send({error:true});
  } else {
    User.findOne({email: req.session.userCheck}, function (err, user) {
      if (err) throw err;

      if (JSON.parse(JSON.stringify(user)).authority == 'manager') {
        Content.find({}, function (err, content) {
          res.send(content);
        });
      } else {  }
    });
  }
});

module.exports = router ;
