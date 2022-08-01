import { Component, HostBinding, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { OrderService } from 'src/app/services/order.service';
import { model } from '../../services/model';

@Component({
  selector: 'app-order-view',
  templateUrl: './order-view.component.html',
  styleUrls: ['./order-view.component.scss']
})
export class OrderViewComponent implements OnInit {
  @HostBinding('class') class = 'content-container';

  public formGroup: FormGroup;
  public model = model;


  constructor(private orderSvc: OrderService, private fb: FormBuilder) {
    const formData: any = {};
    for (const step in this.model) {
      const stepFormData: any = {};
      for (const para in this.model[step]) {
        stepFormData[para] = null;
      }
      formData[step] = this.fb.group(stepFormData);
    }
    this.formGroup = this.fb.group(formData);

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
