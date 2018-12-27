// App.routing.module
// 라우팅을 지정해주는 모듈

// Module
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

// Component
import { UserServiceComponent } from './userService.component'; // Login/SignUp
import { UserPageComponent } from './userPage.component'; // User Page
import { UserInfoComponent } from './userInfo.component'; // User Info
import { UserManageComponent } from './userManage.component'; // User Manage
import { ContentManageComponent } from './contentManage.component'; // Content Manage
import { ReportManageComponent } from './reportManage.component'; // Report Manage
import { AboutComponent } from './about.component'; // About

const routes: Routes = [ // Route 지정

  { path: '', redirectTo: 'login', pathMatch: 'full'}, // '/' 처음에 들어오면 로그인 페이지 이동
  { path: 'login', component: UserServiceComponent}, // 로그인 페이지
  { path: 'main', component: UserPageComponent, // 로그인을 마치면 main 페이지
    children: [
      { path: '', redirectTo: 'userInfo', pathMatch: 'full'},
      { path: 'userInfo', component: UserInfoComponent}, // 사용자 정보
      { path: 'userManage', component: UserManageComponent}, // 사용자 관리
      { path: 'contentManage', component: ContentManageComponent}, // 컨텐츠 관리
      { path: 'reportManage', component: ReportManageComponent}, // 신고 관리
      { path: 'about', component: AboutComponent} ] // 모두의 달성 정보

  },
  {path: '**', redirectTo: 'login'} // 그 외 URL 입력시 로그인 페이지
];

@NgModule({
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule { }
