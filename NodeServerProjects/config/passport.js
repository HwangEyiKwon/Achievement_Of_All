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
      User.findOne({ email : email }, function(err, user) {
        if (err) {
          return done(err);
        }
        if (!user){
          return done(null, false);
        }
        if(! user.validPassword(password)){
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
          return done(null, false);
        }
        else {
          var newUser = new User();
          newUser.name = req.body.name;
          newUser.email = email;
          newUser.authority = "user";
          newUser.password = newUser.generateHash(password);
          newUser.phoneNumber = req.body.phoneNumber;;
          newUser.contentList = [];
          newUser.emailAuthenticated = 0;
          newUser.fcmFailure = 0;
          newUser.fcmVideoFailureFlag = 0;
          newUser.fcmReportAcceptFlag = 0;
          newUser.fcmReportRejectFlag = 0;
          //newUser.password = password;

          newUser.save(function(err) {
            if (err) throw err;

            return done(null, newUser);
          });
        }
      });
    }));

  passport.use('login-manager', new LocalStrategy({

      usernameField : 'email',
      passwordField : 'password',
      passReqToCallback : true
    },
    function(req, email, password, done) {
      User.findOne({ email : email }, function(err, user) {
        if (err) {
          return done(err);
        }
        if (!user){
          return done(null, 0);
        }
        if(! user.validPassword(password)){
          return done(null,1);
        }

        if(user.authority == "user"){
          return done(null,2);
        }else if (user.authority == "manager"){
          return done(null,user);
        }
      });
    }));

  passport.use('signup-manager', new LocalStrategy({
      usernameField : 'email',
      passwordField : 'password',
      passReqToCallback : true
    },
    function(req, email, password, done) {
      User.findOne({ email : email }, function(err, user) {

        if (err) return done(err);

        if (user) {
          return done(null, 0);
        }
        else {

          if(req.body.managerKey ==  req.app.get("managerKey")){
            var newUser = new User();
            newUser.name = req.body.name;
            newUser.email = email;
            newUser.authority = "manager";
            newUser.password = newUser.generateHash(password);
            newUser.phoneNumber = req.body.phoneNumber;;
            newUser.contentList = [];

            newUser.save(function(err) {
              if (err) throw err;

              return done(null, newUser);
            });


          }else{
            return done(null, 1);
          }
        }
      });
    }));
  passport.use('add-newuser', new LocalStrategy({
      usernameField : 'email',
      passwordField : 'password',
      passReqToCallback : true
    },
    function(req, email, password, done) {
      User.findOne({ email : email }, function(err, user) {

        if (err) return done(err);

        if (user) {
          return done(null, 0);
        }
      else {
          var newUser = new User();
          newUser.name = req.body.name;
          newUser.email = email;
          newUser.authority = req.body.authority;
          newUser.password = newUser.generateHash(password);
          newUser.phoneNumber = req.body.phoneNumber;;
          newUser.contentList = [];

          if(req.body.imageChange == 0){
            newUser.imagePath = req.body.name;
          }

          newUser.save(function(err) {
            if (err) throw err;
            return done(null, newUser);
          });
        }
      });
    }));
};
