// Module
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

// Component
import { AppComponent } from './app.component';
import { AboutComponent } from './about.component'; // User Page (About)
import { UserServiceComponent } from './userService.component';
import { UserPageComponent } from './userPage.component';
import { UserInfoComponent } from './userInfo.component';
import { UserManageComponent } from './userManage.component';
import { InputFileComponent } from './inputFile.component'; // for Image Upload
import { ErrorComponent } from './error.component'; // Error Page

// Sub Component
import {ContentManageComponent} from './contentManage.component';
import {CalendarComponent} from './calendar.component';
import {CalendarEndComponent} from './calendar.end.component';
import {ReportManageComponent} from './reportManage.component';
import {ReportVideoComponent} from './reportVideo.component';

@NgModule({
  declarations: [
    AppComponent,
    UserServiceComponent,
    UserPageComponent,
    UserInfoComponent,
    UserManageComponent,
    ContentManageComponent,
    ReportManageComponent,
    ErrorComponent,
    InputFileComponent,
    CalendarComponent,
    CalendarEndComponent,
    AboutComponent,
    ReportVideoComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    FormsModule,
    HttpModule,
    Ng2SmartTableModule,
    ImageUploadModule.forRoot(),
  ],
  providers: [
    HttpService,
    DataService
  ],
  bootstrap: [AppComponent],
  entryComponents: [
    InputFileComponent,
    CalendarComponent,
    CalendarEndComponent,
    ReportVideoComponent
  ]
})
export class AppModule { }
