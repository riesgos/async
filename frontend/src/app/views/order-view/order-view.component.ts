import { Component, HostBinding, OnInit } from '@angular/core';
import { OrderService } from 'src/app/services/order.service';

@Component({
  selector: 'app-order-view',
  templateUrl: './order-view.component.html',
  styleUrls: ['./order-view.component.scss']
})
export class OrderViewComponent implements OnInit {
  @HostBinding('class') class = 'content-container';

  public messageInput: string = 'Test Message';
  public response: string = '';

  constructor(private orderSvc: OrderService) { }

  ngOnInit(): void {
  }

  public send() {
    this.orderSvc.postOrder({
      targetProductId: "shakemap",
      constraints: {
        "quakeledger": {
          "magnitude": [8.5]
        }
      }
    }).subscribe(success => {
      this.response = "Successfully posted order."
    })
  }

}
