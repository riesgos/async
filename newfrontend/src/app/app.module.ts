import { HttpClientModule } from '@angular/common/http';
import { APP_INITIALIZER, NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ApiModule } from './backend_api/api.module';
import { ApiService } from './backend_api/services';
import { CurrentStateComponent } from './views/current-state/current-state.component';
import { PlaceOrderComponent } from './views/place-order/place-order.component';
import { LogvizComponent } from './views/logviz/logviz.component';
import { DropfieldComponent } from './components/dropfield/dropfield.component';
import { PaginatedTableComponent } from './components/paginated-table/paginated-table.component';
import { OrderFormComponent } from './components/order-form/order-form.component';
import { ServiceOrderFormComponent } from './components/service-order-form/service-order-form.component';
import { ParameterOrderFormComponent } from './components/parameter-order-form/parameter-order-form.component';
import { CollapsableComponent } from './components/collapsable/collapsable.component';
import { LatestComponent } from './components/latest/latest.component';
import { LoginComponent } from './components/login/login.component';
import { ConfigService } from './services/config/config.service';

@NgModule({
  declarations: [
    AppComponent,
    CurrentStateComponent,
    PlaceOrderComponent,
    LogvizComponent,
    DropfieldComponent,
    PaginatedTableComponent,
    OrderFormComponent,
    ServiceOrderFormComponent,
    ParameterOrderFormComponent,
    CollapsableComponent,
    LatestComponent,
    LoginComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
    ApiModule.forRoot({
      rootUrl: '/backend'
    })
  ],
  providers: [
    {
      multi: true,
      provide: APP_INITIALIZER,
      deps: [ConfigService],
      useFactory: (configService: ConfigService) => {
        return () => configService.loadConfig();
      }
    },
    ApiService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
