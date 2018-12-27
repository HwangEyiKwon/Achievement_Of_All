// ReportManage.component
// 신고 관리 페이지

import {Component, OnDestroy, OnInit} from '@angular/core';
import { HttpService } from './http-service';
import { Router } from '@angular/router';
import { LocalDataSource } from 'ng2-smart-table';
import { UserPageComponent } from './userPage.component';
import {ReportVideoComponent} from './reportVideo.component';

@Component({
  selector: 'app-report-manage',
  templateUrl: './reportManage.component.html',
  styleUrls: ['./reportManage.component.css'],
})
export class ReportManageComponent implements OnInit, OnDestroy {

  subscription = null;
  subscription_s = null;
  subscription_e = null;
  reportsInfo = [];
  source: LocalDataSource;

  settings: any;

  constructor(
    private httpService: HttpService,
    private parent: UserPageComponent,
    private router: Router,
  ) {}

  // ng2-smart-table 달력을 세팅하는 함수이다.
  setTable() {


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

          // 비디오
          // 자식 컴포넌트와 연동한다.
          renderComponent: ReportVideoComponent,
          onComponentInitFunction : (instance) => {
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
    // 관리자 페이지는 권한이 manager인 사용자만 가능
    // HTTP 통신을 통해 관리자 체크를 해야한다.
    this.httpService.authorityCheck().subscribe(result=>{

      // 접근 권한이 없을 경우 에러페이지로 이동한다.
      if(JSON.parse(JSON.stringify(result)).error == true){
        this.router.navigate(['/error']);
      }else {

        // Session Check
        // 세션 체크 후에 세션이 저장되어 있지 않으면 로그인 페이지로 이동한다.
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
    });

  }

  ngOnDestroy(){
    if(this.subscription != null)
      this.subscription.unsubscribe();
  }
  // ng2-smart-table을 업데이트하는 함수이다.
  updateTable() {
    return new Promise ((resolve,reject) => {
      this.reportsInfo = [];

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
        // 리포트 관리 페이지에서 정보를 수정하면 부모 컴퍼넌트인 userPage.component를 다시 호출
        this.parent.ngOnInit();
        resolve();
      });
    });
  }

  // 정보 삭제하는 함수이다.
  onDeleteConfirm(event) {
    if (window.confirm('정말로 삭제하시겠습니까?')) {
      event.confirm.resolve();

      this.httpService.deleteContentInfo(event.data.name, event.data.id).subscribe(result =>{
        this.updateTable().then(response =>{
          alert("삭제되었습니다.");
        });
      });
    } else {
      event.confirm.reject();
    }
  }
}
