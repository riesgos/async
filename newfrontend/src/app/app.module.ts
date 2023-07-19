import { HttpClientModule } from '@angular/common/http';
import { APP_INITIALIZER, NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ApiModule } from './services/backend/backend_api/api.module';
import { ApiService } from './services/backend/backend_api/services';
import { CurrentStateComponent } from './views/current-state/current-state.component';
import { PlaceOrderComponent } from './views/place-order/place-order.component';
import { DropfieldComponent } from './components/dropfield/dropfield.component';
import { PaginatedTableComponent } from './components/paginated-table/paginated-table.component';
import { OrderFormComponent } from './components/order-form/order-form.component';
import { ServiceOrderFormComponent } from './components/order-form/service-order-form/service-order-form.component';
import { ParameterOrderFormComponent } from './components/order-form/parameter-order-form/parameter-order-form.component';
import { CollapsableComponent } from './components/collapsable/collapsable.component';
import { LatestComponent } from './components/latest/latest.component';
import { LoginComponent } from './components/login/login.component';
import { environment } from 'src/environments/environment';
import { TabsComponent } from './components/tabs/tabs.component';
import { TabComponent } from './components/tabs/tab.component';
import { LogsComponent } from './views/logs/logs.component';
import { PrecalcFormComponent } from './components/precalc-form/precalc-form.component';
import { PrecalcFormEntryComponent } from './components/precalc-form/precalc-form-entry/precalc-form-entry.component';
import { PrecalcFormRangeComponent } from './components/precalc-form/precalc-form-range/precalc-form-range.component';

@NgModule({
  declarations: [
    AppComponent,
    CurrentStateComponent,
    PlaceOrderComponent,
    DropfieldComponent,
    PaginatedTableComponent,
    OrderFormComponent,
    ServiceOrderFormComponent,
    ParameterOrderFormComponent,
    CollapsableComponent,
    LatestComponent,
    LoginComponent,
    TabsComponent,
    TabComponent,
    LogsComponent,
    PrecalcFormComponent,
    PrecalcFormEntryComponent,
    PrecalcFormRangeComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
    ApiModule.forRoot({
      rootUrl: new URL(environment.fastApiUrl.replace(/([^:]\/)\/+/g, "$1").replace(/\/$/, '')).toString()  // making sure that properly formatted url
    })
  ],
  providers: [
    ApiService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
