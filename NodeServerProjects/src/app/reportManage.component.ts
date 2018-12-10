// userManage.component
// 사용자 관리 페이지
// (권한이 마스터인 사용자만 접근 가능)

import {Component, OnChanges, OnDestroy, OnInit} from '@angular/core';
import { HttpService } from './http-service';
import { Router } from '@angular/router';
import { DataService} from './data.service';
import { LocalDataSource } from 'ng2-smart-table';
import { UserPageComponent } from './userPage.component';
import {ReportVideoComponent} from './reportVideo.component';

@Component({
  selector: 'app-report-manage',
  templateUrl: './reportManage.component.html',
  styleUrls: ['./reportManage.component.css'],
})
export class ReportManageComponent implements OnInit, OnDestroy {

  subscription = null; // 옵저버
  subscription_s = null; // 옵저버
  subscription_e = null; // 옵저버
  reportsInfo = [];
  source: LocalDataSource;

  settings: any;

  // 관리자 페이지는 npm에 등록된 ng2-smart-table 모듈을 사용했다.
  // 이 부분에 대해선 구글 참고. (자료가 많지 않음)


  constructor(
    private httpService: HttpService,
    private parent: UserPageComponent,
    private router: Router,
    private dataService: DataService
  ) {}

  setTable() { // Group들의 이름,사진 불러오기


    this.settings = {
      pager : {
        display : true,
        perPage: 10
      },
      actions: {
        add: false,
        edit: false,
        delete: false
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
        id: {
          title: 'Content ID',
          width: '5%'
        },
        name: {
          title: 'Content Name',
          width: '5%'
        },
        email: {
          title: 'Report User Email ',
          width: '10%'
        },
        reportReason: {
          title: 'Report Reason',
        },
        authenDay: {
          title: 'Authentification Day',
          width: '10%',
        },
        reportUsers: {
          title: 'Reported Users Email',
          width: '10%',
        },
        complete: {
          title: 'Complete',
          width: '5%',
        },
        video: {
          title: 'Video & Edit',
          type: 'custom',
          width: '30%',
          renderComponent: ReportVideoComponent,
          onComponentInitFunction : (instance) => {
            console.log(instance.save);
            instance.save.subscribe(row => {
              this.setTable();
              this.updateTable().then();
            });
          },
          editable: false,
          addable: false
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
            console.log("No Session");2
            this.router.navigate(['/']);
          }
        });
      }
    });

  }
  // NgOnDestroy
  // 컴포넌트가 파괴될 때 작동하는 부분
  ngOnDestroy(){
    // Observer unsubscribe
    console.log("123123");
    if(this.subscription != null)
      this.subscription.unsubscribe();
  }
  // Table Update
  updateTable() {
    return new Promise ((resolve,reject) => {
      this.reportsInfo = [];
      console.log("아니 왜안되?");

      this.httpService.getReportsInfo().subscribe(result => {

        for ( const u of JSON.parse(JSON.stringify(result))) {
          console.log(u);
          this.reportsInfo.push({
            id: u.contentId,
            name: u.contentName,
            email: u.userEmail,
            reportReason: u.reportReason,
            authenDay: u.authenDay,
            reportUsers: u.reportUser,
            complete: u.complete
          });
        }
        this.source = new LocalDataSource(this.reportsInfo);
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
    console.log("삭제");
    if (window.confirm('정말로 삭제하시겠습니까?')) {
      event.confirm.resolve();

      this.httpService.deleteContentInfo(event.data.name, event.data.id).subscribe(result =>{
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
    console.log("수정");
    // if (window.confirm('정말로 저장하시겠습니까?')) {
    //
    //   // InputFile.component와 통신
    //   this.subscription = this.dataService.notifyObservable$_parent.subscribe((res) => {
    //
    //     if (res.hasOwnProperty('option') && res.option === 'image') {
    //       event.newData.image = res.value;
    //       // 수정된 정보를 저장
    //       this.httpService.updateUserInfo(event.data, event.newData).subscribe(result =>{
    //         if(JSON.parse(JSON.stringify(result)).success == true){
    //           this.updateTable().then(response =>{
    //             alert("수정되었습니다");
    //             event.confirm.resolve(event.newData);
    //           });
    //         }else{
    //           this.updateTable().then(response =>{
    //             alert("수정에 실패하셨습니다.");
    //             event.confirm.resolve(event.newData);
    //           });
    //         }
    //       });
    //     };
    //   });
    //   // InputFile.component와 통신
    //   this.dataService.notifyOther({option: 'image', value: true});
    // } else {
    //   event.confirm.reject();
    // }
  }
  // Create Info
  // 새로운 정보 저장
  onCreateConfirm(event) {
    console.log("추");
    if(event.newData.id == "" || event.newData.name == ""){
      alert("Id 또는 이름을 입력해주세요");
    }else{
      if (window.confirm('생성하시겠습니까?')) {
        console.log("create");

        this.dataService.notifyOtherStd(event.newData);
        this.dataService.notifyOtherEd(event.newData);

        this.subscription_e = this.dataService.notifyObservableEd$_parent.subscribe((res1) => {

          console.log("뭐야뭐야");
          console.log(res1);

          this.subscription_s = this.dataService.notifyObservableStd$_parent.subscribe((res2) => {

            console.log("뭐야뭐야");
            console.log(res2);

            console.log("뭐인뭐야");

            event.newData.startDate = JSON.parse(JSON.stringify(res2)).date;
            event.newData.endDate = JSON.parse(JSON.stringify(res1)).date;

            if(event.newData.startDate.year === undefined || event.newData.startDate.month === undefined || event.newData.startDate.day === undefined || event.newData.endDate.year === undefined || event.newData.endDate.month === undefined || event.newData.endDate.day === undefined){

              this.updateTable().then(response =>{
                alert("날짜를 기입해주세요.");
                event.confirm.resolve(event.newData);

                this.subscription_s.unsubscribe();
                this.subscription_e.unsubscribe();
              });

            }else{
              this.httpService.addContentInfo(event.newData).subscribe(result => {


                if(JSON.parse(JSON.stringify(result)).success == 0){
                  this.updateTable().then(response =>{
                    alert("생성되었습니다");
                    event.confirm.resolve(event.newData);
                  });
                }else if(JSON.parse(JSON.stringify(result)).success == 1){
                  // 이메일 중복
                  this.updateTable().then(response =>{
                    alert("id 중복입니다.");
                    event.confirm.resolve(event.newData);
                  });
                }else{
                  alert("error 입니다");
                  event.confirm.resolve(event.newData);
                }

                this.subscription_s.unsubscribe();
                this.subscription_e.unsubscribe();

              });
            }


          });

        });


      } else {
        event.confirm.reject();
      }
    }

  }

}
