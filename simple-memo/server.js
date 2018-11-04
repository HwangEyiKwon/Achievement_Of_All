var mongoose = require('mongoose');
mongoose.connect('mongodb://nyangnyangpunch:capd@localhost/admin',{dbName: 'capd'});

//mongoose.connect('mongodb://localhost/test');

const express = require('express');
const path = require('path');
const http = require('http');
var session = require('express-session');
var passport = require('passport');
const passportConfig = require('./config/passport');
const bodyParser = require('body-parser');
const test = require('./server/routes/test');
const video = require('./server/routes/video')
const upload = require('./server/routes/upload')


//var bodyParser = require("pdkdf2-password"); // 암호화
//var hasher = bkfd2Password();

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
// video router
app.use('/video', video);
// upload router
app.use('/upload', upload);


app.get('*', function (req, res) {   res.sendFile(path.join(__dirname, 'dist/index.html')); });


//

//
// //로컬스트랫티지에 있는 콜백함수가 호출되도록 함.
// passport.use(new LocalStrategy(
//   function (username, password, done) {
//    //username, password,done을 섞어서 사용자인지 아닌지 판단.
//   var uname = username;
//   var pwd = password;
//
//   //done에는 함수를 담아주기로 약속되어있음.
//     if() {
//       done(null, user);//로그인 성공
//     }
//     else {
//       done(null,false); // 로그인절차가 끝났는데 실패했다는 것. //첫번째 인자는 err처리
//     }
//
//     done(null,false); //세번째 인자에 메세지를 전달 할 수있는데 이게 이전에 failureFlash에 이 메세지를 전달함.
//   }
// ));
//
//
//
// app.post('/login',
//   passport.authenticate('local', {
//     successRedirect: '/',// redirect
//     failureRedirect: '/login', //실패하면 어디로
//     failuereFlash: false   //사용자를 로그인 ??--> 사용자에게 인증에 실패했다라는 정보를 주기 위해 딱 한번 메세지를 보낼 수 있음.
//   }));

app.post('/login', function(req,res,next){
  passport.authenticate('login', function (err, user, info) {
//    console.log(user+"asdfasdf");
    if(err) console.log(err);
    if(user) res.send({success: true});
    else res.send({success: false});
  })(req,res,next);
});

app.post('/getUserInfo', function (req,res) {

  console.log("get User Info: "+JSON.stringify(req.body));

  var email = req.body.email ;

  user.fineOne({email: email}, function(err, info){
    if(err) console.log(err);
    if(info == null) {
      console.log("사용자 아님");
    }
    else {
      console.log("사용자 찾음");
      res.send(info);
    }

  })

})

//리턴함수를 콜백함수로 리턴해준다. /login이라는 라우터가 캡쳐
// passport.au~~~이게 미들웨어임. 이게 함수가 실행되면 리턴값으로 콜백함수를 리턴한다.
//


// app.post('/login',function (req,res) {
//
//   console.log("login req: "+JSON.stringify(req.body));// JSON.stringfy하면 그 안에 내용을 볼 수 있음 원래는 JSON object형태로 옴
//
//   //console.log("req: "+req.body);
//   var email = req.body.email; //받은 바디의 email을 email 변수에 저장한다.
//   var pw = req.body.password;
//
//   user.findOne({email: email, password: pw},function (err, info) {
//     //findOne --> 알아서 디비에서 찾는것
//     if(err){
//       console.log(err);
//     }
//     //console.log(info);
//     //info는 JSON 형태로 나옴
//     if(info==null){
//
//       console.log("res send login fail");
//       res.send({success: false});//승현이한테 보내는 것 실패했다
//     }
//     else {
//
//       sess = req.session;
//       sess.userCheck = req.body.email;
//       console.log("res send login success");
//       res.send({success: true});
//     } // 성공했다
//   })
// }) // login



//
// app.post('/login', function(req, res, next) {
//   console.log("123415");
//   passport.authenticate('local', function(err, user, info) {
//
//     console.log(info);
//
//     if (err) { return next(err); }
//     if (!user) {
//       console.log("login fail");
//       return res.send({access:false}); } // 로그인 실패
//
//     // 로그인 성공 시 세션에 사용자 이메일 저장
//     sess = req.session;
//     sess.userCheck = req.body.email;
//
//     console.log("login success");
//     return res.send({access:true}); // 로그인 성공
//   })(req, res, next);
// });
//
//

// app.post('/login',passport.authenticate('local',function (err,user,info) {
//
//   user.findOne({email: email, password: pw},function (err, info) {
//     //findOne --> 알아서 디비에서 찾는것
//     if(err){
//       console.log(err);
//     }
//     //console.log(info);
//     //info는 JSON 형태로 나옴
//     if(info==null){
//
//       console.log("res send login fail");
//       res.send({success: false});//승현이한테 보내는 것 실패했다
//     }
//     else {
//
//       sess = req.session;
//       sess.userCheck = req.body.email;
//       console.log("res send login success");
//       res.send({success: true});
//     } // 성공했다
//   })
//
// }))





app.post('/signup', function (req, res) {

  console.log("req: "+JSON.stringify(req.body));

  var email = req.body.email;
  var pw = req.body.password;

  hasher({password: pw}, function (err, pass, salt, hash) {
    console.log(err, pass, salt, hash);


    
  })

  user.findOne({email: email, password: pw},function (err, info) {
    //findOne --> 알아서 디비에서 찾는것
    if(err){
      console.log(err);
    }
    console.log(info);
    //info는 JSON 형태로 나옴

    if(info==null){

      var user1 = new user();
      //user1.name = "psh";
      user1.email = email;
      user1.password = pw;

      user1.save(function(err, savedDocument) {
        if (err)
          return console.error(err);
        //console.log(savedDocument);
        console.log("signup success");

      });


      res.send({success: true});//승현이한테 보내는 것 실패했다
    }
    else {
      console.log("exist user");
      res.send({success: false});
    } // 성공했다
  })

})


// Port 설정
const port = process.env.PORT || '3000';
app.set('port', port);

// HTTP 서버
const server = http.createServer(app);
server.listen(port, function () {   console.log('Express running on localhost'+ port); });
