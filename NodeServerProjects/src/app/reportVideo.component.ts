// ReportVideo.component

// ReportManage.component의 자식 컴포넌트다.
// 페이지에서 비디오를 볼 수 있다.

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
    videoPath = myGlobals.serverPath; // 이미지 경로

    constructor(
        private httpService: HttpService
    ) {}
    ngOnInit() {
        this.renderValue = this.value.toString().toUpperCase();
    }
    video() {
      this.clicked = true;

      if( JSON.parse(JSON.stringify(this.rowData)).complete === 1) {
        this.completed = true;
      }else{
        this.completed = false;
      }
      var contentName = JSON.parse(JSON.stringify(this.rowData)).name;
      var email = JSON.parse(JSON.stringify(this.rowData)).email;
      var videoPath = JSON.parse(JSON.stringify(this.rowData)).authenDay;

      this.videoSource = this.videoPath + '/getReportVideo/' + email + '/' + contentName + '/'+ videoPath + '?' + new Date().getTime();
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
    }
}
