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
import { PulsarService } from './services/pulsar.service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { UkisRoutingModule } from "./app-routing.module";
import { CatalogViewComponent, FilterOnIdPipe } from './views/catalog-view/catalog-view.component';
import { OrderViewComponent } from './views/order-view/order-view.component';
import { LoginComponent } from './components/login/login.component';
import { CurrentStateComponent } from './views/current-state/current-state.component';

import { VerticalNavResizeComponent } from './components/vertical-nav-resize/vertical-nav-resize.component';
import { NavResizeDirectiveDirective } from './directives/nav-resize-directive/nav-resize-directive.directive';

// loading an icon from the "core set" now must be done manually
import { coreCollectionIcons, essentialCollectionIcons, ClarityIcons, travelCollectionIcons } from "@cds/core/icon";
import { MultiOrderViewComponent } from './views/multi-order-view/multi-order-view.component';
import { OrderDropComponent } from './components/order-drop/order-drop.component';
ClarityIcons.addIcons(...[...coreCollectionIcons, ...essentialCollectionIcons, ...travelCollectionIcons]);

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    GlobalAlertComponent,
    GlobalProgressComponent,
    CatalogViewComponent,
    OrderViewComponent,
    VerticalNavResizeComponent,
    NavResizeDirectiveDirective,
    FilterOnIdPipe,
    LoginComponent,
    CurrentStateComponent,
    MultiOrderViewComponent,
    OrderDropComponent
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
    PulsarService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
