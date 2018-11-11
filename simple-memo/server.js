var mongoose = require('mongoose');
//mongoose.connect('mongodb://nyangpun:capd@localhost/admin',{dbName: 'capd'});
 mongoose.connect('mongodb://capd:1234@localhost/admin',{dbName: 'capd'});

const express = require('express');
const path = require('path');
const http = require('http');
var session = require('express-session');
var passport = require('passport');
const passportConfig = require('./config/passport');
const bodyParser = require('body-parser');
const test = require('./server/routes/test');
const video = require('./server/routes/video');
const upload = require('./server/routes/upload');
const index = require('./server/routes/index');
const search = require('./server/routes/search')
var bcrypt = require('bcrypt-nodejs'); // 암호화를 위한 모듈

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

require('./config/passport')(passport);



// 디비 초기화
/*
var user1 = new user({
  name: "psh",
  email: "psh",
  // password : user.generateHash("123"),
  phoneNumber : "01012341124",
  nickName : "4.5man",
  contentList:[{
    contentId : 1,
    joinState : 1,
    authenticationDate : "2018-11-08",
    isAuthenticated : 1,
  }]
});
user1.password = user1.generateHash("123");
// var user1 = new user();
//
// user1.name = "psh1";
// user1.email = "psh1";
// user1.password = user1.generateHash("123");
// user1.phoneNumber = "01012341124";
// user1.nickName = "enji";
// //이런 식으로 저장하면 됨 .
//
user1.save(function(err, savedDocument) {
  if (err)
    return console.error(err);
  console.log(savedDocument);
  console.log("DB initialization");

});*/


// 디비 초기화 완료

//접근할땐 [0] console.log("data : " +user1.contentList[0].authenticationDate);
//저장할땐 user1.contentList = {isAuthenticated : 1};

//app.use(session({ secret: 'jang', store : new redisStore({client : client, ttl : 260}), saveUninitialized: true,resave: false }));



// POST 데이터
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));

app.use(session({
  secret: '123!@#456$%^789&*(0)',
  resave: true,
  saveUninitialized: true
}));

//
// app.use(session({
//   secret: '123!@#456$%^789&*(0)11',
//   resave: true,
//   saveUninitialized: true,
//   cookie:{
//     expires : new Date(Date.now() + 3600000),
//     httpOnly: false,
//     secure: true
//   }
// }));

app.use(passport.initialize());
app.use(passport.session());

// ng build 명령
app.use(express.static(path.join(__dirname, 'dist/simple-memo')));

// test
app.use('/test', test);
// index page router
app.use('/', index);
// video router
app.use('/video', video);
// upload router
app.use('/upload', upload);
// fcm router
//app.use('/fcm', fcm);
//search router
app.use('/', search);

app.set('jwtTokenSecret', "afafaffffff");

app.get('*', function (req, res) {   res.sendFile(path.join(__dirname, 'dist/index.html')); });
//여기 아래
const serverKey = 'AAAAKw66KHo:APA91bE1A1hr5P69HHdOWigZl5FQgYtUn0FzQ554EPrEcJMzG4LfMxieNPko8hKzAg4ImeScWEtYqHmspYb0dJZWKgpEuGJY98iKLFXKf02FhHW-0xUNi2he2LL3pbpSm0VjhsbJ5Y8l';


//수정하는 db 코드, 참고용, 이걸 실제 코드에 넣어야 됨
user.findOneAndUpdate(
{"email": "psh", "contentList.contentId" : "1"}, {$set: { "contentList.$.isAuthenticated" : "0", "contentList.$.authenticationDate": "2018-10-10"}},function(err, doc){
  if(err){
    console.log(err);
  }

  console.log(doc);
});
/*
//어레이 추가하는 db 코드
user.findOneAndUpdate({email: "psh"}, {$push:{contentList: [{contentId: "2", isAuthenticated: "0", authenticationDate: "2018-10-15"}]}},function(err, doc){
  if(err){
    console.log(err);
  }
  console.log(doc);
});
*/

user.findOne({ email: "psh" }, function(err, user) {
  var joinContentCount = user.contentList.length;
  console.log(joinContentCount);
});


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
  //
  user.findOne({ email: userEmail, "contentList.authenticationDate" : today }, function(err, user) {
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

      var sendTime1 = new Date(todayYear, todayMonth - 1, todayDate.getDate(), 20, 30, 0);
      var sendTime2 = new Date(todayYear, todayMonth - 1, todayDate.getDate(), 20, 31, 0);
      var sendTime3 = new Date(todayYear, todayMonth - 1, todayDate.getDate(), 20, 32, 0);
      sendPushMessage(user, authenContentIndex, sendTime1);
      console.log('3');
      sendPushMessage(user, authenContentIndex, sendTime2);
      console.log('4');
      sendPushMessage(user, authenContentIndex, sendTime3);
      console.log('5');
    }
    //console.log(user);
  });
});

//날짜가 바뀔 때마다 푸쉬알림 해당자에게 전송
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
        var sendTime1 = new Date(todayYear, todayMonth - 1, todayDate.getDate(), 09, 00, 0);
        var sendTime2 = new Date(todayYear, todayMonth - 1, todayDate.getDate(), 14, 00, 0);
        var sendTime3 = new Date(todayYear, todayMonth - 1, todayDate.getDate(), 19, 00, 0);
        sendPushMessage(userList[i], authenContentIndex, sendTime1);
        sendPushMessage(userList[i], authenContentIndex, sendTime2);
        sendPushMessage(userList[i], authenContentIndex, sendTime3);
      }
    }
  });

  //실패하거나 인증 수행 안한 사람 데이터 뽑아 처리하기 위한 코드
  user.find({"contentList.authenticationDate": yesterday}, function(err, userList){



    userList[0].save(function (err) {
      if(err) console.log(err);
    });
  });

  user.find({isAuthenticated: 1}, function(err, userList){
    for(var i = 0; i < Object.keys(userList).length; i++){
      userlist[i].contentList[0].isAuthenticated = 0;
      user.save(function (err) {
        if (err) console.log(err);
      });
    }
  });
});

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

/*
user.isAuthenticated = 1; //한번 보내고 1로 바꿔보기
        user.save(function (err) {
          if (err) console.log(err);
        });
 */

// Port 설정
const port = process.env.PORT || '3000';
app.set('port', port);

// HTTP 서버
const server = http.createServer(app);
server.listen(port, function () {   console.log('Express running on localhost'+ port); });
