var mongoose = require('mongoose');
//mongoose.connect('mongodb://nyangpun:capd@localhost/admin',{dbName: 'capd'});
//mongoose.connect('mongodb://nyangnyangpunch:capd@localhost/admin',{dbName: 'capd'});
//mongoose.connect('mongodb://capd:1234@localhost/admin',{dbName: 'capd'});
mongoose.connect('mongodb://localhost:27017');

const express = require('express');
const path = require('path');
const http = require('http');
var session = require('express-session');
var passport = require('passport');
const passportConfig = require('./config/passport');
const bodyParser = require('body-parser');
const video = require('./server/routes/video');
const image = require('./server/routes/image');
const index = require('./server/routes/index');
const appInfo = require('./server/routes/appInfo');
const search = require('./server/routes/search');
const aboutContent = require('./server/routes/aboutContent');
const reward = require('./server/routes/reward');
const report = require('./server/routes/report');

var titleFail = "실패";
var titleAuthen = "인증";
var titleSuccess = "성공";
var titleVideoFail = "비디오실패";
var titleWillSuccess = "성공예정";
var titleNewVideo = "새영상";
var titleReportReject = "신고거절";
var titleReportAccept = "신고승인";

var schedule = require('node-schedule');
var FCM = require('fcm-node');

const app = express();

var db = mongoose.connection;

db.on('error', function(err){
  console.log("error: " + err);
});

db.on('connected', function() {
  console.log("Connected successfully to server");
});

var user = require('./server/models/user');
var content = require('./server/models/content');
var appInfoSchema = require('./server/models/app');
var Report = require('./server/models/report');

require('./config/passport')(passport);

// db 삭제
 dbDelete();
// // db 초기화
 dbInit();

// POST 데이터
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));

app.use(session({
  secret: '123!@#456$%^789&*(0)',
  resave: true,
  saveUninitialized: true
}));

app.use(passport.initialize());
app.use(passport.session());

// ng build 명령
app.use(express.static(path.join(__dirname, 'dist/simple-memo')));

// index(유저 관련) router
app.use('/', index);
// app info router
app.use('/', appInfo);
// video router
app.use('/', video);
//image router
app.use('/', image);
// fcm router
//app.use('/fcm', fcm);
//search router
app.use('/', search);
//aboutContent router
app.use('/', aboutContent);
//report router
app.use('/', report);
//reward router
app.use('/', reward);

app.set('jwtTokenSecret', "afafaffffff");
app.set('managerKey', "3Ke34Meg9ek");

app.get('*', function (req, res) {   res.sendFile(path.join(__dirname, 'dist/simple-memo/index.html')); });

const serverKey = 'AAAAKw66KHo:APA91bE1A1hr5P69HHdOWigZl5FQgYtUn0FzQ554EPrEcJMzG4LfMxieNPko8hKzAg4ImeScWEtYqHmspYb0dJZWKgpEuGJY98iKLFXKf02FhHW-0xUNi2he2LL3pbpSm0VjhsbJ5Y8l';

app.post('/sendToken', function(req, res) {

  res.send({success: true});

  var userEmail = req.body.email;
  var userToken = req.body.fcmToken;
  user.findOne({email: userEmail}, function (err, user) {
    if(user== undefined){}
    else{
      user.pushToken = userToken;
      user.save(function (err) {
        if (err){}
      });

      var joinContentCount = user.contentList.length;
      var contentListIndex;
      for(var i = 0; i < joinContentCount; i++){
        var contentName = user.contentList[i].contentName;
        var tempArray = new Array();
        if(user.contentList[i].fcmFailureFlag == 1){
          contentListIndex = i;
          var sendTime = new Date(todayYear, todayMonth - 1, todayDay, todayDate.getHours(), todayDate.getMinutes(), todayDate.getSeconds() + 2);
          sendPushMessage(user, contentListIndex, sendTime, titleFail, contentName, tempArray, tempArray);
          user.contentList[i].fcmFailureFlag = 0;
          user.save(function(err, savedDocument) {
            if (err)
              return;
          });
        }
        if(user.contentList[i].fcmVideoFailureFlag == 1){
          contentListIndex = i;
          var sendTime = new Date(todayYear, todayMonth - 1, todayDay, todayDate.getHours(), todayDate.getMinutes(), todayDate.getSeconds() + 2);
          sendPushMessage(user, contentListIndex, sendTime, titleVideoFail, contentName, user.contentList[i].fcmMessageArray.failAuthenUserArray, user.contentList[i].fcmMessageArray.reasonArray);
          user.contentList[i].fcmVideoFailureFlag = 0;
          user.contentList[i].fcmMessageArray.failAuthenUserArray = [];
          user.contentList[i].fcmMessageArray.reasonArray = [];
          user.save(function(err, savedDocument) {
            if (err)
              return;
          });
        }
        if(user.contentList[i].fcmReportAcceptFlag == 1){
          contentListIndex = i;
          var sendTime = new Date(todayYear, todayMonth - 1, todayDay, todayDate.getHours(), todayDate.getMinutes(), todayDate.getSeconds() + 2);
          sendPushMessage(user, contentListIndex, sendTime, titleReportAccept, contentName, tempArray, user.contentList[i].fcmMessageArray.reasonArray);
          user.contentList[i].fcmReportAcceptFlag = 0;
          user.contentList[i].fcmMessageArray.reasonArray = [];
          user.save(function(err, savedDocument) {
            if (err)
              return;
          });
        }
        if(user.contentList[i].fcmReportRejectFlag == 1){
          contentListIndex = i;
          var sendTime = new Date(todayYear, todayMonth - 1, todayDay, todayDate.getHours(), todayDate.getMinutes(), todayDate.getSeconds() + 2);
          sendPushMessage(user, contentListIndex, sendTime, titleReportReject, contentName, tempArray, user.contentList[i].fcmMessageArray.reasonArray);
          user.contentList[i].fcmReportRejectFlag =0;
          user.contentList[i].fcmMessageArray.reasonArray = [];
          user.save(function(err, savedDocument) {
            if (err)
              return;
          });
        }
      }
    }

  });
  //날짜 구하기
  var todayDate = new Date();
  var todayMonth = todayDate.getMonth() + 1;
  var todayDay = todayDate.getDate();
  var todayYear = todayDate.getFullYear();

  // 일이 한자리 수인 경우 앞에 0을 붙여주기 위해
  if ((todayDay+"").length < 2) {
    todayDay = "0" + todayDay;
  }
  var today = todayYear+ "-" + todayMonth + "-" + todayDay;

  user.findOne({ email: userEmail, "contentList.authenticationDate" : today }, function(err, user) {

    if(user== null){}
    else{
      var joinContentCount = user.contentList.length;
      var authenContentIndex;
      for(var i = 0; i < joinContentCount; i++){
        if(user.contentList[i].authenticationDate === today){
          authenContentIndex = i;
          break;
        }
      }
      //로그아웃 했다가 로그인 한 인증 필요 사용자에게 푸쉬 알림 전송

      if(user.contentList[authenContentIndex].isUploaded != 1) {

        var tempArray = new Array();

        var sendTime1 = new Date(todayYear, todayMonth - 1, todayDate.getDate(), 9, 0, 0);
        var sendTime2 = new Date(todayYear, todayMonth - 1, todayDate.getDate(), 14, 0, 0);
        var sendTime3 = new Date(todayYear, todayMonth - 1, todayDate.getDate(), 19, 0, 0);
        sendPushMessage(user, authenContentIndex, sendTime1, titleAuthen, user.contentList[authenContentIndex].contentName, tempArray, tempArray);
        sendPushMessage(user, authenContentIndex, sendTime2, titleAuthen, user.contentList[authenContentIndex].contentName, tempArray, tempArray);
        sendPushMessage(user, authenContentIndex, sendTime3, titleAuthen, user.contentList[authenContentIndex].contentName, tempArray, tempArray);
      }
    }
  });
});

//날짜가 바뀔 때마다 푸쉬알림 전송 및 매일 수행될 기능들
var scheduler = schedule.scheduleJob('00 00 * * *', function(){
  var todayDate = new Date();
  var todayYear = todayDate.getFullYear();
  var todayMonth = todayDate.getMonth() + 1;
  var todayDay = todayDate.getDate();

  // 일이 한자리 수인 경우 앞에 0을 붙여주기 위해
  if ((todayDay+"").length < 2) {
    todayDay = "0" + todayDay;
  }

  var today = todayYear+ "-" + todayMonth + "-" + todayDay;
  var yesterday = todayYear+ "-" + todayMonth + "-" + (todayDay-1);
  var yesterdayDate = new Date(todayYear,(todayMonth-1),(todayDay-1));
  var midnightDate = new Date(todayYear,(todayMonth-1),todayDay);

  //content 시작일에 유저들의 상태를 진행중으로 변경
  content.find({"startDate" : midnightDate}, function(err, contentList){
    for(var i = 0; i < Object.keys(contentList).length; i++){
      var contentId = contentList[i].id;
      var contentName = contentList[i].name;
      contentList[i].isDone = 0;

      contentList[i].save(function(err, savedDocument) {
        if (err) {}
      });

      user.find({"contentList.contentId" : contentId, "contentList.contentName": contentName, "contentList.joinState" : 0}, function(err, userList) {
        var userListCount = Object.keys(userList).length;
        for (var i = 0; i < userListCount; i++) {
          var contentListCount = userList[i].contentList.length;
          var contentListIndex;
          for (var j = 0; j < contentListCount; j++) {
            if (userList[i].contentList[j].contentName === contentName) {
              contentListIndex = j;
              break;
            }
          }

          userList[i].contentList[contentListIndex].joinState = 1;
          userList[i].save(function (err, savedDocument) {
            if (err) {}
          });
        }
      });
    }
  });

  /* 모든 컨텐츠에 대해 endDate체크하여 성공한 사람들 디비 수정하고 푸쉬메시지 보내기 */
  content.find({"endDate" : yesterdayDate, "isDone": 0}, function(err, contentList){
console.log(contentList);
    for(var i = 0; i < Object.keys(contentList).length; i++){
      var contentId = contentList[i].id;
      var contentName = contentList[i].name;
      var userListCount = contentList[i].userList.length;
      var balance = contentList[i].balance;
      for(var j = 0; j < userListCount; j ++){
        if(contentList[i].userList[j].result == 2) contentList[i].userList[j].result = 1;
      }
      contentList[i].isDone = 1;
      contentList[i].save(function(err, savedDocument) {
        if (err) {}
      });

      user.find({"contentList.contentId" : contentId, "contentList.contentName": contentName, "contentList.joinState" : 1}, function(err, userList){
        var successUserNum = Object.keys(userList).length;
        for(var i = 0; i < successUserNum; i++){
          var contentListCount = userList[i].contentList.length;
          var contentListIndex;
          for (var j = 0; j < contentListCount; j++) {
            if (userList[i].contentList[j].contentName === contentName) {
              contentListIndex = j;
              break;
            }
          }
          userList[i].contentList[contentListIndex].joinState = 2;
          userList[i].contentList[contentListIndex].reward = (balance / successUserNum) * 0.8;

          userList[i].save(function(err, savedDocument) {
            if (err) {}
          });
          //성공 푸쉬메시지 전송
          if(userList[i].pushToken != "") {
            var tempArray = new Array();
            var sendTime = new Date(todayYear, todayMonth - 1, todayDate.getDate(), todayDate.getHours(), todayDate.getMinutes(), todayDate.getSeconds()+5);
            sendPushMessage(userList[i], contentListIndex, sendTime, titleSuccess, userList[i].contentList[contentListIndex].contentName, tempArray, tempArray);
          }
          else {}
        }
      });
    }
  });

  /* 모든 유저에 대해 authentication Date가 지났는데 인증 안된사람 체크 후 실패 메시지 전송 */
  user.find({"contentList.authenticationDate" : yesterday, "contentList.isUploaded": 0, "contentList.joinState" : 1}, function(err, userList){
    for(var i = 0; i < Object.keys(userList).length; i++){

      var authenContentIndex;
      var contentListCount = userList[i].contentList.length;

      var userListCount;
      var userListIndex;

      var userEmail = userList[i].email;

      for(var j = 0; j < contentListCount; j++){
        if(userList[i].contentList[j].authenticationDate === yesterday){
          authenContentIndex = j;
          break;
        }
      }
      var contentName = userList[i].contentList[authenContentIndex].contentName;
      var contentId = userList[i].contentList[authenContentIndex].contentId;

      if(userList[i].contentList[authenContentIndex].calendar.length != 0) {
        var calendarIndex = userList[i].contentList[authenContentIndex].calendar.length - 1;
      }else{
        var calendarIndex = 0;
      }
      var userMoney = userList[i].contentList[authenContentIndex].money;
      var userReward = userList[i].contentList[authenContentIndex].reward;

      userList[i].contentList[authenContentIndex].penalty = userMoney + userReward;
      userList[i].contentList[authenContentIndex].money = 0;
      userList[i].contentList[authenContentIndex].reward = 0;
      userList[i].contentList[authenContentIndex].joinState = 4;
      //userList[i].contentList[authenContentIndex].calendar[calendarIndex].authen = 0;

      userList[i].save(function(err, savedDocument) {
        if (err)
          return;
      });

      content.findOne({name: contentName, id: contentId}, function(err, content){

        userListCount = content.userList.length;

        for (var m = 0; m < userListCount; m++) {
          if (content.userList[m].email === userEmail) {
            userListIndex = m;
            break;
          }
        }
        content.userList[userListIndex].result = 0;
        content.balance += (userMoney + userReward);

        content.save(function(err, savedDocument) {
          if (err)
            return;
        });

        user.find({"contentList.contentName" : contentName, "contentList.contentId" : contentId, "contentList.joinState" : 1}, function(err, userList2){
          var balance = content.balance;
          var successUserNum = Object.keys(userList2).length;
          for(var i = 0; i < successUserNum; i++) {
            if(userList2[i].email === userEmail){
              successUserNum --;
              break;
            }
          }

          for(var i = 0; i < Object.keys(userList2).length && userList2[i].email !== userEmail; i++) {
            var contentListIndex;
            var contentListCount = userList2[i].contentList.length;

            for (var j = 0; j < contentListCount; j++) {
              if (userList2[i].contentList[j].contentName === contentName) {
                contentListIndex = j;
                break;
              }
            }
            userList2[i].contentList[contentListIndex].reward = (balance / successUserNum) * 0.8;

            userList2[i].save(function(err, savedDocument) {
              if (err)
                return;
            });
          }
        });
      });
      //푸쉬메시지 전송
      if(userList[i].pushToken != ""){

        var tempArray = new Array();
        var sendTime = new Date(todayYear, todayMonth - 1, todayDate.getDate(), todayDate.getHours(), todayDate.getMinutes() + 1, 0);
        sendPushMessage(userList[i], authenContentIndex, sendTime, titleFail, contentName,tempArray, tempArray);
      }
      else{
        var tempArray = new Array();

        userList[i].contentList[authenContentIndex].fcmFailureFlag = 1;
        userList[i].contentList[authenContentIndex].fcmMessageArray.failAuthenUserArray = tempArray;
        userList[i].contentList[authenContentIndex].reasonArray= tempArray;
        userList[i].save(function(err, savedDocument) {
          if (err)
            return;
        });
      }
    }
  });

  /* 모든 유저에 대해 authentication Date가 오늘인지 체크해서 푸쉬메시지 전송 */
  user.find({"contentList.authenticationDate" : today, "contentList.isUploaded": 0}, function(err, userList){
    for(var i = 0; i < Object.keys(userList).length; i++){
      var joinContentCount = userList[i].contentList.length;
      var authenContentIndex;
      for(var j = 0; j < joinContentCount; j++){
        if(userList[i].contentList[j].authenticationDate === today){
          authenContentIndex = j;
          break;
        }
      }
      if(userList[i].pushToken != ""  && userList[i].contentList[authenContentIndex].isUploaded != 1) {
        var tempArray = new Array();
        var sendTime1 = new Date(todayYear, todayMonth - 1, todayDate.getDate(), 9, 0, 0);
        var sendTime2 = new Date(todayYear, todayMonth - 1, todayDate.getDate(), 14, 0, 0);
        var sendTime3 = new Date(todayYear, todayMonth - 1, todayDate.getDate(), 19, 0, 0);
        sendPushMessage(userList[i], authenContentIndex, sendTime1, titleAuthen, userList[i].contentList[authenContentIndex].contentName, tempArray, tempArray);
        sendPushMessage(userList[i], authenContentIndex, sendTime2, titleAuthen, userList[i].contentList[authenContentIndex].contentName, tempArray, tempArray);
        sendPushMessage(userList[i], authenContentIndex, sendTime3, titleAuthen, userList[i].contentList[authenContentIndex].contentName, tempArray, tempArray);
      }
    }
  });

  //매일마다 인증 현황을 0으로 수정해줌
  user.find({isUploaded: 1}, function(err, userList){
    for(var i = 0; i < Object.keys(userList).length; i++){
      for(var j = 0; j < Object.keys(userList[i].contentList).length; j++){
        userlist[i].contentList[j].isUploaded = 0;
        user.save(function (err) {
          if (err) {}
        });
      }
    }
  });

  //달성률 매일 갱신하는 코드
  content.find({isDone: 0}, function(err, contentList){
    for(var i = 0; i < Object.keys(contentList).length; i++){
      var totalDate = (contentList[i].endDate.getTime() - contentList[i].startDate.getTime()) / ( 24*60*60*1000);
      var remainedDate = (todayDate.getTime() - contentList[i].startDate.getTime()) / ( 24*60*60*1000);

      var achievementRate = (remainedDate / totalDate) * 100;
      if(achievementRate >= 100) achievementRate = 100;

      contentList[i].achievementRate = achievementRate;

      contentList[i].save(function (err) {
        if (err) {}
      });
    }
  });
});

var scheduler = schedule.scheduleJob('00 * * * * *', function(){

  var todayDate = new Date();
  var todayYear = todayDate.getFullYear();
  var todayMonth = todayDate.getMonth() + 1;
  var todayDay = todayDate.getDate();

  // 일이 한자리 수인 경우 앞에 0을 붙여주기 위해
  if ((todayDay+"").length < 2) {
    todayDay = "0" + todayDay;
  }

  var today = todayYear+ "-" + todayMonth + "-" + todayDay;
  var yesterday = todayYear+ "-" + todayMonth + "-" + (todayDay-1);
  var yesterdayDate = new Date(todayYear,(todayMonth-1),(todayDay-1));

  var midnightDate = new Date(todayYear,(todayMonth-1),todayDay);


  content.find({"startDate" : midnightDate}, function(err, contentList){
    for(var i = 0; i < Object.keys(contentList).length; i++){

      var contentId = contentList[i].id;
      var contentName = contentList[i].name;
      contentList[i].isDone = 0;

      contentList[i].save(function(err, savedDocument) {
        if (err) {}
      });

      user.find({"contentList.contentId" : contentId, "contentList.contentName": contentName, "contentList.joinState" : 0}, function(err, userList) {
        var userListCount = Object.keys(userList).length;
        for (var i = 0; i < userListCount; i++) {

          var contentListCount = userList[i].contentList.length;
          var contentListIndex;
          for (var j = 0; j < contentListCount; j++) {
            if (userList[i].contentList[j].contentName === contentName) {
              contentListIndex = j;
              break;
            }
          }
          userList[i].contentList[contentListIndex].joinState = 1;
          userList[i].save(function (err, savedDocument) {
            if (err) {}
          });
        }
      });
    }
  });

  /* 모든 컨텐츠에 대해 endDate체크하여 성공한 사람들 디비 수정하고 푸쉬메시지 보내기 */
//   content.find({"endDate" : yesterdayDate, "isDone": 0}, function(err, contentList){
// console.log(contentList);
//     for(var i = 0; i < Object.keys(contentList).length; i++){
//       var contentId = contentList[i].id;
//       var contentName = contentList[i].name;
//       var userListCount = contentList[i].userList.length;
//       var balance = contentList[i].balance;
//       for(var j = 0; j < userListCount; j ++){
//         if(contentList[i].userList[j].result == 2) contentList[i].userList[j].result = 1;
//       }
//       contentList[i].isDone = 1;
//       contentList[i].save(function(err, savedDocument) {
//         if (err) {}
//       });
//
//       user.find({"contentList.contentId" : contentId, "contentList.contentName": contentName, "contentList.joinState" : 1}, function(err, userList){
//         console.log(userList);
//         if(userList != null){
//           var successUserNum = Object.keys(userList).length;
//           for(var i = 0; i < successUserNum; i++){
//             var contentListCount = userList[i].contentList.length;
//             var contentListIndex;
//             for (var j = 0; j < contentListCount; j++) {
//               if (userList[i].contentList[j].contentName === contentName) {
//                 contentListIndex = j;
//                 break;
//               }
//             }
//             userList[i].contentList[contentListIndex].joinState = 2;
//             userList[i].contentList[contentListIndex].reward = (balance / successUserNum) * 0.8;
//
//             userList[i].save(function(err, savedDocument) {
//               if (err) {}
//             });
//             //푸쉬메시지 전송
//
//             if(userList[i].pushToken != "") {
//               console.log("푸쉬 전송~~");
//               var tempArray = new Array();
//               var sendTime = new Date(todayYear, todayMonth - 1, todayDate.getDate(), todayDate.getHours(), todayDate.getMinutes(), todayDate.getSeconds()+5);
//               sendPushMessage(userList[i], contentListIndex, sendTime, titleSuccess, userList[i].contentList[contentListIndex].contentName, tempArray, tempArray);
//             }
//           }
//         }
//       });
//     }
//   });

  content.find({isDone: 0}, function(err, contentList){
    for(var i = 0; i < Object.keys(contentList).length; i++){
      var totalDate = (contentList[i].endDate.getTime() - contentList[i].startDate.getTime()) / ( 24*60*60*1000);
      var remainedDate = (todayDate.getTime() - contentList[i].startDate.getTime()) / ( 24*60*60*1000);

      var achievementRate = (remainedDate / totalDate) * 100;
      if(achievementRate >= 100) achievementRate = 100;

      contentList[i].achievementRate = achievementRate;
      contentList[i].save(function (err) {
        if (err) {}
      });
    }
  });
});

function sendPushMessage(user, arrayIndex, sendTime, titles, contentName, authenUserArray, reasonArray) {
  var fcm = new FCM(serverKey);
  var client_token = user.pushToken;
  if(titles === titleVideoFail){
    var push_data = {
      // 수신대상
      to: client_token,
      // App이 실행중이지 않을 때 상태바 알림으로 등록할 내용
      data: {
        title: titles,
        body: contentName,
        user: authenUserArray,
        checkReason: reasonArray
      },
      // 메시지 중요도
      priority: "high",
      // App 패키지 이름
      restricted_package_name: "com.example.parkseunghyun.achievementofall",
    };
  }
  else if(titles == titleReportReject || titles == titleReportAccept){
    var push_data = {
      // 수신대상
      to: client_token,
      // App이 실행중이지 않을 때 상태바 알림으로 등록할 내용
      data: {
        title: titles,
        body: contentName,
        reason: reasonArray
      },
      // 메시지 중요도
      priority: "high",
      // App 패키지 이름
      restricted_package_name: "com.example.parkseunghyun.achievementofall",
    };
  }
  else{
    var push_data = {
      // 수신대상
      to: client_token,
      // App이 실행중이지 않을 때 상태바 알림으로 등록할 내용
      data: {
        title: titles,
        body: contentName,
      },
      // 메시지 중요도
      priority: "high",
      // App 패키지 이름
      restricted_package_name: "com.example.parkseunghyun.achievementofall",
    };
  }

  var scheduler = schedule.scheduleJob(sendTime, function(){
    if(titles === titleAuthen){
      if(user.contentList[arrayIndex].isUploaded == 1 ) {
        user.contentList[arrayIndex].isUploaded = 0;
      }
      else{
        fcm.send(push_data, function(err, response) {
          if (err) {
            return;
          }
        });
      };
    }
    else if(titles === titleFail){
      fcm.send(push_data, function(err, response) {
        if (err) {
          return;
        }
      });
    }
    else if(titles === titleSuccess){
      fcm.send(push_data, function(err, response) {
        if (err) {
          return;
        }
      });
    }
    else if(titles === titleVideoFail){
      fcm.send(push_data, function(err, response) {
        if (err) {
          return;
        }
      });
    }
    else if(titles === titleWillSuccess){
      fcm.send(push_data, function(err, response) {
        if (err) {
          return;
        }
      });
    }
    else if(titles === titleNewVideo){
      fcm.send(push_data, function(err, response) {
        if (err) {
          return;
        }
      });
    }
    else if(titles === titleReportAccept){
      fcm.send(push_data, function(err, response) {
        if (err) {
          return;
        }
      });
    }
    else if(titles === titleReportReject){
      fcm.send(push_data, function(err, response) {
        if (err) {
          return;
        }
      });
    }
  });
}

//외부 푸쉬메시지 펑션
exports.sendPushMessage2 = function(user, arrayIndex, sendTime, titles, contentName, authenUserArray, reasonArray)  {
  var fcm = new FCM(serverKey);
  var client_token = user.pushToken;
  if(titles === titleVideoFail){
    var push_data = {
      // 수신대상
      to: client_token,
      // App이 실행중이지 않을 때 상태바 알림으로 등록할 내용
      data: {
        title: titles,
        body: contentName,
        user: authenUserArray,
        checkReason: reasonArray
      },
      // 메시지 중요도
      priority: "high",
      // App 패키지 이름
      restricted_package_name: "com.example.parkseunghyun.achievementofall",
    };
  }
  else if(titles == titleReportReject || titles == titleReportAccept){
    var push_data = {
      // 수신대상
      to: client_token,
      // App이 실행중이지 않을 때 상태바 알림으로 등록할 내용
      data: {
        title: titles,
        body: contentName,
        reason: reasonArray
      },
      // 메시지 중요도
      priority: "high",
      // App 패키지 이름
      restricted_package_name: "com.example.parkseunghyun.achievementofall",
    };
  }
  else{
    var push_data = {
      // 수신대상
      to: client_token,
      // App이 실행중이지 않을 때 상태바 알림으로 등록할 내용
      data: {
        title: titles,
        body: contentName,
      },
      // 메시지 중요도
      priority: "high",
      // App 패키지 이름
      restricted_package_name: "com.example.parkseunghyun.achievementofall",
    };
  }

  var scheduler = schedule.scheduleJob(sendTime, function(){
    if(titles === titleFail){
      fcm.send(push_data, function(err, response) {
        if (err) {
          return;
        }
      });
    }
    else if(titles === titleVideoFail){
      fcm.send(push_data, function(err, response) {
        if (err) {
          return;
        }
      });
    }
    else if(titles === titleWillSuccess){
      fcm.send(push_data, function(err, response) {
        if (err) {
          return;
        }
      });
    }
    else if(titles === titleNewVideo){
      fcm.send(push_data, function(err, response) {
        if (err) {
          return;
        }
      });
    }
    else if(titles === titleReportAccept){
      fcm.send(push_data, function(err, response) {
        if (err) {
          return;
        }
      });
    }
    else if(titles === titleReportReject){
      fcm.send(push_data, function(err, response) {
        if (err) {
          return;
        }
      });
    }
  });
}

function dbInit(){
  var user1 = new user({
    name: "ParkSeungHyun17",
    email: "shp17@gmail.com",
    authority: "user",
    // password : user.generateHash("123"),
    phoneNumber : "01093969408",
    nickName : "4.5man",
    imagePath: "ParkSeungHyun17",
    pushToken: "",
    contentList:[{
      contentId : 0,
      videoPath: [{path: "2018-12-01", authen: 1},{path: "2018-12-04", authen: 1}, {path: "2018-12-07", authen: 1}, {path: "2018-12-10", authen: 2}],
      contentName: "NoSmoking",
      joinState : 1,
      authenticationDate : "2018-12-13",
      isUploaded : 0,
      calendar: [{year: "2018", month: "12", day: "1", authen: 1}, {year: "2018", month: "12", day: "4", authen: 1}, {year: "2018", month: "12", day: "7", authen: 1}, {year: "2018", month: "12", day: "10", authen: 2}],
      money: 100000,
      reward: 0,
      rewardCheck: 0,
      penalty: 0,
      fcmFailure: 0,
      fcmVideoFailureFlag : 0,
      fcmReportAcceptFlag : 0,
      fcmReportRejectFlag : 0
    }]
  });
  var user2 = new user({
    name: "HwangEyiKWON17",
    email: "hek17@gmail.com",
    authority: "user",
    // password : user.generateHash("123"),
    phoneNumber : "01093969408",
    nickName : "4.5man",
    imagePath: "HwangEyiKWON17",
    pushToken: "",
    contentList:[{
      contentId : 0,
      videoPath: [{path: "2018-12-01", authen: 1},{path: "2018-12-04", authen: 1}, {path: "2018-12-07", authen: 1}, {path: "2018-12-10", authen: 2}],
      contentName: "NoSmoking",
      joinState : 1,
      authenticationDate : "2018-12-13",
      isUploaded : 0,
      calendar: [{year: "2018", month: "12", day: "1", authen: 1}, {year: "2018", month: "12", day: "4", authen: 1}, {year: "2018", month: "12", day: "7", authen: 1}, {year: "2018", month: "12", day: "10", authen: 2}],
      money: 100000,
      reward: 0,
      rewardCheck: 0,
      penalty: 0,
      fcmFailure: 0,
      fcmVideoFailureFlag : 0,
      fcmReportAcceptFlag : 0,
      fcmReportRejectFlag : 0
    }]
  });

  var user3 = new user({
    name: "ChoGeonHee17",
    email: "cgh17@gmail.com",
    authority: "user",
    // password : user.generateHash("123"),
    phoneNumber : "01093969408",
    nickName : "seoulUnivMan",
    imagePath: "ChoGeonHee17",
    pushToken: "",
    contentList:[{
      contentId : 0,
      videoPath: [{path: "2018-12-01", authen: 1},{path: "2018-12-04", authen: 1}, {path: "2018-12-07", authen: 1}, {path: "2018-12-10", authen: 2}],
      contentName: "NoSmoking",
      joinState : 1,
      authenticationDate : "2018-12-13",
      isUploaded : 0,
      calendar: [{year: "2018", month: "12", day: "1", authen: 1}, {year: "2018", month: "12", day: "4", authen: 1}, {year: "2018", month: "12", day: "7", authen: 1}, {year: "2018", month: "12", day: "10", authen: 2}],
      money: 100000,
      reward: 0,
      rewardCheck: 0,
      penalty: 0,
      fcmFailure: 0,
      fcmVideoFailureFlag : 0,
      fcmReportAcceptFlag : 0,
      fcmReportRejectFlag : 0
    }]
  });
  var user4 = new user({
    name: "JangDongIk17",
    email: "jdi17@gmail.com",
    authority: "user",
    // password : user.generateHash("123"),
    phoneNumber : "01093969408",
    nickName : "Man",
    imagePath: "JangDongIk17",
    pushToken: "",
    contentList:[{
      contentId : 0,
      videoPath: [{path: "2018-12-02", authen: 1},{path: "ns1", authen: 1}, {path: "ns2", authen: 1}],
      contentName: "NoSmoking",
      joinState : 1,
      authenticationDate : "2018-12-12",
      isUploaded : 0,
      calendar: [{year: "2018", month: "12", day: "1", authen: 1}, {year: "2018", month: "12", day: "4", authen: 1}, {year: "2018", month: "12", day: "7", authen: 1}],
      money: 100000,
      reward: 0,
      rewardCheck: 0,
      penalty: 0,
      fcmFailure: 0,
      fcmVideoFailureFlag : 0,
      fcmReportAcceptFlag : 0,
      fcmReportRejectFlag : 0
    }]
  });
  var user5 = new user({
    name: "manager",
    email: "manager@gmail.com",
    authority: "manager",
    // password : user.generateHash("123"),
    phoneNumber : "01084840101",
    nickName : "manager",
    imagePath: "manager",
    pushToken: "",
    contentList:[{
      contentId : 3,
      videoPath: [],
      contentName: "NoSmoking",
      joinState : 1,
      authenticationDate : "2018-12-10",
      isUploaded : 1,
      calendar: [],
      money: 100000,
      reward: 50000,
      rewardCheck: 0,
      fcmFailure: 0,
      fcmVideoFailureFlag : 0,
      fcmReportAcceptFlag : 0,
      fcmReportRejectFlag : 0
    }]
  });

  user1.password = user1.generateHash("123");
  user1.save(function(err, savedDocument) {
    if (err)
      return;
  });

  user2.password = user2.generateHash("123");
  user2.save(function(err, savedDocument) {
    if (err)
      return;
  });
  user3.password = user3.generateHash("123");
  user3.save(function(err, savedDocument) {
    if (err)
      return;
  });
  user4.password = user4.generateHash("123");
  user4.save(function(err, savedDocument) {
    if (err)
      return;
  });
  user5.password = user5.generateHash("123");
  user5.save(function(err, savedDocument) {
    if (err)
      return;
  });

  // --------------------------------
  //
  // --------------------------------
  // 컨텐츠 디비 초기화

  var content1 = new content({
    id: 0,
    name: "NoSmoking",
    startDate: "12/01/2018",
    endDate: "12/31/2018",
    isDone: 0,
    userList: [{name: "ParkSeungHyun17", email: "shp17@gmail.com", newVideo: {path: "2018-12-10", authen: 2, authorizePeople: []}, result: 2},
      {name: "HwangEyiKWON17", email: "hek17@gmail.com", newVideo: {path: "2018-12-10", authen: 2, authorizePeople:[]}, result: 2},
      {name: "ChoGeonHee17", email: "cgh17@gmail.com", newVideo: {path: "2018-12-10", authen: 2, authorizePeople: []}, result: 2},
      {name: "JangDongIk17", email: "jdi17@gmail.com", result: 2}],
    description: "금연 컨텐츠입니다. \n 니코틴 측정기를 통해 영상을 인증해주세요. \n 인증된 영상은 타 사용자를 통해 인증됩니다. \n 해당 기간동안 모든 인증이 완료되면 보상을 받게되고, \n 한번이라도 실패하면 패널티를 받게됩니다. \n\n\n 니코틴 판매 사이트\n http://itempage3.auction.co.kr/DetailView.aspx?ItemNo=B582322485&frm3=V2",
    balance: 0
  })
  var content2 = new content({
    id: 1,
    name: "NoSmoking",
    startDate: "01/14/2019",
    endDate: "11/30/2019",
    isDone: 2,
    userList: [],
    description: "금연 컨텐츠입니다. \n 니코틴 측정기를 통해 영상을 인증해주세요. \n 인증된 영상은 타 사용자를 통해 인증됩니다. \n 해당 기간동안 모든 인증이 완료되면 보상을 받게되고, \n 한번이라도 실패하면 패널티를 받게됩니다. \n\n\n 니코틴 판매 사이트\n http://itempage3.auction.co.kr/DetailView.aspx?ItemNo=B582322485&frm3=V2 \n 이 곳에서 니코틴 검사기를 필히 구매하세요.",
    balance: 0
  })
  var content3 = new content({
    id: 2,
    name: "NoSmoking",
    startDate: "01/07/2019",
    endDate: "12/30/2019",
    isDone: 2,
    userList: [],
    description: "금연 컨텐츠입니다. \n 니코틴 측정기를 통해 영상을 인증해주세요. \n 인증된 영상은 타 사용자를 통해 인증됩니다. \n 해당 기간동안 모든 인증이 완료되면 보상을 받게되고, \n 한번이라도 실패하면 패널티를 받게됩니다. \n\n\n 니코틴 판매 사이트\n http://itempage3.auction.co.kr/DetailView.aspx?ItemNo=B582322485&frm3=V2 \n 이 곳에서 니코틴 검사기를 필히 구매하세요.",
    balance: 0
  })
  var content4 = new content({
    id: 3,
    name: "NoSmoking",
    startDate: "01/01/2018",
    endDate: "12/12/2018",
    isDone: 0,
    userList: [{name: "manager", email: "manager@gmail.com", result: 2}],
    description: "금연 컨텐츠입니다. \n 니코틴 측정기를 통해 영상을 인증해주세요. \n 인증된 영상은 타 사용자를 통해 인증됩니다. \n 해당 기간동안 모든 인증이 완료되면 보상을 받게되고, \n 한번이라도 실패하면 패널티를 받게됩니다. \n",
    balance: 100000
  })
  var content5 = new content({
    id: 4,
    name: "NoSmoking",
    startDate: "12/10/2018",
    endDate: "12/11/2018",
    isDone: 1,
    userList: [{name: "manager", email: "manager@gmail.com", result: 2}],
    description: "금연 컨텐츠입니다. \n 니코틴 측정기를 통해 영상을 인증해주세요. \n 인증된 영상은 타 사용자를 통해 인증됩니다. \n 해당 기간동안 모든 인증이 완료되면 보상을 받게되고, \n 한번이라도 실패하면 패널티를 받게됩니다. \n\n\n 니코틴 판매 사이트\n http://itempage3.auction.co.kr/DetailView.aspx?ItemNo=B582322485&frm3=V2 \n 이 곳에서 니코틴 검사기를 필히 구매하세요.",
    balance: 0
  })
  var content6 = new content({
    id: 5,
    name: "NoSmoking",
    startDate: "12/13/2018",
    endDate: "12/15/2018",
    isDone: 0,
    userList: [],
    description: "금연 컨텐츠입니다. \n 니코틴 측정기를 통해 영상을 인증해주세요. \n 인증된 영상은 타 사용자를 통해 인증됩니다. \n 해당 기간동안 모든 인증이 완료되면 보상을 받게되고, \n 한번이라도 실패하면 패널티를 받게됩니다. \n\n\n 니코틴 판매 사이트\n http://itempage3.auction.co.kr/DetailView.aspx?ItemNo=B582322485&frm3=V2 \n 이 곳에서 니코틴 검사기를 필히 구매하세요.",
    balance: 0
  })
  var content7 = new content({
    id: 6,
    name: "NoSmoking",
    startDate: "12/17/2018",
    endDate: "01/17/2019",
    isDone: 2,
    userList: [],
    description: "금연 컨텐츠입니다. \n 니코틴 측정기를 통해 영상을 인증해주세요. \n 인증된 영상은 타 사용자를 통해 인증됩니다. \n 해당 기간동안 모든 인증이 완료되면 보상을 받게되고, \n 한번이라도 실패하면 패널티를 받게됩니다. \n\n\n 니코틴 판매 사이트\n http://itempage3.auction.co.kr/DetailView.aspx?ItemNo=B582322485&frm3=V2 \n 이 곳에서 니코틴 검사기를 필히 구매하세요.",
    balance: 0
  })
  var content8 = new content({
    id: 7,
    name: "NoSmoking",
    startDate: "12/24/2018",
    endDate: "01/24/2019",
    isDone: 2,
    userList: [],
    description: "금연 컨텐츠입니다. \n 니코틴 측정기를 통해 영상을 인증해주세요. \n 인증된 영상은 타 사용자를 통해 인증됩니다. \n 해당 기간동안 모든 인증이 완료되면 보상을 받게되고, \n 한번이라도 실패하면 패널티를 받게됩니다. \n\n\n 니코틴 판매 사이트\n http://itempage3.auction.co.kr/DetailView.aspx?ItemNo=B582322485&frm3=V2 \n 이 곳에서 니코틴 검사기를 필히 구매하세요.",
    balance: 0
  })
  var content9 = new content({
    id: 8,
    name: "NoSmoking",
    startDate: "12/31/2018",
    endDate: "01/31/2019",
    isDone: 2,
    userList: [],
    description: "금연 컨텐츠입니다. \n 니코틴 측정기를 통해 영상을 인증해주세요. \n 인증된 영상은 타 사용자를 통해 인증됩니다. \n 해당 기간동안 모든 인증이 완료되면 보상을 받게되고, \n 한번이라도 실패하면 패널티를 받게됩니다. \n\n\n 니코틴 판매 사이트\n http://itempage3.auction.co.kr/DetailView.aspx?ItemNo=B582322485&frm3=V2 \n 이 곳에서 니코틴 검사기를 필히 구매하세요.",
    balance: 0
  })
  content1.save(function(err, savedDocument) {
    if (err)
      return;
  });
  content2.save(function(err, savedDocument) {
    if (err)
      return;
  });
  content3.save(function(err, savedDocument) {
    if (err)
      return;
  });
  content4.save(function(err, savedDocument) {
    if (err)
      return;
  });
  content5.save(function(err, savedDocument) {
    if (err)
      return;
  });
  content6.save(function(err, savedDocument) {
    if (err)
      return;
  });
  content7.save(function(err, savedDocument) {
    if (err)
      return;
  });
  content8.save(function(err, savedDocument) {
    if (err)
      return;
  });
  content9.save(function(err, savedDocument) {
    if (err)
      return;
  });
  ///--------------------------------
  ///--------------------------------
  ///앱정보 디비 초기화
  var appInfo_ = new appInfoSchema({
    appInfo: "앱 정보입니다 \n 개발자는 캡스톤 디자인 냥냥펀치 \n 박승현 외 3명입니다. 현재 버전은 \n 1.0으로 앞으로 계속 업데이트될 \n 예정입니다",
    noticeInfo: "공지사항 입니다. \n 이 부분에는 앱에 관련된 공지사항 또는 최신 정보가 업로드 됩니다."
  })
  appInfo_.save(function(err, savedDocument) {
    if (err)
      return;
  });
};

function dbDelete(){
  appInfoSchema.remove(function (err, info) {
  });

  user.remove(function (err, info) {
  });

  content.remove(function (err, info) {
  });

  Report.remove(function (err, info) {
  });

}

//Port 설정
const port = process.env.PORT || '3000';
app.set('port', port);

// HTTP 서버
const server = http.createServer(app);
server.listen(port, function () {   console.log('Express running on localhost'+ port); });
