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
import { DropfieldComponent } from './components/dropfield/dropfield.component';
import { PaginatedTableComponent } from './components/paginated-table/paginated-table.component';
import { OrderFormComponent } from './components/order-form/order-form.component';
import { ServiceOrderFormComponent } from './components/service-order-form/service-order-form.component';
import { ParameterOrderFormComponent } from './components/parameter-order-form/parameter-order-form.component';
import { CollapsibleComponent } from './components/collapsible/collapsible.component';

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
    CollapsibleComponent
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
