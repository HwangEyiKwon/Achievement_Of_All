﻿var mongoose = require('mongoose');
//mongoose.connect('mongodb://nyangpun:capd@localhost/admin',{dbName: 'capd'});

// mongoose.connect('mongodb://nyangnyangpunch:capd@localhost/admin',{dbName: 'capd'});
// mongoose.connect('mongodb://capd:1234@localhost/admin',{dbName: 'capd'});
mongoose.connect('mongodb://localhost:27017');

// mongoose.connect('mongodb://nyangnyangpunch:capd@localhost/admin',{dbName: 'capd'});
//mongoose.connect('mongodb://capd:1234@localhost/admin',{dbName: 'capd'});
// mongoose.connect('mongodb://localhost:27017');


const express = require('express');
const path = require('path');
const http = require('http');
var session = require('express-session');
var passport = require('passport');
const passportConfig = require('./config/passport');
const bodyParser = require('body-parser');
const test = require('./server/routes/test');
const video = require('./server/routes/video');
const image = require('./server/routes/image');
const index = require('./server/routes/index');
const appInfo = require('./server/routes/appInfo');
const search = require('./server/routes/search');
const calendar = require('./server/routes/calendar');
const applyContent = require('./server/routes/applyContent');
var bcrypt = require('bcrypt-nodejs'); // 암호화를 위한 모듈
var mkdirp = require('mkdirp'); // directory 만드는것
var fs = require("fs");


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

require('./config/passport')(passport);


//--------------------------------
// 유저 디비 초기화
//
// var user1 = new user({
//   name: "ParkSeungHyun17",
//   email: "shp17@gmail.com",
//   // password : user.generateHash("123"),
//   phoneNumber : "01093969408",
//   nickName : "4.5man",
//   imagePath: "ParkSeungHyun17",
//   contentList:[{
//     contentId : 0,
//     videoPath: [{path: "ns1", authen: 1},{path: "ns2", authen: 0}],
//     contentName: "NoSmoking",
//     joinState : 1,
//     authenticationDate : "2018-11-15",
//     isAuthenticated : 1,
//     calendar: [{year: "2018", month: "10", day: "15", authen: 1}, {year: "2018", month: "10", day: "18", authen: 1}, {year: "2018", month: "10", day: "21", authen: 1}]
//   },{
//     videoPath: [{path: "d1", authen: 1},{path: "d2", authen: 0}],
//     contentId : 1,
//     contentName: "Diet",
//     joinState : 1,
//     authenticationDate : "2018-11-15",
//     isAuthenticated : 1,
//     calendar: [{year: "2018", month: "10", day: "15", authen: 1}, {year: "2018", month: "10", day: "18", authen: 1}, {year: "2018", month: "10", day: "21", authen: 1}]
//   }]
// });
// var user2 = new user({
//   name: "HwangEyiKWON17",
//   email: "hek17@gmail.com",
//   // password : user.generateHash("123"),
//   phoneNumber : "01093969408",
//   nickName : "4.5man",
//   imagePath: "HwangEyiKWON17",
//   contentList:[{
//     contentId : 0,
//     videoPath: [{path: "ns1", authen: 1},{path: "ns2", authen: 0}],
//     contentName: "NoSmoking",
//     joinState : 1,
//     authenticationDate : "2018-11-15",
//     isAuthenticated : 1,
//     calendar: [{year: "2018", month: "10", day: "7", authen: 1}, {year: "2018", month: "10", day: "10", authen: 1}, {year: "2018", month: "10", day: "13", authen: 1}]
//   },{
//     videoPath: [{path: "d1", authen: 1},{path: "d2", authen: 0}],
//     contentId : 1,
//     contentName: "Diet",
//     joinState : 1,
//     authenticationDate : "2018-11-15",
//     isAuthenticated : 1,
//     calendar: [{year: "2018", month: "10", day: "8", authen: 1}, {year: "2018", month: "10", day: "11", authen: 1}, {year: "2018", month: "10", day: "14", authen: 1}]
//   }]
// });
//
//  var user3 = new user({
//   name: "ChoGeonHee17",
//   email: "cgh17@gmail.com",
//   // password : user.generateHash("123"),
//   phoneNumber : "01093969408",
//   nickName : "seoulUnivMan",
//   imagePath: "ChoGeonHee17",
//   contentList:[{
//     contentId : 0,
//     videoPath: [{path: "ns1", authen: 1},{path: "ns2", authen: 0}],
//     contentName: "NoSmoking",
//     joinState : 1,
//     authenticationDate : "2018-11-18",
//     isAuthenticated : 0,
//     calendar: [{year: "2018", month: "10", day: "7", authen: 1}, {year: "2018", month: "10", day: "10", authen: 1}, {year: "2018", month: "10", day: "13", authen: 1}]
//   },{
//     videoPath: [{path: "d1", authen: 1},{path: "d2", authen: 0}],
//     contentId : 1,
//     contentName: "Diet",
//     joinState : 1,
//     authenticationDate : "2018-11-18",
//     isAuthenticated : 0,
//     calendar: [{year: "2018", month: "10", day: "8", authen: 1}, {year: "2018", month: "10", day: "11", authen: 1}, {year: "2018", month: "10", day: "14", authen: 1}]
//   }]
// });
//  var user4 = new user({
//   name: "JangDongIk17",
//   email: "jdi17@gmail.com",
//   // password : user.generateHash("123"),
//   phoneNumber : "01093969408",
//   nickName : "Man",
//   imagePath: "JangDongIk17",
//   contentList:[]
// });
//
// user1.password = user1.generateHash("123");
// user1.save(function(err, savedDocument) {
//   if (err)
//     return console.error(err);
//   console.log(savedDocument);
//   console.log("DB initialization");
//
// });
//
// user2.password = user1.generateHash("123");
// user2.save(function(err, savedDocument) {
//   if (err)
//     return console.error(err);
//   console.log(savedDocument);
//   console.log("DB initialization");
//
// });
// user3.password = user1.generateHash("123");
// user3.save(function(err, savedDocument) {
//   if (err)
//     return console.error(err);
//   console.log(savedDocument);
//   console.log("DB initialization");
//
// });
// user4.password = user4.generateHash("123");
// user4.save(function(err, savedDocument) {
//   if (err)
//     return console.error(err);
//   console.log(savedDocument);
//   console.log("DB initialization");
//
// });
//
// // --------------------------------
// //
// // --------------------------------
// // 컨텐츠 디비 초기화
//
// var content1 = new content({
//   id: 0,
//   name: "NoSmoking",
//   startDate: "10/01/2018",
//   endDate: "10/30/2018",
//   isDone: 0,
//   userList: [{name: "ParkSeungHyun17", email: "shp17@gmail.com", newVideo: {path: "ns2", authen: 0, authorizePeople: []}},
//     {name: "HwangEyiKWON17", email: "hek17@gmail.com", newVideo: {path: "ns2", authen: 0, authorizePeople:[{name: "ParkSeungHyun17", authenInfo: 1}]}},
//     {name: "ChoGeonHee17", email: "cgh17@gmail.com", newVideo: {path: "ns2", authen: 0, authorizePeople: [{name: "HwangEyiKWON17", authenInfo: 1}]}}],
//   description: "금연 컨텐츠입니다. \n 18년11월1일부터 18년11월30일까지 진행됩니다. \n 니코틴 측정기를 통해 영상을 인증해주세요. \n 인증된 영상은 타 사용자를 통해 인증됩니다. \n 해당 기간동안 모든 인증이 완료되면 보상을 받게되고, \n 한번이라도 실패하면 패널티를 받게됩니다. \n"
// })
// var content2 = new content({
//   id: 1,
//   name: "Diet",
//   startDate: "09/01/2018",
//   endDate: "12/01/2018",
//   endDate: "12/01/2018",
//   isDone: 0,
//   userList: [{name: "ParkSeungHyun17", email: "shp17@gmail.com", newVideo: {path: "d2", authen: 0, authorizePeople: []}},
//     {name: "HwangEyiKWON17", email: "hek17@gmail.com", newVideo: {path: "d2", authen: 0, authorizePeople: [{name: "ParkSeungHyun17", authenInfo: 1}]}},
//     {name: "ChoGeonHee17", email: "cgh17@gmail.com", newVideo: {path: "d2", authen: 0, authorizePeople: [{name: "HwangEyiKWON17", authenInfo: 1}]}}],
//   description: "다이어트 컨텐츠입니다. \n 18년9월1일부터 18년12월1일까지 진행됩니다. \n 인증된 영상은 타 사용자를 통해 인증됩니다. \n 해당 기간동안 모든 인증이 완료되면 보상을 받게되고, \n 한번이라도 실패하면 패널티를 받게됩니다. \n"
// })
// var content3 = new content({
//   id: 2,
//   name: "Study",
//   startDate: "01/01/2019",
//   endDate: "06/30/2019",
//   isDone: 2,
//   userList: [{name: "ParkSeungHyun17", email: "shp17@gmail.com", newVideo: {path: "d2", authen: 0, authorizePeople: []}},
//     {name: "HwangEyiKWON17", email: "hek17@gmail.com", newVideo: {path: "d2", authen: 0, authorizePeople: [{name: "ParkSeungHyun17", authenInfo: 1}]}},
//     {name: "ChoGeonHee17", email: "cgh17@gmail.com", newVideo: {path: "d2", authen: 0, authorizePeople: [{name: "HwangEyiKWON17", authenInfo: 1}]}}],
//   description: "공부 컨텐츠입니다. \n 18년9월1일부터 18년12월31일까지 진행됩니다. /n 인증된 영상은 타 사용자를 통해 인증됩니다. \n 해당 기간동안 모든 인증이 완료되면 보상을 받게되고, \n 한번이라도 실패하면 패널티를 받게됩니다. \n"
// })
// var content4 = new content({
//   id: 3,
//   name: "NoSmoking",
//   roomNum: 2,
//   startDate: "01/01/2019",
//   endDate: "11/30/2019",
//   isDone: 2,
//   userList: [],
//   description: "금연 컨텐츠입니다. \n 19년1월1일부터 19년11월30일까지 진행됩니다. \n 니코틴 측정기를 통해 영상을 인증해주세요. \n 인증된 영상은 타 사용자를 통해 인증됩니다. \n 해당 기간동안 모든 인증이 완료되면 보상을 받게되고, \n 한번이라도 실패하면 패널티를 받게됩니다. \n"
// })
// var content5 = new content({
//   id: 4,
//   name: "NoSmoking",
//   roomNum: 3,
//   startDate: "09/01/2019",
//   endDate: "12/30/2019",
//   isDone: 2,
//   userList: [],
//   description: "금연 컨텐츠입니다. \n 19년9월1일부터 19년12월30일까지 진행됩니다. \n 니코틴 측정기를 통해 영상을 인증해주세요. \n 인증된 영상은 타 사용자를 통해 인증됩니다. \n 해당 기간동안 모든 인증이 완료되면 보상을 받게되고, \n 한번이라도 실패하면 패널티를 받게됩니다. \n"
// })
// content1.save(function(err, savedDocument) {
//   if (err)
//     return console.error(err);
//   console.log(savedDocument);
//   console.log("DB initialization");
//
// });
// content2.save(function(err, savedDocument) {
//   if (err)
//     return console.error(err);
//   console.log(savedDocument);
//   console.log("DB initialization");
//
// });
// content3.save(function(err, savedDocument) {
//   if (err)
//     return console.error(err);
//   console.log(savedDocument);
//   console.log("DB initialization");
//
// });
// content4.save(function(err, savedDocument) {
//   if (err)
//     return console.error(err);
//   console.log(savedDocument);
//   console.log("DB initialization");
//
// });
// content5.save(function(err, savedDocument) {
//   if (err)
//     return console.error(err);
//   console.log(savedDocument);
//   console.log("DB initialization");
//
// });
// ///--------------------------------
// ///--------------------------------
// ///앱정보 디비 초기화
// var appInfo_ = new appInfoSchema({
//   appInfo: "앱 정보입니다 \n 개발자는 캡스톤 디자인 냥냥펀치 \n 박승현 외 3명입니다. 현재 버전은 \n 1.0으로 앞으로 계속 업데이트될 \n 예정입니다",
//   noticeInfo: "공지사항 입니다. \n 이 부분에는 앱에 관련된 공지사항 또는 최신 정보가 업로드 됩니다."
// })
// appInfo_.save(function(err, savedDocument) {
//   if (err)
//     return console.error(err);
//   console.log(savedDocument);
//   console.log("DB initialization");
//
// });
//--------------------------------

//--------------------------------
//--------------------------------
// 디비 모두 제거
// appInfoSchema.remove(function (err, info) {
//   console.log("DELETED");
// });
//
// user.remove(function (err, info) {
//   console.log("DELETED");
// });
//
// content.remove(function (err, info) {
//   console.log("DELETED");
// });
// --------------------------------

//???
//접근할땐 [0] console.log("data : " +user1.contentList[0].authenticationDate);
//저장할땐 user1.contentList = {isAuthenticated : 1};


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

// test
app.use('/test', test);
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
//calendar router
app.use('/', calendar);
//applyContent router
app.use('/', applyContent);

app.set('jwtTokenSecret', "afafaffffff");

app.get('*', function (req, res) {   res.sendFile(path.join(__dirname, 'dist/simple-memo/index.html')); });
//여기 아래
const serverKey = 'AAAAKw66KHo:APA91bE1A1hr5P69HHdOWigZl5FQgYtUn0FzQ554EPrEcJMzG4LfMxieNPko8hKzAg4ImeScWEtYqHmspYb0dJZWKgpEuGJY98iKLFXKf02FhHW-0xUNi2he2LL3pbpSm0VjhsbJ5Y8l';

/*
//수정하는 db 코드, 참고용, 이걸 실제 코드에 넣어야 됨. 작동 됨
user.findOneAndUpdate(
{"email": "psh", "contentList.contentId" : "1"}, {$set: { "contentList.$.isAuthenticated" : "0", "contentList.$.authenticationDate": "2018-10-10"}},function(err, doc){
  if(err){
    console.log(err);
  }

  console.log(doc);
});
*/

//어레이 추가하는 db 코드, 작동 됨.
// user.findOneAndUpdate({email: "psh"}, {$push:{contentList: [{contentId: "2", isAuthenticated: "0", authenticationDate: "2018-10-15"}]}},function(err, doc){
//   if(err){
//     console.log(err);
//   }
//   console.log(doc);
// });

// mkdirp('./server/user/sph2@gmail.com/video/NoSmoking', function (err) {
//   if(err) console.log(err);
//   else console.log("create dir");
// }); //server폴더 아래 /user/useremail/video 폴더가 생김.

// mkdirp('./server/contentImage/', function (err) {
//   if(err) console.log(err);
//   else console.log("create dir");
// }); //server폴더 아래 /user/useremail/video 폴더가 생김.

// var threeDaysAgo = new Date(Date.now());
// for(var i =0;i < 100;i++) {
//   // 2014-03-01 - 월은 0에서부터 시작된다.
//    threeDaysAgo.setDate(threeDaysAgo.getDate() + 3); // 2014-02-26 => 3일전으로~
//   // console.log("year : " + todayYear + "month : " + todayMonth + " day : " + todayDay);
//   console.log("date : "+threeDaysAgo);
// } 얘를 나중에 authen 0 에서 1 로 바꾸는 애한테 끼워넣으면서 date 업데이트 해주면 됨.

app.post('/sendToken', function(req, res) {
  console.log(req.body.fcmToken);
  console.log(req.body.email);

  res.send({success: true});

  var userEmail = req.body.email;
  var userToken = req.body.fcmToken;
  user.findOne({email: userEmail}, function (err, user) {
    user.pushToken = userToken;
    user.save(function (err) {
      if (err) console.log(err);
    });
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
    console.log(user);
    if(user== null){
      console.log("User.contentList is null");
    }else{
      console.log(user.contentList);
      var joinContentCount = user.contentList.length;
      var authenContentIndex;
      for(var i = 0; i < joinContentCount; i++){
        if(user.contentList[i].authenticationDate === today){
          authenContentIndex = i;
          break;
        }
      }
      console.log('1: today = ' + today + 'user Authenticated' + user.contentList[authenContentIndex].isAuthenticated + 'Date : ' + user.contentList[authenContentIndex].authenticationDate);
      //로그아웃 했다가 로그인 한 인증 필요 사용자에게 푸쉬 알림 전송

      if(user.contentList[authenContentIndex].isAuthenticated != 1) {
        console.log('2: if moon');

        var sendTime1 = new Date(todayYear, todayMonth - 1, todayDate.getDate(), 9, 0, 0);
        var sendTime2 = new Date(todayYear, todayMonth - 1, todayDate.getDate(), 14, 0, 0);
        var sendTime3 = new Date(todayYear, todayMonth - 1, todayDate.getDate(), 19, 0, 0);
        sendPushMessage(user, authenContentIndex, sendTime1);
        sendPushMessage(user, authenContentIndex, sendTime2);
        sendPushMessage(user, authenContentIndex, sendTime3);
      }
    }
  });
});

//날짜가 바뀔 때마다 푸쉬알림 해당자에게 전송 및 매일 수행될 기능들
var scheduler = schedule.scheduleJob('00 * * *', function(){
  var todayDate = new Date();
  var todayYear = todayDate.getFullYear();
  var todayMonth = todayDate.getMonth() + 1;
  var todayDay = todayDate.getDate();

  // 일이 한자리 수인 경우 앞에 0을 붙여주기 위해
  if ((todayDay+"").length < 2) {
    todayDay = "0" + todayDay;
  }

  var today = todayYear+ "-" + todayMonth + "-" + todayDay;
  var yesterday = todayYear+ "-" + todayMonth + "-" + todayDay-1;

  /* 모든 유저에 대해 authentication Date체크 */
  user.find({"contentList.authenticationDate" : today}, function(err, userList){
    for(var i = 0; i < Object.keys(userList).length; i++){
      var joinContentCount = userList[i].contentList.length;
      var authenContentIndex;
      for(var j = 0; j < joinContentCount; j++){
        if(userList[i].contentList[j].authenticationDate === today){
          authenContentIndex = j;
          break;
        }
      }
      if(userList[i].pushToken != null  && userList[i].contentList[authenContentIndex].isAuthenticated != 1) {
        var sendTime1 = new Date(todayYear, todayMonth - 1, todayDate.getDate(), 9, 0, 0);
        var sendTime2 = new Date(todayYear, todayMonth - 1, todayDate.getDate(), 14, 0, 0);
        var sendTime3 = new Date(todayYear, todayMonth - 1, todayDate.getDate(), 19, 0, 0);
        sendPushMessage(userList[i], authenContentIndex, sendTime1);
        sendPushMessage(userList[i], authenContentIndex, sendTime2);
        sendPushMessage(userList[i], authenContentIndex, sendTime3);
      }
    }
  });

  //컨텐츠 진행중인데, 인증 실패하거나 인증 수행 안한 사람 데이터 뽑아 처리하기 위한 코드
  user.find({"contentList.authenticationDate": yesterday, "contentList.isAuthenticated" : "0", "contentList.joinState" : "1"}, function(err, userList){
    for(var i = 0; i < Object.keys(userList).length; i++) {
      //fail에 대한 정보를 전달해줘야 할 것임
    }

    userList[0].save(function (err) {
      if(err) console.log(err);
    });
  });

  //매일마다 인증 현황을 0으로 수정해줌
  user.find({isAuthenticated: 1}, function(err, userList){
    for(var i = 0; i < Object.keys(userList).length; i++){
      for(var j = 0; j < Object.keys(userList[i].contentList).length; j++){
        userlist[i].contentList[j].isAuthenticated = 0;
        user.save(function (err) {
          if (err) console.log(err);
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

      contentList[i].achievementRate = achievementRate;
      console.log("Achievement Rate =" + contentList[i].achievementRate);
    }
    contentList.save(function (err) {
      if (err) console.log(err);
    });
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
  content.find({isDone: 0}, function(err, contentList){
    for(var i = 0; i < Object.keys(contentList).length; i++){
      var totalDate = (contentList[i].endDate.getTime() - contentList[i].startDate.getTime()) / ( 24*60*60*1000);
      var remainedDate = (todayDate.getTime() - contentList[i].startDate.getTime()) / ( 24*60*60*1000);

      var achievementRate = (remainedDate / totalDate) * 100;

      contentList[i].achievementRate = achievementRate;
      console.log("Achievement Rate =" + contentList[i].achievementRate);
      contentList[i].save(function (err) {
        if (err) console.log(err);
      });
    }
  });
});



//푸쉬메시지 펑션
function sendPushMessage(user, sendTime) {
  console.log('6');
  var fcm = new FCM(serverKey);
  var client_token = user.pushToken;
  var push_data = {
    // 수신대상
    to: client_token,
    // App이 실행중이지 않을 때 상태바 알림으로 등록할 내용
    notification: {
      title: "모두의 달성",
      body: "목표 달성을 인증하셔야 합니다.",
      sound: "default",
      click_action: "FCM_PLUGIN_ACTIVITY",
      icon: "fcm_push_icon"
    },
    // 메시지 중요도
    priority: "high",
    // App 패키지 이름
    restricted_package_name: "com.example.parkseunghyun.achievementofall",
  };

  var scheduler = schedule.scheduleJob(sendTime, arrayIndex, function(){
    console.log('7');
    if(user.contentList[arrayIndex].isAuthenticated == 1 ) {
      console.log('before user authen : ' + user.contentList[0].isAuthenticated);
      user.contentList[arrayIndex].isAuthenticated = 0;
      console.log('after user authen : ' + user.contentList[0].isAuthenticated);
    } //추후 ) 변수 수정 xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    else{
      console.log('8');
      fcm.send(push_data, function(err, response) {
        if (err) {
          console.error('Push메시지 발송에 실패했습니다.');
          console.error(err);
          return;
        }
        console.log('Push메시지가 발송되었습니다.');
        console.log(response);
      });
    };
  });
}

//Port 설정
const port = process.env.PORT || '3000';
app.set('port', port);

// HTTP 서버
const server = http.createServer(app);
server.listen(port, function () {   console.log('Express running on localhost'+ port); });