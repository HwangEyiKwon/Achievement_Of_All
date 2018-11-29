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


router.post('/jwtCheck', function(req, res){
  console.log("jwtCheck Start");
  // console.log("jwtCheck jwt토큰 "+ req.body.token);
  var decoded = jwt.decode(req.body.token,req.app.get("jwtTokenSecret"));
  // console.log("jwtCheck jwt토큰 디코딩 "+ decoded.userCheck);
  var email = decoded.userCheck;

  User.findOne({ email : email }, function(err, user) {
    // console.log(user);
    if(err){
      console.log("jwtCheck err : "+err);
      res.send({success: false});
    }
    res.send({success: true});
  });

});
router.post('/login', function(req,res,next){
  console.log("login Start");
  passport.authenticate('login', function (err, user, info) {

    if(err) console.log("login err : "+err);
    if(user){
      // var expires = moment().add('days', 7).valueOf();

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
  console.log("logout Start");
  // console.log("logout jwt토큰 "+ req.body.token);
  var decoded = jwt.decode(req.body.token,req.app.get("jwtTokenSecret"));
  // console.log("logout jwt토큰 디코딩 "+ decoded.userCheck);
  var email = decoded.userCheck;

  User.findOne({ email : email }, function(err, user) {
    // console.log(user);
    if(err){
      console.log("logout err : "+err);
      res.send({success: false});
    }
    user.pushToken = null;
    console.log('user token is ='+ user.pushToken + '!!');


  });
  res.send({success: true});
});

router.post('/signup', function (req, res, next) {
  console.log("signup Start");
  passport.authenticate('signup', function (err, user, info) {
    // console.log(user+"s");
    console.log("signUPPPPPPPP");
    if(err) console.log("signup err : "+err);
    if(user) {
      res.send({success: true});
      var userEmail = req.body.email;

      mkdirp('./server/user/'+userEmail+'/video', function (err) {
        if(err) console.log("create dir user err : "+err);
        else console.log("create dir ./user/" +userEmail );
      }); //server폴더 아래 /user/useremail/video 폴더가 생김.

      //아래 코드는 두 폴더를 만들어버리는 코드.
      // mkdirp('./server/user/'+pathName+'/video/Diet', function (err) {
      //   if(err) console.log(err);
      //   else console.log("create dir ./user/" +pathName );
      // });
      // mkdirp('./server/user/'+pathName+'/video/NoSmoking', function (err) {
      //   if(err) console.log(err);
      //   else console.log("create dir ./user/" +pathName );
      // });
    }
    else res.send({success: false});
  })(req,res,next);
});

router.post('/userPasswordEdit', function(req,res){
  console.log("userPasswordEdit Start");

  var decoded = jwt.decode(req.body.token, req.app.get("jwtTokenSecret"));
  var userEmail = decoded.userCheck;
  var originPassword = req.body.passwordCurrent;
  var changePassword = req.body.password;

  User.findOne({email: userEmail}, function(err, user) {
    if (err) {
      res.send({success: 2});
      console.log("userInfoEdit err")
    } else {
      console.log("user pwd hash: "+ user.password);
      if(! user.validPassword(originPassword)){
        res.send({success: 0});
        console.log("origin password is not correct")
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
// router.post('/editUserImage', function(req,res) {
//   console.log("edit User Image start!!");
//   // var decoded = jwt.decode(req.body.token, req.app.get("jwtTokenSecret"));
//   // var userEmail = decoded.userCheck;
//   var decoded = jwt.decode(req.headers.token, req.app.get("jwtTokenSecret"));
//   var userEmail = decoded.userCheck;
//
//   User.findOne({email: userEmail}, function(err, user) {
//     var userName = user.name;
//     var form = new multiparty.Form();
//     form.on('field', function (name, value) {
//       console.log('normal field / name = ' + name + ' , value = ' + value);
//     });
//     form.on('part', function (part) {
//       var filename;
//       var size;
//       if (part.filename) {
//         // filename = part.filename;
//         filename = userName + '.jpg';
//         size = part.byteCount;
//       } else {
//         part.resume();
//       }
//       console.log("Write Streaming file :" + filename);
//       var writeStream = fs.createWriteStream('./server/user/' + userEmail + '/' + filename);
//       writeStream.filename = filename;
//       part.pipe(writeStream);
//       part.on('data', function (chunk) {
//         console.log(filename + ' read ' + chunk.length + 'bytes');
//       });
//       part.on('end', function () {
//         console.log(filename + ' Part read complete');
//         writeStream.end();
//       });
//     });
//     form.on('close', function (err) {
//       if (err) {
//         console.log("close err : " + err);
//         res.send({success: false});
//       }
//       else {
//         console.log("Edit Image success");
//         user.imagePath = userName;
//         user.save(function (err) {
//           if (err) {
//             console.log(err);
//             res.send({success: false});
//           } else {
//             console.log("ImagePath modi Success ");
//           }
//         });
//       }
//     });
//     // track progress
//     form.on('progress', function (byteRead, byteExpected) {
//       console.log(' Reading total  ' + byteRead + '/' + byteExpected);
//     });
//     form.parse(req);
//   });
//
// });
//jwt token 사용
router.post('/userInfoEdit', function(req,res){
  console.log("userInfoEdit Start");

  console.log(req.headers);
  // console.log("userInfoEdit token : "+req.headers.jwt_token);
  // console.log("userInfoEdit number: "+req.headers.phoneNumber);
  // console.log("userInfoEdit userName: "+req.headers.name);
  var decoded = jwt.decode(req.headers.jwt_token, req.app.get("jwtTokenSecret"));
  var userEmail = decoded.userCheck;
  var phoneNumber = req.headers.phone_number;
  var userName = req.headers.name;

  console.log("edit EMAIL" + userEmail);
  console.log("edit phoneNumber" + phoneNumber);
  console.log("edit name" + userName);

  User.findOne({email: userEmail}, function(err, user){
    if(err){
      res.send({success: false});
      console.log("userInfoEdit err")
    }else{
      var form = new multiparty.Form();
      form.on('field', function (name, value) {
        console.log('normal field / name = ' + name + ' , value = ' + value);
      });
      form.on('part', function (part) {
        var filename;
        var size;
        if (part.filename) {
          // filename = part.filename;
          filename = userName + '.jpg';
          size = part.byteCount;
        } else {
          part.resume();
        }
        console.log("Write Streaming file :" + filename);
        var writeStream = fs.createWriteStream('./server/user/' + userEmail + '/' + filename);
        writeStream.filename = filename;
        part.pipe(writeStream);
        part.on('data', function (chunk) {
          // console.log(filename + ' read ' + chunk.length + 'bytes');
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
          console.log("Edit Image success");
          user.imagePath = userName;
          user.name = userName;
          user.phoneNumber = phoneNumber;
          user.save(function (err) {
            if (err) {
              console.log(err);
              res.send({success: false});
            } else {
              console.log("ImagePath modi Success ");
              console.log("userInfoEdit Success ");
                  res.send({success: true});
            }
          });
        }
      });
      // track progress
      form.on('progress', function (byteRead, byteExpected) {
        // console.log(' Reading total  ' + byteRead + '/' + byteExpected);
      });
      form.parse(req);

      // user.name = userName;
      // user.phoneNumber = phoneNumber;
      // user.save(function (err) {
      //   if(err) {
      //     console.log(err);
      //     res.send({success: false});
      //   }else{
      //     console.log("userInfoEdit Success ");
      //     res.send({success: true});
      //   }
      // })
      // res.send({success: true});
    }
  })
});

router.post('/getUserInfo', function (req,res) {
  console.log("getUserInfo Start");
  // console.log("get User Info: "+JSON.stringify(req.body));
  // console.log("받은 jwt토큰 "+ req.body.token);
  var decoded = jwt.decode(req.body.token, req.app.get("jwtTokenSecret"));
  // console.log("받은 jwt토큰 디코딩 "+ decoded.userCheck);
  var email = decoded.userCheck;
  // console.log(email);
  User.findOne({email: email}, function(err, info){
    if(err) console.log("getUserInfo err : "+err);
    if(info == null) {
      console.log("사용자 아님");
    }
    else {
      console.log("사용자 찾음");
      res.send(info);
    }
  })
});

router.post('/getOtherUserInfo', function (req,res) {
  console.log("getUserInfo Start");
  // console.log("get User Info: "+JSON.stringify(req.body));
  var email = req.body.email;
  // console.log(email);
  User.findOne({email: email}, function(err, info){
    if(err) console.log("getUserInfo err : "+err);
    if(info == null) {
      console.log("사용자 아님");
    }
    else {
      console.log("사용자 찾음");
      res.send(info);
    }
  })
});

router.get("/pwdSendMail/:email", function(req, res, next){
  console.log("pwdSendMail Start");
  let email = req.params.email;
  // let email = "hwangeyikwon@gmail.com";

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
    html: '<p>새로운 패스워드를 입력 후 아래의 전송 버튼을 클릭해주세요 !</p>' +
      " <form action=\"http://localhost:3000/pwdEmailAuthen\" method=\"post\"> " +
      "<label for=\"pwd\">PW</label>" +
      "  <input type=\"password\" name=\"pwd\" placeholder=\"패스워드 입력\"><br/><br/>" +
      "  <input type=\"hidden\" name=\"email\" value="+email+" >" +
      "  <input type=\"submit\" value=\"전송\"> " +
      "</form>"
  };

  transporter.sendMail(mailOptions, function(error, info){
    if (error) {
      console.log(error);
      res.send({success: false});
    }
    else {
      console.log('Email sent: ' + info.response);
      res.send({success: true});
    }
  });
})

router.post("/pwdEmailAuthen", function(req, res, next){
  console.log("pwdEmailAuthen Start ");
  // console.log(req.body.pwd);
  var newPassword = req.body.pwd;
  var userEmail = req.body.email;
  User.findOne({email: userEmail}, function(err, user) {
    if (err) {
      console.log("pwdEmailAuthen err : "+err);
    }
    else{
      user.password = user.generateHash(newPassword);
      user.save(function (err) {
        if (err) {console.log(err);}
        else console.log("newPassword Change");
      })
      }
  });

});

router.get("/isParticipated/:jwtToken/:contentName", function(req,res) {
  console.log("isParticipated Start");
  var decoded = jwt.decode(req.params.jwtToken,req.app.get("jwtTokenSecret"));
  // console.log("isParticipated jwt토큰 디코딩 "+ decoded.userCheck);
  var userEmail = decoded.userCheck;

  var contentName = req.params.contentName;

  var joinState;
  var startDate;
  var endDate;
  User.findOne({ email : userEmail , "contentList.contentName": contentName}, function(err, user) {
    if(err){
      console.log("isparticipated err : "+err);
      res.send({success: false});
    }
    else{
      if(user == null) res.send({joinState: 3, startDate: {year: -1, month: -1, day: -1},  endDate: {year: -1, month: -1, day: -1}});
      else  {
        var contentIndex;
        var joinContentCount = user.contentList.length;
        var contentId;

        for (var i = 0; i < joinContentCount; i++) {
          if (user.contentList[i].contentName === contentName) {
            contentIndex = i;
            break;
          }
        }
        joinState = user.contentList[contentIndex].joinState;
        contentId = user.contentList[contentIndex].contentId;

        Content.findOne({name: contentName, id: contentId}, function(err, content){
          startDate = content.startDate;
          endDate = content.endDate;

          res.send({joinState: joinState, startDate: {year: startDate.getFullYear(), month: startDate.getMonth() + 1, day: startDate.getDate()},
            endDate: {year: endDate.getFullYear(), month: endDate.getMonth() + 1, day: endDate.getDate()}});
        });
      }
    }
  });
});

module.exports = router;
