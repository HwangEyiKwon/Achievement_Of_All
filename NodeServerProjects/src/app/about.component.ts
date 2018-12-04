// About.component
// Desing-Plug 에 대한 설명이 있는 컴포넌트
import { Component, OnInit } from '@angular/core';
import { HttpService } from './http-service';

@Component({
    selector: 'app-about',
    templateUrl: './about.component.html',
    styleUrls: ['./about.component.css']
})
export class AboutComponent implements OnInit {

    appInfo: String;
    noticeInfo: String;

    constructor(
      private httpService: HttpService
    ) {
    }
    ngOnInit() {

      this.httpService.getAppInfo().subscribe(result=>{
        console.log(result)
        this.appInfo = JSON.parse(JSON.stringify(result)).appInfo;
        this.noticeInfo = JSON.parse(JSON.stringify(result)).noticeInfo;
      });
    }
}
