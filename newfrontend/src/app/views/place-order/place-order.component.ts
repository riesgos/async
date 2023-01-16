import { Component, OnInit } from '@angular/core';
import { PulsarService, UserOrder } from 'src/app/services/pulsar/pulsar.service';




@Component({
  selector: 'app-place-order',
  templateUrl: './place-order.component.html',
  styleUrls: ['./place-order.component.css']
})
export class PlaceOrderComponent implements OnInit {

  public orders: UserOrder[] = [];

  constructor(private pulsar: PulsarService) {}

  handleNewData(orders: UserOrder[]) {
    console.log(`read new orders: `, orders);
    this.orders = orders;
  }

  onSendOrderClicked() {
    for (const order of this.orders) {
      this.pulsar.postOrder(order).subscribe(success => console.log(`order transmitted with ${success ? 'success' : 'failure'}`));
    }
  }

  ngOnInit(): void { }

}
