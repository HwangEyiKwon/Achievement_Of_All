// Calendar.component

// ContentManage.component의 자식 컴포넌트다.
// 컨텐츠 생성 시 종료 날짜를 적을 수 있는 컴포넌트다.

import {Component, OnInit, Input, OnDestroy} from '@angular/core';
import { DefaultEditor } from 'ng2-smart-table';
import {DataService} from './data.service';

@Component({
  selector: 'app-calendar-end-component',
  template: `
    <select name="year" [ngModel]="cell_y" (change)="changeValueY($event.target.value)">
      <option value="2018" selected="selected">2018</option>
      <option value="2019">2019</option>
      <option value="2020">2020</option>
    </select>
    <select name="month" [ngModel]="cell_m" (change)="changeValueM($event.target.value)">
      <option value="0" selected="selected">1</option>
      <option value="1">2</option>
      <option value="2">3</option>
      <option value="3">4</option>
      <option value="4">5</option>
      <option value="5">6</option>
      <option value="6">7</option>
      <option value="7">8</option>
      <option value="8">9</option>
      <option value="9">10</option>
      <option value="10">11</option>
      <option value="11">12</option>

    </select>
    <select name="day" [ngModel]="cell_d" (change)="changeValueD($event.target.value)">
      <option value="1" selected="selected">1</option>
      <option value="2">2</option>
      <option value="3">3</option>
      <option value="4">4</option>
      <option value="5">5</option>
      <option value="6">6</option>
      <option value="7">7</option>
      <option value="8">8</option>
      <option value="9">9</option>
      <option value="10">10</option>
      <option value="11">11</option>
      <option value="12">12</option>
      <option value="13">13</option>
      <option value="14">14</option>
      <option value="15">15</option>
      <option value="16">16</option>
      <option value="17">17</option>
      <option value="18">18</option>
      <option value="19">19</option>
      <option value="20">20</option>
      <option value="21">21</option>
      <option value="22">22</option>
      <option value="23">23</option>
      <option value="24">24</option>
      <option value="25">25</option>
      <option value="26">26</option>
      <option value="27">27</option>
      <option value="28">28</option>
      <option value="29">29</option>
      <option value="30">30</option>
      <option value="31">31</option>

    </select>
  `
})
export class CalendarEndComponent extends DefaultEditor implements OnInit, OnDestroy {

  cell_y: String; // 해당 셀 (년)
  cell_m: String; // 해당 셀 (월)
  cell_d: String; // 해당 셀 (일)

  @Input() inputClass: string;

  subscription = null;

  constructor(
    private dataService: DataService
  ){
    super();
  }

  changeValueY(event){
    this.cell_y = event;
  }
  changeValueM(event){
    this.cell_m = event;
  }
  changeValueD(event){
    this.cell_d = event;
  }

  ngOnInit() {

    this.subscription = this.dataService.notifyObservableEd$.subscribe((user) => {

      // 셀의 날짜 데이터를 받아온다.
      var ed = {year: this.cell_y, month: this.cell_m, day: this.cell_d};

      // 부모 컴포넌트에 정보를 넘긴다.
      this.dataService.notifyOtherEd_parent({option: 'endDate', date: ed});

      if(this.subscription != null)
      this.subscription.unsubscribe();
    });
  }
  ngOnDestroy(){
      if(this.subscription != null)
       this.subscription.unsubscribe();
  }

}
