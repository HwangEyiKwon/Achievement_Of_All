// userInfo.component
// 사용자 정보 페이지

import { Component, OnInit, ElementRef } from '@angular/core';
import {DataService} from './data.service';
import {NgForm} from '@angular/forms';
import {HttpService} from './http-service';
import {UserPageComponent} from './userPage.component';
import {  FileUploader } from 'ng2-file-upload/ng2-file-upload';
import {Http} from '@angular/http';

import * as myGlobals from './global.service';


@Component({
  selector: 'app-user-info',
  templateUrl: './userInfo.component.html',
  styleUrls: ['./userInfo.component.css']
})
export class UserInfoComponent implements OnInit {

  state: number; // 사용자 정보 제공 = 0, 수정 기능 = 1, 비밀번호 변경 = 2

  imageURI: string;
  imagePath = myGlobals.serverPath; // 이미지 경로

  email: string; // 사용자 이메일
  name: string; // 사용자 이름
  authority: string; // 사용자 권한
  phoneNumber: string; // 사용자 전화번호

  // 사용자 정보 페이지에는 사용자 정보를 수정하는 기능이 있다
  // 그 중 사용자가 이미지를 수정하면 이미지를 서버에 업로드할 수 있도록 해주는 부분이다
  public uploader: FileUploader = new FileUploader({url: '/photo', itemAlias: 'photo',allowedMimeType: ['image/png', 'image/gif', 'video/mp4', 'image/jpeg'] });

  constructor(
    private parent: UserPageComponent,
    private dataService: DataService,
    private httpService: HttpService,
    private http: Http, private el: ElementRef
  ) {}

  ngOnInit() {

    // 부모 컴포넌트인 userPage.component에서 보낸 사용자 정보들을 불러오는 값
    // 컴포넌트 사이에서 데이터 교환은 여러 방식이 있는데 이는 그 중 하나의 방법임. (Data.service 참고)
    this.dataService.currentMessage.subscribe(userInfo => {

      this.email = JSON.parse(JSON.stringify(userInfo)).email;
      this.name = JSON.parse(JSON.stringify(userInfo)).name;
      this.authority = JSON.parse(JSON.stringify(userInfo)).authority;
      this.phoneNumber = JSON.parse(JSON.stringify(userInfo)).phoneNumber;

      this.imageURI = this.imagePath + '/getManagerImage/' + this.email +'?'+ new Date().getTime();
      this.state = 0;

    });

    //override the onAfterAddingfile property of the uploader so it doesn't authenticate with //credentials.
    this.uploader.onAfterAddingFile = (file)=> { file.withCredentials = false; };
    //overide the onCompleteItem property of the uploader so we are
    //able to deal with the server response.
    this.uploader.onCompleteItem = (item:any, response:any, status:any, headers:any) => {
      console.log("ImageUpload:uploaded:", item, status, response);
    };

  }
  // Image Upload 하는 함수
  // (이미지를 서버의 한 경로로 업로드하는 부분)
  upload(name) {

    // Promise 기법 (구글 참고)
    // Promise는 자바스크립트에서 많이 사용하는 기법으로mk
    // 여기에서는 간단히 비동기 함수의 문제를 해결하기 위해 사용하였다.
    // 하단의 실제 이미지 업로드하는 함수들은 코드를 가져다 사용하여 설명은 생략하겠다.
    return new Promise ((resolve, reject)=>{
      //locate the file element meant for the file upload.
      let inputEl: HTMLInputElement = this.el.nativeElement.querySelector('#photo');
      //get the total amount of files attached to the file input.
      let fileCount: number = inputEl.files.length;
      //create a new fromdata instance
      let formData = new FormData();

      // console.log(inputEl.files);
      //check if the filecount is greater than zero, to be sure a file was selected.
      if (fileCount > 0) { // a file was selected
        //append the key name 'photo' with the first file in the element
        formData.append('photo', inputEl.files.item(0));
        formData.append('name', name);
        //call the angular http method
        this.http
        //post the form data to the url defined above and map the response. Then subscribe //to initiate the post. if you don't subscribe, angular wont post.
          .post('/photo', formData ).subscribe(
          //map the success function and alert the response
          (result) => {
            alert("이미지 업로드 완료.");
            // console.log(JSON.parse(JSON.stringify(result))._body);
            resolve(JSON.parse(JSON.stringify(result))._body);
          },
          (error) => {
            alert("이미지 업로드 중 에러 발생.");
            console.log(error);
            resolve();
          }
        )
      }else{
        resolve();
      }
    })
  }
  // 수정 버튼 함수
  openEdit(){
    this.state=1;
  }
  // 취소 버튼 함수
  cancel(){
    this.state=0;
    this.ngOnInit();
  }
  // 비밀번호 수정 버튼 함수
  openChangePassword(){
    this.state = 2;
  }

  // 수정 완료 버튼 함수
  saveEdit(form: NgForm){
    this.upload(form.value.name).then( response => {
      //upload()를 우선 호출 후 upload 내부 모든 비동기 함수가 끝나면 이어서 아래 코드 진행
      var userImagePath = response;

      // HTTP 통신으로 서버에 수정하려는 정보들을 전달 (성공시 데이터베이스에 적용)
      this.httpService.changeUserInfo(form.value.name, this.email, form.value.phoneNumber, userImagePath).subscribe(
        result =>{
          // 사용자 정보 수정 완료
          if(JSON.parse(JSON.stringify(result)).success === true ){
            this.state = 0;
            // 수정이 완료되면 부모 컴포넌트를 다시 호출하여 수정된 사용자 정보를 가져오도록 함.
            this.parent.ngOnInit();
          }else{
            // 수정하려는 이메일이 중복 메일일 경우
            alert("존재하는 메일입니다.")
          }

        }
      );
    });
  }
  // 비밀번호 수정 함수
  changePassword(form: NgForm){
    if (window.confirm('비밀번호를 바꾸시겠습니까?')) {
      if(form.value.newPassword == form.value.newPasswordCheck){
        // HTTP 통신으로 서버에 수정하려는 정보들을 전달 (성공시 데이터베이스에 적용)
        this.httpService.changeUserPassword(form.value.currentPassword, form.value.newPassword, form.value.newPasswordCheck).subscribe(
          result =>{
            // 비밀번호 수정 완료
            if(JSON.parse(JSON.stringify(result)).success == 1 ){
              this.state=0;
              // 수정이 완료되면 부모 컴포넌트를 다시 호출하여 수정된 사용자 정보를 가져오도록 함.
              this.parent.ngOnInit();
              alert("비밀번호가 수정되었습니다.");
            }else if(JSON.parse(JSON.stringify(result)).success == 0){
              alert("비밀번호가 틀립니다.");
            }else {
              alert("오류");
            }

          }
        );
      }
      else{
        // 비밀번호와 확인 비밀번호가 불일치할 경우
        alert("비밀번호 확인이 틀립니다. ");
      }
    }else{
    }
  }

}
