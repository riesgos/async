import { Component, OnInit } from '@angular/core';
import { AppStateService } from 'src/app/services/appstate/appstate.service';
import { UserOrder } from 'src/app/services/backend/backend.service';




@Component({
  selector: 'app-place-order',
  templateUrl: './place-order.component.html',
  styleUrls: ['./place-order.component.css']
})
export class PlaceOrderComponent implements OnInit {

  public orders: UserOrder[] = [];

  constructor(private state: AppStateService) {}

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
