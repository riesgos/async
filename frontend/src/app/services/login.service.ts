import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { DbService } from './db.service';

@Injectable({
  providedIn: 'root'
})
export class LoginService implements CanActivate {

  constructor(private db: DbService) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
    return this.isLoggedIn();
  }

  public login(email: string, password: string) {
    return this.db.login(email, password).pipe(map((userData: any) => {
      return true;
    }));
  }

  public register(email: string, password: string) {
    return this.db.register(email, password).pipe(map((userData: any) => {
      return true;
    }));
  }

  public isLoggedIn(): boolean {
    return this.db.getApiKey() !== '';
  }
}
