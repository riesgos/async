import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { map, Observable } from 'rxjs';
import { AppStateService, AppState } from 'src/app/services/appstate/appstate.service';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  public loginState$: Observable<{ 
    state: AppState["authentication"],
    data: AppState["authenticationData"]
  }>;
  public loginForm = new FormGroup({
    'email': new FormControl('a@b.com', [Validators.required, Validators.email]),
    'password': new FormControl('1234', [Validators.required, Validators.minLength(3)]),
  });


  constructor(private state: AppStateService) {
    this.loginState$ = state.state.pipe(map(s => {
      return {
        state: s.authentication,
        data: s.authenticationData
      }
    }));
    state.state.pipe(map(s => {
      return {
        email: s.localStoreData['email'],
        password: s.localStoreData['password']
      }
    })).subscribe(r => {
      if (r.email) {
        console.log("Setting email from locally stored value");
        this.loginForm.controls.email.setValue(r.email);
      }
      if (r.password) {
        console.log("Setting password from locally stored value");
        this.loginForm.controls.password.setValue(r.password);
      }
    });
  }

  ngOnInit(): void {}

  doLogin() {
    const email = this.loginForm.value.email!;
    const password = this.loginForm.value.password!;
    this.state.action({
      type: 'loginStart',
      payload: {
        email: email,
        password: password
      }
    });
  }

}
