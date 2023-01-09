import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ApiModule } from './backend_api/api.module';
import { ApiService } from './backend_api/services';
import { CurrentStateComponent } from './views/current-state/current-state.component';
import { PlaceOrderComponent } from './views/place-order/place-order.component';

@NgModule({
  declarations: [
    AppComponent,
    CurrentStateComponent,
    PlaceOrderComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
    ApiModule.forRoot({
      rootUrl: 'http://localhost'
    })
  ],
  providers: [ApiService],
  bootstrap: [AppComponent]
})
export class AppModule { }
