const express = require('express');
const router = express.Router();
var User = require('../models/user');
var Content = require('../models/content');
var Report = require('../models/report');
var passport = require('passport');
const passportConfig = require('../../config/passport');

router.get('/getSearchUserData', function (req,res) {
  console.log("getSearchUserData Start");
  User.find(function(err, userList){
    var searchData = new Array();

    for(var i = 0; i < Object.keys(userList).length; i++){
      searchData.push({email: userList[i].email, name: userList[i].name});
    }
    res.send({users: searchData});
  });
});

router.get('/getSearchContentData', function (err, res ) {
  console.log("getSearchContentData Start ");
  Content.collection.distinct("name", function(err, results){
    if(err)  console.log(err);
    else{
      console.log("Content List : " +results);
      res.send({contents: results});
    }
  });
});


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

router.get('/reportUserList/:jwtToken/:contentName', function (req,res) {
  console.log("reportUserList Start!!!");
  var decoded = jwt.decode(req.params.jwtToken,req.app.get("jwtTokenSecret"));
  // console.log("isParticipated jwt토큰 디코딩 "+ decoded.userCheck);
  var userEmail = decoded.userCheck;
  var contentName = req.params.contentName;
  var reportContentId;
  var reportContentName = contentName;
  var reportUserEmail = userEmail;
  var reportReason = req.params.reportReason;
  var reportAuthenDay;
  var complete;
  var reportUser = new Array();

  User.findOne({email: userEmail}, function(err, user) {

    var contentListCount = user.contentList.length;
    var contentListIndex;
    for (var i = 0; i < contentListCount; i++) {
      if (user.contentList[i].contentName === contentName) {
        contentListIndex = i;
        break;
      }
    }
    var contentId = user.contentList[contentListIndex].contentId;
    Content.findOne({id: contentId, name : contentName}, function(err, content) {
      var userListCount = content.userList.length;
      var userListIndex;
      var authrizePeopleCount;
      var authrizePeopleIndex;
      for(var i = 0; i < userListCount; i++){
        if(content.userList[i].email === userEmail){
          userListIndex = i;
          break;
        }
      }
      reportAuthenDay = content.userList[userListIndex].newVideo.path;
      authrizePeopleCount = content.userList[userListIndex].newVideo.authorizePeople.length;
      for(var i = 0; i < authrizePeopleCount; i++){
        if(content.userList[userListIndex].newVideo.authorizePeople[i].authenInfo === 0){
          reportUser.push(content.userList[userListIndex].newVideo.authorizePeople[i].email);
        }
      }

      var newReport = new Report({
        contentId: reportContentId,
        contentName: reportContentName,
        userEmail: reportUserEmail,
        reportReason: reportReason,
        authenDay: reportAuthenDay,
        reportUser: reportUser,
        complete: 0
      });
      newReport.save(function(err, savedDocument) {
        if (err)
          return console.error(err);
        console.log(savedDocument);
        console.log("report save");
      });
    });
  });
});
// -----------------------------------------------------
module.exports = router ;

