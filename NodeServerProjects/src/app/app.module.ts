import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpModule } from '@angular/http';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app.routing.module';
import { FormsModule } from '@angular/forms';

// Service
import { HttpService } from './http-service'; // Http Service


import { AppComponent } from './app.component';
import {UserServiceComponent} from './userService.component';
import {UserInfoComponent} from './userInfo.component';
import {UserManageComponent} from './userManage.component';
import {ConentManageComponent} from './contentManage.component';

@NgModule({
  declarations: [
    AppComponent,
    UserServiceComponent,
    UserInfoComponent,
    UserManageComponent,
    ConentManageComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    FormsModule,
    HttpModule
  ],
  providers: [
    HttpService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
