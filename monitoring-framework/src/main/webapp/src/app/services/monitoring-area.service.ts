import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})

@Injectable({ providedIn: 'root' })
export class MonitoringAreaService {

  private baseUrl: string = '//localhost:8080/';
  private patternApi: string = this.baseUrl + 'monitoring-areas';

  constructor(private http: HttpClient) {
  }

  getAll(): Observable<any> {
    return this.http.get(this.patternApi + "/");
  }

  get(id: string) {
    return this.http.get(this.patternApi + '/' + id)
  }

  add(monitoringArea: any): Observable<any> {
    return this.http.post(this.patternApi + "/", monitoringArea);
  }

  update(monitoringArea: any): Observable<any> {
    return this.http.put(this.patternApi + '/' + monitoringArea.id, monitoringArea)
  }

  remove(monitoringArea: any): Observable<any> {
    return this.http.post(this.patternApi + '/remove', monitoringArea);
  }
}
