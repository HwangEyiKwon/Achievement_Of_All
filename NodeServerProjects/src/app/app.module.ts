// App.module

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
import { AppComponent } from './app.component'; // Root Component
import { UserServiceComponent } from './userService.component'; // Login/SignUp
import { UserPageComponent } from './userPage.component'; // User Page
import { UserInfoComponent } from './userInfo.component'; // User Info
import { UserManageComponent } from './userManage.component'; // User Manage
import { ContentManageComponent } from './contentManage.component'; // Component Manage
import { ReportManageComponent } from './reportManage.component'; // Report Manage
import { AboutComponent } from './about.component'; // About
import { ErrorComponent } from './error.component'; // Error Page

// Sub Component
import { CalendarComponent } from './calendar.component'; // User Manage -> Start Date
import { InputFileComponent } from './inputFile.component'; // User Manage -> Image Upload
import { CalendarEndComponent } from './calendar.end.component'; // User Manage -> End Date
import { ReportVideoComponent } from './reportVideo.component'; // Report Manage -> Video

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
