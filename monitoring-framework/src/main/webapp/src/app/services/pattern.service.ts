import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})

@Injectable({ providedIn: 'root' })
export class PatternService {

  private baseUrl: string = '//localhost:8080/';
  private patternApi: string = this.baseUrl + 'patterns';

  constructor(private http: HttpClient) {
  }

  getAll(): Observable<any> {
    return this.http.get(this.patternApi + "/");
  }

  get(id: string) {
    return this.http.get(this.patternApi + '/' + id);
  }

  add(pattern: any): Observable<any> {
    return this.http.put(this.patternApi + "/", pattern, {observe: 'response'});
  }

  update(pattern: any): Observable<any> {
    return this.http.put(this.patternApi + '/', pattern, {observe: 'response'});
  }

  remove(pattern: any): Observable<any> {
    return this.http.post(this.patternApi + "/delete", pattern)
  }
}
