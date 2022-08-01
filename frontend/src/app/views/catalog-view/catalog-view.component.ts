import { Component, HostBinding, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { DbService, IProduct } from 'src/app/services/db.service';

@Component({
  selector: 'app-catalog-view',
  templateUrl: './catalog-view.component.html',
  styleUrls: ['./catalog-view.component.scss']
})
export class CatalogViewComponent implements OnInit {
  @HostBinding('class') class = 'content-container';

  productTypes$ : Observable<IProduct['wps_identifier'][]>;
  products$: Observable<IProduct[]>;
  constructor(private dbSvc: DbService) {
    this.productTypes$ = this.dbSvc.getProductsTypes('/api/complex-outputs');
    this.products$ = this.dbSvc.getProducts('/api/complex-outputs');
  }

  ngOnInit(): void {

  }

}
