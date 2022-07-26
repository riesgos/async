import { Component, HostBinding, OnInit } from '@angular/core';
import { DbService } from 'src/app/services/db.service';

@Component({
  selector: 'app-catalog-view',
  templateUrl: './catalog-view.component.html',
  styleUrls: ['./catalog-view.component.scss']
})
export class CatalogViewComponent implements OnInit {
  @HostBinding('class') class = 'content-container';

  constructor(private dbSvc: DbService) { }

  ngOnInit(): void {
  }

}
