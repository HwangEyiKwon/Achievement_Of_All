const express = require('express');
const router = express.Router();
var User = require('../models/user');
var Content = require('../models/content');
var Report = require('../models/report');
var passport = require('passport');
var jwt = require('jwt-simple'); // jwt token 사용
const passportConfig = require('../../config/passport');
var mkdirp = require('mkdirp'); // directory 만드는것

router.get('/getSearchUserData', function (req,res) {
  console.log("getSearchUserData Start");
  User.find(function(err, userList){
    var searchData = new Array();

    for(var i = 0; i < Object.keys(userList).length; i++){
      searchData.push({email: userList[i].email, name: userList[i].name});
    }
    res.send({users: searchData});
  });
});

router.get('/getSearchContentData', function (err, res ) {
  console.log("getSearchContentData Start ");
  Content.collection.distinct("name", function(err, results){
    if(err)  console.log(err);
    else{
      console.log("Content List : " +results);
      res.send({contents: results});
    }
  });
});
// -----------------------------------------------------
// WEB 관리자 페이지 용

router.get('/getManagersInfo', function(req, res) {
  if(req.session.userCheck == undefined) { // 사용자 세션 체크, 세션 없으면 오류페이지
    res.send({error:true});
  } else {
    User.findOne({email: req.session.userCheck}, function (err, user) {
      if (err) throw err;

      if (JSON.parse(JSON.stringify(user)).authority == 'manager') {
        User.find({}, function (err, user) {
          res.send(user);
        });
      } else {  }
    });
  }
});
router.post('/deleteManagerInfo', function(req, res) {
  if(req.session.userCheck == undefined) { // 사용자 세션 체크, 세션 없으면 오류페이지
    res.send({error:true});
  } else {
    User.findOne({email: req.session.userCheck}, {password: 0}, function (err, user) {
      if (err) throw err;

      if (JSON.parse(JSON.stringify(user)).authority == 'manager') {
        User.remove({name: req.body.name, email: req.body.email}, function (err, result) {
          if (err) throw err;
          console.log('delete success');
          res.send({});
        });
      } else { }
    });
  }
});
router.post('/updateManagerInfo', function(req, res) {
  if(req.session.userCheck == undefined) { // 사용자 세션 체크, 세션 없으면 오류페이지
    res.send({error:true});
  } else {
    User.findOne({email: req.session.userCheck}, function (err, user) {
      if (err) throw err;


      console.log(req.body);

      if (JSON.parse(JSON.stringify(user)).authority == 'manager') {

        User.findOne({email: req.body.userInfo.email}, function (err, ur) {

          console.log(ur);
          console.log("vzcvzxcvzxcvzxcvzxcvzxcvz");

          if (ur.password == req.body.userNewInfo.password) {

            if(req.body.imageChange == 0){

              User.findOneAndUpdate({email: req.body.userInfo.email},
                {
                  name: req.body.userNewInfo.name,
                  email: req.body.userNewInfo.email,
                  phoneNumber: req.body.userNewInfo.phoneNumber,
                  authority: req.body.userNewInfo.authority,
                  imagePath: req.body.userNewInfo.name
                },
                function (err, userUpdate) {
                  if (err) throw err;
                  console.log(userUpdate);
                  console.log('update success with image change');
                  res.send({success:true});
                });

            }else{

              User.findOneAndUpdate({email: req.body.userInfo.email},
                {
                  name: req.body.userNewInfo.name,
                  email: req.body.userNewInfo.email,
                  phoneNumber: req.body.userNewInfo.phoneNumber,
                  authority: req.body.userNewInfo.authority,
                },
                function (err, userUpdate) {
                  if (err) throw err;
                  console.log(userUpdate);
                  console.log('update success without image change');
                  res.send({success:true});
                });

            }

          } else {

            if(req.body.imageChange == 0){
              User.findOneAndUpdate({email: req.body.userInfo.email},
                {
                  name: req.body.userNewInfo.name,
                  email: req.body.userNewInfo.email,
                  phoneNumber: req.body.userNewInfo.phoneNumber,
                  authority: req.body.userNewInfo.authority,
                  imagePath: req.body.userNewInfo.name,
                  password: user.generateHash(req.body.userNewInfo.password)
                },
                function (err, userUpdate) {
                  if (err) throw err;
                  console.log(userUpdate);
                  console.log('update success with password  with image change');
                  res.send({success:true});
                });
            }else{
              User.findOneAndUpdate({email: req.body.userInfo.email},
                {
                  name: req.body.userNewInfo.name,
                  email: req.body.userNewInfo.email,
                  phoneNumber: req.body.userNewInfo.phoneNumber,
                  authority: req.body.userNewInfo.authority,
                  // imagePath: req.body.userNewInfo.image,
                  password: user.generateHash(req.body.userNewInfo.password)
                },
                function (err, userUpdate) {
                  if (err) throw err;
                  console.log(userUpdate);
                  console.log('update success with password  without image change');
                  res.send({success:true});
                });
            }

          }
        })

      } else { }
    });
  }
});
router.post('/addManagerInfo', function(req, res, next) {
  var ch = req.body.imageChange;
  req.body = req.body.userNewInfo;
  req.body.imageChange = ch;

  console.log("매니저가 유저 생");
  console.log(req.body);

  passport.authenticate('add-newuser', function(err, user, info) {
    if (err) { return next(err); }
    if (!user) { res.send({success:false}); }
    else {

      if(ch == 2){
        mkdirp('./server/user/'+req.body.email+'/video', function (err) {
          if(err) console.log("create dir user err : "+err);
          else console.log("create dir ./user/" + req.body.email );
        }); //server폴더 아래 /user/useremail/video 폴더가 생김.
      }
      res.send({success:true}); }
  })(req, res, next);
});
router.get('/getContentsInfo', function(req, res) {
  if(req.session.userCheck == undefined) { // 사용자 세션 체크, 세션 없으면 오류페이지
    res.send({error:true});
  } else {
    User.findOne({email: req.session.userCheck}, function (err, user) {
      if (err) throw err;

      if (JSON.parse(JSON.stringify(user)).authority == 'manager') {
        Content.find({}, function (err, content) {
          res.send(content);
        });
      } else {  }
    });
  }
});

router.post('/deleteContentInfo', function(req, res) {
  if(req.session.userCheck == undefined) { // 사용자 세션 체크, 세션 없으면 오류페이지
    res.send({error:true});
  } else {
    User.findOne({email: req.session.userCheck}, {password: 0}, function (err, user) {
      if (err) throw err;

      if (JSON.parse(JSON.stringify(user)).authority == 'manager') {

        Content.remove({name: req.body.name, id: req.body.id}, function (err, result) {
          if (err) throw err;
          console.log('delete success');
          res.send({});
        });
      } else { }
    });
  }
});
router.post('/addContentInfo', function(req, res, next) {
  req.body = req.body.contentNewInfo;
  console.log("SERVERSERVER");
  console.log(req.body);
  // new Date(req.body.startDate.month, todayDate.getMonth(), todayDate.getDate(), 0, 0, 0);

  var startDate = new Date(req.body.startDate.year, req.body.startDate.month, req.body.startDate.day, 0, 0, 0);
  var endDate = new Date(req.body.endDate.year,req.body.endDate.month, req.body.endDate.day, 0, 0, 0);


  Content.findOne({id: req.body.id}, function (err, content) {
    if(err){

    }
    if(content){
      res.send({success:1});
    }
    else{
      var content = new Content({
        id: req.body.id,
        name: req.body.name,
        startDate: startDate,
        endDate: endDate,
        isDone: 2,
        userList: [],
        description: req.body.description,
        balance: req.body.balance
      })

      content.save(function(err, savedDocument) {
        if (err){
          res.send({success:2});

        }
        console.log(savedDocument);
        console.log("DB initialization");
        res.send({success:0});
      });
    }
  });
});
router.get('/getReportsInfo', function(req, res) {
  console.log("getReportsInfo");
  if(req.session.userCheck == undefined) { // 사용자 세션 체크, 세션 없으면 오류페이지
    res.send({error:true});
  } else {
    console.log("리포트리포트");
    User.findOne({email: req.session.userCheck}, function (err, user) {
      if (err) throw err;

      if (JSON.parse(JSON.stringify(user)).authority == 'manager') {
        Report.find({}, function (err, content) {
          res.send(content);
        });
      } else {  }
    });
  }
});
// -----------------------------------------------------
// -----------------------------------------------------
module.exports = router ;

