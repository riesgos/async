import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgxFileDropEntry } from 'ngx-file-drop';

@Component({
  selector: 'app-order-drop',
  templateUrl: './order-drop.component.html',
  styleUrls: ['./order-drop.component.scss']
})
export class OrderDropComponent implements OnInit {
  
  public files: NgxFileDropEntry[] = [];
  @Output() fileDropped = new EventEmitter<string>();

  constructor() {}

  ngOnInit(): void {
  }

  public dropped(files: NgxFileDropEntry[]) {
    this.files = files;
    for (const droppedFile of files) {

      // Is it a file?
      if (droppedFile.fileEntry.isFile) {
        const fileEntry = droppedFile.fileEntry as FileSystemFileEntry;
        fileEntry.file((f: File) => {

          // Here you can access the real file
          console.log(droppedFile.relativePath, f);

          const reader = new FileReader();
          reader.addEventListener('load', event => {
            const content = event.target?.result;
            if (content) {
              if (typeof content === 'string') this.fileDropped.emit(content);
            }
          });
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