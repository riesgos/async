import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable, tap } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LogsService {

  private email: string = "";
  private password: string = "";

  constructor(private http: HttpClient) { }

  public connect(email: string, password: string): Observable<boolean> {

    return this.http.get(`${environment.logsUrl.replace(/\/$/, "")}/current`, {
      responseType: 'text',
      headers: new HttpHeaders({
        'Content-Type':  'text/plain',
        'Authorization': 'Basic ' + btoa(`${email}:${password}`)
      })
    }).pipe(
      map(result => {
        if (!!result) {
          this.email = email;
          this.password = password;
          return true;
        }
        return false;
      })
    )
  }

  public isConnected() {
    return this.email !== "" || this.password !== "";
  }

  public readLatest(): Observable<string[]> {
    if (!this.isConnected()) throw Error(`Cannot fetch logs: no credentials given.`);

    return this.http.get(`${environment.logsUrl.replace(/\/$/, "")}/current`, {
      responseType: 'text',
      headers: new HttpHeaders({
        'Content-Type':  'text/plain',
        'Authorization': 'Basic ' + btoa(`${this.email}:${this.password}`)
      })
    }).pipe(
      map(results => {
        return results.split("\n");
      })
    );
  }

}
