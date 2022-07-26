import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { ClarityModule } from "@clr/angular";
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



@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    GlobalAlertComponent,
    GlobalProgressComponent,
    CatalogViewComponent,
    OrderViewComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    ClarityModule,
    FormsModule,
    ReactiveFormsModule,
    UkisRoutingModule
  ],
  providers: [
    AlertService,
    ProgressService,
    OrderService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
