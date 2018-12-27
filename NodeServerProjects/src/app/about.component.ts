// About.component
// 모두의 달성에 대한 설명이 있는 컴포넌트

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
    ) {}
    ngOnInit() {

      // About Page에 보여줄 모두의 달성 정보를 불러온다.
      this.httpService.getAppInfo().subscribe(result => {
        this.appInfo = JSON.parse(JSON.stringify(result)).appInfo;
        this.noticeInfo = JSON.parse(JSON.stringify(result)).noticeInfo;
      });
    }
}
