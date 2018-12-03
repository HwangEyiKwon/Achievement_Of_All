// Http Service

// 서버와의 HTTP 통신과 관련된 모든 함수들을 모아놓은 서비스
// Dependency Injection 개념을 통해 HTTP 통신이 필요한 컴포넌트에서 필요 함수 객체를 주입가능하도록 함.
// 개념 이해, 숙지 후 보는 것이 좋을 거 같음

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Http, RequestOptions, ResponseContentType } from '@angular/http';
import { Observable } from 'rxjs/Rx';

@Injectable()
export class HttpService {

  constructor(private http: HttpClient,
              private http2: Http) {}

  // ------------------------------------------------------------------------
  // 컴포넌트 공통으로 사용되는 함수
  sessionCheck() {
      // request에 담긴 session을 통해 서버에게 세션 체크를 요청
      return this.http.get('/sessionCheck');
  }
  authorityCheck(){
      // 접근 제어를 위한 함수
      return this.http.get('/authorityCheck');
  }
  photo(formData: FormData){
      // 이미지 업로드를 하기 위한 함수
      return this.http.post('/photo',{
          formData: formData
      });
  }
  downloadFile(bt, id): Observable<Blob> {
      // 파일 (엑셀) 다운로드 관련 함수
      let options = new RequestOptions({responseType: ResponseContentType.Blob });
      return this.http2.get( 'downloadCertification/' + bt + '/' + id, options)
          .map(res => res.blob());

  }
  // ------------------------------------------------------------------------


  // ------------------------------------------------------------------------
  // userService 에서 사용되는 함수
  userLogin(userEmail: string, userPassword: string) {
    // 사용자 로그인
    // 로그인과 동시에 세션을 등록
    return this.http.post('/managerLogin', {
      email: userEmail,
      password: userPassword
    });
  }
  userLogout() {
    // 사용자 로그아웃
    // 로그아웃과 동시에 세션 파괴
    return this.http.get('/managerLogout');
  }
  userSignUp(userName: string, userPhoneNumber: string, userPassword: string, userEmail: string, managerKey: string ) {
    // 사용자 등록 (회원 가입)
    return this.http.post('/managerSignUp', {
      name: userName,
      email: userEmail,
      password: userPassword,
      managerKey: managerKey,
      phoneNumber: userPhoneNumber
    });
  }
  changePassword(userEmail: string) {
      // 비밀번호 찾기를 위한 이메일 인증
      return this.http.get('/pwdSendMail/' + userEmail);
  }



  // ------------------------------------------------------------------------


  // ------------------------------------------------------------------------
  // userPage에서 사용되는 함수
  getUserInfo() {
      // 사용자 페이지에 사용자 정보 로드
      return this.http.get('/getManagerInfo');
  }
  // ------------------------------------------------------------------------


  // ------------------------------------------------------------------------
  // userInfo에서 사용되는 함수
  changeUserInfo(name: string, email: string, phoneNumber: string, imagePath: Object) { // 사용자 페이지(사용자 관리) 사용자들 정보 로드
      return this.http.post('/managerInfoEdit', {
          name: name,
          email: email,
          phoneNumber: phoneNumber,
          imagePath: imagePath
      });
  }
  changeUserPassword(currentPassword: string, newPassword: string,  newPasswordCheck: string){
      return this.http.post('/managerPasswordEdit', {
          currentPassword: currentPassword,
          newPassword: newPassword,
          newPasswordCheck: newPasswordCheck
      });
  }
  // ------------------------------------------------------------------------

  // ------------------------------------------------------------------------
  // userManage에서 사용되는 함수
  getUsersInfo() {
      // 사용자 페이지(사용자 관리) 사용자들 정보 로드
    return this.http.get('/getManagersInfo');
  }

  deleteUserInfo(userName: string, userEmail: string){
      // 사용자 페이지 (사용자 관리) 사용자 정보 제거
      return this.http.post('/deleteManagerInfo',{
          name: userName,
          email: userEmail,
      });
  }
  updateUserInfo(userInfo: Object, userNewInfo: Object){
      // 사용자 페이지 (사용자 관리) 사용자 정보 수정
      return this.http.post('/updateManagerInfo', {
          userInfo, userNewInfo
      });
  }
  addUserInfo(userNewInfo: Object){
      // 사용자 페이지 (사용자 관리) 사용자 정보 추가
      return this.http.post('/addUserInfo',{
          userNewInfo
      });
  }
  // ------------------------------------------------------------------------


  // ------------------------------------------------------------------------
  // groupManage에서 사용되는 함수
  deleteGroupInfo(groupName: string){
      // 사용자 페이지(그룹 관리) 그룹 정보 삭제
      return this.http.post('/deleteGroupInfo',{
          groupName: groupName
      });
  }
  updateGroupInfo(groupInfo: Object, groupNewInfo: Object){
      // 사용자 페이지(그룹 관리) 그룹 정보 수정
      return this.http.post('/updateGroupInfo',{
          groupInfo, groupNewInfo
      });
  }
  addGroupInfo(groupNewInfo: Object){
      // 사용자 페이지(그룹 관리) 그룹 정보 추가
      return this.http.post('/addGroupInfo',{
          groupNewInfo
      });
  }
  // ------------------------------------------------------------------------


  // ------------------------------------------------------------------------
  // deviceManage에서 사용되는 함수

  getContentsInfo() {
      // 사용자 페이지(디바이스 관리, 그룹 관리) 디바이스 정보 로드
      return this.http.get('/getContentsInfo');
  }








  deleteDeviceInfo(topicName: string, deviceID: string, group: string){
      // 사용자 페이지 (디바이스 관리)  제거
      return this.http.post('/deleteDeviceInfo',{
          topicName: topicName,
          deviceID: deviceID,
          group: group
      });
  }
  updateDeviceInfo(deviceInfo: Object, deviceNewInfo: Object){
      // 사용자 페이지 (디바이스 관리) 수정
      return this.http.post('/updateDeviceInfo',{
          deviceInfo,deviceNewInfo
      });
  }
  addDeviceInfo(deviceNewInfo: Object){
      // 사용자 페이지 (디바이스 관리) 추가
      return this.http.post('/addDeviceInfo',{
          deviceNewInfo
      });
  }
  // ------------------------------------------------------------------------


  // ------------------------------------------------------------------------
  // deviceInfo에서 사용되는 함수

  getAllGroupNameImage() { // Group 정보 (Admin 전용)
    return this.http.get('/getAllGroupNameImage');
  }
  getAllDevicesData(groupName) { // Group 내 Device 정보 (groupAdmin,Admin 전용)
    console.log('GroupName: ' + groupName);
    return this.http.get('/getAllDevicesData/' + groupName);
  }
  getAllDeviceData(id) { // 모든 Device 정보
    console.log('DeviceID: ' + id);
    return this.http.get('/getAllDeviceData/' + id);
  }

  getDeviceInfo(id){ // Device 정보
    return this.http.get('/getDeviceInfo/' + id);
  }

  getMacAddress(){ // MacAddress 요청
      return this.http.get('/getMacAddress');
  }

  updateDevicePlugStatus(id, e){ // Device plug 상태 변경
    return this.http.post('/updateDevicePlugStatus/' + id, {
      e
    });
  }
  reload(id){ // reload 버튼
    return this.http.get('/reload/'+id);
  }
  getManualKey(id){ // Manual Key 갱신
      return this.http.get('/getManualKey/' + id);
  }
  // ------------------------------------------------------------------------


  // ------------------------------------------------------------------------
  // Farm에서 사용되는 함수
  getFarmData(){
      // Farm 측정 값들 볼러오는 함수
    return this.http.get('/getFarmData');
  }
  // ------------------------------------------------------------------------
}
