import { Injectable } from '@angular/core';
import { map } from 'rxjs/operators';
import { DbService } from './db.service';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  constructor(private db: DbService) { }

  public login(email: string, password: string) {
    return this.db.login(email, password).pipe(map((userData: any) => {
      this.db.setApiKey(userData.apikey);
      return true;
    }));
  }

  public register(email: string, password: string) {
    return this.db.register(email, password).pipe(map((userData: any) => {
      this.db.setApiKey(userData.apikey);
      return true;
    }));
  }

  public isLoggedIn(): boolean {
    return this.db.getApiKey() !== '';
  }
}
