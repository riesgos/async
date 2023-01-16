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
import { LogvizComponent } from './views/logviz/logviz.component';
import { OrderDropComponent } from './components/order-drop/order-drop.component';
import { NgxFileDropComponent, NgxFileDropModule } from 'ngx-file-drop';

@NgModule({
  declarations: [
    AppComponent,
    CurrentStateComponent,
    PlaceOrderComponent,
    LogvizComponent,
    OrderDropComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
    NgxFileDropModule,
    ApiModule.forRoot({
      rootUrl: 'http://localhost'
    })
  ],
  providers: [ApiService],
  bootstrap: [AppComponent]
})
export class AppModule { }
