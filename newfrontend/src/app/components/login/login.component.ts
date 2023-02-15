import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { BehaviorSubject, Observable } from 'rxjs';
import { UserSelfInformation } from 'src/app/backend_api/models';
import { DbService } from 'src/app/services/db/db.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  public loginResult$ = new BehaviorSubject<UserSelfInformation | null>(null);
  public loginForm = new FormGroup({
    'email': new FormControl('a@b.com', [Validators.required, Validators.email]),
    'password': new FormControl('1234', [Validators.required, Validators.minLength(3)]),
  });

  constructor(public db: DbService) {
    this.db.login('a@b.com', '1234').subscribe(result => {
      console.log('login', result);
      this.loginResult$.next(result);
    });
  }

  ngOnInit(): void {
  }

  doLogin() {
    const email = this.loginForm.value.email!;
    const password = this.loginForm.value.password!;
    this.db.login(email, password).subscribe(result => {
      console.log('login', result);
      this.loginResult$.next(result);
    })
  }

}
