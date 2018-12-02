import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpModule } from '@angular/http';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app.routing.module';
import { FormsModule } from '@angular/forms';
import { Ng2SmartTableModule } from 'ng2-smart-table';
import { ImageUploadModule } from 'angular2-image-upload';


// Service
import { HttpService } from './http-service'; // Http Service
import { DataService } from './data.service'; // Data Service

import { AppComponent } from './app.component';
import {UserServiceComponent} from './userService.component';
import {UserPageComponent} from './userPage.component';
import {UserInfoComponent} from './userInfo.component';
import {UserManageComponent} from './userManage.component';

import { InputFileComponent } from './inputFile.component'; // for Image Upload

// import {ConentManageComponent} from './contentManage.component';

@NgModule({
  declarations: [
    AppComponent,
    UserServiceComponent,
    UserPageComponent,
    UserInfoComponent,
    UserManageComponent,
    InputFileComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    FormsModule,
    HttpModule,
    Ng2SmartTableModule,
    ImageUploadModule
  ],
  providers: [
    HttpService,
    DataService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
