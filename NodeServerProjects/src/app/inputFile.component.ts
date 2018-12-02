// inputFile.component

// 모든 관리 페이지의 이미지 업로드를 위한 컴포넌트다.
// 모든 관리 페이지의 관리 테이블은 ng2-smart-table 모듈을 사용하여 생성된다.
// 하지만 해당 모듈에서는 이미지 업로드 기능을 제공하지 않아 직접 기능을 구현하였다.
// 관리 페이지의 테이블 setting을 보면 editor 부분에 inputFile을 렌더링 하는 것을 볼 수 있다.
// 이는 수정 버튼 또는 생성 버튼 때만 업로드 기능이 작동하도록 구현한 것이다.
// 사용자는 이미지를 업로드하고 수정/생성 버튼을 누르면 이미지는 서버에 업로드된 후 이미지 이름을 사용자 정보에 저장한다.
// 이 때 이미지 이름을 사용자 정보에 저장하기 위해 InputFile은 이미지 이름을 DataService를 통해 부모 컴포넌트인 관리자 컴포넌트에 전송한다.
// 이 또한 매우 복잡한 로직이므로 직접 코드를 분석해보는 것을 추천한다.

import {Component, OnInit, Input, Output, EventEmitter, ElementRef, OnDestroy} from '@angular/core';
import { HttpService } from './http-service';
import { Http } from '@angular/http';
import { Cell, DefaultEditor } from 'ng2-smart-table';
import {DataService} from './data.service';
import { FileUploader } from 'ng2-file-upload/ng2-file-upload';

@Component({
    selector: 'input-file',
    template: `
        <input id = 'photo' (change)="change($event)" (onStopEditing) = "stopEditing($event)" type="file" [ngModel]="cell.value.value">
  `
})
export class InputFileComponent extends DefaultEditor implements OnInit, OnDestroy {

    @Input() cell: Cell; // 해당 셀 (하난)
    @Input() inputClass: string;

    @Output() onStopEditing = new EventEmitter<any>();
    @Output() onEdited = new EventEmitter<any>();
    @Output() onClick = new EventEmitter<any>();

    public uploader: FileUploader = new FileUploader({url: '/photo', itemAlias: 'photo',allowedMimeType: ['image/png', 'image/gif', 'video/mp4', 'image/jpeg'] });

    subscription = null;
    changeState: boolean;
    constructor(
            private httpService: HttpService,
            private dataService: DataService,
            private http: Http,
            private el: ElementRef
    ){
    super();
    }

    // 이벤트 함수
    // 업로드에 변화가 올경우 실행됨
    public change(event) {
        let value = event.srcElement.value
        this.rightValueChange(value);
        this.changeState = true;
    }
    rightValueChange(value) {
        this.cell.newValue = value;
    }
    stopEditing(event){
    }
    ngOnInit() {
        this.subscription = this.dataService.notifyObservable$.subscribe((res) => {
            if (res.hasOwnProperty('option') && res.option === 'image') {
                // perform your other action from here
                    // 사용자가 수정/생성 버튼을 누를 경우

                    // 이미지 변화가 있을 경우
                    if(this.changeState == true){
                        // 이미지를 업로드한다.
                        this.upload().then(response => {
                            this.rightValueChange(response);

                            // 부모 컴포넌트인 관리자 컴포넌트에 해당 이미지 파일이름을 전달한다.
                            // 이는 관리자 컴포넌트에서 직접 데이터베이스에 저장 요청을 하는 http 함수가 있기 때문이다.
                            this.dataService.notifyOther_parent({option: 'image', value: response});

                            this.subscription.unsubscribe();
                        })

                    }else {
                        // 정보를 생성했는데 이미지를 업로드 하지 않은 경우 Default 사진을 준다.
                        if(this.cell.newValue ==  ""){
                            if(res.from == 'device'){
                                this.rightValueChange('deviceDefault.png');
                            }else if(res.from == 'user'){
                                this.rightValueChange('userDefault.png');
                            }else if(res.from == 'group'){
                                this.rightValueChange('groupDefault.png');
                            }
                            console.log("default Image");
                        }
                        var response = this.cell.newValue;

                        // 부모 컴포넌트인 관리자 컴포넌트에 해당 이미지 파일이름을 전달한다.
                        // 이는 관리자 컴포넌트에서 직접 데이터베이스에 저장 요청을 하는 http 함수가 있기 때문이다.
                        this.dataService.notifyOther_parent({option: 'image', value: response});
                        this.subscription.unsubscribe();
                    }
                };
            });
        //override the onAfterAddingfile property of the uploader so it doesn't authenticate with //credentials.
        this.uploader.onAfterAddingFile = (file)=> { file.withCredentials = false; };
        //overide the onCompleteItem property of the uploader so we are
        //able to deal with the server response.
        this.uploader.onCompleteItem = (item:any, response:any, status:any, headers:any) => {
            console.log("ImageUpload:uploaded:", item, status, response);
        };
    }
    ngOnDestroy(){
        this.subscription.unsubscribe();
    }

    // 서버에 이미지를 업로드 하는 기능
    // HTTP 통신을 통해 파일을 전송한다.
    upload() {
        // Promise 함수를 사용하여 우선 이미지 업로드가 완료되면 resolve()한다.
        return new Promise ((resolve, reject)=>{
            //locate the file element meant for the file upload.
            let inputEl: HTMLInputElement = this.el.nativeElement.querySelector('#photo');
            //get the total amount of files attached to the file input.
            let fileCount: number = inputEl.files.length;
            //create a new fromdata instance
            let formData = new FormData();
            console.log(inputEl.files);
            //check if the filecount is greater than zero, to be sure a file was selected.
            if (fileCount > 0) { // a file was selected
                //append the key name 'photo' with the first file in the element
                formData.append('photo', inputEl.files.item(0));
                //call the angular http method
                this.http
                //post the form data to the url defined above and map the response. Then subscribe //to initiate the post. if you don't subscribe, angular wont post.
                    .post('/photo', formData).subscribe(
                    //map the success function and alert the response
                    (result) => {
                        console.log(JSON.parse(JSON.stringify(result))._body);
                        resolve(JSON.parse(JSON.stringify(result))._body);
                    },
                    (error) => {
                        alert(error);
                        console.log('error');
                        resolve();
                    }
                )
            }else{
                resolve();
            }
        })
    }
}
