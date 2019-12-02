import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})

@Injectable({ providedIn: 'root' })
export class EventTypeService {

  private baseUrl: string = '//localhost:8080/';
  private eventTypeApi: string = this.baseUrl + 'eventTypes';

  constructor(private http: HttpClient) {
  }

  getAll(): Observable<any> {
    return this.http.get(this.eventTypeApi + "/");
  }

  get(id: string) {
    return this.http.get(this.eventTypeApi + '/' + id);
  }

  add(eventType: any): Observable<any> {
    return this.http.put(this.eventTypeApi + "/", eventType, {observe: 'response'});
  }

  update(eventType: any): Observable<any> {
    return this.http.put(this.eventTypeApi + '/', eventType, {observe: 'response'});
  }

  remove(id: any): Observable<any> {
    return this.http.delete(this.eventTypeApi + "/" + id)
  }

}
