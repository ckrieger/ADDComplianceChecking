import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})

@Injectable({ providedIn: 'root' })
export class FileUploadService {

  private baseUrl: string = '//localhost:8080/';

  constructor(private http: HttpClient) {
  }

  uploadFile(file): Observable<any> {
    return this.http.post(this.baseUrl + "uploadFile", file)
  }

}
