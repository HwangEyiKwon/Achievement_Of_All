// userManage.component
// 유저 관리 페이지
// (권한이 manager인 사용자만 접근 가능)

import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpService } from './http-service';
import { Router } from '@angular/router';
import { DataService} from './data.service';
import { LocalDataSource } from 'ng2-smart-table';
import { InputFileComponent } from './inputFile.component';
import { UserPageComponent } from './userPage.component';

@Component({
    selector: 'app-user-manage',
    templateUrl: './userManage.component.html',
    styleUrls: ['./userManage.component.css'],
})
export class UserManageComponent implements OnInit, OnDestroy {

    subscription = null; // 옵저버
    usersInfo = [];
    source: LocalDataSource;
    groupList = [];
    settings: any;

    constructor(
        private httpService: HttpService,
        private parent: UserPageComponent,
        private router: Router,
        private dataService: DataService
    ) {}

    setTable() {


          this.settings = {
            pager : {
              display : true,
              perPage:10
            },
            add:{
              addButtonContent: '<i class="fa fa-plus"></i>',
              createButtonContent: '<i class="fa fa-check"></i>&nbsp&nbsp',
              cancelButtonContent: '<i class="fa fa-times"></i>',
              confirmCreate: true
            },
            edit: {
              editButtonContent: '<i class="fa fa-pencil"></i>&nbsp&nbsp',
              saveButtonContent: '<i class="fa fa-check"></i>&nbsp&nbsp',
              cancelButtonContent: '<i class="fa fa-times"></i>',
              // mode: 'external'
              confirmSave: true,
            },
            delete: {
              deleteButtonContent: '<i class="fa fa-trash" aria-hidden="true"></i>',
              confirmDelete: true
            },
            columns: {
              email: {
                title: 'Email',
                width: '12%',
                editable:false
              },
              name: {
                title: 'Name'
              },
              authority: {
                title: 'Authority',
                editor:{
                  type: 'list',
                  config: {
                    list: [{title: 'user', value: 'user'}, {title: 'manager', value: 'manager'}]
                  }
                }
              },

              password:{
                title: 'Password',
                width: '25%'
              },

              phoneNumber:{
                title: 'PhoneNumber',
                width: '10%'
              },
              image:{
                title: 'Image',
                width: '20%',
                type: 'text',
                // 이미지 수정을 위한 부분
                // 수정 버튼을 누를 시 InputFileComponent를 호출함.
                editor: {
                  type: 'custom',
                  component: InputFileComponent,
                  mode: 'external'
                }
              }
            },
            attr: {
              class: 'table table-responsive'
            },

          };
    }

    ngOnInit() {
        // Authority Check
        // 관리자 페이지는 권한이 마스터인 사용자만 가능
        // HTTP 통신을 통해 관리자 체크를 해야함.
        this.httpService.authorityCheck().subscribe(result=>{

            // 접근 권한이 없을 경우 에러페이지로 이동
            if(JSON.parse(JSON.stringify(result)).error == true){
                this.router.navigate(['/error']);
            }else {

                // Session Check
                // 세션 체크 후에 세션이 저장되어 있지 않으면 로그인 페이지로 이동
                this.httpService.sessionCheck().subscribe(result => {
                    if(JSON.parse(JSON.stringify(result)).userSess !== undefined ){
                        console.log("Session: " + JSON.stringify(result));

                        this.setTable();
                        this.updateTable();
                    }
                    else {
                        console.log("No Session");
                        this.router.navigate(['/']);
                    }
                });
            }
        })
    }
    // NgOnDestroy
    // 컴포넌트가 파괴될 때 작동하는 부분
    ngOnDestroy(){
        // Observer unsubscribe
        if(this.subscription != null)
            this.subscription.unsubscribe();
    }
    // Table Update
    updateTable(){
        return new Promise ((resolve,reject) => {
            this.usersInfo = [];
            this.httpService.getUsersInfo().subscribe(result => {

                for ( const u of JSON.parse(JSON.stringify(result))) {
                    console.log(u);
                    this.usersInfo.push({
                        email: u.email,
                        name: u.name,
                        authority: u.authority,
                        password: u.password,
                        phoneNumber: u.phoneNumber,
                        image: u.imagePath
                    });
                }
                this.source = new LocalDataSource(this.usersInfo);
                if(this.subscription != null)
                    this.subscription.unsubscribe();
                // 사용자 관리 페이지에서 정보를 수정하면 부모 컴퍼넌트인 userPage.component를 다시 호출
                // 변경된 정보가 자기 자신 것일 경우 바로 메뉴바의 정보가 바뀔수 있도록
                this.parent.ngOnInit();
                resolve();
            });
        });
    }
    // Delete Info
    // 정보 삭제
    onDeleteConfirm(event) {
        if (window.confirm('정말로 삭제하시겠습니까?')) {
            event.confirm.resolve();

            this.httpService.deleteUserInfo(event.data.name, event.data.email).subscribe(result =>{
                // delete userInfo
                this.updateTable().then(response =>{
                    alert("삭제되었습니다.");
                });
            });
        } else {
            event.confirm.reject();
        }
    }
    // Edit Info
    // 수정된 정보를 저장
    onSaveConfirm(event) {
        if (window.confirm('정말로 저장하시겠습니까?')) {

          event.newData.isAdd = false;
            // InputFile.component와 통신
            this.dataService.notifyOther(event.newData);

            this.subscription = this.dataService.notifyObservable$_parent.subscribe((res) => {
                if(res.change == 1) {
                  alert("이미지 업로드에 문제가 생겼습니다.");
                }
                else if (res.option === 'image') {

                    this.httpService.updateUserInfo(event.data, event.newData, res.change).subscribe(result => {
                        if(JSON.parse(JSON.stringify(result)).success == true){
                            this.updateTable().then(response =>{
                                alert("수정되었습니다");
                                event.confirm.resolve(event.newData);
                            });
                        }else{
                            this.updateTable().then(response =>{
                                alert("수정에 실패하셨습니다.");
                                event.confirm.resolve(event.newData);
                            });
                        }
                    });
                }
            });
        } else {
            event.confirm.reject();
        }
    }
    // Create Info
    // 새로운 정보 저장
    onCreateConfirm(event) {
        if(event.newData.email == "" || event.newData.password == ""){
            alert("Email, Password를 입력해주세요.");
        }else{
            if (window.confirm('생성하시겠습니까?')) {

              event.newData.isAdd = true;
              this.dataService.notifyOther(event.newData);

              this.subscription = this.dataService.notifyObservable$_parent.subscribe((res) => {
                if(res.change == 1) {

                  alert("이미지 업로드에 문제가 생겼습니다.");
                }
                else if (res.option === 'image') {

                  this.httpService.addUserInfo(event.newData, res.change).subscribe(result => {
                    if(JSON.parse(JSON.stringify(result)).success == true){
                      this.updateTable().then(response =>{
                        alert("생성되었습니다");
                        event.confirm.resolve(event.newData);
                      });
                    }else{
                      this.updateTable().then(response =>{
                        alert("Email 중복입니다.");
                        event.confirm.resolve(event.newData);
                      });
                    }
                  });
                }

            })
        } else {
              event.confirm.reject();
            }

      }
    }

}
