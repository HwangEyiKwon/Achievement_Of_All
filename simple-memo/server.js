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
user1.password = "123";

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


app.get('*', function (req, res) {   res.sendFile(path.join(__dirname, 'dist/index.html')); });

// Port 설정
const port = process.env.PORT || '3000';
app.set('port', port);

// HTTP 서버
const server = http.createServer(app);
server.listen(port, function () {   console.log('Express running on localhost'+ port); });
