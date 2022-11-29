import { Component, OnInit } from '@angular/core';
import { NgxFileDropEntry } from 'ngx-file-drop';
import { ParserService } from 'src/app/services/parser.service';
import { PulsarService, UserOrder } from 'src/app/services/pulsar.service';
import { User } from '../../../../../node-test-wss/fastAPI-Types';

@Component({
  selector: 'app-order-drop',
  templateUrl: './order-drop.component.html',
  styleUrls: ['./order-drop.component.scss']
})
export class OrderDropComponent implements OnInit {
  
  public files: NgxFileDropEntry[] = [];
  public orders: UserOrder[] = [];

  constructor(
    private parser: ParserService,
    private orderSvc: PulsarService
  ) {}

  ngOnInit(): void {
  }

  public dropped(files: NgxFileDropEntry[]) {
    this.files = files;
    for (const droppedFile of files) {

      // Is it a file?
      if (droppedFile.fileEntry.isFile) {
        const fileEntry = droppedFile.fileEntry as FileSystemFileEntry;
        fileEntry.file((file: File) => {

          // Here you can access the real file
          console.log(droppedFile.relativePath, file);

          const orders: UserOrder[] = this.parser.parseFile(file);
          this.orders.push(...orders);
          
        });
      } else {
        // It was a directory (empty directories are added, otherwise only files)
        const fileEntry = droppedFile.fileEntry as FileSystemDirectoryEntry;
        console.log(droppedFile.relativePath, fileEntry);
      }
    }
  }

  public fileOver(event: DragEvent){
    console.log(event);
  }

  public fileLeave(event: DragEvent){
    console.log(event);
  }
}