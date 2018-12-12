const express = require('express');
const router = express.Router();
var User = require('../models/user');
var Content = require('../models/content');
var Report = require('../models/report');
var jwt = require('jwt-simple'); // jwt token 사용
var fcmMessage = require('../../server.js');
var fs = require("fs");

router.get('/reportUserList/:jwtToken/:contentName/:reportReason', function (req,res) {
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
    reportContentId = contentId;
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
        else{
          console.log(savedDocument);
          console.log("report save");
          res.send({success:true});
        }
      });
    });
  });
});

router.get('/getReportVideo/:email/:contentName/:videoPath', function(req,res){
  var userEmail = req.params.email;
  var contentName = req.params.contentName;
  var videoPath = req.params.videoPath;

  var filename = "./server/user/"+userEmail+"/video/"+contentName+"/"+videoPath+".mp4"
  var file = fs.createReadStream(filename, {flags: 'r'});
  file.pipe(res);
});

router.post('/reportReject', function (req,res) {
  console.log("reportReject start !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
  console.log(req.body);
  var reportUserEmail = req.body.report.email;
  var contentId = req.body.report.contentId;
  var contentName = req.body.report.contentName;
  var reason = req.body.report.reason;

  User.findOne({email: reportUserEmail}, function(err, user) {
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
      userMoney = user.contentList[contentListIndex].money;

      user.contentList[contentListIndex].joinState = 4;
      user.contentList[contentListIndex].penalty = userMoney;
      user.contentList[contentListIndex].money = 0;
      user.save(function(err, savedDocument) {
        if (err)
          return console.error(err);
      });
    }

    Content.findOne({id: contentId}, function (err, content) {
      var userListCount = content.userList.length;
      var userListIndex;

      for (var i = 0; i < userListCount; i++) {
        if (content.userList[i].email === reportUserEmail) {
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
        "contentList.contentId": contentId,
        "contentList.joinState": 1
      }, function (err, userList) {
        var successUserNum = Object.keys(userList).length;
        for (var i = 0; i < successUserNum; i++) {
          if (userList[i].email === reportUserEmail) {
            successUserNum--;
            break;
          }
        }

        for (var i = 0; i < Object.keys(userList).length && userList[i].email !== reportUserEmail; i++) {
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

    var reasonArray = new Array();
    var tempArray = new Array();
    reasonArray.push(reason);

    if(user.pushToken != ""){
      console.log("신고 reject 푸쉬메시지 전송");
      var todayDate = new Date();
      var todayMonth = todayDate.getMonth() + 1;
      var todayDay = todayDate.getDate();
      var todayYear = todayDate.getFullYear();
      var currentHour = todayDate.getHours();
      var currentMinute = todayDate.getMinutes();
      var titleReportReject = "신고거절";
      var sendTime = new Date(todayYear, todayMonth - 1, todayDay, currentHour, currentMinute, todayDate.getSeconds()+5);
      fcmMessage.sendPushMessage2(user, contentListIndex, sendTime, titleReportReject, contentName, tempArray, reasonArray);
    }
    else{
      console.log("push message 디비 세팅, logout한 유저");
      user.contentList[contentListIndex].fcmReportRejectFlag = 1;
      user.contentList[contentListIndex].fcmMessageArray.failAuthenUserArray = tempArray;
      user.contentList[contentListIndex].reasonArray = reasonArray;
      user.save(function(err, savedDocument) {
        if (err)
          return console.error(err);
      });
    }

    Report.findOne({contentId: contentId, contentName: contentName, userEmail: reportUserEmail}, function (err, report) {
      report.complete = 1;
      report.save(function (err, savedDocument) {
        if (err)
          return console.error(err);
      });
    });

    res.send({success:true});
  });
});

router.post('/reportAccept', function (req,res) {
  console.log("reportAccept start !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
  var reportUserEmail = req.body.report.email;
  var contentId = req.body.report.contentId;
  var contentName = req.body.report.contentName;
  var reason = req.body.report.reason;

  User.findOne({email: reportUserEmail}, function(err, user) {
    if (user.contentList.length != 0) {
      var contentListCount = user.contentList.length;
      var contentListIndex;
      for (var i = 0; i < contentListCount; i++) {
        if (user.contentList[i].contentName === contentName) {
          contentListIndex = i;
          break;
        }
      }

      Content.findOne({name: contentName, id: contentId}, function(err, content){
        var userListCount = content.userList.length;
        var userListIndex;
        var videoIndex = 0;
        var calendarIndex = 0;

        for (var i = 0; i < userListCount; i++) {
          if (content.userList[i].email === reportUserEmail) {
            userListIndex = i;
            break;
          }
        }

        if(user.contentList[contentListIndex].calendar.length != 0) {
          calendarIndex = user.contentList[contentListIndex].calendar.length - 1;
        }
        if(user.contentList[contentListIndex].videoPath.length != 0) {
          videoIndex = user.contentList[contentListIndex].videoPath.length - 1;
        }

        content.userList[userListIndex].newVideo.authen = 1;

        user.contentList[contentListIndex].videoPath[videoIndex].authen = 1;
        user.contentList[contentListIndex].calendar[calendarIndex].authen = 1;

        content.save(function(err, savedDocument) {
          if (err)
            return console.error(err);
        });
        user.save(function(err, savedDocument) {
          if (err)
            return console.error(err);
        });
      });
    }

    var reasonArray = new Array();
    var tempArray = new Array();
    reasonArray.push(reason);

    if(user.pushToken != ""){
      console.log("신고 accept 푸쉬메시지 전송");
      var todayDate = new Date();
      var todayMonth = todayDate.getMonth() + 1;
      var todayDay = todayDate.getDate();
      var todayYear = todayDate.getFullYear();
      var currentHour = todayDate.getHours();
      var currentMinute = todayDate.getMinutes();
      var titleReportAccept = "신고승인";
      var sendTime = new Date(todayYear, todayMonth - 1, todayDay, currentHour, currentMinute , todayDate.getSeconds()+5);

      fcmMessage.sendPushMessage2(user, contentListIndex, sendTime, titleReportAccept, contentName, tempArray, reasonArray);
    }
    else{
      console.log("push message 디비 세팅, logout한 유저");
      user.contentList[contentListIndex].fcmReportAcceptFlag = 1;
      user.contentList[contentListIndex].fcmMessageArray.failAuthenUserArray = tempArray;
      user.contentList[contentListIndex].reasonArray = reasonArray;
      user.save(function(err, savedDocument) {
        if (err)
          return console.error(err);
      });
    }

    Report.findOne({contentId: contentId, contentName: contentName, userEmail: reportUserEmail}, function (err, report) {
      report.complete = 1;
      report.save(function (err, savedDocument) {
        if (err){
          return console.error(err);
        }
        else{

        }

      });
    });

    res.send({success:true});
  });
});

// -----------------------------------------------------
module.exports = router ;
