import { Component, HostBinding, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { OrderService } from 'src/app/services/order.service';

@Component({
  selector: 'app-order-view',
  templateUrl: './order-view.component.html',
  styleUrls: ['./order-view.component.scss']
})
export class OrderViewComponent implements OnInit {
  @HostBinding('class') class = 'content-container';

  public formGroup: FormGroup;
  public schemas = ['SARA_v1.0'];
  public models = ['LimaCVT1_PD30_TI70_5000', 'LimaCVT2_PD30_TI70_10000', 'LimaCVT3_PD30_TI70_50000', 'LimaCVT4_PD40_TI60_5000', 'LimaCVT5_PD40_TI60_10000', 'LimaCVT6_PD40_TI60_50000', 'LimaBlocks'];


  constructor(private orderSvc: OrderService, private fb: FormBuilder) {
    this.formGroup = this.fb.group({
      schema: null,
      model: null, 
    });

    this.formGroup.valueChanges.subscribe(val => console.log(val));
  }

  ngOnInit(): void {
  }

  public submit() {
    this.send(this.formGroup.value);
  }

  public resetForm() {
    
  }

  private send(data: any) {
    this.orderSvc.postOrder({
      constraints: {
        ... data
      }
    }).subscribe(success => {
      console.log("success: ", success);
    })
  }

}
