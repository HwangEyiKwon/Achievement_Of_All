// Error.component

// 접근 권한 제어를 위한 컴포넌트다.
// 권한이 없는 사람이 접근할 경우 에러 페이지를 띄워 접근을 제어하고 메인페이지로 이동할 수 있도록 해준다.
// 직접 url을 변경하여 접근할 경우

import {Component, OnDestroy, OnInit} from '@angular/core';
import { Router } from '@angular/router';

@Component({
    selector: 'app-error',
    template: '<div style=" text-align: center; vertical-align: middle"> ' +
        '<div style = "position: relative; top: 250px">'+
        '<img src="../assets/attention.png" style ="width: 7%; height: 7%;">  <br><br> ' +
        '<p style="font-size: 150%; font-weight: bold;">요청하신 페이지에 대한 권한이 없습니다.</p> ' +
        '<br> <button (click)="goToMain()" style = "font-weight: bold;" >메인으로</button> ' +
        '</div>' +
        '</div>',
    styles: [''],
})
export class ErrorComponent implements OnInit, OnDestroy {

    constructor(
        private router: Router
    ) {

    }
    ngOnInit() {}
    ngOnDestroy(){}
    // 메인페이지 이동 버튼
    goToMain(){
        this.router.navigate(['/']);
    }
}
