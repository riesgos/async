import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { BehaviorSubject, Observable } from 'rxjs';
import { UserSelfInformation } from 'src/app/backend_api/models';
import { CredentialsError, DbService, isAuthenticationError, isSuccessfulAuthentication } from 'src/app/services/db/db.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  public state$ = new BehaviorSubject<'loading' | 'done'>('loading');
  public loginResult$ = new BehaviorSubject<UserSelfInformation | null>(null);
  public loginError$ = new BehaviorSubject<CredentialsError | null>(null);

  public loginForm = new FormGroup({
    'email': new FormControl('a@b.com', [Validators.required, Validators.email]),
    'password': new FormControl('1234', [Validators.required, Validators.minLength(3)]),
  });
  public registerForm = new FormGroup({
    'email': new FormControl('a@b.com', [Validators.required, Validators.email]),
    'password': new FormControl('1234', [Validators.required, Validators.minLength(3)]),
  });

  ngOnInit(): void {
  }

  constructor(public db: DbService) {
    this.state$.next('loading');
    this.db.login('a@b.com', '1234').subscribe(result => {
      this.handleRegistrationResult(result);
    });
  }

  private handleRegistrationResult(result: UserSelfInformation | CredentialsError) {
    this.state$.next('done');
    if (isSuccessfulAuthentication(result)) {
      this.loginResult$.next(result);
      this.loginError$.next(null);
    } else if (isAuthenticationError(result)) {
      this.loginError$.next(result);
    }
  }

  doLogin() {
    const email = this.loginForm.value.email!;
    const password = this.loginForm.value.password!;
    this.state$.next('loading');
    this.db.login(email, password).subscribe(result => {
      this.handleRegistrationResult(result);
    })
  }

  doRegister() {
    const email = this.registerForm.value.email!;
    const password = this.registerForm.value.password!;
    this.state$.next('loading');
    this.db.register(email, password).subscribe(result => {
      this.handleRegistrationResult(result);
    })
  }

}
