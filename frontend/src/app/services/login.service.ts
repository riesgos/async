import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { DbService } from './db.service';

@Injectable({
  providedIn: 'root'
})
export class LoginService implements CanActivate {

  private userId: number = 0;

  constructor(private db: DbService) {
    const storedApiKey = localStorage.getItem('apikey');
    if (storedApiKey) this.db.setApiKey(storedApiKey);
    const storedUserId = localStorage.getItem('userid');
    if (storedUserId) this.userId = +storedUserId;
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
    return this.isLoggedIn();
  }

  public login(email: string, password: string) {
    return this.db.login(email, password).pipe(map((userData: any) => {
      localStorage.setItem('apikey', userData.apikey);
      localStorage.setItem('userid', userData.id);
      return true;
    }));
  }

  public register(email: string, password: string) {
    return this.db.register(email, password).pipe(map((userData: any) => {
      localStorage.setItem('apikey', userData.apikey);
      localStorage.setItem('userid', userData.id);
      return true;
    }));
  }

  public isLoggedIn(): boolean {
    return localStorage.getItem('apikey') !== null;
  }

  public getUserId(): number {
    return this.userId;
  }
}
