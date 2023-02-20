import { Component, OnInit } from '@angular/core';
import { map, Observable } from 'rxjs';
import { AppState, AppStateService } from 'src/app/services/appstate/appstate.service';
import { UserOrder } from 'src/app/services/backend/backend.service';




@Component({
  selector: 'app-place-order',
  templateUrl: './place-order.component.html',
  styleUrls: ['./place-order.component.css']
})
export class PlaceOrderComponent implements OnInit {

  public orders: UserOrder[] = [];
  public orderState$: Observable<AppState['orderState']>;

  constructor(private state: AppStateService) {
    this.orderState$ = this.state.state.pipe(map(s => {
      return s.orderState
    }));
  }

  handleNewData(orders: UserOrder[]) {
    console.log(`read new orders: `, orders);
    this.orders = orders;
  }

  onSendOrderClicked() {
    this.state.action({
      type: 'orderStart',
      payload: this.orders
    });
  }

  ngOnInit(): void { }

}
