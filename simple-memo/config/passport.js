var LocalStrategy = require('passport-local').Strategy
var User = require('../server/models/user');


module.exports = function(passport) {
  passport.serializeUser(function(user, done) {
    done(null, user.id);
  });
  passport.deserializeUser(function(id, done) {
    User.findById(id, function(err, user) {
      done(err, user);
    });
  });


  passport.use('signup', new LocalStrategy({
      usernameField : 'email',
      passwordField : 'password',
      passReqToCallback : true
    },
    function(req, email, password, done) {
      User.findOne({ 'email' : email }, function(err, user) {
        if (err) return done(err);
        if (user) {
          console.log(req.flash());
          console.log('이메일 존재');
          return done(null, false, req.flash('signupMessage', '이메일이 존재합니다.'));
        } else {
          console.log('회원가입 성공');
          var newUser = new User();
          newUser.name = req.body.name;
          newUser.email = email;
          //newUser.password = newUser.generateHash(password);
          newUser.password = password;
          newUser.save(function(err) {
            if (err)
              throw err;
            return done(null, newUser);
          });
        }
      });
    }));

  passport.use('login', new LocalStrategy({
      usernameField : 'email',
      passwordField : 'password',
      passReqToCallback : true
    },
    function(req, email, password, done) {
      User.findOne({ 'email' : email }, function(err, user) {
        if (err) {
          return done(err);
        }
        if (!user){
          console.log('사용자 없음');
          return done(null, false, req.flash('loginMessage', '사용자를 찾을 수 없습니다.'));
        }
        return user.validPassword(password, (passError, isMatch) => {

          if(isMatch) {console.log('로그인 성공'); return done(null, user);}
          console.log('패스워드 불일치');
          return done(null, false, req.flash('loginMessage', '비밀번호가 다릅니다.'));
        });
        /*        if (!user.validPassword(password, (passError, isMatch))){
                  console.log('패스워드 불일치');
                  return done(null, false, req.flash('loginMessage', '비밀번호가 다릅니다.'));
                }
                console.log('로그인 성공');
                return done(null, user);*/
      });
    }));
};




// const passport = require('passport');
// const LocalStrategy = require('passport-local').Strategy;
// const Users = require('../server/models/user');
// //
// // module.exports = () => {
// //   passport.serializeUser(function(useremail, done){ // Strategy 성공 시 호출됨
// //     console.log('passport serializeUser call');
// //     done(null, user); // 여기의 user가 deserializeUser의 첫 번째 매개변수로 이동
// //   });
// //
// //   passport.deserializeUser(function(user, done) { // 매개변수 user는 serializeUser의 done의 인자 user를 받은 것
// //     console.log('passport deserializeUser call');
// //     done(null, user); // 여기의 user가 req.user가 됨
// //   });
//
// module.exports = function (passport) {
//
//   console.log("55");
//   passport.serializeUser(function(user, done) {
//     console.log('seriallizeUser ',user);
//     done(null, user.email);
//     // done(null,user)가 실행이 되면 이 시리얼라이즈유저로 와서 콜백함수를 실행
//     //여기에 세션이 저장//user.email이게 세션에 저장됨.
//   });
//
//   passport.deserializeUser(function (id, done) {
//     console.log('deserializeUser', id);
//     Users.findById(email, function (err, user) {
//       console.log("3");
//      return done(err, user);
//     })
//     //세션정보를 가지고 있는 유저가 들어올때 이 id값으로 이 유저가 어떤유전지 찾는거임.
//   });
//
//   passport.use('local', new LocalStrategy({
//       userenameField: 'email',
//       passwordField: 'password',
//
//       passReqToCallback: true
//
//     }, function(req, email, password, done) {
//     console.log("4");
//       Users.findOne({email: email}, function(err, user) {
//         if (err) {
//           console.log("5");
//           return done(err);
//
//
//         }
//         console.log("99");
//         if (!user) {
//           console.log("6");
//           return done(null, false, null);
//         }
//         //if (!user.comparePassword(password)){
//          // console.log("7");
//          // return done(null, false, null);
//         //}
//         console.log("8");
//          return done(null, user);
//       });
//     }
//   ));
//
//
//
//
//
//
//   //
//   // passport.use(new LocalStrategy(function (useremail, password, done) {
//   //   process.nextTick(function () {
//   //     console.log('login local strtegy check ['+useremail+'],[' +password+']');
//   //
//   //     user.findOne({email: useremail, password: password},function (err, info) {
//   //   //findOne --> 알아서 디비에서 찾는것
//   //   if(err){
//   //     console.log(err);
//   //     }
//   //   //console.log(info);
//   //   //info는 JSON 형태로 나옴
//   //   if(info==null){
//   //
//   //     console.log("res send login fail");
//   //     res.send({success: false});//승현이한테 보내는 것 실패했다
//   //     return done(false,null);
//   //   }
//   //   else {
//   //
//   //     console.log("res send login success");
//   //     res.send({success: true});
//   //     return done(null,useremail);
//   //   } // 성공했다
//   // })
//   //     return done(false,null);
//   //
//   //     //if(useremail == )
//   //
//   //   });
//   //
//   // }));
//
//
//
//
//
//   // passport.use(new LocalStrategy({            // local 전략을 세움
//   //   useremailField: 'email',
//   //   passwordField: 'pw',
//   //   session: true, // 세션에 저장 여부
//   //   passReqToCallback: false,
//   // }, (email, password, done) => {
//   //   Users.findOne({ email: email }, (findError, user) => {
//   //     if (findError) {
//   //       return done(findError);
//   //     } // 서버 에러 처리
//   //     if (!user){
//   //       return done(null, false, { message: '존재하지 않는 아이디입니다' });
//   //     } // 임의 에러 처리
//   //
//   //
//   //     return user.comparePassword(password, (passError, isMatch) => {
//   //       if (isMatch) {
//   //         return done(null, user); // 검증 성공
//   //       }
//   //       return done(null, false, { message: '비밀번호가 틀렸습니다' }); // 임의 에러 처리
//   //     });
//   //   });
//   // }));
// };
