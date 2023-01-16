import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgxFileDropEntry } from 'ngx-file-drop';
import { UserOrder } from 'src/app/services/pulsar/pulsar.service';

@Component({
  selector: 'app-order-drop',
  templateUrl: './order-drop.component.html',
  styleUrls: ['./order-drop.component.scss']
})
export class OrderDropComponent implements OnInit {
  
  public files: NgxFileDropEntry[] = [];
  @Output() fileDropped = new EventEmitter<UserOrder[]>();

  constructor() {}

  ngOnInit(): void {
  }

  public dropped(files: NgxFileDropEntry[]) {
    this.files = files;
    for (const droppedFile of files) {

      // Is it a file?
      if (droppedFile.fileEntry.isFile) {
        const fileEntry = droppedFile.fileEntry as FileSystemFileEntry;
        fileEntry.file(async (f: File) => {

          const text = await f.text();
          const data: UserOrder[] = JSON.parse(text);
          this.fileDropped.emit(data);

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