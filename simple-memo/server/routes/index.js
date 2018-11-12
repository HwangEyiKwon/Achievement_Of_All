const express = require('express');
const router = express.Router();
var passport = require('passport');
const passportConfig = require('../../config/passport');
var User = require('../models/user');
var Content = require('../models/content');
var App = require('../models/app');
var jwt = require('jwt-simple'); // jwt token 사용
var mkdirp = require('mkdirp'); // directory 만드는것



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
    if(user) {
      res.send({success: true});
      var pathName = req.body.email;
      mkdirp('./server/user/'+pathName+'/video', function (err) {
        if(err) console.log(err);
        else console.log("create dir ./user/" +pathName );
      }); //server폴더 아래 /user/useremail/video 폴더가 생김.
    }
    else res.send({success: false});
  })(req,res,next);
});

//jwt token 사용
router.post('/userInfoEdit', function(req,res){

  User.findOne({email: userEmail}, function(err, user){
    user.password = user.generateHash(req.body.password);
    user.nickname = req.body.nickname;
    user.phoneNumber = req.body.phoneNumber;
    user.imagePath = req.body.imagePath;
  })
})

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

// 옮길 것, user도 추가해야 함.
router.get('/getSearchData', function (req,res) {

    Content.find(function (err, info) {
      console.log("searchdata" + info);

      var searchData = {
        contents: [],
        users: []
      }

      for(var i in info){
        searchData.contents.push(info[i].name);
      }
      res.send(searchData);
    })
  }
)
router.get('/getAppInfo', function (req,res) {

    App.findOne(function (err, info) {
      console.log("AppInfo: " + info);
      res.send(info);
    })
  }
)

//jwt토큰으로 사용자 파악,(decode encode)
router.get('/:contentID/contentJoin',  function (req,res) {
  User.findOne({email: req.body.email}, function(err, user){
    //이건 어떻게 해야 할까??
  });
})

//calendar : json 배열로 {date, 인증여부} 가지고 있게끔
//컨텐츠 아이디or네임으로 유저의 해당되는 컨텐츠 리스트를 알아야 함(새로운 컨텐츠 리스트를 추가해야 됨, 푸시만 하면)
router.post('/:contentID/contentJoinComplete',  function (req,res) {
  User.findOne({email: req.body.email}, function(err, user){
    //이렇게 하는 게 맞는지 토론
    user.contentList[0].calendar[0] = req.body.periodInfo;
  });
})

module.exports = router ;
