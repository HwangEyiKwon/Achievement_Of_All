// UserPage.component

// 사용자 페이지
// 사용자 페이지의 큰틀을 구성하는 컴포넌트다.
// 좌측의 메뉴 버튼에 따라 여러 자식 컴포넌트를 불러온다. (app.routing.module 참고)

import {Component, OnDestroy, OnInit} from '@angular/core';
import {HttpService} from './http-service';
import {Router} from '@angular/router';
import {DataService} from './data.service';
import * as myGlobals from './global.service'; // 글로벌 변수를 주입하기 위한 서비스

@Component({
  selector: 'app-user-page',
  templateUrl: './userPage.component.html',
  styleUrls: ['./userPage.component.css']
})
export class UserPageComponent implements OnInit, OnDestroy {

  imageURI: string;
  imagePath = myGlobals.serverPath; // 이미지 경로
  menuState: string = 'out';

  userInfo = { // 사용자 정보 변수
    email: '',
    name: '',
    authority: '',
    phoneNumber: '',
  }

  menu = [];

  // subscribe를 위한 옵저버 선언
  myObserver = null;
  myObserver_sess = null;
  myObserver_logOut = null;

  constructor(
    private httpService: HttpService,
    private router: Router,
    private dataService: DataService
  ) {}

  ngOnInit() {

    // Session Check
    // 처음 로그인 페이지를 들어가면 세션 정보를 확인한다.
    // 만약 세션이 존재할 경우 로그인 진행 없이 사용자 페이지로 이동하고
    // 세션이 존재하지 않을 경우 로그인 페이지로 이동한다.

    this.myObserver_sess = this.httpService.sessionCheck().subscribe(result => {
      // 세션에 사용자 정보가 남아 있을 경우
      if( JSON.parse(JSON.stringify(result)).userSess !== undefined ){

        console.log('Session: ' + JSON.stringify(result));
        // HTTP 통신을 통해 현재 세션에 남은 사용자의 모든 정보를 불러온다.

        this.myObserver = this.httpService.getUserInfo().subscribe(result2 => { // Session을 통해 정보 불러옴

          // 서버/데이터베이스에서 가져온 사용자 정보를 모두 변수에 담는다.
          var email = JSON.parse(JSON.stringify(result2)).email;
          var name = JSON.parse(JSON.stringify(result2)).name;
          var authority = JSON.parse(JSON.stringify(result2)).authority;
          var phoneNumber= JSON.parse(JSON.stringify(result2)).phoneNumber;

          this.imageURI = this.imagePath + '/getManagerImage/' + email +'?'+ new Date().getTime();

          // 변수들에 불러온 값들을 대입하는 함수
          this.setupUserInfo(email, name , authority, phoneNumber );
          // 메뉴 세팅 함수
          this.setupMenu();
          // 다른 컴포넌트에 데이터를 넣기 위해 현재 사용자의 모든 정보를 넣는다.
          this.updateUserInfo(this.userInfo);

        });
      }
      else {
        // 세션에 아무 정보도 없을 경우 로그인 페이지로 이동
        console.log("No Session");
        this.router.navigate(['/']);
      }
    });
  }

  // setupMenu 함수
  // 파라미터로 현재 사용자의 권한이 들어온다.
  // 그 권한에 따라 맞는 좌측 메뉴를 생성한다.
  setupMenu() {
    this.menu = ['userInfo', 'userManage' , 'contentManage', 'reportManage'];
  }

  // 사용자 정보 로드 함수
  setupUserInfo(email: string, name: string, authority: string, phoneNumber: string) {
    this.userInfo.email = email;
    this.userInfo.name = name;
    this.userInfo.authority = authority;
    this.userInfo.phoneNumber = phoneNumber;

  }

  // updataUserInfo 함수
  // Data.service를 통해 다른 자식 컴포넌트에 전달한 현재 사용자 정보를 넘긴다.
  updateUserInfo(value: Object){
    this.dataService.updateData(value);
  }

  // LogOut 함수
  // 사용자가 로그아웃 버튼을 누르면 세션을 파괴한 후 로그인 페이지로 이동시킨다.
  logOut(){
      if (window.confirm('로그아웃 하시겠습니까?')) {
          this.myObserver_logOut = this.httpService.userLogout().subscribe(result => {
              //Logout session destroy
              this.router.navigate(['/']);
          });
      }
      else{
      }
  }

  ngOnDestroy(){
    // 위에서 HTTP 통신을 위한 Observer가 Subscribe 중이므로 Unsubscribe를 해준다.
    if(this.myObserver != undefined){
      this.myObserver.unsubscribe();
    }
    if(this.myObserver_sess != undefined){
      this.myObserver_sess.unsubscribe();
    }
    if(this.myObserver_logOut != undefined){
      this.myObserver_logOut.unsubscribe();
    }
  }
}
