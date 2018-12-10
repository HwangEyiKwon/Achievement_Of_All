// buttonView.component
// 부모 컴포넌트인 디바이스 관리페이지에서 사용되는 자식 컴포넌트다.
// 이는 inputFile.component와 마찬가지로 ng2-smart-table에서 제공하는 기능이 제한적이여서 직접만든 새로운 기능이다.
// 테이블의 칸에 html 요소를 넣었다 (버튼 3개)
// 각각의 버튼은 디바이스의 인증서 3종류를 다운로드 받을 수 있는 버튼이다.

import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { ViewCell } from 'ng2-smart-table';
import { HttpService } from './http-service';
import * as myGlobals from './global.service';

@Component({
    selector: 'app-video',
    template: `      
      
        <button *ngIf="clicked==false" (click)="video()">Watch Video</button>
        
        <div *ngIf="clicked==true" class="video">
          <video width="300" height="300" controls (click)="toggleVideo()" #videoPlayer> 
            <source src="{{videoSource}}" type="video/mp4" />
            Browser not supported
          </video>
        </div>
        
        <div *ngIf="clicked==true">
          <div *ngIf="completed==false">
            <input type="radio" [value] = true  [(ngModel)]="isConfirm" > Accept
            <input type="radio" [value] = false  [(ngModel)]="isConfirm"> Reject
            
            <div>
              <input #textbox type="text" name = "사유" [(ngModel)]="reason" required>
            </div>
            
            <button (click)="confirm()">Confirm</button>
            <button (click)="cancel()">Cancel</button>
          </div>
          <div *ngIf="completed==true">
            <p>이미 처리됬습니다.</p>
            <button (click)="cancel()">Cancel</button>
          </div>
        </div>
  `
})
export class ReportVideoComponent implements ViewCell, OnInit {
    renderValue: string;

    @Input() value: string | number;
    @Input() rowData: any;

    @Output() save: EventEmitter<any> = new EventEmitter();

    clicked = false;
    isConfirm: boolean;
    completed: boolean;
    reason: String;

    videoSource: String;
    imagePath = myGlobals.imagePath; // 이미지 경로

    constructor(
        private httpService: HttpService
    ) {}
    ngOnInit() {
        this.renderValue = this.value.toString().toUpperCase();
    }
    video() {
      this.clicked = true;
      console.log(this.rowData);

      if( JSON.parse(JSON.stringify(this.rowData)).complete === 1) {
        this.completed = true;
      }else{
        this.completed = false;
      }

      var contentName = JSON.parse(JSON.stringify(this.rowData)).name;
      var email = JSON.parse(JSON.stringify(this.rowData)).email;
      var videoPath = JSON.parse(JSON.stringify(this.rowData)).authenDay;

      // 날짜가 중요!! 이거 새로만들어야할듯
      this.videoSource = this.imagePath + '/getReportVideo/' + email + '/' + contentName + '/'+ videoPath + '?' + new Date().getTime();
      // this.videoSource = this.imagePath + '/getOthersVideo/' + 'shp17@gmail.com/NoSmoking';
    }
    cancel(){
      this.clicked = false;
    }
    confirm(){
      if(this.isConfirm == null){
        alert("체크 표시를 해주세요.");
      }
      else{
        this.clicked = false;
        console.log(this.isConfirm);


        var rp = {
          email: JSON.parse(JSON.stringify(this.rowData)).email,
          contentId: JSON.parse(JSON.stringify(this.rowData)).id,
          contentName: JSON.parse(JSON.stringify(this.rowData)).name,
          reason: this.reason
        };

        if(this.isConfirm === true){

          this.httpService.reportAccept(rp).subscribe(result => {
              if(JSON.parse(JSON.stringify(result)).success === true){
                alert("신고 접수 승인했습니다.");
                this.save.emit(this.isConfirm);
              }
          });
        }else{
          this.httpService.reportReject(rp).subscribe(result => {
            if(JSON.parse(JSON.stringify(result)).success === true){
              alert("신고 접수 거절했습니다.");
                this.save.emit(this.isConfirm);
            }
          });
        }


      }

      // 저장저장저장저장
    }
}
