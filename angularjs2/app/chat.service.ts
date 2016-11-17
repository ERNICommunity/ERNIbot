import { Injectable } from '@angular/core';
import { Http, Response, Headers } from '@angular/http';
import { Message } from './message';

import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/distinctUntilChanged';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/switchMap';
import 'rxjs/add/operator/toPromise';
import { URLSearchParams, QueryEncoder } from '@angular/http';

@Injectable()
export class ChatService {

  private server: string = "https://aiaas.pandorabots.com";
  private apiUrl: string = "/talk/1409613245650/german";
  private parameter: string = "?user_key=cfb485db5f62981ea63aa4f9c5bfcea8&input=Hallo";

  private actionUrl: string;
  private headers: Headers;

  constructor(private http: Http) {
    this.actionUrl = 'http://localhost:8081/pandorabots-proxy/rest/talk';
  }


  public getResponse(text: string): Observable<Answer> {

    let params = new URLSearchParams();
    params.set('input', text);

    let response = this.http.get(this.actionUrl, {search:params}).map(res => res.json());
    
    return response;
  }

  private handleError(error: Response) {
    console.error(error);
    return Observable.throw(error.json().error || 'Server error');
  }

}

export interface Answer {
  status: string,
  ok: string,
  responses: Array<string>,
  sessionid: number
}

