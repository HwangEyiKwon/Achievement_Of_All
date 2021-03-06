const express = require('express');
const router = express.Router();
var jwt = require('jwt-simple'); // jwt token 사용
var Content = require('../models/content')
var User = require('../models/user');
var fcmMessage = require('../../server.js');

//유저가 참여중인 컨텐츠의 현재 money 및 reward를 보내준다.
router.get('/getContentMoney/:jwtToken/:contentName',  function (req,res) {
  var decoded = jwt.decode(req.params.jwtToken,req.app.get("jwtTokenSecret"));
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
      if (contentListIndex == -1 && indexFlag != 1) {
        res.send({success: false});
      }
    }
  });
});

router.post('/getRewardCheck',  function (req,res) {
  var decoded = jwt.decode(req.body.token,req.app.get("jwtTokenSecret"));
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
  var decoded = jwt.decode(req.body.token,req.app.get("jwtTokenSecret"));
  var userEmail = decoded.userCheck;
  var contentName = req.body.contentName;
  var contentId;

  User.findOne({email: userEmail}, function(err, user) {
    var userMoney;
    var userReward;
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
      userReward = user.contentList[contentListIndex].reward;
      user.contentList[contentListIndex].joinState = 4;
      user.contentList[contentListIndex].penalty = userMoney + userReward;
      user.contentList[contentListIndex].money = 0;
      user.contentList[contentListIndex].reward = 0;
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
      content.balance += (userMoney + userReward);

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
      var todayDate = new Date();
      var todayMonth = todayDate.getMonth() + 1;
      var todayDay = todayDate.getDate();
      var todayYear = todayDate.getFullYear();
      var currentHour = todayDate.getHours();
      var currentMinute = todayDate.getMinutes();
      var titleFail = "실패";
      var sendTime = new Date(todayYear, todayMonth - 1, todayDay, currentHour, currentMinute, todayDate.getSeconds()+5);
      var tempArray = new Array();
      fcmMessage.sendPushMessage2(user, contentListIndex, sendTime, titleFail, contentName, tempArray, tempArray);
    }
    else{
      var tempArray = new Array();
      user.contentList[contentListIndex].fcmFailureFlag = 1;
      user.contentList[contentListIndex].fcmMessageArray.failAuthenUserArray = tempArray;
      user.contentList[contentListIndex].fcmMessageArray.reasonArray = tempArray;
      user.save(function(err, savedDocument) {
        if (err)
          return console.error(err);
      });
    }

    res.send({success:true});
  });
});

module.exports = router ;
