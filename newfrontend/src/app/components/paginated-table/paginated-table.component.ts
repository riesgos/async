import { Component, Input, OnInit } from '@angular/core';

export interface Row {

}

@Component({
  selector: 'app-paginated-table',
  templateUrl: './paginated-table.component.html',
  styleUrls: ['./paginated-table.component.css']
})
export class PaginatedTableComponent implements OnInit {

  @Input() title = '';
  @Input() rows: Row[] = [];
  public columnNames: string[] = [];

  constructor() { }

  ngOnInit(): void {
    if (this.rows.length > 0) {
      this.columnNames = Object.keys(this.rows[0]);
    }
  }

}
