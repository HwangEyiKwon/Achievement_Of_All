var mongoose = require('mongoose');
mongoose.connect('mongodb://nyangnyangpunch:capd@localhost/admin',{dbName: 'capd'});

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

/*
// 디비 초기화
var user1 = new user();
user1.name = "psh";
user1.email = "psh";
user1.password = user1.generateHash("123");

user1.save(function(err, savedDocument) {
  if (err)
    return console.error(err);
  console.log(savedDocument);
  console.log("DB initialization");

});
// 디비 초기화 완료*/

//app.use(session({ secret: 'jang', store : new redisStore({client : client, ttl : 260}), saveUninitialized: true,resave: false }));


// POST 데이터
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));

app.use(session({
  secret: '123!@#456$%^789&*(0)',
  resave: false,
  saveUninitialized: true,
}));

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



app.get('*', function (req, res) {   res.sendFile(path.join(__dirname, 'dist/index.html')); });
//여기 아래
const serverKey = 'AAAAKw66KHo:APA91bE1A1hr5P69HHdOWigZl5FQgYtUn0FzQ554EPrEcJMzG4LfMxieNPko8hKzAg4ImeScWEtYqHmspYb0dJZWKgpEuGJY98iKLFXKf02FhHW-0xUNi2he2LL3pbpSm0VjhsbJ5Y8l';

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
    var todayDate = new Date();
    var todayMonth = todayDate.getMonth() + 1;
    var todayDay = todayDate.getDate();
    var todayYear = todayDate.getFullYear();

    // 일이 한자리 수인 경우 앞에 0을 붙여주기 위해
    if ((todayDay+"").length < 2) {
      todayDay = "0" + todayDay;
    }

    var today = todayYear+ "-" + todayMonth + "-" + todayDay;

    console.log('1: today = ' + today + 'user Authenticated' + user.isAuthenticated + 'Date : ' + user.authenticationDate);
    //로그아웃 했다가 로그인 한 인증 필요 사용자에게 푸쉬 알림 전송
    if(user.authenticationDate === today && user.isAuthenticated != 1) {
      console.log('2: if moon');

      var sendTime1 = new Date(todayYear, todayMonth - 1, todayDate.getDate(), 20, 30, 0);
      var sendTime2 = new Date(todayYear, todayMonth - 1, todayDate.getDate(), 20, 31, 0);
      var sendTime3 = new Date(todayYear, todayMonth - 1, todayDate.getDate(), 20, 32, 0);
      sendPushMessage(user, sendTime1);
      console.log('3');
      sendPushMessage(user, sendTime2);
      console.log('4');
      sendPushMessage(user, sendTime3);
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
  user.find({authenticationDate: today}, function(err, userList){
    for(var i = 0; i < Object.keys(userList).length; i++){
      if(userList[i].pushToken != null  && userList[i].isAuthenticated != 1) {
        var sendTime1 = new Date(todayYear, todayMonth - 1, todayDate.getDate(), 19, 51, 0);
        var sendTime2 = new Date(todayYear, todayMonth - 1, todayDate.getDate(), 19, 52, 0);
        var sendTime3 = new Date(todayYear, todayMonth - 1, todayDate.getDate(), 19, 55, 0);
        sendPushMessage(userList[i], sendTime1);
        sendPushMessage(userList[i], sendTime2);
        sendPushMessage(userList[i], sendTime3);
      }
    }
  });

  //실패하거나 인증 수행 안한 사람 데이터 뽑아 처리하기 위한 코드
  user.find({authenticationDate: yesterday}, function(err, userList){




    userList.save(function (err) {
      if(err) console.log(err);
    });
  });

  user.find({isAuthenticated: 1}, function(err, userList){
    for(var i = 0; i < Object.keys(userList).length; i++){
      userlist[i].isAuthenticated = 0;
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

  var scheduler = schedule.scheduleJob(sendTime, function(){
    console.log('7');
    if(user.isAuthenticated == 1 ) {
      console.log('before user authen : ' + user.isAuthenticated);
      user.isAuthenticated = 0;
      console.log('after user authen : ' + user.isAuthenticated);
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
