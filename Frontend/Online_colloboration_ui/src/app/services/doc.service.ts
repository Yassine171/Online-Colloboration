import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Doc } from '../models/Doc';


@Injectable({
  providedIn: 'root'
})
export class DocService {

  private baseUrl = 'http://localhost:8081/api/documents';

  constructor(private http: HttpClient) { }

  createDoc(doc: Doc): Observable<Doc> {
    return this.http.post<Doc>(`${this.baseUrl}/new`, doc);
  }
}
