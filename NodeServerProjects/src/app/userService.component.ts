// UserService.component
// 로그인 및 회원가입 관련 컴포넌트다.

import {Component, OnDestroy, OnInit} from '@angular/core';
import { NgForm } from '@angular/forms';
import { HttpService } from './http-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-service',
  templateUrl: './userService.component.html',
  styleUrls: ['./userService.component.css']
})

export class UserServiceComponent implements OnInit , OnDestroy {

  state = 0; // 로그인 = 0 , 회원가입 = 1, 비밀번호 찾기 = 2, 비밀번호 변경 = 3

  username: string;
  phoneNumber: string;
  email: string;
  password: string;
  managerKey: string; // 관리자 키

  myObserver = null;

  defaultUser = { // default (예시)
    username : '홍길동' ,
    phoneNumber : '010-0000-0000',
    email : 'hgd123@gmail.com',
    password : '123123',
    managerKey: 'Secret Key'
  }

  constructor(
    private httpService: HttpService,
    private router: Router
  ) {}

  ngOnInit() {

    // Session Check
    // 처음 로그인 페이지를 들어가면 세션 정보를 확인한다.
    // 만약 세션이 존재할 경우 로그인 진행 없이 사용자 페이지로 이동하고
    // 세션이 존재하지 않을 경우 로그인 페이지로 이동한다.

    this.myObserver = this.httpService.sessionCheck().subscribe(result => { // HTTP 통신을 통해 서버에게 세션 확인을 요구한다.
      // 세션에 사용자 정보가 남아 있을 경우
      if (JSON.parse(JSON.stringify(result)).userSess !== undefined ) {
        console.log('Session: ' + JSON.stringify(result));
        this.router.navigate(['/main']);
      }
    });
  }

  // 회원가입 함수이다.
  onSubmit(form: NgForm) {
    // Sign Up
    // 회원가입 버튼을 누를 경우

    // 빈칸을 모두 입력하지 않을 경우 알람을 띄움.
    if (form.value.username == undefined || form.value.phoneNumber == undefined || form.value.password == undefined || form.value.email == undefined || form.value.managerKey == undefined){
      alert('빈칸을 모두 입력해주세요.');
    } else {
      // 빈칸이 모두 입력되고 회원가입 버튼을 누른 경우

      // HTTP 통신을 통해 서버에게 정보들을 전달.
      // 서버 측에서는 보낸 정보들을 통해 중복 가입 등을 체크하고 성공 여부를 다시 클라이언트에 날림.
      this.httpService.userSignUp(form.value.username, form.value.phoneNumber, form.value.password, form.value.email, form.value.managerKey).subscribe(result => {

        // 성공할 경우
        if (JSON.parse(JSON.stringify(result)).success === true ) {
          alert('가입에 성공하셨습니다.');

          // 로그인 창으로 보내주고
          // 회원가입 창의 칸들을 모두 비운다.
          this.state = 0;
          this.username = '';
          this.phoneNumber = '';
          this.email = '';
          this.password = '';
          this.managerKey = '';

        } else {
          //
          if (JSON.parse(JSON.stringify(result)).why == 0) {
            alert('존재하는 계정입니다.');
          } else if(JSON.parse(JSON.stringify(result)).why == 1) {
            alert('Manager Key가 틀렸습니다.');
          }else{
            alert('서버 상태가 좋지 않습니다. 다시 시도해주세요.');
          }
        }
      });
    }
  }

  // 로그인 함수이다.
  onLogin(form: NgForm) {
    // Login
    // 로그인 버튼을 누를 경우

    // 빈칸을 모두 입력하지 않을 경우 알람을 띄움.
    if (form.value.emailLogin == "" || form.value.passwordLogin == ""){
      alert('빈칸을 모두 입력해주세요.');
    } else {
      // 빈칸이 모두 입력되고 회원가입 버튼을 누른 경우

      // HTTP 통신을 통해 서버에게 정보들을 전달.
      // 서버 측에서는 보낸 정보들을 통해 로그인 정보 등을 체크하고 성공 여부를 다시 클라이언트에 날림. (성공시 데이터베이스에 정보 적용)
      this.httpService.userLogin(form.value.emailLogin, form.value.passwordLogin).subscribe(result => {

        // 로그인 성공시 사용자 페이지로 이동
        if (JSON.parse(JSON.stringify(result)).access === true){
          this.router.navigate(['/main/userInfo']);
        }
        else {
          if(JSON.parse(JSON.stringify(result)).why === 0){
            // 로그인 정보가 틀릴 경우
            alert('유효하지 않는 계정입니다.');
          }else if(JSON.parse(JSON.stringify(result)).why === 1){
            // 로그인 정보가 틀릴 경우
            alert('Password가 틀립니다.');
          }else if(JSON.parse(JSON.stringify(result)).why === 2){
            // 로그인 정보가 틀릴 경우
            alert('권한이 없습니다.');
          }
        }
      });
    }
  }

  // 비밀번호 수정 함수다.
  onChangePassword(form: NgForm) {
    // Change Password
    // 비밀번호 변경을 위한 이메일 인증 버튼을 누를 경우

    // 빈칸을 모두 입력하지 않을 경우 알람을 띄움.
    if(form.value.emailChangeEmail == ""){
      alert('빈칸을 모두 입력해주세요.');
    }else{

      // HTTP 통신을 통해 서버에게 정보들을 전달.
      // 서버 측에서는 보낸 정보들을 통해 비밀번호 정보 등을 체크하고 성공 여부를 다시 클라이언트에 날림. (성공시 데이터베이스에 정보 적용)
      this.httpService.changePassword(form.value.emailChangeEmail).subscribe(result => {
        // 유효한 이메일로 메일을 발송할 경우
        if(JSON.parse(JSON.stringify(result)).success === true){
          alert('인증 메일을 발송했습니다.');
          this.state = 0;
        }
        else{

          if(JSON.parse(JSON.stringify(result)).why === 0){
            alert('등록되지 않은 이메일입니다.');
          }else if(JSON.parse(JSON.stringify(result)).why === 1){
            alert('유효하지 않은 이메일입니다.');
          }
        }
      });
    }
  }

  // 창 상태 변경 함수
  stateChange(n: number) {
    // 파라미터 (state)를 통해 창을 바꿈
    // 바꾸기 이전에 이전 창의 빈칸을 모두 지움
    this.state = n;
    this.username = '';
    this.phoneNumber = '';
    this.email = '';
    this.password = '';
  }

  ngOnDestroy() {
    this.myObserver.unsubscribe();
  }
}
