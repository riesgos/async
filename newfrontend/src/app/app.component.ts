import { Component, ViewEncapsulation } from '@angular/core';
import { AppStateService } from './services/appstate/appstate.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class AppComponent {

  constructor(private state: AppStateService) {
    this.state.action({
      type: 'appStart'
    });
  }
}
