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

app.post('/sendToken', function(req, res){
  console.log(req.body.fcmToken);
  console.log(req.body.email);

  res.send({success: true});

  var userEmail = req.body.email;
  var userToken = req.body.fcmToken;
  user.findOne({email: userEmail}, function (err,user) {
    user.pushToken = userToken;
    user.save(function (err) {
      if(err) console.log(err);
    });
  });

  var serverKey = 'AAAAKw66KHo:APA91bE1A1hr5P69HHdOWigZl5FQgYtUn0FzQ554EPrEcJMzG4LfMxieNPko8hKzAg4ImeScWEtYqHmspYb0dJZWKgpEuGJY98iKLFXKf02FhHW-0xUNi2he2LL3pbpSm0VjhsbJ5Y8l';
  var client_token = userToken;
  var push_data = {
    // 수신대상
    to: client_token,
    // App이 실행중이지 않을 때 상태바 알림으로 등록할 내용
    notification: {
      title: "모두의 달성",
      body: "목표 달성을 인증할 시간입니다.",
      sound: "default",
      click_action: "FCM_PLUGIN_ACTIVITY",
      icon: "fcm_push_icon"
    },
    // 메시지 중요도
    priority: "high",
    // App 패키지 이름
    restricted_package_name: "com.example.parkseunghyun.achievementofall",
    /*
    // App에게 전달할 데이터
    data: {
      num1: 2000,
      num2: 3000
    }*/
  };
  var fcm = new FCM(serverKey);

  var scheduler = schedule.scheduleJob('*/10 * * * * *', function(){
    fcm.send(push_data, function(err, response) {
      if (err) {
        console.error('Push메시지 발송에 실패했습니다.');
        console.error(err);
        return;
      }
      console.log('Push메시지가 발송되었습니다.');
      console.log(response);
    });
  });
})



var scheduler = schedule.scheduleJob('6', function(){
  fcm.send(push_data, function(err, response) {
    if (err) {
      console.error('Push메시지 발송에 실패했습니다.');
      console.error(err);
      return;
    }
    console.log('Push메시지가 발송되었습니다.');
    console.log(response);
  });
});
// Port 설정
const port = process.env.PORT || '3000';
app.set('port', port);

// HTTP 서버
const server = http.createServer(app);
server.listen(port, function () {   console.log('Express running on localhost'+ port); });
