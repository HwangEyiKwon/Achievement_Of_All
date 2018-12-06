const express = require('express');
const router = express.Router();
var jwt = require('jwt-simple'); // jwt token 사용
var Content = require('../models/content')
var User = require('../models/user');
var fcmMessage = require('../../server.js');

//유저가 참여중인 컨텐츠의 현재 money 및 reward를 보내준다.
router.get('/getContentMoney/:jwtToken/:contentName',  function (req,res) {
  var decoded = jwt.decode(req.params.jwtToken,req.app.get("jwtTokenSecret"));
  // console.log("achievementRate jwt토큰 디코딩 "+ decoded.userCheck);
  var userEmail = decoded.userCheck;
  var contentName = req.params.contentName;
  var contentListIndex = -1;

  User.findOne({ email : userEmail }, function(err, user) {
    if (user.contentList.length == 0) {
      res.send({success: "false1"});
    }
    else {
      var contentListCount = user.contentList.length;
      var indexFlag = 0;
      for (var i = 0; i < contentListCount; i++) {
        if (user.contentList[i].contentName == contentName) {
          contentListIndex = i;
          indexFlag = 1;
        }
        if(contentListIndex != -1 && indexFlag == 1){
          res.send({money: user.contentList[contentListIndex].money, reward: user.contentList[contentListIndex].reward, penalty: user.contentList[contentListIndex].penalty});
        }
      }
      console.log("content list index: "+contentListIndex);
      if (contentListIndex == -1 && indexFlag != 1) {
        res.send({success: false});
      }
    }
  });
});

router.post('/getRewardCheck',  function (req,res) {
  console.log("getRewardCheck start");
  var decoded = jwt.decode(req.body.token,req.app.get("jwtTokenSecret"));
  // console.log("achievementRate jwt토큰 디코딩 "+ decoded.userCheck);
  var userEmail = decoded.userCheck;
  var contentName = req.body.contentName;

  User.findOne({ email : userEmail }, function(err, user) {
    var contentListIndex;
    var contentListCount = user.contentList.length;

    for (var i = 0; i < contentListCount; i++) {
      if (user.contentList[i].contentName === contentName) {
        contentListIndex = i;
        break;
      }
    }
    console.log("reward check = " + user.contentList[contentListIndex].rewardCheck);
    if(user.contentList[contentListIndex].rewardCheck === false){
      user.contentList[contentListIndex].money += user.contentList[contentListIndex].reward;
      user.contentList[contentListIndex].reward = 0;
      user.contentList[contentListIndex].rewardCheck = true;
      user.save(function(err, savedDocument) {
        if (err)
          return console.error(err);
      });
      res.send({success:true});
    }
    else res.send({success:false});
  });
});

router.post('/getFailureCheck',  function (req,res) {
  console.log("getFailure check start !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
  var decoded = jwt.decode(req.body.token,req.app.get("jwtTokenSecret"));
  // console.log("achievementRate jwt토큰 디코딩 "+ decoded.userCheck);
  var userEmail = decoded.userCheck;
  var contentName = req.body.contentName;
  var contentId;

  User.findOne({email: userEmail}, function(err, user) {
    var userMoney;
    if (user.contentList.length != 0) {
      var contentListCount = user.contentList.length;
      var contentListIndex;
      for (var i = 0; i < contentListCount; i++) {
        if (user.contentList[i].contentName === contentName) {
          contentListIndex = i;
          break;
        }
      }
      contentId = user.contentList[contentListIndex].contentId;
      userMoney = user.contentList[contentListIndex].money;

      console.log(userMoney);

      user.contentList[contentListIndex].joinState = 4;
      user.contentList[contentListIndex].penalty = userMoney;
      user.contentList[contentListIndex].money = 0;
      user.save(function(err, savedDocument) {
        if (err)
          return console.error(err);
      });
    }

    Content.findOne({name: contentName, id: contentId}, function (err, content) {
      var userListCount = content.userList.length;
      var userListIndex;
      var contentListIndex;

      for (var i = 0; i < userListCount; i++) {
        if (content.userList[i].email === userEmail) {
          userListIndex = i;
          break;
        }
      }
      content.userList[userListIndex].result = 0;
      content.balance += userMoney;

      content.save(function(err, savedDocument) {
        if (err)
          return console.error(err);
      });

      //reward 다른 사람들 올려주는 코드
      User.find({
        "contentList.contentName": contentName,
        "contentList.contentId": contentId,
        "contentList.joinState": 1
      }, function (err, userList) {
        var successUserNum = Object.keys(userList).length;
        for (var i = 0; i < successUserNum; i++) {
          if (userList[i].email === userEmail) {
            successUserNum--;
            break;
          }
        }

        for (var i = 0; i < Object.keys(userList).length && userList[i].email !== userEmail; i++) {
          var contentListIndex;
          var contentListCount = userList[i].contentList.length;

          for (var j = 0; j < contentListCount; j++) {
            if (userList[i].contentList[j].contentName === contentName) {
              contentListIndex = j;
              break;
            }
          }
          userList[i].contentList[contentListIndex].reward = (content.balance / successUserNum) * 0.8;

          userList[i].save(function (err, savedDocument) {
            if (err)
              return console.error(err);
          });
        }
      });
    });

    if(user.pushToken != ""){
      console.log("실패 푸쉬메시지 전송");
      var todayDate = new Date();
      var todayMonth = todayDate.getMonth() + 1;
      var todayDay = todayDate.getDate();
      var todayYear = todayDate.getFullYear();
      var currentHour = todayDate.getHours();
      var currentMinute = todayDate.getMinutes();
      var titleFail = "실패";
      var sendTime = new Date(todayYear, todayMonth - 1, todayDate.getDate(), todayDate.getHours(), todayDate.getMinutes() + 1, 0);
      var tempArray = new Array();
      fcmMessage.sendPushMessage2(user, contentListIndex, sendTime, titleFail, contentName, tempArray, tempArray);
    }

    res.send({success:true});
  });
});

module.exports = router ;
