const express = require('express');
const router = express.Router();
var passport = require('passport');
const passportConfig = require('../../config/passport');
var User = require('../models/user');
var Content = require('../models/content');
var App = require('../models/app');
var jwt = require('jwt-simple'); // jwt token 사용
var fs = require("fs");
var mkdirp = require('mkdirp'); // directory 만드는것
var nodemailer = require('nodemailer');
var multiparty = require('multiparty');
var schedule = require('node-schedule');


router.post('/jwtCheck', function(req, res){
  var decoded = jwt.decode(req.body.token,req.app.get("jwtTokenSecret"));
  var email = decoded.userCheck;

  User.findOne({ email : email }, function(err, user) {
    if(err){
      console.log("jwtCheck err : "+err);
      res.send({success: false});
    }
    res.send({success: true});
  });

});
router.post('/login', function(req,res,next){
  passport.authenticate('login', function (err, user, info) {

    if(err) console.log("login err : "+err);
    if(user){
      // jwt 토큰 생성
      var token = jwt.encode({
        userCheck: req.body.email,
        // exp: expires
      }, req.app.get("jwtTokenSecret"));

      // response header에 추가
      res.setHeader("token", token)
      res.send({success: true});
    }
    else res.send({success: false});
  })(req,res,next);
});

router.post('/logout', function(req, res){
  var decoded = jwt.decode(req.body.token,req.app.get("jwtTokenSecret"));
  var email = decoded.userCheck;

  User.findOne({ email : email }, function(err, user) {
    if(user != null){
      if(err){
        console.log("logout err : "+err);
        res.send({success: false});
      }
      user.pushToken = "";
      user.save(function(err, savedDocument) {
        if (err)
          return console.error(err);
      });
    }
  });
  res.send({success: true});
});

router.post('/emailAuthentication',function (req, res, next) {
  var validEmail = 0;
  var duplicatedEmail = 2;
  var authenEmail = req.body.email;

  User.findOne({email : authenEmail},function (err, user) {
    if (user){
      res.send({success: duplicatedEmail});
    }
    else{
      res.send({success: validEmail});
    }
  });
})

router.post('/emailConfirm',function (req, res, next) {
  var validEmail = 0;
  var invalidEmail = 1;
  var userEmail = req.body.email;
  var password = req.body.password;
  var name = req.body.name;
  var phoneNumber = req.body.phoneNumber;

  var todayDate = new Date();
  var deleteTime = new Date(todayDate.getFullYear(), todayDate.getMonth(), todayDate.getDate(), todayDate.getHours(), todayDate.getMinutes()+5, 0);

  passport.authenticate('signup', function (err, user, info) {
    if(err) console.log("signup err : "+err);
    if(user) {}
    else {}
  })(req,res,next);


  let transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
      user: 'ehddlrdk123@ajou.ac.kr',  // gmail 계정 아이디를 입력
      pass: 'wkdehddlr12'          // gmail 계정의 비밀번호를 입력
    }
  });
  let mailOptions = {
    from: 'ehddlrdk123@ajou.ac.kr',
    to: userEmail,
    subject: '안녕하세요, 모두의 달성입니다. 5분 안에 이메일 인증을 해주세요.',
    html: '<p style="font-size: 30px; color: darkblue;">아래의 확인 버튼을 클릭하시면 회원가입이 완료됩니다 !</p>' +
       // " <form action=\"http://54.180.32.212/confirm\" method=\"get\"> " +
     " <form action=\"http://192.168.8.97:3000/confirm\" method=\"get\"> " +
      "  <input type=\"hidden\" name=\"email\" value="+userEmail+" >" +
      "  <input type=\"submit\" value=\"확인\" style=\"color: tomato; font-size: 40px; height:100px; width: 300px;\"> " +
      "</form>"
  };
  transporter.sendMail(mailOptions, function(error, info, response){
    if (error) {
      res.send({success: invalidEmail});
    }
    else {
      var scheduler = schedule.scheduleJob(deleteTime, function(){
        User.findOne({email : userEmail}, function(err, user){
          if(user.emailAuthenticated == 0){
            user.remove(function (err) {
              if(err) console.log(err);
              else {}
            })
          }
        });
      });

      res.send({success: validEmail});
    }
  });
})

router.get('/confirm',function (req,res) {
  var userEmail = req.query.email;
  User.findOne({email : userEmail},function (err,user) {
    if(err) console.log(err);
    if(user != null){
      user.emailAuthenticated = 1;
      mkdirp('./server/user/' + userEmail + '/video', function (err) {
        if (err) console.log("create dir user err : " + err);
        else console.log("create dir ./user/" + userEmail);
      }); //server폴더 아래 /user/useremail/video 폴더가 생김.
      user.save(function (err) {
        if(err) console.log(err);
        else {
          res.send("축하합니다~~ "+userEmail+"님 회원가입에 성공하셨습니다! 모두의 달성에서 로그인 진행해주세요.");
        }
      })
    }
    else{
      res.send("5분이 초과되었습니다.\n 다시 회원가입 절차를 진행해주세요!");
    }
  });
})

router.post('/userPasswordEdit', function(req,res){
  var decoded = jwt.decode(req.body.token, req.app.get("jwtTokenSecret"));
  var userEmail = decoded.userCheck;
  var originPassword = req.body.passwordCurrent;
  var changePassword = req.body.password;

  User.findOne({email: userEmail}, function(err, user) {
    if (err) {
      res.send({success: 2});
      console.log("userInfoEdit err : "+err)
    } else {
      if(! user.validPassword(originPassword)){
        res.send({success: 0});
      }
      else{
        user.password = user.generateHash(changePassword);
        user.save(function (err) {
          if (err) {
            console.log(err);
            res.send({success: 2});
          } else {
            res.send({success: 1});
          }
        })
      }
      // res.send({success: true});
    }
  });
});

router.post('/editProfileWithoutImage', function(req,res){
  var decoded = jwt.decode(req.body.jwt_token, req.app.get("jwtTokenSecret"));
  var userEmail = decoded.userCheck;
  var phoneNumber = req.body.phone_number;
  var userName = decodeURIComponent(req.body.name);

  User.findOne({email: userEmail}, function(err, user){
    var contentListCount = user.contentList.length;
    var contentName ;
    var contentId;
    if(contentListCount == 0){}
    else {
      for (var i = 0; i < contentListCount; i++) {
        contentName = user.contentList[i].contentName;
        contentId = user.contentList[i].contentId;
        Content.findOne({name: contentName, id: contentId}, function (err, content) {
          var userListCount = content.userList.length;
          for (var i = 0; i < userListCount; i++) {
            if (content.userList[i].email === userEmail) {
              content.userList[i].name = userName;
              break;
              }
            }
            content.save(function (err) {
              if(err){
                console.log("err");
              }
              else{}
            })
          });
        }
      }
    if(err){
      res.send({success: false});
      console.log("editProfileWithoutImage err : "+err)
    }else{

      if(user.imagePath == null)
      {
        user.name = userName;
        user.phoneNumber = phoneNumber;

        user.save(function (err) {
          if (err) {
            console.log(err);
            res.send({success: false});
          } else {
            res.send({success: true});
          }
        });
      }
      else {
        fs.rename('./server/user/' + user.email + '/' + user.imagePath + '.jpg', './server/user/' + user.email + '/' + userName + '.jpg', function (err) {
          if (err) console.log('ERROR: ' + err);
          else {}
        });

        user.name = userName;
        user.phoneNumber = phoneNumber;
        user.imagePath = userName;

        user.save(function (err) {
          if (err) {
            console.log(err);
            res.send({success: false});
          } else {
            res.send({success: true});
          }
        });
      }
        }
      });

});

router.post('/editProfileWithImage', function(req,res){

  var decoded = jwt.decode(req.headers.jwt_token, req.app.get("jwtTokenSecret"));
  var userEmail = decoded.userCheck;
  var phoneNumber = req.headers.phone_number;
  var userName = decodeURIComponent(req.headers.name);

  User.findOne({email: userEmail}, function(err, user){
    var contentListCount = user.contentList.length;
    var contentName ;
    var contentId;
    if(contentListCount == 0){}
    else{
      for (var i = 0; i < contentListCount; i++) {
        contentName = user.contentList[i].contentName;
        contentId = user.contentList[i].contentId;
        Content.findOne({name: contentName, id: contentId}, function (err, content) {
          var userListCount = content.userList.length;
          for (var i = 0; i < userListCount; i++) {
            if (content.userList[i].email === userEmail) {
              content.userList[i].name = userName;
              break;
            }
          }
          content.save(function (err) {
            if(err){
              console.log("err");
            }
            else{}
          })
        });
      }
    }
    if(err){
      res.send({success: false});
    }else{
      var form = new multiparty.Form();
      form.on('field', function (name, value) {
      });
      form.on('part', function (part) {
        var filename;
        var size;
        if (part.filename) {
          filename = userName + '.jpg';
          size = part.byteCount;
        } else {
          part.resume();
        }
        var writeStream = fs.createWriteStream('./server/user/' + userEmail + '/' + filename);
        writeStream.filename = filename;
        part.pipe(writeStream);
        part.on('data', function (chunk) {
        });
        part.on('end', function () {
          console.log(filename + ' Part read complete');
          writeStream.end();
        });
      });
      form.on('close', function (err) {
        if (err) {
          console.log("close err : " + err);
          res.send({success: false});
        }
        else {
          user.imagePath = userName;
          user.name = userName;
          user.phoneNumber = phoneNumber;
          user.save(function (err) {
            if (err) {
              console.log(err);
              res.send({success: false});
            } else {
              res.send({success: true});
            }
          });
        }
      });
      // track progress
      form.on('progress', function (byteRead, byteExpected) {
      });
      form.parse(req);
    }
  })
});


router.post('/getUserInfo', function (req,res) {
  var decoded = jwt.decode(req.body.token, req.app.get("jwtTokenSecret"));
  var email = decoded.userCheck;

  User.findOne({email: email}, function(err, info){
    if(err) console.log("getUserInfo err : "+err);
    if(info == null) {}
    else {
      res.send(info);
    }
  })
});

router.post('/getOtherUserInfo', function (req,res) {
  var email = req.body.email;
  User.findOne({email: email}, function(err, info){
    if(err) console.log("getUserInfo err : "+err);
    if(info == null) {}
    else {
      res.send(info);
    }
  })
});

router.get("/pwdSendMail/:email", function(req, res, next){
  let email = req.params.email;

  User.findOne({email: email}, function(err, user) {

    let transporter = nodemailer.createTransport({
      service: 'gmail',
      auth: {
        user: 'ehddlrdk123@ajou.ac.kr',  // gmail 계정 아이디를 입력
        pass: 'wkdehddlr12'          // gmail 계정의 비밀번호를 입력
      }
    });
    let mailOptions = {
      from: 'ehddlrdk123@ajou.ac.kr',
      to: email,
      subject: '안녕하세요, 모두의 달성입니다. 이메일 인증을 해주세요.',
      html: '<p style="font-size: 20px; color: darkblue;">새로운 패스워드를 입력 후 아래의 전송 버튼을 클릭해주세요 !</p>' +
       // " <form action=\"http://54.180.32.212/pwdEmailAuthen\" method=\"get\"> " +
      " <form action=\"http://192.168.8.97:3000/pwdEmailAuthen\" method=\"get\"> " +
      "<label for=\"pwd\">PW</label>" +
      "  <input type=\"password\" name=\"pwd\" placeholder=\"패스워드 입력\"><br/><br/>" +
      "  <input type=\"hidden\" name=\"email\" value="+email+" >" +
      "  <input type=\"submit\" value=\"전송\" style=\"color: tomato; font-size: 20px; height:40px; width: 60px;\"> " +
      "</form>"
    };

    if(err) console.log("error");

    if(user){
      transporter.sendMail(mailOptions, function(error, info){
        if (error) {
          console.log("sendMaiil err : "+error);
          res.send({success: false,  why:1});
        }
        else {
          res.send({success: true});
        }
      });
    }else{
      res.send({success: false, why:0});
    }
  })
})

router.get("/pwdEmailAuthen", function(req, res, next){
  var newPassword = req.query.pwd;
  var userEmail = req.query.email;
  User.findOne({email: userEmail}, function(err, user) {
    if (err) {
      console.log("pwdEmailAuthen err : "+err);
    }
    else{
      user.password = user.generateHash(newPassword);
      user.save(function (err) {
        if (err) {console.log(err);}
        else {}
      })
      }
  });
  res.send("비밀번호 변경에 성공하셨습니다!!");
});

// -----------------------------------------------------
// WEB 관리자 페이지 용

router.post('/managerlogin', function(req, res, next) {
  passport.authenticate('login-manager', function(err, user, info) {
    if (err) { return next(err); }

    if (!user) { return res.json({access:false, why: 0});} // 로그인 실패 사용자 없음
    if(user == 1){ // password 불일치
      return res.json({access:false, why: 1});
    }
    if(user == 2){
      return res.json({access:false, why: 2});
    }

    // 로그인 성공 시 세션에 사용자 이메일 저장
    sess = req.session;
    sess.userCheck = req.body.email;

    return res.json({access:true}); // 로그인 성공
  })(req, res, next);
});

router.post('/managerSignUp', function (req, res, next) {
  passport.authenticate('signup-manager', function (err, user, info) {
    if(err) console.log("signup err : "+err);

    if(user == 0){
      res.send({success: false, why: 0});
    }
    else if(user == 1){
      res.send({success: false, why: 1});
    }
    else if(user) {
      res.send({success: true});
      var userEmail = req.body.email;
      mkdirp('./server/user/'+userEmail+'/video', function (err) {
        if(err) console.log("create dir user err : "+err);
        else console.log("create dir ./user/" +userEmail );
      }); //server폴더 아래 /user/useremail/video 폴더가 생김.
    } else {
      res.send({success: false, why: 2});
    }
  })(req,res,next);
});
router.get('/managerLogout', function(req, res) { // 로그아웃
  req.session.destroy(); // 세션 삭제
  res.send({});
});
router.get('/sessionCheck', function(req, res) { // 세션체크
  res.send({userSess: req.session.userCheck}); // 첫 화면에서 사용자의 로그인 여부
});

router.get('/getManagerInfo', function(req, res) { // 유저 정보 (로그인 시)
  if(req.session.userCheck == undefined) { // 사용자 세션 체크, 세션 없으면 오류페이지
    res.send({error:true});
  } else {
    User.findOne({email: req.session.userCheck}, {password: 0}, function (err, user) { // 웹 페이지 좌측에 나타나는 사용자 정보를 불러오는
      if (err) throw err;
      res.send(user);
    });
  }
});
router.post('/managerInfoEdit', function(req, res) {
  if(req.session.userCheck == undefined) { // 사용자 세션 체크, 세션 없으면 오류페이지
    res.send({error:true});
  } else {

    User.findOne({email: req.session.userCheck}, function (err, dupuser) {
      if (err) throw err;

      if (dupuser) {
        User.findOne({email: req.body.email}, function (err, user) {
          if (err) throw err;
          if (req.session.userCheck == req.body.email) {
            user = null;
          } else { }

          if (!user) {
            if (req.body.imagePath == undefined) {
              User.findOneAndUpdate({email: req.session.userCheck},
                {
                  name: req.body.name,
                  email: req.body.email,
                  phoneNumber: req.body.phoneNumber
                }, function (err, userUpdate) {
                  if (err) throw err;
                  res.send({success: true});
                });
            } else {
              User.findOneAndUpdate({email: req.session.userCheck},
                {
                  name: req.body.name,
                  email: req.body.email,
                  phoneNumber: req.body.phoneNumber,
                  imagePath: req.body.name
                  // userImage: req.body.userImagePath.replace('\\', '/')
                }, function (err, userUpdate) {
                  if (err) throw err;
                  res.send({success: true});
                });
            }
          } else {
            res.send({success: false});
          }
        });
      } else { }
    });
  }
});
router.post('/managerPasswordEdit', function(req,res){

  User.findOne({email: req.session.userCheck}, function(err, user) {
    if (err) {
      res.send({success: 2});
      console.log("userInfoEdit err : "+err)
    } else {
      if(! user.validPassword(req.body.currentPassword)){
        res.send({success: 0});
      }
      else{
        user.password = user.generateHash(req.body.newPassword);
        user.save(function (err) {
          if (err) {
            console.log(err);
            res.send({success: 2});
          } else {
            res.send({success: 1}); // 성공

          }
        })
      }
    }
  });
});
router.get('/authorityCheck', function(req, res) { // 사용자 권한 체크
  // 접근이 가능한 사용자는 {error:false}
  // 접근이 불가능한 사용자는 {error:true} 반환
  // {error:true}를 받은 클라이언트는 에러 페이지 출력

  if(req.session.userCheck == undefined) { // 사용자 세션 체크, 세션 없으면 오류페이지
    res.send({error:true});
  } else {
    User.findOne({email: req.session.userCheck}, function (err, user) { // 세션을 통해 사용자 정보 불러오기
      if (err) throw err;

      var authority = JSON.parse(JSON.stringify(user)).authority; // 접근한 사용자의 권한

      if(authority == 'manager'){
        res.send({error:false});
      } else { // 위에서 정의되지 않은 페이지에 접근할 경우(URL 변경 등)
        res.send({error:true});
      }
    });
  }
});

// -----------------------------------------------------

module.exports = router;
