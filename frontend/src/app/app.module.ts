import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { ClarityModule } from "@clr/angular";
import { HttpClientModule } from '@angular/common/http';
import { MapOlModule } from '@dlr-eoc/map-ol';
import { LayerControlModule } from '@dlr-eoc/layer-control';

import { AppComponent } from "./app.component";
import { HeaderComponent } from './components/header/header.component';
import { GlobalAlertComponent } from './components/global-alert/global-alert.component';
import { AlertService } from './components/global-alert/alert.service';
import { GlobalProgressComponent } from './components/global-progress/global-progress.component';
import { ProgressService } from './components/global-progress/progress.service';
import { OrderService } from './services/order.service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { UkisRoutingModule } from "./app-routing.module";
import { CatalogViewComponent } from './views/catalog-view/catalog-view.component';
import { OrderViewComponent } from './views/order-view/order-view.component';
import { LoginComponent } from './components/login/login.component';



@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    GlobalAlertComponent,
    GlobalProgressComponent,
    CatalogViewComponent,
    OrderViewComponent,
    LoginComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    ClarityModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    UkisRoutingModule,
    MapOlModule,
    LayerControlModule
  ],
  providers: [
    AlertService,
    ProgressService,
    OrderService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
