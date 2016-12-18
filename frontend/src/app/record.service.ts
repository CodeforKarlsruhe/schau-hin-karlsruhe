import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import {Observable} from "rxjs";

@Injectable()
export class RecordService {

  constructor (private http: Http) {}

  private apiUrl = "http://localhost:3000/";

  public createRecord(record) {
    let headers      = new Headers({ 'Content-Type': 'application/json' });
    let options       = new RequestOptions({ headers: headers });
    console.log("Erstelle Record");
    return this.http.post(this.apiUrl, record, options)
        .map((res:Response) => res.json())
        .catch((error:any) => Observable.throw(error.json().error || 'Server error'));
  }
}
