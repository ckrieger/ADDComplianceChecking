import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})

@Injectable({ providedIn: 'root' })
export class TemplateService {

  private baseUrl: string = '//localhost:8080/';
  private templateApi: string = this.baseUrl + 'instrumentationTemplates';

  constructor(private http: HttpClient) {
  }

  getAll(): Observable<any> {
    return this.http.get(this.templateApi + "/");
  }

  get(id: string) {
    return this.http.get(this.templateApi + '/' + id);
  }

  add(template: any): Observable<any> {
    return this.http.put(this.templateApi + "/", template, {observe: 'response'});
  }

  update(template: any): Observable<any> {
    return this.http.put(this.templateApi + '/', template, {observe: 'response'});
  }

  remove(id: any): Observable<any> {
    return this.http.delete(this.templateApi + "/" + id)
  }

  getAllPatternsOfTemplate(id: string): Observable<any> {
    return this.http.get(this.templateApi + "/" + id + "/patterns"  )
  }

  updatePatternsOfTemplate(id: string, patterns): Observable<any> {
    return this.http.put(this.templateApi + "/" + id + "/patterns", patterns)
  }

  uploadFileOfTemplate(id: string, file): Observable<any> {
    return this.http.post(this.templateApi + "/" + id + "/uploadFile", file)
  }
}
