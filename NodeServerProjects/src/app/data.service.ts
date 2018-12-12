// Data Service

// Data 공유 함수들을 모아놓은 서비스
// 컴포넌트 사이의 Data 공유로 Data Binding, Observer을 통한 Data 공유 등이 있는데 여기서는 Observer를 통해 Data를 주고 받는 서비스를 구현했다
// Dependency Injection 개념을 통해 Data 공유가 필요한 컴포넌트에서 필요 함수를 주입가능하도록 함.

import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Subject } from 'rxjs/Subject';

@Injectable()
export class DataService {


  private dataObs = new BehaviorSubject({});
  currentMessage = this.dataObs.asObservable();

  //------------------------------------------------------------------------


  private notify = new Subject<any>();
  notifyObservable$ = this.notify.asObservable();

  private notify_parent = new Subject<any>();
  notifyObservable$_parent = this.notify_parent.asObservable();
  //------------------------------------------------------------------------


  private notifyStd = new BehaviorSubject({});
  notifyObservableStd$ = this.notifyStd.asObservable();

  private notifyStd_parent = new BehaviorSubject({});
  notifyObservableStd$_parent = this.notifyStd_parent.asObservable();
  //------------------------------------------------------------------------


  private notifyEd = new BehaviorSubject({});
  notifyObservableEd$ = this.notifyEd.asObservable();

  private notifyEd_parent = new BehaviorSubject({});
  notifyObservableEd$_parent = this.notifyEd_parent.asObservable();
  //------------------------------------------------------------------------


  private notifyReport = new Subject<any>();
  notifyObservableReport$ = this.notifyReport.asObservable();

  private notifyReport_parent = new Subject<any>();
  notifyObservableReport$_parent = this.notifyReport_parent.asObservable();
  //------------------------------------------------------------------------


  //------------------------------------------------------------------------

  updateData(data: Object) {
    this.dataObs.next(data);
  }
  //------------------------------------------------------------------------


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

  public notifyOtherStd(data: any) {
    if (data) {
      this.notifyStd.next(data);
    }
  }
  public notifyOtherStd_parent(data: any) {
    if (data) {
      console.log(data);
      this.notifyStd_parent.next(data);
    }
  }

  public notifyOtherEd(data: any) {
    if (data) {
      this.notifyEd.next(data);
    }
  }
  public notifyOtherEd_parent(data: any) {
    if (data) {
      this.notifyEd_parent.next(data);
    }
  }
  //------------------------------------------------------------------------

  public notifyOtherReport(data: any) {
    if (data) {
      this.notifyReport.next(data);
    }
  }
  public notifyOtherReport_parent(data: any) {
    if (data) {
      this.notifyReport_parent.next(data);
    }
  }
  //------------------------------------------------------------------------

}

