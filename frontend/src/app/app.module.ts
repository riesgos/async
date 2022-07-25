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
import { ExampleViewComponent } from './views/example-view/example-view.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    GlobalAlertComponent,
    GlobalProgressComponent,
    ExampleViewComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    ClarityModule,
  ],
  providers: [
    AlertService,
    ProgressService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
