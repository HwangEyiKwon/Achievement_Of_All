// Data Service

// Data 공유 함수들을 모아놓은 서비스
// 컴포넌트 사이의 Data 공유로 Data Binding, Observer을 통한 Data 공유 등이 있는데 여기서는 Observer를 통해 Data를 주고 받는 서비스를 구현했다
// Dependency Injection 개념을 통해 Data공유가 필요한 컴포넌트에서 필요 함수를 주입가능하도록 함.
// 개념 이해, 숙지 후 보는 것이 좋을 거 같음

import { Injectable , Inject} from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Subject } from 'rxjs/Subject';
import * as io from 'socket.io-client';
import {Observable} from 'rxjs/Rx';
import * as myGlobals from './global.service';

@Injectable()
export class DataService {


  //------------------------------------------------------------------------
  // Device Info Component <-> selectGroup, selectDevice Component
  private dataObs = new BehaviorSubject({});
  currentMessage = this.dataObs.asObservable();

  private dataObs_device = new BehaviorSubject({});
  currentMessage_device = this.dataObs_device.asObservable();

  private dataObs_group = new BehaviorSubject({});
  currentMessage_group = this.dataObs_group.asObservable();

  updateData(data: Object) {
      this.dataObs.next(data);
  }
  updateData_device(data: Object) {
      console.log(JSON.stringify(data));
      this.dataObs_device.next(data);
      console.log(this.currentMessage_device);
  }
  updateData_group(data: Object) {
      this.dataObs_group.next(data);
  }
  //------------------------------------------------------------------------


  //------------------------------------------------------------------------
  // Manange Component <-> InputFile Component
  private notify = new Subject<any>();
  notifyObservable$ = this.notify.asObservable();

  private notify_parent = new Subject<any>();
  notifyObservable$_parent = this.notify_parent.asObservable();

  public notifyOther(data: any) {
      if (data) {
          this.notify.next(data);
      }
  }
  public notifyOther_parent(data: any) {
      if (data) {
          this.notify_parent.next(data);
      }
  }
  //------------------------------------------------------------------------


  //------------------------------------------------------------------------
  // Socket 통신
  // 디바이스 페이지에서 reload, plug, manual key 업데이틀 를 위한 소켓 통신이다

  private url = myGlobals.serverPath; // 소캣 경로
  private socket; // 소켓

  sendMessage(message) {
      this.socket.emit('add-message', message); // emit
      console.log('send Message: ' + message);
  }

  getMessages() {
      let observable = new Observable(observer => {
          this.socket = io(this.url); // 소켓 생성
          this.socket.on('message', (data) => { // 대기 중
              console.log('get Message: ' + data);
              observer.next(data);
          });
          return () => {
              this.socket.disconnect();
          };
      })
      return observable;
  }
  //------------------------------------------------------------------------


  //------------------------------------------------------------------------
  // Farm Chart Component
  private dataObs_farm = new BehaviorSubject({});
  currentMessage_farm = this.dataObs_farm.asObservable();
  updateData_farm(data: Object){
      this.dataObs_farm.next(data);
  }
  //------------------------------------------------------------------------

}

