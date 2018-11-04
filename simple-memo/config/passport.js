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


  passport.use('login', new LocalStrategy({
      usernameField : 'email',
      passwordField : 'password',
      passReqToCallback : true
    },
    function(req, email, password, done) {
      console.log("passlogin");
      User.findOne({ email : email }, function(err, user) {
        if (err) {
          return done(err);
        }
        if (!user){
          console.log('사용자 없음');
          return done(null, false);
        }
        if(! user.validPassword(password)){
          console.log("패스워드 불일치");
          return done(null,false,null);
        }
        return done(null,user);
      });
    }));

  passport.use('signup', new LocalStrategy({
      usernameField : 'email',
      passwordField : 'password',
      passReqToCallback : true
    },
    function(req, email, password, done) {
      User.findOne({ email : email }, function(err, user) {

        if (err) return done(err);

        if (user) {
          console.log('이메일 존재');

          return done(null, false);

        }
        else {
          console.log('회원가입 성공');
          var newUser = new User();
          newUser.name = req.body.name;
          newUser.email = email;
          newUser.password = newUser.generateHash(password);
          //newUser.password = password;

          newUser.save(function(err) {
            if (err) throw err;

            return done(null, newUser);
          });
        }
      });
    }));
};
