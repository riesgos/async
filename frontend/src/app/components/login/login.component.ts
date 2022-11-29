import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, RequiredValidator, Validators } from '@angular/forms';
import { DbService } from 'src/app/services/db.service';
import { LoginService } from 'src/app/services/login.service';
import { PulsarService } from 'src/app/services/pulsar.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  public isLoggedIn: boolean = false;
  public errorMessage: string = '';
  public loginForm: UntypedFormGroup;
  public registerForm: UntypedFormGroup;
  public action: 'login' | 'register' = 'login';

  constructor(
    private loginSvc: LoginService,
    private formBuilder: UntypedFormBuilder,
    private dbSvc: DbService,
    private orderSvc: PulsarService
  ) {
    this.loginForm = this.formBuilder.group({
      email: new UntypedFormControl('', [Validators.required, Validators.email]),
      password: new UntypedFormControl('', [Validators.required]),
    });
    this.registerForm = this.formBuilder.group({
      email: new UntypedFormControl('', [Validators.required, Validators.email]),
      password: new UntypedFormControl('', [Validators.required]),
    });
  }

  ngOnInit(): void {
    this.isLoggedIn = this.loginSvc.isLoggedIn();
  }

  public login() {
    const formValue = this.loginForm.value;
    const email = formValue.email;
    const password = formValue.password;
    this.loginSvc.login(email, password).subscribe(success => {
      if (!success) {
        this.errorMessage = `Could not log in`;
        this.isLoggedIn = false;
      } else {
        this.isLoggedIn = true;
      }
    })
  }


  public register() {
    const formValue = this.registerForm.value;
    const email = formValue.email;
    const password = formValue.password;
    this.loginSvc.register(email, password).subscribe(success => {
      if (!success) {
        this.errorMessage = `Could not register`;
        this.isLoggedIn = false;
      } else {
        this.isLoggedIn = true;
      }
    })
  }
}
