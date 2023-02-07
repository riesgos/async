import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { UserOrder } from 'src/app/services/pulsar/pulsar.service';

@Component({
  selector: 'app-dropfield',
  templateUrl: './dropfield.component.html',
  styleUrls: ['./dropfield.component.css']
})
export class DropfieldComponent implements OnInit {

  @Output() fileDropped = new EventEmitter<UserOrder[]>();
  public dropFieldText = "Drop file here";

  constructor() { }

  ngOnInit(): void {
  }

  onDrop(event: DragEvent) {
    event.preventDefault(); // prevents new tab with contents from being opened
    const data = event.dataTransfer;
    const files = data?.files;
    if (files) {

      const fileNames: string[] = [];
      const tasks: Promise<string>[] = [];

      for (let i = 0; i < files.length; i++) {
        const file = files[i];
        tasks.push(file.text());
        fileNames.push(file.name);
      }

      Promise.all(tasks).then(results => {
        const orders: UserOrder[] = [];
        for (const result of results) {
          const rOrders: UserOrder[] = JSON.parse(result);
          orders.push(...rOrders);
        }
        this.fileDropped.emit(orders);
      });

      this.dropFieldText = `Read new file(s): ${fileNames.join(', ')}`;
    }
  }

  onDrag(event: DragEvent) {
    event.preventDefault(); // otherwise drop won't work
  }

}
